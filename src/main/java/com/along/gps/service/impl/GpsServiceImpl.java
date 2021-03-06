package com.along.gps.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.along.gps.config.CacheExpire;
import com.along.gps.dao.GpsDao;
import com.along.gps.entity.*;
import com.along.gps.service.GpsService;
import com.along.gps.util.FileUtil;
import com.along.gps.util.Gps.HandleData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.along.gps.util.FileUtil.*;
import static com.along.gps.util.Gps.HandleData.saveDatasToLog;

@Service
public class GpsServiceImpl implements GpsService {
	@Resource
	private GpsDao gpsDao;

	@Override
	public boolean saveGpsLogData(List<NgpsData> list) {
		return gpsDao.saveGpsLogData(list);
	}

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

	@Override
	public String getEquipCard(String id) {
		return gpsDao.getEquipCard(id);
	}

	@Override
	public 	List<HistData> getfile(String taskId) {
		try {
		//	System.out.println("1111111");
			List<HistData> data = getData(taskId);
			if(null==data||data.size()<1){
				List<WSgpsData> hisBySql = gpsDao.getHisBySql(Integer.valueOf(taskId));
				if (hisBySql!=null&&hisBySql.size()>0) {
					System.out.println("11111");
					saveDatasToLog(hisBySql);
				}
			}else{
				return data;
			}
			return getData(taskId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getTaskArea(String areaId) {
		return gpsDao.getTaskArea(areaId);
	}

	@Override
	public String getTaskName(String id) {
		return gpsDao.getTaskName(id);
	}

	@Override
	public int addNumb(String numb, String card) {
		return gpsDao.addNumb(numb,card);
	}

	@Override
	public int deleteGpslog() {
		return gpsDao.deleteGpslog();
	}


}
