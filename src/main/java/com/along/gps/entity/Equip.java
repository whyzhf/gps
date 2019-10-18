package com.along.gps.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Equip {
	private String num;//设备编号
	private String card;//手机号码
	private Integer  taskId;//设备编号
	private Integer status;//是否初始化设置
	private String power="获取中";//电量状态
	private String deploy="获取中";//布防状态
	private String  onAndoff="获取中";//电击开关状态
	private String lock="获取中";//锁状态
	private String demolition="获取中";//破拆状态
	private String clog="获取中";//防塞状态
	private Calendar uptime;//更新时间 如果now > uptime.add(Calendar.MINUTE, 30)?发送更新命令
	private String errorStatus="无";//防塞状态

	@Override
	public String toString() {
		return "Equip{" +
				"num='" + num + '\'' +
				", card='" + card + '\'' +
				", taskId=" + taskId +
				", status=" + status +
				", power='" + power + '\'' +
				", deploy='" + deploy + '\'' +
				", onAndoff='" + onAndoff + '\'' +
				", lock='" + lock + '\'' +
				", demolition='" + demolition + '\'' +
				", clog='" + clog + '\'' +
				", uptime=" + uptime +
				", errorStatus='" + errorStatus + '\'' +
				'}';
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getDeploy() {
		return deploy;
	}

	public void setDeploy(String deploy) {
		this.deploy = deploy;
	}

	public String getOnAndoff() {
		return onAndoff;
	}

	public void setOnAndoff(String onAndoff) {
		this.onAndoff = onAndoff;
	}

	public Integer getUptime() {
		uptime.add(Calendar.MINUTE, 30);
		return uptime.getTime().compareTo(new Date());
	}

	public void setUptime(Calendar uptime) {

		this.uptime = uptime;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getDemolition() {
		return demolition;
	}

	public void setDemolition(String demolition) {
		this.demolition = demolition;
	}

	public String getClog() {
		return clog;
	}

	public void setClog(String clog) {
		this.clog = clog;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
}
