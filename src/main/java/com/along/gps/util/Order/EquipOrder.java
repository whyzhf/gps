package com.along.gps.util.Order;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.along.gps.util.Order.HexadecimalUtil.hexStringToByteArray;
import static com.along.gps.util.Order.OrderUtil.send;
import static com.along.gps.util.Order.OrderUtil.sendStatus;
import static com.along.gps.util.SaveData.saveOrderToLog;
import static com.along.gps.util.SystemUtil.ORDERMAP;
import static com.along.gps.util.SystemUtil.orderloglist;

public class EquipOrder {
	//开启布防，设置时间
	public static void InitialSetup(ChannelHandlerContext cxt, String equip){
		new Thread(()->{
			sendToWeb(cxt,send(equip, "9175040", 17));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sendToWeb(cxt,send(equip, "9175040", 13));
		}).start();
	}
	//查询设备状态
	public static void selEquipStatus(ChannelHandlerContext cxt,String equipId){
		new Thread(()->{
			sendToWeb(cxt,sendStatus(equipId));
		}).start();
	}

	//设置是否电击状态
	public static void setEquipStatus(ChannelHandlerContext cxt,String equipId){


	}

	//send 发送电击命令
	public static String sendOrder(String equipId,String UserID){
	//	UserID = (1000000+Integer.parseInt(UserID))+"";
		return send(equipId,UserID,15);
	}

	public static void sendToWeb(ChannelHandlerContext cxt,String order){
		//将命令转换成ByteBuf
		byte[]orderArr=hexStringToByteArray(order);
		ByteBuf byteBuf = Unpooled.copiedBuffer(orderArr);
		cxt.writeAndFlush(byteBuf);
		//保存设备命令日志
		saveOrderToLog(cxt, order);

	}

	public static void sendToWebPL(ChannelHandlerContext cxt,String order){
		//将命令转换成ByteBuf
		char[] chars = order.toCharArray();
		cxt.writeAndFlush(chars);
		//保存设备命令日志
		saveOrderToLog(cxt, order);
	}
}
