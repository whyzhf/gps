package com.along.gps.util;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 写入日志工具类
 * @author Administrator
 *
 */

public  class LogUtil {
	/**
	 * 写入日志信息
	 * @param msg 消息
	 */
	public synchronized static void writeLog(String msg){

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());   
		Writer w = null;
		BufferedWriter bw = null;
		try {
			String FileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
			File dir = new File(SysUtil.CONSOLE_LOG_LOCAL);
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			File f = new File(dir+"\\"+FileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			bw.write(dateTime+" -> "+msg + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				w.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	
}
