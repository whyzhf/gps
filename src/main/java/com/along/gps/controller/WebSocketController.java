package com.along.gps.controller;
import com.alibaba.fastjson.JSON;

import com.along.gps.entity.GpsDescData;
import com.along.gps.service.GpsService;
import com.along.gps.util.SaveData;
import com.along.gps.util.ServerEncoder;
import com.along.gps.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


import static com.along.gps.util.SaveData.read;


/**
 * @Auther: why
 * @Data:2019/6/10
 * @Deacription: 打war包WebConfig 中ServerEndpointExporter得注释掉
 */
@ServerEndpoint(value ="/WebSocket/{taskId}" ,encoders = {ServerEncoder.class})
@Controller
@Scope("prototype")
public class WebSocketController   {
    // 解决WebSocket中Service层不能注入的问题
    @Resource
    private GpsService carService;
   /* private static GpsService gpsService;
    // 注入的时候，给类的 service 注入
    @Autowired
    public void setChatService(GpsService gpsService) {
        WebSocketController.gpsService= gpsService;
    }
*/
    //private static ApplicationContext applicationContext;
    // 用来记录当前连接数的变量
    private static volatile int onlineCount = 0;
    //记录请求用户角色
    private Map<String, String> usermap = new ConcurrentHashMap<>();
    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketController> webSocketSet = new CopyOnWriteArraySet<WebSocketController>();
    //记录请求用户角色
    private Map<String, Session> sessionmap = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过它来与客户端进行数据收发
    private static Session session;
    private static String taskId;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);
    @OnOpen
    public void onOpen(Session session, @PathParam("taskId") String taskId) throws Exception {
        System.out.println("open:"+session.getId());
        this.session = session;
        this.taskId=taskId;
        if(this.session.isOpen() &&  SystemUtil.sessionmap.get(taskId)!=null) {
            SystemUtil.sessionmap.get(taskId).add(this.session);
         //   SystemUtil.sessionmap.put(taskId, this.session);
        }else{
            Set<Session> set=new HashSet<>();
            set.add(this.session);
            SystemUtil.sessionmap.put(taskId, set);
        }
        usermap.put(this.session.getId(),taskId);
        webSocketSet.add(this);
       // sendMessage(this.taskId);
       // ClearGpsData();
        if(taskId.equals("549")) {
            sendMessageDemo(session);
        }
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        usermap.remove(this.session.getId());
        System.out.println(("websocket连接关闭")+ session.getId());
      //  SystemUtil.sessionmap.clear();
        SystemUtil.sessionmap.get( this.taskId).remove(this.session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
       // sendMessage();
       // sendMessage(this.taskId);
        //send(taskId,message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(("websocket连接出错"+ session.getId()));
        webSocketSet.remove(this);
        usermap.remove(this.session.getId());
        //  SystemUtil.sessionmap.clear();
        SystemUtil.sessionmap.get(this.taskId).remove(this.session);
        error.printStackTrace();
    }
    public static void sendMessageDemo2(String str)  {
        if (session != null && session.isOpen() && taskId.length()>10) {
            synchronized (session) {
                //session.getBasicRemote().sendObject(data);
                try {
                    session.getBasicRemote().sendText(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void sendMessageDemo(Session session)  {
                        read(session);
    }
    public static void sendMessage2(GpsDescData gdd)  {
        Set<Session> sessionSet= SystemUtil.sessionmap.get(gdd.getOutboundRoadlog().getTaskId()+"");
        if (sessionSet!=null) {
            for (Session session : sessionSet) {
                System.out.println(sessionSet.size()+"send:"+session.getId()+"##"+session.isOpen());
                if (session != null && session.isOpen() && taskId.equals(gdd.getOutboundRoadlog().getTaskId() + "")) {

                    try {
                        Map<String, GpsDescData> data = new HashMap<>();
                        data.put("data", gdd);
                        synchronized (session) {
                            session.getBasicRemote().sendObject(data);
                           // System.out.println("已发送数据" + JSON.toJSONString(data));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //群发
   /* public static void sendMessage2(GpsDescData gdd)  {
       Session session= SystemUtil.sessionmap.get(gdd.getOutboundRoadlog().getTaskId()+"");

        if(session!=null && session.isOpen()){
            try {
                Map<String,GpsDescData> data=new HashMap<>();
                data.put("data",gdd);
                synchronized(session){
                    session.getBasicRemote().sendObject(data);
                    System.out.println("已发送数据"+JSON.toJSONString(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    }*/
    //群发
  /*  public static void sendMessage()  {
        SystemUtil.sessionmap.forEach((K,V)-> {
            if(V.isOpen()){
                try {
                    System.out.println(K);
                    V.getBasicRemote().sendText(SaveData.SendGpsData(K));
                    ClearGpsData();
                 //   V.getBasicRemote().sendText("11111111111");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
/*
    public void sendMessage(String taskId)  {
     //   SystemUtil.sessionmap.forEach((K,V)-> System.out.println(K+"    "+V+" "+ V.isOpen()));
        try {
            if(null== this.session||!this.session.isOpen()){
                this.session=  SystemUtil.sessionmap.get(taskId);
                this.taskId=taskId;
            }
            if (this.session.isOpen()){
                String str = SaveData.SendGpsData(taskId);
                // List转json
                 this.session.getBasicRemote().sendText(str);
               // this.session.getBasicRemote().sendText(taskId);
           }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketController.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketController.onlineCount--;
    }

    /*public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketController.applicationContext = applicationContext;
    }*/

}
