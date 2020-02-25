package com.reminder.request.model;

import org.springframework.web.multipart.MultipartFile;

public class AssetRequest {

	private int assetId;
	private int groupId;
	private int locationId;
	private int assetTypeId;
	private int assetSubType;
	private String id;
	private String assetDescription;
/*	private String ccList;
	private String toList;*/
	private String additionalCcList;
	private String remarks;
	private int moduleTypeId;
	private ReminderRequest reminder;
	private MultipartFile[] file;
 
	
	

	public MultipartFile[] getFile() {
		return file;
	}
	public void setFile(MultipartFile[] file) {
		this.file = file;
	}
/*	public String getToList() {
		return toList;
	}
	public void setToList(String toList) {
		this.toList = toList;
	}*/
	public String getAdditionalCcList() {
		return additionalCcList;
	}
	public void setAdditionalCcList(String additionalCcList) {
		this.additionalCcList = additionalCcList;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public int getAssetSubType() {
		return assetSubType;
	}
	public void setAssetSubType(int assetSubType) {
		this.assetSubType = assetSubType;
	}
	
	public int getAssetId() {
		return assetId;
	}
	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssetDescription() {
		return assetDescription;
	}
	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
/*	public String getCcList() {
		return ccList;
	}
	public void setCcList(String ccList) {
		this.ccList = ccList;
	}*/
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getModuleTypeId() {
		return moduleTypeId;
	}
	public void setModuleTypeId(int moduleTypeId) {
		this.moduleTypeId = moduleTypeId;
	}
	
	public ReminderRequest getReminder() {
		return reminder;
	}
	public void setReminder(ReminderRequest reminder) {
		this.reminder = reminder;
	}
 
	

	

}
