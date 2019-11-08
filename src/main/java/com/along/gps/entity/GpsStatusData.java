package com.along.gps.entity;

import java.util.Calendar;
import java.util.Date;

public class GpsStatusData {
	private String num;//设备编号
	private String card;//手机号码
	private Integer  taskId;//设备编号
	private Integer type;//是否初始化设置
	private String errorStatus="无";//异常状态
	private String status="获取中";//状态
	private Calendar uptime;//更新时间 如果now > uptime.add(Calendar.MINUTE, 30)?发送更新命令

	@Override
	public String toString() {
		return "GpsStatusData{" +
				"num='" + num + '\'' +
				", card='" + card + '\'' +
				", taskId=" + taskId +
				", type=" + type +
				", errorStatus='" + errorStatus + '\'' +
				", status='" + status + '\'' +
				", uptime=" + uptime +
				'}';
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getUptime() {
		uptime.add(Calendar.MINUTE, 30);
		return uptime.getTime().compareTo(new Date());
	}

	public void setUptime(Calendar uptime) {

		this.uptime = uptime;
	}
}
