package com.along.gps.dao;

import com.along.gps.entity.OutboundRoadlog;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GpsDao {
	@InsertProvider(type = SqlProvider.class, method = "saveGpsData")
	boolean saveGpsData(@Param("list") List<OutboundRoadlog> list);

	@Select(" SELECT  CONCAT_WS(' ',p.name,p.card)\n" +
			" FROM outboundmanage.outbound_prisoner  p \n" +
			" LEFT JOIN outboundmanage.outbound_equipment e on e.id=p.equipment_id\n" +
			" where e.card=#{id}")
	String getPrisoner(@Param("id") String id);

	@Select(" SELECT  group_concat( CONCAT_WS(': ',p.name,p.card),' ')" +
			" FROM outbound_task_police_rel  r" +
			" LEFT JOIN outbound_police p on r.police_id=p.id" +
			" where r.task_id=#{id} and p.name IS NOT NULL")
	String getPolice(@Param("id") String id);

	@Select("select e.id ,e.card from outbound_task_prisoner_rel r " +
			" LEFT JOIN outbound_prisoner p on r.prisoner_id=p.id" +
			" LEFT JOIN outbound_equipment e on p.equipment_id=e.id" +
			" where r.task_id=#{taskId} AND e.id IS not NULL")
	String getEquipByTaskId(@Param("taskId") Integer taskId);

	@Select("select r.task_id from outbound_task_prisoner_rel r"+
			" LEFT JOIN outbound_prisoner p on r.prisoner_id=p.id"+
			" LEFT JOIN outbound_equipment e on p.equipment_id=e.id"+
			" where e.card=#{card}")
	Integer getTaskByEquipId(@Param("card") String card);

	@Select(" SELECT  id FROM outbound_equipment where card=#{id}")
	Integer getEquipId(@Param("id") String id);
}
