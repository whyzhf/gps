package com.along.gps.entity;

public class TaskArea {
	private Integer taskId;
	private Integer areaId;

	@Override
	public String toString() {
		return "TaskArea{" +
				"taskId=" + taskId +
				", areaId=" + areaId +
				'}';
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
}
