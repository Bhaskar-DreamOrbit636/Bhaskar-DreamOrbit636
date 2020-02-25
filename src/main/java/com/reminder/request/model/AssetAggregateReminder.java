package com.reminder.request.model;

public class AssetAggregateReminder {

	private String assetType;
	private String assetSubType;
	private String assetId;
	private String location;
	private String expiryDate;
	private String toList;
	private String ccList;
	private String type;
	private String userName;
	private String groupName;
	private int firstCount;
	private int secondCount;
	private int thirdCount;
	private int id;

	public AssetAggregateReminder(String assetType, String assetSubType, String assetId, String location,
			String expiryDate, String toList, String ccList, String type, String userName, String groupName, int firstCount, int secondCount,int thirdCount, int id ) {
		super();
		this.assetType = assetType;
		this.assetSubType = assetSubType;
		this.assetId = assetId;
		this.location = location;
		this.expiryDate = expiryDate;
		this.toList = toList;
		this.ccList = ccList;
		this.type = type;
		this.userName = userName;
		this.groupName = groupName;
		this.firstCount = firstCount;
		this.secondCount = secondCount;
		this.thirdCount = thirdCount;
		this.id = id;
	}

	public AssetAggregateReminder() {
		super();
	}

	public String getToList() {
		return toList;
	}

	public void setToList(String toList) {
		this.toList = toList;
	}

	public String getCcList() {
		return ccList;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetSubType() {
		return assetSubType;
	}

	public void setAssetSubType(String assetSubType) {
		this.assetSubType = assetSubType;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
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
