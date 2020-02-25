package com.reminder.request.model;

public class StaffRequest {
	
	private int recordNumber;
	private String refrenceNumber;
	private int staffId;
	private int groupId;
	private int recordId;
	private int moduleTypeId;
	private String validPeriod;
	private ReminderRequest reminderReq;
	//private String ccList;
	//private String toList;
	private String additionalCcList;
	private String remarks;

	
	
	public int getrecordNumber() {
		return recordNumber;
	}
	public void setrecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}
	public int getModuleTypeId() {
		return moduleTypeId;
	}
	public void setModuleTypeId(int moduleTypeId) {
		this.moduleTypeId = moduleTypeId;
	}
	public String getRefrenceNumber() {
		return refrenceNumber;
	}
	public void setRefrenceNumber(String refrenceNumber) {
		this.refrenceNumber = refrenceNumber;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public String getValidPeriod() {
		return validPeriod;
	}
	public void setValidPeriod(String validPeriod) {
		this.validPeriod = validPeriod;
	}
	public ReminderRequest getReminderReq() {
		return reminderReq;
	}
	public void setReminderReq(ReminderRequest reminderReq) {
		this.reminderReq = reminderReq;
	}
/*	public String getCcList() {
		return ccList;
	}
	public void setCcList(String ccList) {
		this.ccList = ccList;
	}
	public String getToList() {
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
 
	
}
