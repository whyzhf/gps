package com.along.gps.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class HistData {
	private String equipCard;
	private List<JSONObject> data;

	public HistData(String equipCard) {
		this.equipCard = equipCard;
	}

	public HistData(String equipCard, List<JSONObject> data) {
		this.equipCard = equipCard;
		this.data = data;
	}

	@Override
	public String toString() {
		return "HistData{" +
				"equipCard='" + equipCard + '\'' +
				", data=" + data +
				'}';
	}

	public String getEquipCard() {
		return equipCard;
	}

	public void setEquipCard(String equipCard) {
		this.equipCard = equipCard;
	}

	public List<JSONObject> getData() {
		return data;
	}

	public void setData(List<JSONObject> data) {
		this.data = data;
	}
}
