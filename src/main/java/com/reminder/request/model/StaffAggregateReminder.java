package com.reminder.request.model;

public class StaffAggregateReminder {

	private String staffName;
	private String staffCode;
	private String recordType;
	private String toList;
	private String ccList;
	private String type;
	private String userName;
	private String expiryDate;
	private String groupName;
	private int firstCount;
	private int secondCount;
	private int thirdCount;
	private int id;

	public StaffAggregateReminder(String staffName, String staffCode, String recordType, String toList, String ccList,
			String type, String userName, String expiryDate, String groupName, int firstCount, int secondCount,int thirdCount, int id) {
		super();
		this.staffName = staffName;
		this.staffCode = staffCode;
		this.recordType = recordType;
		this.toList = toList;
		this.ccList = ccList;
		this.type = type;
		this.userName = userName;
		this.expiryDate = expiryDate;
		this.groupName = groupName;
		this.firstCount = firstCount;
		this.secondCount = secondCount;
		this.thirdCount = thirdCount;
		this.id = id;
	}

	public String getToList() {
		return toList;
	}

	public void setToList(String toList) {
		this.toList = toList;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getCcList() {
		return ccList;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getFirstCount() {
		return firstCount;
	}

	public void setFirstCount(int firstCount) {
		this.firstCount = firstCount;
	}

	public int getSecondCount() {
		return secondCount;
	}

	public void setSecondCount(int secondCount) {
		this.secondCount = secondCount;
	}

	public int getThirdCount() {
		return thirdCount;
	}

	public void setThirdCount(int thirdCount) {
		this.thirdCount = thirdCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
