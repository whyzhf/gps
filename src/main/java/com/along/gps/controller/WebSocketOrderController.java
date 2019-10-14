package com.along.gps.controller;

import com.along.gps.entity.GpsDescData;
import com.along.gps.service.GpsService;
import com.along.gps.util.ServerEncoder;
import com.along.gps.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.along.gps.controller.WebSocketController.hexStringToByteArray;
import static com.along.gps.util.GpsServer.ContextMap;
import static com.along.gps.util.GpsServer.send;
import static com.along.gps.util.SaveData.read;


/**
 * @Auther: why
 * @Data:2019/6/10
 * @Deacription: 打war包WebConfig 中ServerEndpointExporter得注释掉
 */
@ServerEndpoint(value ="/Order/{card}" ,encoders = {ServerEncoder.class})
@Controller
@Scope("prototype")
public class WebSocketOrderController {

    // 用来记录当前连接数的变量
    private static volatile int onlineCount = 0;
    //记录请求用户角色
    private Map<String, String> usermap = new ConcurrentHashMap<>();
    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketOrderController> webSocketSet = new CopyOnWriteArraySet<WebSocketOrderController>();
    //记录请求用户角色
    private Map<String, Session> sessionmap = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过它来与客户端进行数据收发
    private static Session session;
    private static String card;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketOrderController.class);


    @OnOpen
    public void onOpen(Session session, @PathParam("card") String card) throws Exception {
        this.session = session;
        this.card=card;
        if(this.session.isOpen() &&  SystemUtil.sessionmap.get(card)!=null) {
            SystemUtil.sessionmap.get(card).add(this.session);
         //   SystemUtil.sessionmap.put(taskId, this.session);
        }else{
            Set<Session> set=new HashSet<>();
            set.add(this.session);
            SystemUtil.sessionmap.put(card, set);
        }
        usermap.put(this.session.getId(),card);
        webSocketSet.add(this);

    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        usermap.remove(this.session.getId());
      //  SystemUtil.sessionmap.clear();
        SystemUtil.sessionmap.get( this.card).remove(this.session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
       // sendMessage();
       // sendMessage(this.taskId);
        send(this.card,hexStringToByteArray(message));
    }
    public static void sendMessage(String str) {
        // sendMessage();
        // sendMessage(this.taskId);
        try {
            session.getBasicRemote().sendText(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(("连接出错"));
    }
    public static void sendMessageDemo()  {

    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketOrderController.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketOrderController.onlineCount--;
    }


}
