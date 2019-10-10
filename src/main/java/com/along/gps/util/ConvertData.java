package com.along.gps.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 回传设备十六进制转换成指定格式输出工具类
 * 
 * @author Administrator
 */
public class ConvertData {
	private static int sortNum=0x00;

	enum field {
		WB_HEAD(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, "WB_HEAD"), // 伟邦定义头部
		WB_BODY_LEN(new int[] { 3, 4 }, "WB_BODY_LEN"), // 伟邦消息体定义长度
		WB_MEG_ID(new int[] { 1, 2 }, "WB_MEG_ID"),//消息id
		WB_PBACKCODE(new int[] { 5, 6, 7, 8, 9, 10 }, "WB_PBACKCODE"), // 伟邦gps设备编号
		WB_SORTCODE(new int[] { 11, 12 }, "WB_PBACKCODE"), // 伟邦自定义顺序编码
		WB_ALARM(new int[] { 13, 14,15, 16 }, "WB_ALARM"), // 警报标识
		WB_STATE(new int[] { 17, 18,19, 20 }, "WB_STATE"), // 状态
		WB_LAT(new int[] { 21, 22, 23, 24 }, "WB_LAT"), // 纬度
		WB_LOT(new int[] { 25, 26, 27, 28 }, "WB_LOT"), // 经度
		WB_ALTITUDE(new int[] { 29,30 }, "WB_ALTITUDE"), // 海拔高度
		WB_SPEED(new int[] {31,32 }, "WB_SPEED"), // 速度
		WB_DIR(new int[] { 33,34 }, "WB_DIR"),// 方向
		WB_CURRTIME(new int[] { 35,36,37,38,39,40 }, "WB_CURRTIME");//当前时间
		private final int[] index;
		private final String value;

		field(int[] index, String value) {
			this.index = index;
			this.value = value;
		}

