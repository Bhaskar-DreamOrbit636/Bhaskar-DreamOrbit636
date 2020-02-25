package com.reminder.request.model;

import java.sql.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.reminder.model.FileUpload;

public class ReminderRequest {

	private int reminderId;
	private Date effectiveStartDate;
	private Date effectiveExpiryDate;
	private String remarks;
	private Date firstReminderDate;
	private Date secondReminderDate;
	private Date thirdReminderDate;
	private Boolean active;
	private int createdById;
	private DateTime createdAt;
	private int lastModifiedById;
	private DateTime lastModifiedAt;
	private Date firstReminderSentAt;
	private Date secondReminderSentAt;
	private Date thirdReminderSentAt;
	//private int statusId;
	//private ContractRequest contract;
	//private Asset asset;
	//private StaffRecord staffRecord;
	//private String ccListLastReminder;
	//private String ccListExpiryReminder;
	private String addCcListLastReminder;
	private String addCcListExpiryReminder;
	private List<FileUpload> files;
	
	
	
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	public Date getEffectiveExpiryDate() {
		return effectiveExpiryDate;
	}
	public void setEffectiveExpiryDate(Date effectiveExpiryDate) {
		this.effectiveExpiryDate = effectiveExpiryDate;
	}
	public Date getFirstReminderDate() {
		return firstReminderDate;
	}
	public void setFirstReminderDate(Date firstReminderDate) {
		this.firstReminderDate = firstReminderDate;
	}
	public Date getSecondReminderDate() {
		return secondReminderDate;
	}
	public void setSecondReminderDate(Date secondReminderDate) {
		this.secondReminderDate = secondReminderDate;
	}
	public Date getThirdReminderDate() {
		return thirdReminderDate;
	}
	public void setThirdReminderDate(Date thirdReminderDate) {
		this.thirdReminderDate = thirdReminderDate;
	}
	public Date getFirstReminderSentAt() {
		return firstReminderSentAt;
	}
	public void setFirstReminderSentAt(Date firstReminderSentAt) {
		this.firstReminderSentAt = firstReminderSentAt;
	}
	public Date getSecondReminderSentAt() {
		return secondReminderSentAt;
	}
	public void setSecondReminderSentAt(Date secondReminderSentAt) {
		this.secondReminderSentAt = secondReminderSentAt;
	}
	public Date getThirdReminderSentAt() {
		return thirdReminderSentAt;
	}
	public void setThirdReminderSentAt(Date thirdReminderSentAt) {
		this.thirdReminderSentAt = thirdReminderSentAt;
	}
	public List<FileUpload> getFiles() {
		return files;
	}
	public void setFiles(List<FileUpload> files) {
		this.files = files;
	}
	public int getReminderId() {
		return reminderId;
	}
	public void setReminderId(int reminderId) {
		this.reminderId = reminderId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public int getCreatedById() {
		return createdById;
	}
	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}
	public DateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	public int getLastModifiedById() {
		return lastModifiedById;
	}
	public void setLastModifiedById(int lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
/*	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}*/
	/*public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}*/
/*	public String getCcListLastReminder() {
		return ccListLastReminder;
	}
	public void setCcListLastReminder(String ccListLastReminder) {
		this.ccListLastReminder = ccListLastReminder;
	}
	public String getCcListExpiryReminder() {
		return ccListExpiryReminder;
	}
	public void setCcListExpiryReminder(String ccListExpiryReminder) {
		this.ccListExpiryReminder = ccListExpiryReminder;
	}*/
	public String getAddCcListLastReminder() {
		return addCcListLastReminder;
	}
	public void setAddCcListLastReminder(String addCcListLastReminder) {
		this.addCcListLastReminder = addCcListLastReminder;
	}
	public String getAddCcListExpiryReminder() {
		return addCcListExpiryReminder;
	}
	public void setAddCcListExpiryReminder(String addCcListExpiryReminder) {
		this.addCcListExpiryReminder = addCcListExpiryReminder;
	}


	
	
}
