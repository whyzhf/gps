package com.along.gps.entity;


import java.math.BigDecimal;

import static com.along.gps.util.DataUtil.dateToStr;
import static com.along.gps.util.DataUtil.strToSqlDate;

public class OutboundRoadlog {

  //private int id;
  //private int routeId;//路线号
  private Integer equipmentId;//设备号
  private String equipmentCardId;//设备编号
//  private int type;//0：路线，1：围栏，2：实时
  private BigDecimal longitude;//经度值
  private BigDecimal latitude;//纬度值
  private String lot;//0东经，1西经，
  private String lat;//0：北纬；1：南纬
  private int speed;//速度
  private int direction;//方向
 // private int form;//0：线型，1：圆形，2:多边形，3矩形
  private java.sql.Timestamp uptime;
  private Integer taskId;

  @Override
  public String toString() {
    return "OutboundRoadlog{" +
            "equipmentId=" + equipmentId +
            ", equipmentCardId='" + equipmentCardId + '\'' +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", lot='" + lot + '\'' +
            ", lat='" + lat + '\'' +
            ", speed=" + speed +
            ", direction=" + direction +
            ", uptime=" + uptime +
            ", taskId=" + taskId +
            '}';
  }

  public String getEquipmentCardId() {
    return equipmentCardId;
  }

  public void setEquipmentCardId(String equipmentCardId) {
    this.equipmentCardId = equipmentCardId;
  }

  public Integer getTaskId() {
    return taskId;
  }

  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }



  public Integer getEquipmentId() {
    return equipmentId;
  }

  public void setEquipmentId(Integer equipmentId) {
    this.equipmentId = equipmentId;
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
