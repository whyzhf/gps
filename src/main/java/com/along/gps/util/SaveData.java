package com.along.gps.util;



import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.along.gps.entity.GPS;
import com.along.gps.entity.GpsDescData;
import com.along.gps.entity.OutboundRoadlog;
import com.along.gps.service.GpsService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import javax.websocket.Session;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.along.gps.util.DataUtil.StringToLong;
import static com.along.gps.util.DataUtil.getNowData;
import static com.along.gps.util.SystemUtil.gpsloglist;
import static com.along.gps.util.SystemUtil.orderloglist;

/**
 * 数据处理
 */
@Component
public class SaveData  {

//	private static GpsService gpsService=new GpsServiceImpl();
	// 注入的时候，给类的 service 注入
	/*@Autowired
	public void setChatService(GpsService gpsService) {
		SaveData.gpsService= gpsService;
	}*/

	@Autowired
	protected   GpsService gpsService;
	private static SaveData  saveData ;
	@PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
	public void init() {
		saveData = this;
		saveData.gpsService = this.gpsService;
		// 初使化时将已静态化的testService实例化
	}

	//httpData
	public static void httpData(String hexData){
		String str=WarpData( hexData);
		if (str.split(";").length>8) {
			OutboundRoadlog or = toEntity(str);
			gpsData(or);
		}
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
	private static OutboundRoadlog toEntity(String data) {
		String[] arr = data.split(";");

		OutboundRoadlog gd = new OutboundRoadlog();
		gd.setUptime(arr[9]);
		gd.setDirection(Short.parseShort(arr[8]));
		gd.setLat(arr[1]);
		gd.setLot(arr[2]);
		//坐标转换
		if (SystemUtil.FLAG==0) {
			GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(arr[4]), Double.parseDouble(arr[5]));
			gd.setLatitude(new BigDecimal(gps.getLat() + ""));
			gd.setLongitude(new BigDecimal(gps.getLon() + ""));
		}else {
			gd.setLatitude(new BigDecimal(arr[4]));
			gd.setLongitude(new BigDecimal(arr[5]));
		}
		gd.setSpeed(Short.parseShort(arr[7]));
		gd.setEquipmentId(1);//id:需要编号做匹配
		gd.setEquipmentCardId(arr[0]);
		gd.setTaskId(saveData.gpsService.getTaskByEquipId(arr[0]));//id:需要编号做匹配
		//gd.setTaskId(548);//id:需要编号做匹配
		return gd;
	}



	//缓存数据 taskid equipId entity
	private static ConcurrentHashMap <String,Map<String,List<GpsDescData>>> GPS_DATA=new ConcurrentHashMap<>();
	public static void gpsData(OutboundRoadlog or){
		or.setEquipmentId(saveData.gpsService.getEquipId(or.getEquipmentCardId()));
		GpsDescData gdd=new GpsDescData();
		gdd.setEquip(or.getEquipmentCardId());
		gdd.setPolice(saveData.gpsService.getPolice(or.getTaskId()+""));
		gdd.setPrisoner(saveData.gpsService.getPrisoner(or.getEquipmentCardId()));
		gdd.setStauts("正常");
		gdd.setType("");
		gdd.setTime(or.getUptime());
		gdd.setOutboundRoadlog(or);

		if(GPS_DATA.get(gdd.getOutboundRoadlog().getTaskId()+"")!=null){//判断该任务是否存在
			if (GPS_DATA.get(gdd.getOutboundRoadlog().getTaskId()+"").get(gdd.getOutboundRoadlog().getEquipmentCardId()+"")!=null){//判断该任务中是否存在该设备
				GPS_DATA.get(gdd.getOutboundRoadlog().getTaskId()+"").get(gdd.getOutboundRoadlog().getEquipmentCardId()+"").add(gdd);
			}else{
				List<GpsDescData> list= new ArrayList<>();
				list.add(gdd);
				GPS_DATA.get(gdd.getOutboundRoadlog().getTaskId()+"").put(gdd.getOutboundRoadlog().getEquipmentCardId()+"",list);
			}
		}else{
			Map<String,List<GpsDescData>> DATA=new HashMap<>();
			List<GpsDescData> list= new ArrayList<>();
			list.add(gdd);
			DATA.put(gdd.getOutboundRoadlog().getEquipmentCardId()+"",list);
			GPS_DATA.put(gdd.getOutboundRoadlog().getTaskId()+"",DATA);
		}

	}

	public static  List<OutboundRoadlog> list=new ArrayList<>();

	public static GpsDescData httpData2(String hexData){

		String str=WarpData( hexData);
		if (str.split(";").length>8) {
			OutboundRoadlog or = toEntity(str);
			or.setEquipmentId(saveData.gpsService.getEquipId(or.getEquipmentCardId()));
			list.add(or);
			GpsDescData gdd = new GpsDescData();
			gdd.setEquip(or.getEquipmentCardId());
			gdd.setPolice(saveData.gpsService.getPolice(or.getTaskId()+""));
			gdd.setPrisoner(saveData.gpsService.getPrisoner(or.getEquipmentCardId()));
			gdd.setStauts("");
			gdd.setType("");
			gdd.setTime(or.getUptime());
			or.setUptime(null);
			gdd.setOutboundRoadlog(or);
			gdd.setErrorStatus(str.contains("未定位")?"未定位":"无");
			return gdd;
		}else{
			return null;
		}

	}
	public static GpsDescData ErrorMsg(Integer taskId,String cardId,String status){
			GpsDescData gdd = new GpsDescData();
			gdd.setEquip(cardId);
			gdd.setEquipCard(cardId);
			gdd.setPolice(saveData.gpsService.getPolice(cardId));
			gdd.setPrisoner(saveData.gpsService.getPrisoner(cardId));
			gdd.setStauts(status);
			gdd.setType("");
			gdd.setTime(getNowData("yyyy-MM-dd HH:mm:ss"));
			OutboundRoadlog or =new OutboundRoadlog();
			or.setTaskId(taskId);
			gdd.setOutboundRoadlog(or);
			gdd.setErrorStatus(status);
			return gdd;

	}
	public static void ClearGpsData(){
		 GPS_DATA.clear();
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
	public static void read(Session session){
		BufferedInputStream fis =null;
		BufferedReader reader = null;

		try {
			File file = new File(SysUtil.WEB_DATA_LOCATION+"/549-json.txt");
			fis = new BufferedInputStream(new FileInputStream(file));
			reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件

			String line = "";
			int i=0;
			long startTime = System.currentTimeMillis();
			while((line = reader.readLine()) != null ){
				//TODO: write your business
				//	JSONObject.toJSONString(line)
			//	JSONObject jsonObject = JSONObject.parseObject(line);
				//System.out.println(JSONObject.parseObject(jsonObject.get("data") + "").get("outboundRoadlog"));
				//System.out.println(i++);
				/*Map<String, String> data = new HashMap<>();
				data.put("data", line);*/

				/*	Set<Session> sessionSet= SystemUtil.sessionmap.get("549");
					if (sessionSet!=null) {
						System.out.println("size:"+sessionSet.size());
						for (Session session : sessionSet) {
							if (session != null && session.isOpen()) {
								synchronized (session) {*/
									//session.getBasicRemote().sendObject(data);
									session.getBasicRemote().sendText("{\"data\":"+line+"}");
									//session.sendText("{\"data\":"+line+"}");
							/*	}
							}
						}
					}else{
						break;
					}*/

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			long endTime = System.currentTimeMillis();
			System.out.println("读取"+i+"条数据，运行时间:" + (endTime - startTime) + "ms");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
