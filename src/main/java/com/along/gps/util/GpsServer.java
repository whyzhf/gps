package com.along.gps.util;

import com.along.gps.controller.WebSocketController;
import com.along.gps.entity.GpsDescData;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.along.gps.util.SaveData.*;


public class GpsServer {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		new GpsServer().openNettyServer(8899);
	//	new GpsServer().openServer(8800);

	}

	/**
	 * 服务器相关
	 */
	private ServerSocketChannel ssc;
	private Selector sel;


	/**
	 * 启动服务
	 */
	private void openServer(int port) {
		try {

			ssc = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress(port));
			ssc.configureBlocking(false);
			sel = Selector.open();
			ssc.register(sel, SelectionKey.OP_ACCEPT);
			System.out.println("初始化服务器...");
		} catch (IOException ex) {
			Logger.getLogger(GpsServer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * 监听sel多路复合选择器
	 */
	private void listen() {
		openServer(8894);
		System.out.println("开始监听...");
		while (true) {
			try {
				sel.select();
				Iterator<SelectionKey> keys = sel.selectedKeys().iterator();
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					// 删除已选key，防止重复处理
					keys.remove();
					// sel.selectNow();
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel sc = server.accept();
						sc.configureBlocking(false);
						sc.register(sel, SelectionKey.OP_READ);

						System.out.println(sc.socket().getInetAddress().getHostAddress() + ":" + sc.socket().getPort()
								+ "->tcp连接成功");
					}
					if (key.isReadable()) {
						System.out.println("开始读取数据...");
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer bf = ByteBuffer.allocate(1024);
						int len;
						StringBuilder sb = new StringBuilder();
						try {
							while ((len = sc.read(bf)) > 0) {
								bf.flip();// 翻转指针
								byte[] data = bf.array();
								for (int i = 0; i < len; i++) {
									// sb.append(byteToHex(data[i]) + " ");
								}
								bf.clear();
								//System.out.println("11111111111");
							}

							if (!sb.toString().trim().equals("")) {

								// 去掉前后空格
								// addClient(sc, sb.toString().trim());
								//saveMsgToLog(sc, sb.toString().trim());
								//saveData(sb.toString().trim());
								// System.out.println(sb.toString().trim());
							}

						} catch (Exception ex) {
							ex.printStackTrace();
							key.cancel();
							//sc.close();

							sc.socket().close();
							sc.close();
						}

					}

				}
			} catch (IOException ex) {
				Logger.getLogger(GpsServer.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
	}



	/**
	 * 保存到数据库
	 *
	 * @param hexData
	 */
	private void saveData(ChannelHandlerContext ctx, String hexData) {
		// gps上传数据
		if (hexData.startsWith("7E 02 00")) {
			// 检查数据有效性
			if (ConvertData.checkData(hexData)) {
				//saveMsgToLog(ctx, hexData);
				String data = ConvertData.getHexMsgToString(hexData);
				String[] strArr = data.split(";");
				System.out.println(data);
				//	service.updateData(strArr);

			} else {
				System.out.println("数据格式校验未通过...");
			}

		} else if (hexData.startsWith("7E 07 04")) {// 批量上传或是补传

		} else {
			// 不处理
		}

	}

	/**
	 * 基于netty启动服务，并开启监听
	 *
	 */

	public void openNettyServer(int port) {

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
										//数据处理
										GpsDescData gpsDescData = httpData2(hexStr);
										new Thread(()->{
											saveMsgToLog(ctx,hexStr);
											if (gpsDescData!=null) {
												saveDataToLog(gpsDescData);
											}
										}).start();
										if (gpsDescData!=null) {
											WebSocketController.sendMessage2(gpsDescData);
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
								}

								/**
								 * 断开状态
								 */
								@Override
								public void channelInactive(ChannelHandlerContext ctx) throws Exception {

								}

								@Override
								public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

								}


								/**
								 * 异常
								 */
								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
										throws Exception {
									cause.printStackTrace();
									ctx.close();// 关闭客户端
								}

								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									// TODO Auto-generated method stub
									System.out.println("msg:"+ msg);
									super.channelRead(ctx, msg);
								}
							});
						}
					});

			// 绑定端口，并开启异步阻塞
			ChannelFuture f = b.bind(port).sync();
			System.out.println(f.toString());
			System.out.println("服务器启动，开始监听：" + f.channel().localAddress());

			//listen();
			// 等待客户端关闭，并阻塞，阻止main运行结束
			f.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	/**
	 * 保存数据
	 *
	 * @param ctx
	 * @param hexData
	 */
	private void saveMsg(ChannelHandlerContext ctx, String hexData) {
		if (!hexData.equals("")) {
			// 去掉前后空格
			saveData(ctx, hexData);


		}
	}

}
