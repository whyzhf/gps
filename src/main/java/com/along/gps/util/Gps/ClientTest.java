/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.along.gps.util.Gps;


import com.alibaba.fastjson.JSONObject;
import com.along.gps.util.ConvertData;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.along.gps.util.SysUtil.WEB_DATA_LOCATION;


/**
 *
 * @author Administrator
 */
public class ClientTest {

	public static void main(String args[]) {
		for (int i = 0; i <1; i++) {
			initServer(8899);
		}
	}
public static void initServer(int port){
	//for (int i = 0; i <2; i++) {

		if(flagbb==0) {
			flagbb=1;
			ClientTest client = new ClientTest();
			try {
				client.initClient(port);
				client.lister();
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	//}
}
	Selector sel;
	private static int flagbb=0;
	public synchronized void  initClient(int port) throws IOException {
		System.out.println(sel);
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		sel = Selector.open();
		boolean connect = sc.connect(new InetSocketAddress("172.18.65.129", port));
		//boolean connect = sc.connect(new InetSocketAddress("localhost", port));

		sc.register(sel, SelectionKey.OP_CONNECT);
		System.out.println("初始化客户端成功");
	}

	public void lister() throws IOException {
		while(true){
			sel.select();
			// 监听key
			Iterator<SelectionKey> keys = sel.selectedKeys().iterator();
			while (keys.hasNext()) {

				SelectionKey key = keys.next();
				// 删除已选key，防止重复处理
				keys.remove();
				// 表示请求连接的key
				if (key.isConnectable()) {
					// 请求连接
					SocketChannel sc = (SocketChannel) key.channel();
					if (sc.finishConnect()) {
						System.out.println("客户端连接成功");
						sc.configureBlocking(false);
						sc.register(sel, SelectionKey.OP_READ);
						ByteBuffer bf = ByteBuffer.allocate(1024);
						byte[] bs2=hexStringToByteArray("A514000C34FB008C00001253D0130B040F31152C");
						sc.write(bf.wrap(bs2));
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						byte[] bs21=hexStringToByteArray("A51400000072000000001258401209020A2F032E");
						sc.write(bf.wrap(bs2));
						System.out.println("客户端向服务端发送消息");
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}

						File file = new File(WEB_DATA_LOCATION+"/demo.txt");
						Reader in = new FileReader(file);
						BufferedReader br = new BufferedReader(in);
						while(br.ready()) {
							String str=br.readLine();
							if(!"".equals(str)) {
								if (str.contains("7E02000051010603455587")) {

								} else {
									byte[] bs = hexStringToByteArray(str);
									sc.write(bf.wrap(bs));
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
						flagbb=0;
						in.close();
						br.close();

						/*String str2="7E02000050010603455587000200000000000C00000157CEC806CA933800F900000000191108142023010400000000300118FE29E602000155070014534730322D56312E30313B4B3A353B473A33303020000A89860445041890132355BA7E";
						byte[] bs=hexStringToByteArray(str2);
						for (int i = 0; i < 1000; i++) {
							sc.write(bf.wrap(bs));
							try {
								java.lang.Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}*/


					}

				}

				//System.out.println(key.isWritable());

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
								sb.append(ConvertData.byteToHex(data[i]) + " ");
							}
							bf.clear();
						}

						if (!sb.toString().trim().equals("")) {
							// 去掉前后空格;
							System.out.println("www:"+sb.toString().trim());
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						key.cancel();
						sc.close();
					}

				}
			}
			break;
		}
	}
	public static byte[] hexStringToByteArray(String hexString) {
		hexString = hexString.replaceAll(" ", "");
		int len = hexString.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
			bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
					.digit(hexString.charAt(i + 1), 16));
		}
		return bytes;
	}
}
