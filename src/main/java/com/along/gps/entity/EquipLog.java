package com.along.gps.entity;

public class EquipLog {
	private int id;//id
	private int logId;//日志id
	private int equipId;//记录的遥控器或者手环ID
	private int equipId02;//查询设备ID
	private int userID;//查询人ID
	private String name;//查询人
	private int prisonerId;//犯人id
	private String prisonerName;//犯人姓名
	private String prisonerCard;//犯人编号
	private String datetime;//时间
	private String desc;//命令描述
	private String order;//命令

	private EquipLog(Builder builder) {
		setId(builder.id);
		setLogId(builder.logId);
		setEquipId(builder.equipId);
		setEquipId02(builder.equipId02);
		setUserID(builder.userID);
		setName(builder.name);
		setPrisonerId(builder.prisonerId);
		setPrisonerName(builder.prisonerName);
		setPrisonerCard(builder.prisonerCard);
		setDatetime(builder.datetime);
		setDesc(builder.desc);
		setOrder(builder.order);
	}

	@Override
	public String toString() {
		return "EquipLog{" +
				"id=" + id +
				", logId=" + logId +
				", equipId=" + equipId +
				", equipId02=" + equipId02 +
				", userID=" + userID +
				", name='" + name + '\'' +
				", prisonerId=" + prisonerId +
				", prisonerName='" + prisonerName + '\'' +
				", prisonerCard='" + prisonerCard + '\'' +
				", datetime='" + datetime + '\'' +
				", desc='" + desc + '\'' +
				", order='" + order + '\'' +
				'}';
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public int getEquipId02() {
		return equipId02;
	}

	public void setEquipId02(int equipId02) {
		this.equipId02 = equipId02;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrisonerId() {
		return prisonerId;
	}

	public void setPrisonerId(int prisonerId) {
		this.prisonerId = prisonerId;
	}

	public String getPrisonerName() {
		return prisonerName;
	}

	public void setPrisonerName(String prisonerName) {
		this.prisonerName = prisonerName;
	}

	public String getPrisonerCard() {
		return prisonerCard;
	}

	public void setPrisonerCard(String prisonerCard) {
		this.prisonerCard = prisonerCard;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public static final class Builder {
		private int id;
		private int logId;
		private int equipId;
		private int equipId02;
		private int userID;
		private String name;
		private int prisonerId;
		private String prisonerName;
		private String prisonerCard;
		private String datetime;
		private String desc;
		private String order;

		public Builder() {
		}

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder logId(int logId) {
			this.logId = logId;
			return this;
		}

		public Builder equipId(int equipId) {
			this.equipId = equipId;
			return this;
		}

		public Builder equipId02(int equipId02) {
			this.equipId02 = equipId02;
			return this;
		}

		public Builder userID(int userID) {
			this.userID = userID;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder prisonerId(int prisonerId) {
			this.prisonerId = prisonerId;
			return this;
		}

		public Builder prisonerName(String prisonerName) {
			this.prisonerName = prisonerName;
			return this;
		}

		public Builder prisonerCard(String prisonerCard) {
			this.prisonerCard = prisonerCard;
			return this;
		}

		public Builder datetime(String datetime) {
			this.datetime = datetime;
			return this;
		}

		public Builder desc(String desc) {
			this.desc = desc;
			return this;
		}

		public Builder order(String order) {
			this.order = order;
			return this;
		}

		public EquipLog build() {
			return new EquipLog(this);
		}
	}
}
