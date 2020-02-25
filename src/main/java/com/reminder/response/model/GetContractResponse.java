package com.reminder.response.model;

import org.joda.time.DateTime;

public class GetContractResponse {

	private String contractTitle;
	private String contractReferenceNumber;
	private String supplier;
	private DateTime effectiveStartDate;
	private DateTime effectiveExpiryDate;
	private String userName;

	public String getContractTitle() {
		return contractTitle;
	}

	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}

	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public DateTime getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(DateTime effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public DateTime getEffectiveExpiryDate() {
		return effectiveExpiryDate;
	}

	public void setEffectiveExpiryDate(DateTime effectiveExpiryDate) {
		this.effectiveExpiryDate = effectiveExpiryDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "GetContractResponse [contractTitle=" + contractTitle + ", contractReferenceNumber="
				+ contractReferenceNumber + ", supplier=" + supplier + ", effectiveStartDate=" + effectiveStartDate
				+ ", effectiveExpiryDate=" + effectiveExpiryDate + ", userName=" + userName + "]";
	}
	
	

}
