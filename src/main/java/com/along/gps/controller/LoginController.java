package com.along.gps.controller;
/**
 * @Description:
 * @Author: why
 * @Date: 2018-11-29 19:41
 */


import com.along.gps.entity.EquipS;
import com.along.gps.entity.GpsDescData;
import com.along.gps.entity.GpsStatusData;
import com.along.gps.entity.WSgpsData;
import com.along.gps.service.GpsService;
import com.along.gps.util.FileUtil;

import com.along.gps.util.Gps.GpsHandleServer;
import com.along.gps.util.JedisUtil;
import com.along.gps.util.SystemUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

import static com.along.gps.controller.NettyWebSocketController.sendMessage2;
import static com.along.gps.controller.NettyWebSocketController.sendMessageByApp;
import static com.along.gps.util.Gps.ClientTest.initServer;
import static com.along.gps.util.Gps.GpsHandleServer.*;
import static com.along.gps.util.Order.EquipOrder.sendOrder;
import static com.along.gps.util.SystemUtil.ContextMap;
import static com.along.gps.util.SystemUtil.ORDERMAP;


/**
   * 功能描述 
   * @author why
   * @date 2019/4/29
   * @return
   * @description  ${description}
 */


@RestController
@RequestMapping(value = "Order")
public class LoginController {
    @Resource
    private GpsService gpsService;


