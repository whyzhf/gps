package com.along.gps.util.Order;

import org.thymeleaf.util.StringUtils;

import static com.along.gps.util.DataUtil.getNowData;


/**
 * 进制装换
 * */
public class HexadecimalUtil {
	public static void main(String[] args) {
		System.out.println(low8("01"));
		System.out.println((byte) 26);
	}

	/**
	 * 十六进制转十进制
	 *
	 * @param num
	 * @return
	 */
	public static Integer get10HexNum(String num) {
		if (num.contains("0X")) {
			num = num.replace("0X", "");
		}
		return Integer.parseInt(num.substring(0), 16);
	}

	/**
	 * 十进制转十六进制
	 *
	 * @param num
	 * @return
	 */
	public static String get16Num(Object num) {

		return Integer.toHexString(Integer.parseInt(num + ""));
	}

	/**
	 * 十进制转十六进制,设置长度，不足补0
	 *
	 * @param num
	 * @return
	 */
	public static String get16NumAdd0(String num, int len) {
		String str = Integer.toHexString(Integer.parseInt(num)).toUpperCase();
		String res = "";
		if (len >= str.length()) {
			res = StringUtils.repeat("0", (len - str.length())) + str;
		} else {
			return str;
		}
		return res;
	}

	/**
	 * 当前时间转16进制
	 * 返回 “16进制时间值-时间值总和”
	 */
	public static String get16NumByTime(String strFormat) {
		String str = getNowData(strFormat);
		String[] arr = str.split("-");
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += Integer.parseInt(arr[i]);
			arr[i] = get16NumAdd0(arr[i], 2);

		}
		return String.join("", arr) + "-" + sum;
	}

	//num & 0xff
	public static int low8(Object num) {
		return Integer.parseInt(num + "") & 0xff;
	}

	//获取高四位
	public static int getHeight4(byte data) {
		int height;
		height = ((data & 0xf0) >> 4);
		return height;
	}

	/**
	 * 16进制表示的字符串转换为字节数组
	 *
	 * @param hexString 16进制表示的字符串
	 * @return byte[] 字节数组
	 */
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
	/**
	 * 16进制表示的字符串转换为二进制数组
	 */
	public static char[] hex16Byte(int num) {
		char[] chs = {'0','1'};
		char[] arr = new char[8];//设置位数
		int pos = arr.length;
		while(num!=0) {
			int temp = num & 1;
			arr[--pos] = chs[temp];
			num = num >>> 1;
		}
		return  arr;
	}
	/**
	 * 16进制表示的字符串转换为二进制数组
	 */
	public static char[] hex10Byte(int num) {
		return Integer.toBinaryString(num).toCharArray();
	}

}