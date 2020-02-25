package com.reminder.request.model;

import java.util.Date;

public class StaffSearchCriteria {
	
	private String recordToMonitor;
	
	private String referenceNumber;
	
	//private String nricFin;
	
	private String staffCode;
	
	private int departmentId;
	
	private String ofoSfo;
	
	private String section;
	
	private Date expiryDateFrom;
	
	private Date expiryDateTo;
	
	private int userGroupId;
	
	private String active;
	
	private String status;
	
	private String staffName;
	
	private String localCrew;
	
	
	
	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getRecordToMonitor() {
		return recordToMonitor;
	}

	public void setRecordToMonitor(String recordToMonitor) {
		this.recordToMonitor = recordToMonitor;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/*public String getNricFin() {
		return nricFin;
	}

	public void setNricFin(String nricFin) {
		this.nricFin = nricFin;
	}*/

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getOfoSfo() {
		return ofoSfo;
	}

	public void setOfoSfo(String ofoSfo) {
		this.ofoSfo = ofoSfo;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Date getExpiryDateFrom() {
		return expiryDateFrom;
	}

	public void setExpiryDateFrom(Date expiryDateFrom) {
		this.expiryDateFrom = expiryDateFrom;
	}

	public Date getExpiryDateTo() {
		return expiryDateTo;
	}

	public void setExpiryDateTo(Date expiryDateTo) {
		this.expiryDateTo = expiryDateTo;
	}

	public int getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLocalCrew() {
		return localCrew;
	}

	public void setLocalCrew(String localCrew) {
		this.localCrew = localCrew;
	}

}
