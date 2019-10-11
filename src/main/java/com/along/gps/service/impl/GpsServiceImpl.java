package com.along.gps.service.impl;

import com.along.gps.dao.GpsDao;
import com.along.gps.entity.OutboundRoadlog;
import com.along.gps.entity.TaskEquip;
import com.along.gps.service.GpsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class GpsServiceImpl implements GpsService {
	@Resource
	private GpsDao gpsDao;

	@Override
	public boolean saveGpsData(List<OutboundRoadlog> list) {
		return gpsDao.saveGpsData(list);
	}

	@Override
	public String getPrisoner(String id) {
		return gpsDao.getPrisoner(id);
	}

	@Override
	public List<TaskEquip>  getEquipByTaskId(Integer taskId) {
		return gpsDao.getEquipByTaskId(taskId);
	}

	@Override
	public String getPolice(String id) {
		return gpsDao.getPolice(id);
	}

	@Override
	public Integer getTaskByEquipId(String card) {
		return gpsDao.getTaskByEquipId(card);
	}

	@Override
	public Integer getEquipId(String id) {

		return gpsDao.getEquipId(id);
	}
}
