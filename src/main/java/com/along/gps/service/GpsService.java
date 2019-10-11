package com.along.gps.service;

import com.along.gps.entity.OutboundRoadlog;
import com.along.gps.entity.TaskEquip;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


public interface GpsService {

	boolean saveGpsData( List<OutboundRoadlog> list);

	String getPrisoner( String id);

	List<TaskEquip>  getEquipByTaskId(Integer taskId);

	String getPolice(String id);
	Integer getTaskByEquipId(String card);
	Integer getEquipId( String id);
}
