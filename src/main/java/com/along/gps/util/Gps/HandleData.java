package com.along.gps.util.Gps;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.along.gps.entity.*;
import com.along.gps.service.GpsService;
import com.along.gps.util.*;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import javax.annotation.PostConstruct;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.along.gps.util.DataUtil.StringToLong;
import static com.along.gps.util.DataUtil.getNowData;
import static com.along.gps.util.Gps.GpsHandleServer.getcardByNum;
import static com.along.gps.util.SystemUtil.*;

/**
 * 数据处理
 */
@Component
public class HandleData {


	@Autowired
	protected   GpsService gpsService;
	private static HandleData saveData ;
	@PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
	public void init() {
		saveData = this;
		saveData.gpsService = this.gpsService;
		// 初使化时将已静态化的testService实例化
	}

	//数据包装
	public static String WarpData(String hexData){
		// gps上传数据
		if (hexData.startsWith("7E 02 00")) {
			// 检查数据有效性
			if (ConvertData.checkData(hexData)) {
				hexData = ConvertData.getHexMsgToString(hexData);

			} else {
				System.out.println("数据格式校验未通过...");
			}
		} else if (hexData.startsWith("7E 07 04")) {// 批量上传或是补传

		} else {
			// 不处理
		}
		return hexData;
	}
	//gps to entity
	private static NgpsData toEntity(String data) {
		String[] arr = data.split(";");
		NgpsData nd = new NgpsData();
		nd.setUptime(arr[9]);
		nd.setDirection(Short.parseShort(arr[8]));
		nd.setLat(arr[1]);
		nd.setLot(arr[2]);
		//坐标转换
		GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(arr[4]), Double.parseDouble(arr[5]));
		nd.setLatitude(new BigDecimal(gps.getLat() + ""));
		nd.setLongitude(new BigDecimal(gps.getLon() + ""));

		nd.setSpeed(Short.parseShort(arr[7]));
		nd.setEquip(arr[0]);//电话号码
		String equipCard= saveData.gpsService.getEquipCard(arr[0]);
		if(equipCard!=null&&equipCard.isEmpty()){
		}else{
			equipCard=getcardByNum(ContextMap,arr[0]);
			if (!"".equals(equipCard)){

			}
		}
		nd.setEquipCard(equipCard);
		nd.setTaskId(saveData.gpsService.getTaskByEquipId(equipCard));
		nd.setPolice(saveData.gpsService.getPolice(nd.getTaskId()+""));
		nd.setPrisoner(saveData.gpsService.getPrisoner(equipCard));

