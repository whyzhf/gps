package com.along.gps.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.along.gps.util.DataUtil.dateToStr;
import static com.along.gps.util.DataUtil.strToSqlDate;

/**
 * 实时GPS数据webscoket使用
 */
public class WSgpsData {
	private Integer taskId;
	private String police;//干警信息
	private String prisoner;//犯人信息
	private String equip;//设备号
	private String equipCard;//设备编号
	private String stauts;//设备状态
	private String errorStatus;//设备异常状态
	private BigDecimal longitude;//经度值
	private BigDecimal latitude;//纬度值
	private String uptime;

	public WSgpsData() {

	}

	public WSgpsData(NgpsData ngpsData) {
		this.taskId = ngpsData.getTaskId();
		this.police =ngpsData.getPolice();
		this.prisoner=ngpsData.getPrisoner();
		this.equip = ngpsData.getEquip();
		this.equipCard = ngpsData.getEquipCard();
		this.stauts = ngpsData.getStauts();
		this.errorStatus = ngpsData.getErrorStatus();
		this.longitude = ngpsData.getLongitude();
		this.latitude = ngpsData.getLatitude();
		this.uptime =ngpsData.getUptime();
	}

	public String getPolice() {
		return police;
	}

	public void setPolice(String police) {
		this.police = police;
	}

	public String getPrisoner() {
		return prisoner;
	}

	public void setPrisoner(String prisoner) {
		this.prisoner = prisoner;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getEquip() {
		return equip;
	}

	public void setEquip(String equip) {
		this.equip = equip;
	}

	public String getEquipCard() {
		return equipCard;
	}

	public void setEquipCard(String equipCard) {
		this.equipCard = equipCard;
	}

	public String getStauts() {
		return stauts;
	}

	public void setStauts(String stauts) {
		this.stauts = stauts;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
}
