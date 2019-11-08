package com.along.gps.service;

import com.alibaba.fastjson.JSONObject;
import com.along.gps.config.CacheExpire;
import com.along.gps.entity.HistData;
import com.along.gps.entity.NgpsData;
import com.along.gps.entity.OutboundRoadlog;
import com.along.gps.entity.TaskEquip;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface GpsService {

	boolean saveGpsData( List<OutboundRoadlog> list);

	boolean saveGpsLogData(List<NgpsData> list);

	@Cacheable(value = "getPrisoner",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	String getPrisoner( String id);

	List<TaskEquip>  getEquipByTaskId(Integer taskId);

	String getPolice(String id);
	Integer getTaskByEquipId(String card);
	Integer getEquipId( String id);

	String getEquipCard( String id);

/*	@Cacheable(value = "getfile",key="#p0",unless="#result == null",condition = "#result != null && #result.size()<100000")
	@CacheExpire(expire = 60*5)*/
	List<HistData> getfile(String taskId);

	List<Integer> getTaskArea(String areaId);

	int addNumb(String numb, String card);
}
