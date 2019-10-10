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
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.along.gps.util.SaveData.ClearGpsData;


/**
 * @Auther: why
 * @Data:2019/6/10
 * @Deacription: 打war包WebConfig 中ServerEndpointExporter得注释掉
 */
@ServerEndpoint(value ="/WebSocket/{taskId}" ,encoders = {ServerEncoder.class})
@Controller
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
    private Session session;
    private String taskId;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);


    @OnOpen
    public void onOpen(Session session, @PathParam("taskId") String taskId) throws Exception {
        System.out.println("88888888888888:"+taskId);
        this.session = session;
        this.taskId=taskId;
        if(this.session.isOpen()) {
            SystemUtil.sessionmap.put(taskId, this.session);
        }
        usermap.put(this.session.getId(),taskId);
        webSocketSet.add(this);
       // sendMessage(this.taskId);
       // ClearGpsData();
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        usermap.remove(this.session.getId());
        SystemUtil.sessionmap.clear();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
       // sendMessage();
        sendMessage(this.taskId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(("连接出错"));
    }

    //群发
    public static void sendMessage2(GpsDescData gdd)  {
       Session session= SystemUtil.sessionmap.get(gdd.getOutboundRoadlog().getTaskId()+"");
        if(session!=null && session.isOpen()){
            try {
                Map<String,GpsDescData> data=new HashMap<>();
                data.put("data",gdd);
                System.out.println(JSON.toJSONString(data));
                synchronized(session){
                    session.getBasicRemote().sendObject(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    }
    //群发
    public static void sendMessage()  {
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
    }
    public void sendMessage(String taskId)  {
        SystemUtil.sessionmap.forEach((K,V)-> System.out.println(K+"    "+V+" "+ V.isOpen()));
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
