package com.along.gps.entity;

public class TaskEquip {
	private Integer taskId;
	private String cardId;

	@Override
	public String toString() {
		return "TaskEquip{" +
				"taskId=" + taskId +
				", cardId='" + cardId + '\'' +
				'}';
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
