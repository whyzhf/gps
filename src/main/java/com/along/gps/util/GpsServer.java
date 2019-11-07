package com.along.gps.util;

import com.alibaba.fastjson.JSONObject;
import com.along.gps.controller.WebSocketController;
import com.along.gps.entity.Equip;
import com.along.gps.entity.GpsDescData;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.along.gps.controller.WebSocketController.sendOrderDemo;
import static com.along.gps.util.DataUtil.StringToLong;
import static com.along.gps.util.DataUtil.getNowData;
import static com.along.gps.util.Order.EquipOrder.*;
import static com.along.gps.util.Order.ErrorMsg.ERRORMAP;
import static com.along.gps.util.Order.HexadecimalUtil.*;
import static com.along.gps.util.Order.HexadecimalUtil.get10HexNum;
import static com.along.gps.util.Order.OrderUtil.retuenPowerOrder;
import static com.along.gps.util.Order.OrderUtil.sendStatus;
import static com.along.gps.util.SaveData.*;
import static com.along.gps.util.SystemUtil.*;


public class GpsServer {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		new GpsServer().openNettyServer(8899);
	}




	/**
	 * 基于netty启动服务，并开启监听
	 *
	 */

	public void openNettyServer(int port) {
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
										//测试命令收发
										sendOrderDemo(hexStr);

										//初次查询设备状态
										if( ContextMap.get(ctx).getCard()==null && ContextMap.get(ctx).getStatus()==0){
											selEquipStatus(ctx,"00");
										}
										//发送更新设备状态，30分钟更新一次（非设备异常情况下）
										if ( ContextMap.get(ctx).getCard()!= null && ContextMap.get(ctx).getUptime()<=0){
											selEquipStatus(ctx,ContextMap.get(ctx).getCard());
										}
										//数据处理
										if (hexStr.startsWith("7E 02 00")) {//定位信息
											GpsDescData gpsDescData = httpData2(hexStr);
											if (gpsDescData != null) {
												//加上设备状态
												gpsDescData.setStauts("  "+ContextMap.get(ctx).getPower()+"(电量)    "+ ContextMap.get(ctx).getDeploy()+" "
														+ ContextMap.get(ctx).getClog()+" "+ ContextMap.get(ctx).getDemolition()+" "
														+ ContextMap.get(ctx).getLock()+" "+ ContextMap.get(ctx).getOnAndoff()+" ");

												gpsDescData.setEquipCard(ContextMap.get(ctx).getCard());

												//存储gps日志
												saveMsgToLog(ctx, hexStr);
												/*if (gpsDescData != null) {//存储gps数据
													new SaveData().saveDataToLog(gpsDescData);

												}*/
												//发送gps数据
												if(!"未定位".equals(gpsDescData.getErrorStatus())) {
													gpsDescData.setErrorStatus(ContextMap.get(ctx).getErrorStatus());
													WebSocketController.sendMessage2(gpsDescData);
													//在SaveData.saveRedis()方法中将数据存储到redis;
													SystemUtil.gpsDatalist.add(gpsDescData);
												}else{
													gpsDescData.setErrorStatus(ContextMap.get(ctx).getErrorStatus());
												}
												ContextMap.get(ctx).setTaskId(gpsDescData.getOutboundRoadlog().getTaskId());
												if (ContextMap.get(ctx)==null){//保存电话号码 通过电话号码判断定位信息发送到哪个任务
													Equip equip=new Equip();
													equip.setNum(gpsDescData.getEquip());
													ContextMap.put(ctx,equip);
												}else {
													if (gpsDescData.getEquip().equals(ContextMap.get(ctx).getNum())) {
													}else{
														//Equip equip=new Equip();
														ContextMap.get(ctx).setNum(gpsDescData.getEquip());
													}
												}
											}

										}else if(hexStr.startsWith("A5 14")){//脚扣反馈   //通过设备ID发送到设备命令
											//保存设备命令日志
											saveOrderToLog(ctx, hexStr);
											//解析出设备ID
											String[]str=hexStr.split(" ");
											String num=get10HexNum(str[2]+str[3]+str[4]+str[5])+"";
											String user=get10HexNum(str[6]+str[7]+str[8]+str[9])+"";

											//读取命令反馈
											//System.out.println(num+user+str[10]+str[11]+"::"+retuenPowerOrder(str[12]));
											ORDERMAP.put(num+user+str[10]+str[11],retuenPowerOrder(str[12]));
											if("12".equals(str[10])){//解析状态
												ContextMap.get(ctx).setPower(get10HexNum(str[11])+"%");
												char[] sta=hex10Byte(Integer.parseInt(str[12],16));
												StringBuffer error=new StringBuffer();
												if (sta[0]=='1'){
													ContextMap.get(ctx).setDeploy("已布防");
												}else{
													ContextMap.get(ctx).setDeploy("已撤防");
													error.append("已撤防 ");
												}
												if (sta[1]=='1'){
													ContextMap.get(ctx).setClog("被遮挡");
													error.append("被遮挡 ");
												}else{
													ContextMap.get(ctx).setClog("无遮挡");

												}
												if (sta[2]=='1'){
													ContextMap.get(ctx).setDemolition("被破拆");
													error.append("被破拆");
												}else{
													ContextMap.get(ctx).setDemolition("无破拆");

												}
												if (sta[3]=='1'){
													ContextMap.get(ctx).setLock("主锁打开");
													error.append("主锁打开 ");
												}else{
													ContextMap.get(ctx).setLock("主锁关闭");

												}
												if (sta[4]=='1'){
													ContextMap.get(ctx).setOnAndoff("电击启动");
												}else{
													ContextMap.get(ctx).setOnAndoff("电击关闭");
													error.append("电击关闭 ");
												}
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
											if (ContextMap.get(ctx)==null){//保存设备编号
												Equip equip=new Equip();
												equip.setCard(num);
												ContextMap.put(ctx,equip);
											}else {
												if (num.equals(ContextMap.get(ctx).getCard())) {
												}else{
													ContextMap.get(ctx).setCard(num);
												}
											}
											if (ContextMap.get(ctx).getStatus()==0) {//初始化布防设置时间
												FirConn(ctx,num);
												ContextMap.get(ctx).setStatus(1);
											}
											if ("A0".equals(str[10])){//异常反馈
												saveOrderToLog(ctx, hexStr);
												//触发设备状态查询
												selEquipStatus(ctx,num);
												//发送预警信息
												WebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),num,ERRORMAP.get(str[11])));
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
									Equip equip=new Equip();
									equip.setStatus(0);
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
									WebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),ContextMap.get(ctx).getCard(),"gps掉线"));
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
									WebSocketController.sendMessage2(ErrorMsg(ContextMap.get(ctx).getTaskId(),ContextMap.get(ctx).getCard(),"gps掉线"));
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


	//保存每个设备的连接通道
	public static Map<ChannelHandlerContext, Equip> ContextMap=null;
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
	private static ChannelHandlerContext getKeyByNum(Map<ChannelHandlerContext,Equip> map,String value){
		ChannelHandlerContext key=null;
		for (Map.Entry<ChannelHandlerContext, Equip> entry : map.entrySet()) {
			if(value.equals(entry.getValue().getNum())){
				key=entry.getKey();
			}
		}
		return key;
	}
	//通过手机号找设备号
	public static String getcardByNum(Map<ChannelHandlerContext,Equip> map,String value){
		String card="";
		if (map==null && map.isEmpty()){
			return card;
		}
		for (Map.Entry<ChannelHandlerContext, Equip> entry : map.entrySet()) {
			if(value.equals(entry.getValue().getNum())){
				card=entry.getValue().getCard();
			}
		}
		return card;
	}
	//通过设备号找通道
	private static ChannelHandlerContext getKeyByCard(Map<ChannelHandlerContext,Equip> map,String value){
		ChannelHandlerContext key=null;
		if (map==null && map.isEmpty()){
			return key;
		}
		for (Map.Entry<ChannelHandlerContext, Equip> entry : map.entrySet()) {
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