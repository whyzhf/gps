package com.along.gps.entity;

public class EquipS {
	private String card;
	private String status;

	public EquipS() {
	}

	public EquipS(String card, String status) {
		this.card = card;
		this.status = status;
	}

	@Override
	public String toString() {
		return "EquipS{" +
				"card='" + card + '\'' +
				", status='" + status + '\'' +
				'}';
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
