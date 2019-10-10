package com.along.gps.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtil {
	/**
	 * 获取当前时间
	 * */
	public static String getNowData( String strFormat) {
		Calendar calendar= Calendar.getInstance();
		SimpleDateFormat dateFormat= new SimpleDateFormat(strFormat);
		return dateFormat.format(calendar.getTime());
	}
	/**
	 * 将java.sql.Timestamp对象转化为String字符串
	 * @param time
	 *            要格式的java.sql.Timestamp对象
	 * @param strFormat
	 *            输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
	 * @return 表示日期的字符串
	 */
	public static String dateToStr(java.sql.Timestamp time, String strFormat) {
		DateFormat df = new SimpleDateFormat(strFormat);
		String str = df.format(time);
		return str;
	}

	/**
	 * 将String字符串转换为java.sql.Timestamp格式日期,用于数据库保存
	 * @param strDate
	 *            表示日期的字符串
	 * @param dateFormat
	 *            传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
	 * @return java.sql.Timestamp类型日期对象（如果转换失败则返回null）
	 */
	public static java.sql.Timestamp strToSqlDate(String strDate, String dateFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		java.sql.Timestamp dateSQL = new java.sql.Timestamp(date.getTime());
		return dateSQL;
	}
	/**
	 * 获得上一个月
	 * @return
	 */
	public static String getLastMonth( String dateFormat){
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // 设置为当前时间
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
		date = calendar.getTime();
		String accDate = format.format(date);
		return accDate;
	}

	/**
	 * 获得该月第一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year,int month,String dateFormat){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String firstDayOfMonth = sdf.format(cal.getTime());
		return firstDayOfMonth;
	}

	/**
	 * 获得该月最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year,int month,String dateFormat){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}


}


