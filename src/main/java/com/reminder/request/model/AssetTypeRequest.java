package com.reminder.request.model;

public class AssetTypeRequest {

	private String assetType;
	 private int parentAssetTypeId;
	 private int firstReminderDay;
	 private int secondReminderDay;
	 private int thirdReminderDay;
	 private Boolean active;
	 
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public int getParentAssetTypeId() {
		return parentAssetTypeId;
	}
	public void setParentAssetTypeId(int parentAssetTypeId) {
		this.parentAssetTypeId = parentAssetTypeId;
	}
	public int getFirstReminderDay() {
		return firstReminderDay;
	}
	public void setFirstReminderDay(int firstReminderDay) {
		this.firstReminderDay = firstReminderDay;
	}
	public int getSecondReminderDay() {
		return secondReminderDay;
	}
	public void setSecondReminderDay(int secondReminderDay) {
		this.secondReminderDay = secondReminderDay;
	}
	public int getThirdReminderDay() {
		return thirdReminderDay;
	}
	public void setThirdReminderDay(int thirdReminderDay) {
		this.thirdReminderDay = thirdReminderDay;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
