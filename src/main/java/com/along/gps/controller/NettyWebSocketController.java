package com.along.gps.controller;

import com.alibaba.druid.sql.visitor.functions.Isnull;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.along.gps.entity.GpsDescData;
import com.along.gps.entity.WSgpsData;
import com.along.gps.service.GpsService;
import com.along.gps.util.Gps.HandleData.*;
import com.along.gps.util.SysUtil;
import com.along.gps.util.SystemUtil;
import com.along.gps.util.UnicodeReader;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import org.yeauty.pojo.ParameterMap;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


import static com.along.gps.util.Gps.ClientTest.initServer;
import static com.along.gps.util.Gps.GpsHandleServer.send;
import static com.along.gps.util.Order.GeneralUtils.getJsonStr;
import static com.along.gps.util.SystemUtil.sessionmap;

@ServerEndpoint(prefix = "netty-websocket" )
@RestController
@Scope("prototype")
public class NettyWebSocketController {
/*	@Resource
	private static GpsService gpsService;*/
	private static GpsService gpsService;
	// 注入的时候，给类的 service 注入
	@Autowired
	public void setChatService(GpsService gpsService) {
		NettyWebSocketController.gpsService= gpsService;
	}

	private  static String taskId;
	@OnOpen
	public void onOpen(Session session, HttpHeaders headers,ParameterMap parameterMap) throws IOException {

		String taskId=parameterMap.getParameter("taskId");
		String areaId=parameterMap.getParameter("areaId");
		if(!"-1".equals(areaId)) {//将区域session保存在区域任务下
			List<Integer> taskArea = gpsService.getTaskArea(areaId);
			if (null!=taskArea && !taskArea.isEmpty()){
				for (int i = 0; i < taskArea.size(); i++) {
					if(session.isOpen() &&  SystemUtil.NETTYSESSIONMAP.get(taskArea.get(i)+"")!=null) {
						SystemUtil.NETTYSESSIONMAP.get(taskArea.get(i)+"").add(session);
						//   SystemUtil.sessionmap.put(taskId, this.session);
					}else{
						Set<Session> set=new HashSet<>();
						set.add(session);
						SystemUtil.NETTYSESSIONMAP.put(taskArea.get(i)+"", set);
					}
				}

			}
		}
		if(session.isOpen() &&  SystemUtil.NETTYSESSIONMAP.get(taskId)!=null) {
			SystemUtil.NETTYSESSIONMAP.get(taskId).add(session);
			//   SystemUtil.sessionmap.put(taskId, this.session);
		}else{
			Set<Session> set=new HashSet<>();
			set.add(session);
			SystemUtil.NETTYSESSIONMAP.put(taskId, set);
		}
		//app测试专用
		if ("584".equals(taskId)||"230184".equals(areaId)) {
			sendMessageByApp();
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException {
	//	System.out.println("one connection closed");
		session.flush();
		session.close();
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
	}

	@OnMessage
	public void OnMessage(Session session, String message) {
			session.sendText("");
		send(message);
	}

	@OnBinary
	public void OnBinary(Session session, byte[] bytes) {
		for (byte b : bytes) {
			System.out.println(b);
		}
		session.sendBinary(bytes);
	}

	/**
	 * 群发
	 */
	public static synchronized void sendMessage2(WSgpsData wsData) {
		//获取session
		Set<Session> sessionSet = SystemUtil.NETTYSESSIONMAP.get(wsData.getTaskId() + "");
		if (sessionSet!=null) {
			Iterator<Session> it = sessionSet.iterator();
			while (it.hasNext()) {
				Session session = it.next();
				if (session != null && session.isOpen()) {
				//	Map<String, WSgpsData> data = new HashMap<>();
				//	data.put("data",wsData);
					session.sendText("{\"data\":"+getJsonStr(wsData)+"}");
				}else if(session != null && !session.isOpen()){
					it.remove();
				}
			}
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new Thread(()->{
				sendMessageByApp();
			}).start();

		}




	}
	/**
	 * 群发
	 */
	public static  void sendMessageByApp() {
		File file = new File("/usr/tomcat/tomcat8/gps/data/AppCs-json.txt" );
		if(file.renameTo(file)){
			//System.out.println("文件未被操作");
			String taskId="";
			//	System.out.println("start.....");
			FileInputStream fis = null;
			UnicodeReader unicodeReader =null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(file);
				unicodeReader = new UnicodeReader(fis, Charset.defaultCharset().name());
				br = new BufferedReader(unicodeReader);
				String tempString=null;
				while (( tempString = br.readLine()) != null) {
					Thread.sleep(1000);
					//System.out.println(br.readLine());
					//br.readLine();
					JSONObject jsonObject = JSONObject.parseObject(tempString);
					taskId=jsonObject.get("taskId")+"";
					Set<Session> sessionSet = SystemUtil.NETTYSESSIONMAP.get(taskId);
					if (sessionSet!=null) {
						Iterator<Session> it = sessionSet.iterator();
						while (it.hasNext()) {
							Session session = it.next();
							if (session != null && session.isOpen()) {
								session.sendText("{\"data\":"+getJsonStr(jsonObject)+"}");
							}else if(session != null && !session.isOpen()){
								it.remove();
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					unicodeReader.close();
					fis.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}else{
			//System.out.println("文件正在被操作");

		}
		//获取session
		/*Set<Session> sessionSet = SystemUtil.NETTYSESSIONMAP.get(wsData.getTaskId() + "");
		if (sessionSet!=null) {
			Iterator<Session> it = sessionSet.iterator();
			while (it.hasNext()) {
				Session session = it.next();
				if (session != null && session.isOpen()) {
					session.sendText("{\"data\":"+getJsonStr(wsData)+"}");
				}else if(session != null && !session.isOpen()){
					it.remove();
				}
			}
		}*/
	}
	public static synchronized void sendMessageDemo(String string) {
		//获取session
		Set<Session> sessionSet = SystemUtil.NETTYSESSIONMAP.get("along");
		if (sessionSet!=null) {
			Iterator<Session> it = sessionSet.iterator();
			while (it.hasNext()) {
				Session session = it.next();
				if (session != null && session.isOpen()) {
					//	Map<String, WSgpsData> data = new HashMap<>();
					//	data.put("data",wsData);
					session.sendText(string);
				}else if(session != null && !session.isOpen()){
					it.remove();
				}
			}
		}
	}
	//演示专用
	public static void sendMessageDemo(Session session)  {
		BufferedInputStream fis =null;
		BufferedReader reader = null;
		try {
			File file = new File(SysUtil.WEB_DATA_LOCATION+"/549-json.txt");
			fis = new BufferedInputStream(new FileInputStream(file));
			reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件

			String line = "";
			while((line = reader.readLine()) != null ){
				session.sendText("{\"data\":"+line+"}");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

	@OnEvent
	public void onEvent(Session session, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			switch (idleStateEvent.state()) {
				case READER_IDLE:
					System.out.println("read idle");
					break;
				case WRITER_IDLE:
					System.out.println("write idle");
					break;
				case ALL_IDLE:
					System.out.println("all idle");
					break;
				default:
					break;
			}
		}
	}
}