		/**
		 * @return the index
		 */
		public int[] getIndex() {
			return index;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * 检测数据有效性
	 * 
	 * @param hexStr
	 * @return
	 */
	public static boolean checkData(String hexStr) {
		String[] arr = hexStr.split(" ");
		long bodyLen = convertHexToInt(arr[field.WB_BODY_LEN.getIndex()[0]] + arr[field.WB_BODY_LEN.getIndex()[1]]);
		// 头部+消息体+校验码+结尾
		final int len = (int) (field.WB_HEAD.getIndex().length + bodyLen + 2);
		// 根据校验码检测数据的有效性
		int temp = 0;
		for (int i = 1; i < len - 2; i++) {
			temp ^= Integer.parseInt(arr[i], 16);
		}
		// System.out.println(temp);
		if (temp != Integer.parseInt(arr[len - 2], 16)) {
			return false;
		}
		return true;
	}

	/**
	 * 替换字符串
	 * 
	 * @param hexStr
	 *            7D 02 =>7E /7D 01=>7D
	 * @return
	 */
	public static String replaceData(String hexStr) {
		hexStr = hexStr.replaceAll("7D 01", "7D");
		hexStr = hexStr.replaceAll("7D 02", "7E");
		return hexStr;
	}

	/**
	 * 十六进制设备数据转换成java数据字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String getHexMsgToString(String data) {
		StringBuilder sb = new StringBuilder();
		try{
			String[] arr = data.split(" ");
			
			field[] fs = field.class.getEnumConstants();
			for (field f : fs) {
				switch (f) {
				case WB_PBACKCODE:
					int[] codeIndexs = f.getIndex();
					String pcode = "";
					for (int i : codeIndexs) {
						pcode += arr[i];
					}
					if (pcode.startsWith("0")) {
						pcode = pcode.replaceFirst("0", "");
					}
					sb.append(pcode);
					sb.append(";");
					break;
				case WB_STATE:
					String state = "";
					for (int index : field.WB_STATE.getIndex()) {
						state += arr[index];
					}

					long weidu = (convertHexToInt(state) & (0x0004)) >> 2;
					if (weidu == 1) { // 南纬
						sb.append("南纬");
						sb.append(";");
					} else {// 北纬
						sb.append("北纬");
						sb.append(";");
					}

					long jingdu = (convertHexToInt(state) & (0X0008)) >> 3;
					if (jingdu == 1) { // 西经
						sb.append("西经");
						sb.append(";");
					} else {// 东经
						sb.append("东经");
						sb.append(";");
					}
					//是否定位
					long dwei = (convertHexToInt(state) & (0X0002)) >> 1;
					String isDw="";
					if(dwei==1){
						isDw="定位";
					}else{
						isDw="未定位";
					}
					sb.append(isDw);
					sb.append(";");
					break;
				case WB_LAT:
					String lat ="";
					for (int index : field.WB_LAT.getIndex()) {
						lat += arr[index];
					}
					//System.out.println(lat);
					double latVal =(convertHexToInt(lat) /Math.pow(10,6));
					sb.append(latVal);
					sb.append(";");
					break;
				case WB_LOT:
					String lot ="";
					for (int index : field.WB_LOT.getIndex()) {
						lot += arr[index];
					}
					double lotVal =(convertHexToInt(lot) /Math.pow(10,6));
					sb.append(lotVal);
					sb.append(";");
					break;
				case WB_ALTITUDE:
					String h ="";
					for (int index : field.WB_ALTITUDE.getIndex()) {
						h += arr[index];
					}
					sb.append(convertHexToInt(h));
					sb.append(";");
					break;
				case WB_SPEED:
					String speed ="";
					for (int index : field.WB_SPEED.getIndex()) {
						speed+= arr[index];
					}
					sb.append(convertHexToInt(speed));
					sb.append(";");
					break;
				case WB_DIR:
					String dir ="";
					for (int index : field.WB_DIR.getIndex()) {
						dir += arr[index];
					}
					sb.append(convertHexToInt(dir));
					sb.append(";");
					break;
				case WB_CURRTIME:
				String y=2000+Integer.parseInt(arr[field.WB_CURRTIME.getIndex()[0]])+"";
				String M=arr[field.WB_CURRTIME.getIndex()[1]];
				String d=arr[field.WB_CURRTIME.getIndex()[2]];
				String H=arr[field.WB_CURRTIME.getIndex()[3]];
				String m=arr[field.WB_CURRTIME.getIndex()[4]];
				String s=arr[field.WB_CURRTIME.getIndex()[5]];
				sb.append(y+"-"+M+"-"+d+" "+H+":"+m+":"+s);
					break;
				}
			}
		}catch(Exception ex){
			
		   ex.printStackTrace();
		   LogUtil.writeLog(data);
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}
	/**
	 * 根据消息生成消息应答
	 * @param data
	 * @return
	 */
    public static  byte[] replyMsg(String data){
    	String [] arr= data.split(" ");
        List<Byte> bts = new ArrayList<>();
          //头部
		byte [] head = new byte[]{ 0x7E,(byte) 0x80,0x01,0x00,0x05};
        for(byte h:head){
        	bts.add(h);
        }
		//设备编号
    	int[] codeIndexs = field.WB_PBACKCODE.getIndex();
		for (int i : codeIndexs) {
		 bts.add((byte) convertHexToInt( arr[i]));
		}
		//自定义消息流水号
		if(sortNum>=0xFFFF){
			sortNum=0x0000;
		}else{
			sortNum++;
		}
		byte high = (byte)((sortNum & 0xff00)>>8);
		byte low = (byte)(sortNum & 0x00ff);
		bts.add(high);
		bts.add(low);
    	//消息流水号
		byte reviceSortId_h=(byte) convertHexToInt(arr[field.WB_SORTCODE.getIndex()[0]]);
		byte reviceSortId_l=(byte) convertHexToInt(arr[field.WB_SORTCODE.getIndex()[1]]);
		 bts.add(reviceSortId_h);
		 bts.add(reviceSortId_l);
		//消息id
		 byte  msgId_h=(byte) convertHexToInt(arr[field.WB_MEG_ID.getIndex()[0]]);
		 byte  msgId_l=(byte) convertHexToInt(arr[field.WB_MEG_ID.getIndex()[1]]);
		 bts.add(msgId_h);
		 bts.add(msgId_l);
		//成功返回
		byte succ= 0x00;
		bts.add(succ);
		//验证码
		byte vCode =0x00;
		for(int i=0;i<bts.size();i++){
			if(i>0){
				vCode^= bts.get(i);
			}
			
		}
		bts.add(vCode);
		//结束
		byte end = 0x7E;
		bts.add(end);
    	byte [] result = new byte[20];
    	//复制
		for(int i=0;i<bts.size();i++){
			result[i]=bts.get(i);
		}
		return result;
    }
	private static long convertHexToInt(String hex) {
		return Long.parseLong(hex, 16);
	}

	/**
	 * 将byte转为16进制 @param bytes @return
	 */
	public static String byteToHex(byte b) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		temp = Integer.toHexString(b & 0xFF).toUpperCase();
		if (temp.length() == 1) {
			stringBuffer.append("0");
		}
		stringBuffer.append(temp);
		return stringBuffer.toString();
	}

	public static void main(String args[]) {
		String data = "7E 02 00 00 5C 08 57 03 58 00 03 00 00 00 00 00 00 80 0C 00 01 01 57 C1 E6 06 CA A5 F0 00 00 00 00 00 00 19 09 21 16 59 15 01 04 00 00 00 0E 30 01 10 FE 35 E6 02 00 01 27 07 00 20 57 36 36 38 47 42 2D 56 31 2E 30 32 44 3B 4D 30 3A 31 3B 4F 4E 3A 32 36 2C 34 31 38 33 36 3B 30 20 00 0A 89 86 06 19 11 00 12 66 00 FF ED 7E";
		data = replaceData(data);
		String[] split = data.split(" ");
		String t="";
		for(String s:split){
			t+="(byte)0X"+s+",";
		}
		System.out.println(t);;
		String result = getHexMsgToString(data);
		System.out.println(checkData(data));
		System.out.println(result);
	}
}