    /**
     * 电击
     * @param request
     * @param pubParam
     * @return
     */
    @RequestMapping(value = "sendOrder")
    public  Map<String,Object> login(HttpServletRequest request, @RequestBody Map<String,String> pubParam) {
        Map<String,Object> resmap=new HashMap<>();
        List<EquipS> list=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        resmap.put("code","200");
        resmap.put("message","200");
        String card=pubParam.get("card");
        String[] cardArr= card.split(",");
        String userId=pubParam.get("userId");
        String flag=pubParam.get("flag");//1:定点 2：间隔 3：持续
        String duration=pubParam.get("duration");//持续时间
        String interval=pubParam.get("interval");//间隔时间
        userId = (1000000+Integer.parseInt(userId))+"";
        String dim3="";
        if ("1".equals(flag)){
            dim3="80";
        }else if("2".equals(flag)){
            dim3="40";
        }else{
            dim3="20";
        }
        //存储已连接的card
        List<String> caraList=new ArrayList<>();
       // ContextMap.forEach((K,V)-> System.out.println(K+"  "+V));
        for (int i = 0; i < cardArr.length; i++) {
            if (cardArr[i].length()>8){//统一转换成card操作
                card = getcardByNum(ContextMap, cardArr[i]);
            }else{
                card =cardArr[i];
            }
            if ("".equals(card)) {//通道没有存贮该电话号码
               // map.put(cardArr[i],"该设备未连接");
                list.add(new EquipS(cardArr[i],"该设备未连接",""));
            }else {
                caraList.add(cardArr[i]);
                sendPower(flag, card, userId, duration, interval);
            }
        }
        if (list.size()==cardArr.length){
            resmap.put("data",list);
      //      System.out.println("end......1");
            return resmap;
        }
       /*ORDERMAP.forEach((K,V)->{
            System.out.println("  "+K+"  ## " +V);
        });*/
        for (int i = 0; i <15 ; i++) {
            try {
                Iterator<String> iter = caraList.iterator();
                while (iter.hasNext()) {
                    String equipcard = iter.next();
                    if (equipcard.length() > 8) {
                        card = getcardByNum(ContextMap, equipcard);
                    } else {
                        card = equipcard;
                    }
               //     System.out.println("mingling:  "+(card + userId + "14"+dim3));
                    if(null!=ORDERMAP.get(card+userId+"14"+dim3)) {
                        if (!"0".equals(ORDERMAP.get(card + userId + "14"+dim3))) {
                            list.add(new EquipS(equipcard, ORDERMAP.get(card + userId + "14"+dim3), gpsService.getPrisoner(card)));
                            iter.remove();
                            ORDERMAP.remove(card + userId + "14"+dim3);
                        }
                    }
                }
                if (list.size() == cardArr.length) {
                    resmap.put("data", list);
             //       System.out.println("end......12");
                    return resmap;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
        for (int i = 0; i < caraList.size(); i++) {
            if (caraList.get(i).length() > 8) {
                card = getcardByNum(ContextMap, caraList.get(i));
            } else {
                card = caraList.get(i);
            }

            list.add(new EquipS(caraList.get(i), "等待超时", gpsService.getPrisoner(card)));

        }
      //  System.out.println("end......13");
      /* ORDERMAP.forEach((K,V)->{
            System.out.println("  "+K+"  ## " +V);
        });*/

        Iterator<Map.Entry<String, String>> entries = ORDERMAP.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, String> entry = entries.next();
            if (entry.getKey().endsWith("1480")||entry.getKey().endsWith("1440")
                    ||entry.getKey().endsWith("1420")||entry.getKey().endsWith("1410")){

            }else{
                ORDERMAP.remove(entry.getKey());
            }
        }
        resmap.put("data", list);
        return resmap;

    }
    /**
     * 停止电击
     * @param request
     * @param pubParam
     * @return
     */
    @RequestMapping(value = "stopPower")
    public  Map<String,Object> stopPower(HttpServletRequest request, @RequestBody Map<String,String> pubParam) {
        Map<String,Object> resmap=new HashMap<>();
        List<EquipS> list=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        resmap.put("code","200");
        resmap.put("message","200");
        String card=pubParam.get("card");
        String[] cardArr= card.split(",");
        String userId=pubParam.get("userId");
        userId = (1000000+Integer.parseInt(userId))+"";
        //存储已连接的card
        List<String> caraList=new ArrayList<>();
        //    ContextMap.forEach((K,V)-> System.out.println(K+"  "+V));
        for (int i = 0; i < cardArr.length; i++) {
            if (cardArr[i].length()>8){//统一转换成card操作
                card = getcardByNum(ContextMap, cardArr[i]);
            }else{
                card =cardArr[i];
            }
            if ("".equals(card)) {//通道没有存贮该电话号码
                // map.put(cardArr[i],"该设备未连接");
                list.add(new EquipS(cardArr[i],"该设备未连接",""));
            }else {
                caraList.add(cardArr[i]);
                GpsHandleServer.stopPower(card,userId);
            }
        }
        if (list.size()==cardArr.length){
            resmap.put("data",list);
	    //    System.out.println("end......1");
            return resmap;
        }
        for (int i = 0; i <15 ; i++) {
            try {
                Iterator<String> iter = caraList.iterator();
                while (iter.hasNext()) {
                    String equipcard = iter.next();
                    if (equipcard.length() > 8) {
                        card = getcardByNum(ContextMap, equipcard);
                    } else {
                        card = equipcard;
                    }
                    if(null!=ORDERMAP.get(card+userId+"1410")) {
                        if (!"0".equals(ORDERMAP.get(card + userId + "1410"))) {
                            list.add(new EquipS(equipcard, ORDERMAP.get(card + userId + "1410"), gpsService.getPrisoner(card)));
                            iter.remove();
                            ORDERMAP.remove(card + userId + "1410");
                        }
                    }
                }
                if (list.size() == cardArr.length) {
                    resmap.put("data", list);
	             //   System.out.println("end......2");
                    return resmap;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < caraList.size(); i++) {
            if (caraList.get(i).length() > 8) {
                card = getcardByNum(ContextMap, caraList.get(i));
            } else {
                card = caraList.get(i);
            }

            list.add(new EquipS(caraList.get(i), "等待超时", gpsService.getPrisoner(card)));



        }
	 //   System.out.println("end......3");
        Iterator<Map.Entry<String, String>> entries = ORDERMAP.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, String> entry = entries.next();
            if (entry.getKey().endsWith("1480")||entry.getKey().endsWith("1440")
                    ||entry.getKey().endsWith("1420")||entry.getKey().endsWith("1410")){

            }else{
                ORDERMAP.remove(entry.getKey());
            }
        }
        resmap.put("data", list);
        return resmap;

    }

   // private static   Map<String,Object> resmap=new HashMap<>();

    /**
     * 历史数据
     * @param request
     * @param pubParam
     * @return
     */
    @RequestMapping(value = "gethisData")
    public Map<String,Object> demo(HttpServletRequest request,@RequestBody Map<String,String> pubParam) {
      //  System.out.println("weeee");
        String taskId=pubParam.get("taskId");
        Map<String,Object> map=new HashMap<>();
        try {
           /* if (resmap.get(taskId)!=null){
                return resmap;
            }*/

            map.put("data",gpsService.getfile(taskId));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }

    }

    /**
     * 模拟gps数据发送（测试用）
     * @param request
     * @param gdd
     * @return
     */
    @RequestMapping(value = "sendgps")
    public String sendgps(HttpServletRequest request,@RequestBody WSgpsData gdd) {
        sendMessage2( gdd);
        return "ok";
    }


    /**
     *是否开启坐标转换（测试用）
     * @param request
     * @return
     */
    @RequestMapping(value = "changeFalg")
    public String changeFalg(HttpServletRequest request,int flag) {
        SystemUtil.FLAG=flag;
        return SystemUtil.FLAG==0?"已开启坐标转换":"已关闭坐标转换";
    }

   /* @RequestMapping(value = "initgpsServer")
    public String initgpsServer(HttpServletRequest request) {
        JedisUtil jedisUtil = JedisUtil.getInstance();
        Jedis jedis = jedisUtil.getJedis();
        Set<String> set = jedis.keys("548*");
        for (String s : set) {
            jedis.zremrangeByRank(s,0,-1);
        }
        gpsService.deleteGpslog();
        initServer(8899);
        System.out.println("已开启gps发送");
        return "已开启gps发送";
    }*/


    /**
     * 模拟gps数据发送（测试用）
     * @param request
     * @return
     */
    @RequestMapping(value = "sendStri")
    public String sendStri2(HttpServletRequest request,String Order,String card) {
        sendStri(Order,card);
        return "ok";
    }
}
