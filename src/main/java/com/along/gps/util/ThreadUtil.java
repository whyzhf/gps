package com.along.gps.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static com.along.gps.util.Gps.HandleData.*;
import static com.along.gps.util.SystemUtil.*;

//用于处理数据存储，日志记录线程管理
public class ThreadUtil {
	public static void init(){
		//数据存储redis
		redisThread();
		//脚扣命令日志存储
		orderLogThread();
		//gps命令日志存储
		gpsLogThread();
		//存入数据库
		saveThread();
	}
	/**
	 * 开启一个线程，gps数据存储写入（存入数据库）
	 *
	 */
	public static void saveThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.out.println("1111111111111");
					try {
						while (!GPSDATALIST.isEmpty()) {
							saveDataBySql();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();

	}

	/**
	 * 开启一个线程，redis数据存储写入文件（存入数据库）
	 *
	 */
	public static void redisThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						while (!SystemUtil.WSGPSLIST.isEmpty()) {
							saveRedis();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();

	}
	/**
	 * 开启一个线程，保存脚扣相关命令
	 *
	 */
	public static void orderLogThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String FileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-Order.txt";
					String FileUrl = SysUtil.WEB_LOG_LOCATION;
					try {
						while (!orderloglist.isEmpty()) {
							pubWriterFile(FileName,	FileUrl,orderloglist);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();

	}

	/**
	 * 开启一个线程，保存gps相关命令
	 *
	 */
	public static void gpsLogThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						String FileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-Gps.txt";
						String FileUrl =SysUtil.WEB_LOG_LOCATION;
						while (!gpsloglist.isEmpty()) {
							pubWriterFile(FileName,	FileUrl,gpsloglist);

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();

	}
}