		return nd;
	}





	public static  List<OutboundRoadlog> list=new ArrayList<>();

	public static NgpsData httpData2(String hexData){

		String str=WarpData( hexData);
		if (str.split(";").length>8) {
			NgpsData ngd = toEntity(str);
			ngd.setEquipCard(saveData.gpsService.getEquipId(ngd.getEquipCard())+"");
			ngd.setPolice(saveData.gpsService.getPolice(ngd.getTaskId()+""));
			ngd.setPrisoner(saveData.gpsService.getPrisoner(ngd.getEquipCard()));
			ngd.setStauts("");
			ngd.setUptime(null);
			ngd.setErrorStatus(str.contains("未定位")?"未定位":"无");
			return ngd;
		}else{
			return null;
		}

	}
	public static WSgpsData ErrorMsg(Integer taskId,String cardId,String status){
		WSgpsData gdd = new WSgpsData();
			gdd.setEquip(cardId);
			gdd.setEquipCard(cardId);
			gdd.setPolice(saveData.gpsService.getPolice(cardId));
			gdd.setPrisoner(saveData.gpsService.getPrisoner(cardId));
			gdd.setStauts(status);
			gdd.setUptime(getNowData("yyyy-MM-dd HH:mm:ss"));
			gdd.setTaskId(taskId);
			gdd.setErrorStatus(status);

			return gdd;

	}




	//保存到redis
	public static JedisUtil jutil = JedisUtil.getInstance();// jedis工具对象
	public static void saveRedis(){
			GpsDescData poll = SystemUtil.gpsDatalist.poll();
			//写入文件夹
			saveDataToLog(poll);
			//存入缓存
			String key=poll.getOutboundRoadlog().getTaskId()+"";
			double score=StringToLong(poll.getTime(),"yyyy-MM-dd HH:mm:ss").doubleValue();
			String jsonString = JSONObject.toJSONString(poll);
			jutil.SORTSET.zadd(key,score, jsonString);
			jutil.expire(key, 60 * 60 * 24 * 7);

	}

	//保存到数据库
	public static void saveDataBySql(){
		if (list.size()>0) {
			saveData.gpsService.saveGpsData(list);
			list=new ArrayList<>();

		}
	}


	/**
	 * 将消息写入日志文件
	 *
	 * @param msg
	 */
	public synchronized static void saveMsgToLog( String msg) {
		String[] strings = msg.split("&&");
		String hexStr = strings[0];
		String txt = strings[1];
		Writer w = null;
		BufferedWriter bw = null;
		Writer w1 = null;
		BufferedWriter bw1 = null;
		try {
			String FileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-json.txt";
			String FileName1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-hex.txt";
			File dir = new File(SysUtil.WEB_LOG_LOCATION);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写入文本
			File f = new File(dir + "/" + FileName);
			File f1 = new File(dir + "/" + FileName1);
			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			bw.write(txt + "\r\n");
			// 写入16进制
			if (!f1.exists()) {
				f1.createNewFile();
			}
			w1 = new FileWriter(f1, true);
			bw1 = new BufferedWriter(w1);
			bw1.write( hexStr + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				w.close();
				bw1.close();
				w1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	//gps命令
	public synchronized static void saveMsgToLog(ChannelHandlerContext ctx, String msg) {
		String txt = ConvertData.getHexMsgToString(msg);
		StringBuilder sb = new StringBuilder();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		String address = ctx.channel().remoteAddress()+"";
		sb.append(time + " # ");
		sb.append(address + " : [");
		sb.append(msg.replace(" ",""));
		gpsloglist.add(sb.toString()+"\r\n"+txt+"]");

	}
	//脚扣命令
	public synchronized static void saveOrderToLog(ChannelHandlerContext ctx, String msg) {
		StringBuilder sb = new StringBuilder();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		String address = ctx.channel().remoteAddress()+"";
		sb.append(time + " # ");
		sb.append(address + " # ");
		sb.append(" :"+msg) ;
		orderloglist.add(sb.toString());
	}
	/*public  static void saveOrderToLog( String msg) {
		Writer w = null;
		BufferedWriter bw = null;
		try {
			String FileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-Order.txt";
			File dir = new File(SysUtil.WEB_LOG_LOCATION);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写入文本
			File f = new File(dir + "/" + FileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			bw.write(  msg + "\r\n");
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
*/


	/**
	 * 将数据写入日志文件
	 *
	 * @param msg
	 */
	public static   void saveDataToLog( GpsDescData msg) {
		Writer w = null;
		BufferedWriter bw = null;
		try {
			String FileName = msg.getOutboundRoadlog().getTaskId()+"-"+new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date())+ "-json.txt";
			File dir = new File(SysUtil.WEB_DATA_LOCATION);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写入文本
			File f = new File(dir + "/" + FileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			bw.write(JSON.toJSONString(msg)+ "\r\n");
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

	//写入文件方法
	public  static  void pubWriterFile(String FileName,String FileUrl,ConcurrentLinkedQueue<String> text){
		Writer w = null;
		BufferedWriter bw = null;
		try {
			File dir = new File(FileUrl);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写入文本
			File f = new File(dir + "/" + FileName);

			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			while (!text.isEmpty()) {
				bw.write(text.poll() + "\r\n");
			}
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


/****************************************************************/
	public  synchronized static void saveLog( String msg) {
		Writer w = null;
		BufferedWriter bw = null;
		Writer w1 = null;
		BufferedWriter bw1 = null;
		try {
			String FileName = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date())+ "-All.txt";

			File dir = new File(SysUtil.WEB_DATA_LOCATION);
			//File dir = new File(SysUtil.LOCAL_DATA_LOCATION);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写入文本
			File f = new File(dir + "/" + FileName);

			if (!f.exists()) {
				f.createNewFile();
			}
			w = new FileWriter(f, true);
			bw = new BufferedWriter(w);
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
			bw.write(time+ " ## " +msg+ "\r\n");

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

	/***********************************************************************/

}
