package com.along.gps.entity;

public class EquipS {
	private String prisoner;
	private String card;
	private String status;

	@Override
	public String toString() {
		return "EquipS{" +
				"prisoner='" + prisoner + '\'' +
				", card='" + card + '\'' +
				", status='" + status + '\'' +
				'}';
	}

	public EquipS() {
	}

	public EquipS(String card, String status) {
		this.card = card;
		this.status = status;
	}

	public EquipS( String card,String status, String  prisoner) {
		this.prisoner = prisoner;
		this.card = card;
		this.status = status;
	}

	public String getPrisoner() {
		return prisoner;
	}

	public void setPrisoner(String prisoner) {
		this.prisoner = prisoner;
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
