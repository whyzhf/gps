package com.along.gps.controller;

import com.along.gps.util.ExcelUtil;
import com.along.gps.util.SysUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "login")
public class IndexController {
	@RequestMapping(value = "")
	public ModelAndView login(ModelAndView modelAndView, HttpServletRequest request) {
		modelAndView.setViewName("login");
		return  modelAndView;

	}
	//下载模板
	@ResponseBody
	@RequestMapping(value = "downloadExcel")
	public void downloadExcel(HttpServletResponse response, HttpServletRequest request, String url) {
		ExcelUtil.downloadExcel(response ,request,url);
	}
	//上传模板
	@ResponseBody
	@RequestMapping("uploadExcel")
	public String uploadExcel(@RequestParam("file") MultipartFile file,HttpServletRequest request){
		return ExcelUtil.uploadExcel(file,request);
	}


}
