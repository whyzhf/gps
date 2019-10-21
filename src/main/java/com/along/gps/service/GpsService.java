package com.along.gps.service;

import com.along.gps.config.CacheExpire;
import com.along.gps.entity.OutboundRoadlog;
import com.along.gps.entity.TaskEquip;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface GpsService {

	boolean saveGpsData( List<OutboundRoadlog> list);

	@Cacheable(value = "getPrisoner",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	String getPrisoner( String id);

	List<TaskEquip>  getEquipByTaskId(Integer taskId);

	String getPolice(String id);
	Integer getTaskByEquipId(String card);
	Integer getEquipId( String id);

	@Cacheable(value = "getfile",unless="#result == null")
	@CacheExpire(expire = 60*5)
	List<String>  getfile();
}
