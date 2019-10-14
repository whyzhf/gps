package com.along.gps.controller;
/**
 * @Description:
 * @Author: why
 * @Date: 2018-11-29 19:41
 */


import com.along.gps.service.GpsService;
import com.along.gps.util.GpsServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
   * 功能描述 
   * @author why
   * @date 2019/4/29
   * @return
   * @description  ${description}
 */


@Controller
@RequestMapping(value = "")
public class LoginController {
    @Resource
    private GpsService carService;


    @RequestMapping(value = "sendOrder")
    public String login(HttpServletRequest request,byte[] order,String card ) {
        GpsServer.send(card,order);
        return  "";

    }


}
