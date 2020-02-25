package com.reminder.response.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class AssetTypeResponse implements Serializable {
	
	 private static final long serialVersionUID = -1232395859408322328L;
	 
	 private int assetTypeId;
	 private String assetType;
	 private int parentAssetTypeId;
	 private int firstReminderDay;
	 private int secondReminderDay;
	 private int thirdReminderDay;
	 private Boolean active;
	 private int assetSubTypeId;
	 private String assetSubType;
	 private int created_by;
	 private Timestamp created_at;
	 private int lastModified_by;
	 private Timestamp lastModified_at;
	 
	 
	public int getParentAssetTypeId() {
		return parentAssetTypeId;
	}
	public void setParentAssetTypeId(int parentAssetTypeId) {
		this.parentAssetTypeId = parentAssetTypeId;
	}
	public int getCreated_by() {
		return created_by;
	}
	public void setCreated_by(int created_by) {
		this.created_by = created_by;
	}
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
	public int getLastModified_by() {
		return lastModified_by;
	}
	public void setLastModified_by(int lastModified_by) {
		this.lastModified_by = lastModified_by;
	}
	public Timestamp getLastModified_at() {
		return lastModified_at;
	}
	public void setLastModified_at(Timestamp lastModified_at) {
		this.lastModified_at = lastModified_at;
	}
	public int getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public int getAssetSubTypeId() {
		return assetSubTypeId;
	}
	public void setAssetSubTypeId(int assetSubTypeId) {
		this.assetSubTypeId = assetSubTypeId;
	}
	public String getAssetSubType() {
		return assetSubType;
	}
	public void setAssetSubType(String assetSubType) {
		this.assetSubType = assetSubType;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
/*	public int getParentAssetTypeId() {
		return parentAssetTypeId;
	}
	public void setParentAssetTypeId(int parentAssetTypeId) {
		this.parentAssetTypeId = parentAssetTypeId;
	}*/
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
