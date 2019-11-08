package com.along.gps.entity;

import com.along.gps.util.Gps.ColorUtil;

import javax.sound.midi.SoundbankResource;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.along.gps.util.DataUtil.dateToStr;
import static com.along.gps.util.DataUtil.strToSqlDate;

/**
 * 实时GPS数据入库存储使用
 */
public class NgpsData {
	private Integer taskId;
	private String police;//干警信息
	private String prisoner;//犯人信息
	private String equip;//设备电话号
	private String equipCard;//设备编号
	private String stauts;//设备状态
	private String errorStatus="无";//设备异常状态
	private BigDecimal longitude;//经度值
	private BigDecimal latitude;//纬度值
	private String lot;//0东经，1西经，
	private String lat;//0：北纬；1：南纬
	private int speed;//速度
	private int direction;//方向
	private String color;
	private java.sql.Timestamp uptime;

	@Override
	public String toString() {
		return "NgpsData{" +
				"taskId=" + taskId +
				", police='" + police + '\'' +
				", prisoner='" + prisoner + '\'' +
				", equip='" + equip + '\'' +
				", equipCard='" + equipCard + '\'' +
				", stauts='" + stauts + '\'' +
				", errorStatus='" + errorStatus + '\'' +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", lot='" + lot + '\'' +
				", lat='" + lat + '\'' +
				", speed=" + speed +
				", direction=" + direction +
				", color='" + color + '\'' +
				", uptime=" + uptime +
				'}';
	}

	public String getColor() {
		return color;
	}

	public void setColor(String equipCard) {
		int i =1;
		if(equipCard.length()>2) {
			i = Integer.parseInt(equipCard.substring(equipCard.length() - 1,equipCard.length())) % 21;
		}else{
			i=Integer.parseInt(equipCard)%21;
		}

		this.color = ColorUtil.COLORMAP.get(i);
	}

	public Integer getTaskId() {
		return taskId;
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

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getUptime() {
		return  uptime==null?null:dateToStr(uptime,"yyyy-MM-dd HH:mm:ss");
	}

	public void setUptime(String startTime) {

		this.uptime =  startTime==null?null:strToSqlDate(startTime,"yyyy-MM-dd HH:mm:ss");
	}

}
