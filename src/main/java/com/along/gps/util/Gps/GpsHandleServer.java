package com.along.gps.util.Gps;

import com.along.gps.controller.NettyWebSocketController;
import com.along.gps.controller.WebSocketController;
import com.along.gps.entity.*;
import com.along.gps.util.ConvertData;
import com.along.gps.util.Order.ErrorMsg;
import com.along.gps.util.SystemUtil;
import com.along.gps.util.ThreadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.along.gps.util.Order.EquipOrder.*;
import static com.along.gps.util.Order.ErrorMsg.ERRORMAP;
import static com.along.gps.util.Order.HexadecimalUtil.*;
import static com.along.gps.util.Order.OrderUtil.retuenPowerOrder;
import static com.along.gps.util.Gps.HandleData.*;
import static com.along.gps.util.SystemUtil.*;
import static org.springframework.jmx.support.ObjectNameManager.getInstance;


public class GpsHandleServer {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		new GpsHandleServer().openNettyServer(8899);
	}

	/**
	 * 基于netty启动服务，并开启监听
	 * 1.设备连接，设备初始化设置（布防，设置时间）,获取设备状态，保存设备连接通道
	 * 2.接收数据（gps+脚扣），封装对象，webscoket发送，
	 * 3.发送命令，通过设备号码匹配通道发送数据
	 * 4.设备断开，销毁通道
	 */
	public void openNettyServer(int port) {
		SystemUtil.NETTYSESSIONMAP=new ConcurrentHashMap<>();
		new ErrorMsg();
		new ColorUtil();
		//创建通道保存集合
		ContextMap=new ConcurrentHashMap<>();
		//开启数据存储线程
		ThreadUtil.init();
		EventLoopGroup group = new NioEventLoopGroup();// 连接服务对象
        EventLoopGroup workGroup = new NioEventLoopGroup();// 读写服务对象
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(group, workGroup).channel(NioServerSocketChannel.class)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.option(ChannelOption.SO_BACKLOG, 2048)//serverSocketchannel的设置，链接缓冲池的大小
					.childOption(ChannelOption.SO_KEEPALIVE, true)//socketchannel的设置,维持链接的活跃，清除死链接
					.childOption(ChannelOption.TCP_NODELAY, true)//socketchannel的设置,关闭延迟发送
					.childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
						@Override
						public void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
							// ch.pipeline().addLast(new
							// GpsMsgEncoder(1024,3,2,10,0));//处理断包
							ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
								@Override
								protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bf) throws Exception {
									// System.out.println("开始读取数据...");
									StringBuilder sb = new StringBuilder();
									try {
										byte[] data = new byte[bf.readableBytes()];
										bf.readBytes(data);
										for (int i = 0; i < data.length; i++) {
											sb.append(ConvertData.byteToHex(data[i]) + " ");
										}
										// 转义
										String hexStr = ConvertData.replaceData(sb.toString().trim());
										//初次查询设备状态
										if( ContextMap.get(ctx).getCard()==null && ContextMap.get(ctx).getType()==0){
											selEquipStatus(ctx,"00");
										}
										//发送更新设备状态，30分钟更新一次（非设备异常情况下）
										if ( ContextMap.get(ctx).getCard()!= null && ContextMap.get(ctx).getUptime()<=0){
											selEquipStatus(ctx,ContextMap.get(ctx).getCard());
										}
										//System.out.println(hexStr);
										//数据处理
										if (hexStr.startsWith("7E 02 00")) {//定位信息
											NgpsData gpsDescData = httpData2(hexStr);
											//存储gps日志
											saveMsgToLog(ctx, hexStr);
											if (null!=gpsDescData) {
												ContextMap.get(ctx).setTaskId(gpsDescData.getTaskId());
											//	System.out.println(gpsDescData);
												if (ContextMap.get(ctx) == null) {//保存电话号码 通过电话号码判断定位信息发送到哪个任务
													GpsStatusData equip = new GpsStatusData();
													equip.setNum(gpsDescData.getEquip());
													ContextMap.put(ctx, equip);
												} else {
													if (gpsDescData.getEquip().equals(ContextMap.get(ctx).getNum())) {
													} else {
														//Equip equip=new Equip();
														ContextMap.get(ctx).setNum(gpsDescData.getEquip());
													}
												}
												if (!"-1".equals(gpsDescData.getErrorStatus())) {
													//加上设备状态
													gpsDescData.setStauts(ContextMap.get(ctx).getStatus());
													gpsDescData.setErrorStatus(ContextMap.get(ctx).getErrorStatus());
													//数据保存
													WSgpsData wSgpsData = new WSgpsData(gpsDescData);
													GPSDATALIST.add(gpsDescData);
													WSGPSLIST.add(wSgpsData);
													//发送gps数据
													NettyWebSocketController.sendMessage2(wSgpsData);
													//在SaveData.saveRedis()方法中将数据存储到redis;
													//SystemUtil.gpsDatalist.add(gpsDescData);
												}
											}
										}else if(hexStr.startsWith("A5 14")){//脚扣反馈   //通过设备ID发送到设备命令
											//保存设备命令日志
											saveOrderToLog(ctx, hexStr);
											//解析出设备ID
											String[]str=hexStr.split(" ");
											String card=get10HexNum(str[2]+str[3]+str[4]+str[5])+"";
											String user=get10HexNum(str[6]+str[7]+str[8]+str[9])+"";
											if (ContextMap.get(ctx)==null){//保存设备编号
												GpsStatusData equip=new GpsStatusData();
												equip.setCard(card);
												ContextMap.put(ctx,equip);
											}else {
												if (card.equals(ContextMap.get(ctx).getCard())) {
												}else{
													ContextMap.get(ctx).setCard(card);
												}
											}
											//读取命令反馈
											ORDERMAP.put(card+user+str[10]+str[11],retuenPowerOrder(str[12]));
											if("12".equals(str[10])){//解析状态
												StringBuffer status=new StringBuffer();
												status.append(get10HexNum(str[11])).append("%(电量)");
												char[] sta=hex10Byte(Integer.parseInt(str[12],16));
												StringBuffer error=new StringBuffer();

												if (sta[0]=='1'){
													status.append(",已布防");
												}else{
													status.append(",已撤防");
													error.append("已撤防 ");
												}
												if (sta[1]=='1'){
													status.append(",被遮挡");
													error.append("被遮挡 ");
												}else{
													status.append(",无遮挡");

												}
												if (sta[2]=='1'){
													status.append(",被破拆");
													error.append("被破拆 ");
												}else{
													status.append(",无破拆");
												}
												if (sta[3]=='1'){
													status.append(",主锁打开");
													error.append("主锁打开 ");
												}else{
													status.append(",主锁关闭");
												}
												if (sta[4]=='1'){
													status.append(",电击启动");
												}else{
													status.append(",电击关闭");
													error.append("电击关闭 ");
												}
												ContextMap.get(ctx).setStatus(status.toString());
												/*ContextMap.get(ctx).setDeploy(sta[0]=='1'?"已布防":"已撤防");
												ContextMap.get(ctx).setClog(sta[1]=='1'?"被遮挡":"无遮挡");
												ContextMap.get(ctx).setDemolition(sta[2]=='1'?"被破拆":"无破拆");
												ContextMap.get(ctx).setLock(sta[3]=='1'?"主锁打开":"主锁关闭");
												ContextMap.get(ctx).setOnAndoff(sta[4]=='1'?"电击启动":"电击关闭");*/
												if ( (error.toString())!=null && (error.toString()).length()>2) {
													ContextMap.get(ctx).setErrorStatus(error.toString());
												}
												ContextMap.get(ctx).setUptime(Calendar.getInstance());
											}

											if (ContextMap.get(ctx).getType()==0) {//初始化布防设置时间
												FirConn(ctx,card);
												ContextMap.get(ctx).setType(1);
											}
											if ("A0".equals(str[10])){//异常反馈
												saveOrderToLog(ctx, hexStr);
												//触发设备状态查询
												selEquipStatus(ctx,card);
												//发送预警信息
												NettyWebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),card,ERRORMAP.get(str[11])));
											}
										}
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}

								/**
								 * 活动状态
								 */
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									System.out.println(ctx.channel().remoteAddress() + "->tcp连接成功");
									GpsStatusData equip=new GpsStatusData();
									equip.setType(0);
									equip.setErrorStatus("gps定位成功");
									ContextMap.put(ctx,equip);
									selEquipStatus(ctx,"00");
								}

								/**
								 * 断开状态
								 */
								@Override
								public void channelInactive(ChannelHandlerContext ctx) throws Exception {
									System.out.println(ctx.channel().remoteAddress() + "->tcp断开连接");
									NettyWebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),ContextMap.get(ctx).getCard(),"gps掉线"));
									ContextMap.remove(ctx);
								}

								@Override
								public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

								}
								/**
								 * 异常
								 */
								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
									cause.printStackTrace();
									System.out.println(ctx.channel().remoteAddress() + "->tcp异常:断开连接");
									System.out.println(ErrorMsg(ContextMap.get(ctx).getTaskId(), ContextMap.get(ctx).getCard(), "gps掉线"));
									NettyWebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),ContextMap.get(ctx).getCard(),"gps掉线"));
									ctx.close();// 关闭客户端
									ContextMap.remove(ctx);
								}

								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									// TODO Auto-generated method stub
									//System.out.println("msg:"+ msg);
									super.channelRead(ctx, msg);
								}
							});
						}
					});

			// 绑定端口，并开启异步阻塞
			ChannelFuture f = b.bind(port).sync();
			System.out.println("服务器启动，开始监听：" + f.channel().localAddress());
			// 等待客户端关闭，并阻塞，阻止main运行结束
			f.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}



	/***********************************发送数据*****************************************/



	public static void send(String Order) {
		System.out.println("发送命令...");
		Order=Order.replaceAll(" ","");
		//拆分字符串
		String[]str = Order.split("(?<=\\G.{2})");
		String card=get10HexNum(str[2]+str[3]+str[4]+str[5])+"";
		//System.out.println(card);
		ChannelHandlerContext ctx=getKeyByCard(ContextMap,card);
		byte[]order=hexStringToByteArray(Order);
		if(ctx!=null) {
			//将命令转换成ByteBuf
			ByteBuf byteBuf = Unpooled.copiedBuffer(order);
			//发送命令
			ctx.writeAndFlush(byteBuf);
		}
	}
	public static void sendPower(String card,String userId) {
		System.out.println("开始电击...");
		ChannelHandlerContext ctx=getKeyByCard(ContextMap,card);
		String orderStr=sendOrder(card,userId);
		byte[]order=hexStringToByteArray(orderStr);
		if(ctx!=null) {
			//将命令转换成ByteBuf
			ByteBuf byteBuf = Unpooled.copiedBuffer(order);
			//发送命令
			ctx.writeAndFlush(byteBuf);
			//保存设备命令日志
			saveOrderToLog(ctx, orderStr);
			ORDERMAP.put(card+userId+"0120","0");
		}
	}
	//通过手机号找通道
	private static ChannelHandlerContext getKeyByNum(Map<ChannelHandlerContext,GpsStatusData> map,String value){
		ChannelHandlerContext key=null;
		for (Map.Entry<ChannelHandlerContext, GpsStatusData> entry : map.entrySet()) {
			if(value.equals(entry.getValue().getNum())){
				key=entry.getKey();
			}
		}
		return key;
	}
	//通过手机号找设备号
	public static String getcardByNum(Map<ChannelHandlerContext, GpsStatusData> map, String value){
		String card="";
		if (map==null && map.isEmpty()){
			return card;
		}
		for (Map.Entry<ChannelHandlerContext, GpsStatusData> entry : map.entrySet()) {
			if(value.equals(entry.getValue().getNum())){
				card=entry.getValue().getCard();
			}
		}
		return card;
	}
	//通过设备号找通道
	private static ChannelHandlerContext getKeyByCard(Map<ChannelHandlerContext,GpsStatusData> map,String value){
		ChannelHandlerContext key=null;
		if (map==null && map.isEmpty()){
			return key;
		}
		for (Map.Entry<ChannelHandlerContext, GpsStatusData> entry : map.entrySet()) {
			if(value.equals(entry.getValue().getCard())){
				key=entry.getKey();
			}
		}
		return key;
	}
	/*************************************设备连接后操作******************************************************/
	public static  void  FirConn(ChannelHandlerContext cxt,String equipId){
		//获取到设备id
		//开启布防，设置时间
		//查询设备状态
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 InitialSetup(cxt,equipId);
	}


}