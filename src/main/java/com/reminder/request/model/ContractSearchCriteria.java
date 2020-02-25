package com.reminder.request.model;

import java.util.Date;

public class ContractSearchCriteria {
	
	private String title;
	
	private String referenceNumber;
	
	private Date startDateFrom;
	
	private Date startDateTo;
	
	private Date expiryDateFrom;
	
	private Date expiryDateTo;
	
	private String supplier;
	
	private String officerInCharge;
	
	private int userGroupId;
	
	private String active;
	
	private String status;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
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

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getOfficerInCharge() {
		return officerInCharge;
	}

	public void setOfficerInCharge(String officerInCharge) {
		this.officerInCharge = officerInCharge;
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
	
}
