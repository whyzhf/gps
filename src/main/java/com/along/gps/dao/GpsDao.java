package com.along.gps.dao;

import com.along.gps.config.CacheExpire;
import com.along.gps.entity.NgpsData;
import com.along.gps.entity.OutboundRoadlog;

import com.along.gps.entity.TaskArea;
import com.along.gps.entity.TaskEquip;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface GpsDao {
	@InsertProvider(type = SqlProvider.class, method = "saveGpsData")
	boolean saveGpsData(@Param("list") List<OutboundRoadlog> list);

	@InsertProvider(type = SqlProvider.class, method = "saveGpsLogData")
	boolean saveGpsLogData(@Param("list") List<NgpsData> list);

	@Select(" SELECT  CONCAT_WS(' ',p.name,p.card)\n" +
			" FROM outbound_prisoner  p \n" +
			" LEFT JOIN outbound_equipment e on e.id=p.equipment_id\n" +
			" where e.card=#{id}")
	String getPrisoner(@Param("id") String id);

	@Cacheable(value = "getPolice",key="#p0"  ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	@Select(" SELECT  group_concat( CONCAT_WS(':',p.name,p.card),' ')" +
			" FROM outbound_task_police_rel  r" +
			" LEFT JOIN outbound_police p on r.police_id=p.id" +
			" where r.task_id=#{id} and p.name IS NOT NULL  limit 5 ")
	String getPolice(@Param("id") String id);

	@Cacheable(value = "getEquipByTaskId",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	@Select("select e.id ,e.card from outbound_task_prisoner_rel r " +
			" LEFT JOIN outbound_prisoner p on r.prisoner_id=p.id" +
			" LEFT JOIN outbound_equipment e on p.equipment_id=e.id" +
			" where r.task_id=#{taskId} AND e.id IS not NULL")
	List<TaskEquip> getEquipByTaskId(@Param("taskId") Integer taskId);

	@Cacheable(value = "getTaskByEquipId",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	@Select("select r.task_id from outbound_task_prisoner_rel r"+
			" LEFT JOIN outbound_prisoner p on r.prisoner_id=p.id"+
			" LEFT JOIN outbound_equipment e on p.equipment_id=e.id"+
			" LEFT JOIN outbound_task t on r.task_id = t.id" +
			" where e.card=#{card} and t.status=3 limit 1")
	Integer getTaskByEquipId(@Param("card") String card);

	@Cacheable(value = "getEquipId",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*5)
	@Select(" SELECT  id FROM outbound_equipment where card=#{id}")
	Integer getEquipId(@Param("id") String id);

	@Cacheable(value = "getEquipCard",key="#p0" ,unless="#result == null")
	@CacheExpire(expire = 60*60)
	@Select(" SELECT card  FROM outbound_equipment where numb=#{id}")
	String getEquipCard(@Param("id") String id);

	@Select(" SELECT DISTINCT id from outbound_task where status=3 and area_id=${areaId}")
	List<Integer> getTaskArea(@Param("areaId") String areaId);

	@Update("UPDATE outbound_equipment " +
			"SET numb=#{numb} WHERE card=#{card}")
	int addNumb(@Param("numb") String numb,@Param("card") String card);

}
