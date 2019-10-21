package com.along.gps.dao;

import com.alibaba.fastjson.JSONObject;
import com.along.gps.GpsApplication;
import com.along.gps.entity.OutboundRoadlog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@SpringBootTest(classes =  GpsApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GpsDaoTest {
	@Resource
	private GpsDao gpsDao;

	@Test
	public void main() {
		gpsDao.getEquipByTaskId(548);
	}



	@Cacheable(value = "1")
	public  void read(){
		BufferedInputStream fis =null;
		BufferedReader reader = null;

		try {
			File file = new File("D:\\qianduan\\4.txt");
			fis = new BufferedInputStream(new FileInputStream(file));
			reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),100*1024*1024);// 用5M的缓冲读取文本文件

			String line = "";
			int i=0;
			long startTime = System.currentTimeMillis();
			List<OutboundRoadlog>data=new ArrayList<>();
			while((line = reader.readLine()) != null ){
				//TODO: write your business
				//	JSONObject.toJSONString(line);
				//line=line.replace("data","{\"data\"");

				JSONObject jsonObject = JSONObject.parseObject(line);

				String str=JSONObject.parseObject(jsonObject.get("data") + "").get("outboundRoadlog")+"";
				OutboundRoadlog outboundRoadlog=JSONObject.parseObject(str,OutboundRoadlog.class);
				System.out.println(outboundRoadlog);
				data.add(outboundRoadlog);
				if (data.size()==1000) {
					gpsDao.saveGpsData(data);
					data=new ArrayList<>();
				}
				i++;
				//System.out.println(i++);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("读取"+i+"条数据，运行时间:" + (endTime - startTime) + "ms");
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
}