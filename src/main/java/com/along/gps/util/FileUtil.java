package com.along.gps.util;

import com.alibaba.fastjson.JSON;
import com.along.gps.config.CacheExpire;
import com.along.gps.entity.GpsDescData;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.along.gps.util.MyThread.asyncGet;
import static com.along.gps.util.MyThread.getAllFileName;
/**
 * 读取文件数据
 * */
public class FileUtil {
	public static void main(String[] args) throws Exception {
		System.out.println(getDataList("548").size());
	}

	//获取单个文件里的值
	public static List<String> getData(String taskId){
		String path =SysUtil.LOCAL_DATA_LOCATION;
		//String path =SysUtil.WEB_DATA_LOCATION;
		String fileName=FindFile("548");
		String url=path+"/"+fileName;
		try {
			return readFile(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取多个文件里的值
	public static List<String> getDataList(String taskId) throws Exception {
		String path =SysUtil.LOCAL_DATA_LOCATION;
		//String path =SysUtil.WEB_DATA_LOCATION;
		List<String> fileNames=FindFiles("548",path);
		return asyncGet(fileNames);
	}

	//读取文件
	public static List<String> readFile(String path) throws IOException {
		List<String> list=new ArrayList<>();
		int i=0;
		long start = System.currentTimeMillis();
		File file = new File(path );
		Reader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);
		System.out.println("start.....");
		while(br.ready()) {
			//System.out.println(br.readLine());
			//br.readLine();
			//JSONObject jsonObject = JSONObject.parseObject(br.readLine());
			list.add(br.readLine());
			i++;
		}

		in.close();
		br.close();
		long end = System.currentTimeMillis();
		//System.out.println(i/10000.0+" w条数据   readTxt1方法，使用内存="+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/ 1024 / 1024 + "M"+",使用时间毫秒="+(end-start));

		return list;
	}




	//获取文件名
	public static List<String> FindFiles(String taskId,String path){
		List<String> pathList=new ArrayList<>();
		// 设置日期转换格式
		SimpleDateFormat smp = new SimpleDateFormat("yyMMddHHmmss");
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(files[i].getName().startsWith(taskId+"-") && !files[i].isDirectory()) {
					pathList.add(path+"/"+files[i].getName());
				}
			}
		}
		// 打印符合要求的文件名
		return pathList;
	}


	//获取最新文件名
	public static String FindFile(String taskId){
		// 设置日期转换格式
		SimpleDateFormat smp = new SimpleDateFormat("yyMMddHHmmss");
		//String path =SysUtil.WEB_DATA_LOCATION;
		String path =SysUtil.LOCAL_DATA_LOCATION;
		File file = new File(path);
		// 定义文件修改时间
		long modify_time = 0;
		long tmp = 0;
		String fileName = "";
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(files[i].getName().startsWith(taskId+"-") && !files[i].isDirectory()) {
					//获取文件修改日期
					modify_time = Long.parseLong(smp.format(new Date(files[i].lastModified())));
					//对比获得最新修改的文件
					if (modify_time > tmp) {
						fileName = files[i].getName();
						tmp = modify_time;
					}
				}
			}
		}
		// 打印符合要求的文件名
		return fileName;
	}


}
