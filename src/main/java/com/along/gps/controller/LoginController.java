package com.along.gps.controller;
/**
 * @Description:
 * @Author: why
 * @Date: 2018-11-29 19:41
 */


import com.along.gps.service.GpsService;
import com.along.gps.util.FileUtil;
import com.along.gps.util.GpsServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.along.gps.util.GpsServer.*;
import static com.along.gps.util.Order.EquipOrder.sendOrder;
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
    private GpsService carService;



    @RequestMapping(value = "sendOrder")
    public  Map<String,Object> login(HttpServletRequest request, @RequestBody Map<String,String> pubParam) {
        Map<String,Object> resmap=new HashMap<>();
        Map<String,String> map=new HashMap<>();
        resmap.put("code","200");
        resmap.put("message","200");
        String card=pubParam.get("card");
        String[] cardArr= card.split(",");
        String userId=pubParam.get("userId");
        userId = (1000000+Integer.parseInt(userId))+"";
        for (int i = 0; i < cardArr.length; i++) {
            if (cardArr[i].length()>8){
                card = getcardByNum(ContextMap, cardArr[i]);
            }else{
                card =cardArr[i];
            }
            if ("".equals(card)) {
                map.put(cardArr[i],"该设备未连接");
                resmap.put("data",map);
            }else {
                GpsServer.sendPower(card, userId);
            }
        }

        for (int j = 0; j <15 ; j++) {//15秒数据反馈
            try {
                for (int i = 0; i < cardArr.length; i++) {
                    if (cardArr[i].length()>8){
                        card = getcardByNum(ContextMap, cardArr[i]);
                    }else{
                        card =cardArr[i];
                    }
                    if(null==ORDERMAP.get(card+userId+"0120")){
                        map.put(cardArr[i],"该设备未连接");
                        resmap.put("data",map);
                        if(map.size()==cardArr.length){
                            return resmap;
                        }
                    }else if(!"0".equals(ORDERMAP.get(card+userId+"0120"))){
                        map.put(cardArr[i],ORDERMAP.get(card+userId+"0120"));
                        resmap.put("data",map);
                        ORDERMAP.remove(card+userId+"0120");
                        if(map.size()==cardArr.length){
                            return resmap;
                        }
                    }
                }
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
     /*   ORDERMAP.forEach((K,V)->{
            System.out.println("11........"+K+"   #    "+V);
        });*/
        for (int i = 0; i < cardArr.length; i++) {
            if (cardArr[i].length()>8){
                card = getcardByNum(ContextMap, cardArr[i]);
            }else{
                card =cardArr[i];
            }
            System.out.println(card + userId + "0120");
            if("0".equals(ORDERMAP.get(card+userId+"0120"))){
                map.put(cardArr[i],"等待超时");
                resmap.put("data",map);
                ORDERMAP.remove(card+userId+"0120");
                if(map.size()==cardArr.length){
                    return resmap;
                }
            }
        }
       /* ORDERMAP.forEach((K,V)->{
            System.out.println(K+"   #    "+V);
        });*/
        return  resmap;
    }
    private static   Map<String,Object> resmap=new HashMap<>();
    @RequestMapping(value = "gethisData")
    public Map<String,Object> demo(HttpServletRequest request,@RequestBody Map<String,String> pubParam) {
      //  System.out.println("weeee");
        String taskId=pubParam.get("taskId");
        Map<String,Object> map=new HashMap<>();
        if (resmap.get(taskId)!=null){
            return resmap;
        }
        try {
            resmap.put("data",carService.getfile(taskId));
            return resmap;
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }

    }
}
