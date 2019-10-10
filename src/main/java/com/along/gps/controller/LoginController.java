package com.along.gps.controller;
/**
 * @Description:
 * @Author: why
 * @Date: 2018-11-29 19:41
 */


import com.along.gps.service.GpsService;
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

    /**
     * 判断Session是否过期 过期则返回登录页
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("login");
        System.out.println(carService.getPrisoner("009"));
        return  modelAndView;

    }
    @RequestMapping(value = "login")
    public String login2(HttpServletRequest request) {

        return  "login";

    }


}
