package com.reminder.request.model;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContractRequest {

	private int contractId;
	private int groupId;
	private String contractReferenceNumber;
	private String contractTitle;
	private String description;
	private int baPoNumber;
	private String supplier;
	private String contractValueCurrency;
	private String contractValue;
	private String performanceBondSubmission;
	private DateTime optionYear;
	private int officerInChargeId;
	private DateTime plpExpiryDate;
	private DateTime wcpExpiryDate;
	private DateTime hnmExpiryDate;
	private String savingCurrency;
	//private String toList;
	//private String ccList;
	private String additionalCcList;
	private String saving;
	private int moduleTypeId;
	private ReminderRequest reminder;
	private List<ContractReviewerRequest> contractReviewer;
	private ContractHasStatusRequest contractHasStatus;
	private boolean isRenew;
	private boolean isVerifiedRejected;
	@JsonProperty
	private boolean isResubmit;
	@JsonProperty
	private boolean isReviewerChanged;

	


	public boolean isRenew() {
		return isRenew;
	}

	public void setRenew(boolean isRenew) {
		this.isRenew = isRenew;
	}

	public boolean isReviewerChanged() {
		return isReviewerChanged;
	}

	public void setReviewerChanged(boolean isReviewerChanged) {
		this.isReviewerChanged = isReviewerChanged;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	public String getContractTitle() {
		return contractTitle;
	}

	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getBaPoNumber() {
		return baPoNumber;
	}

	public void setBaPoNumber(int baPoNumber) {
		this.baPoNumber = baPoNumber;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getContractValueCurrency() {
		return contractValueCurrency;
	}

	public void setContractValueCurrency(String contractValueCurrency) {
		this.contractValueCurrency = contractValueCurrency;
	}

	public String getContractValue() {
		return contractValue;
	}

	public void setContractValue(String contractValue) {
		this.contractValue = contractValue;
	}

	public String getPerformanceBondSubmission() {
		return performanceBondSubmission;
	}

	public void setPerformanceBondSubmission(String performanceBondSubmission) {
		this.performanceBondSubmission = performanceBondSubmission;
	}

	public DateTime getOptionYear() {
		return optionYear;
	}

	public void setOptionYear(DateTime optionYear) {
		this.optionYear = optionYear;
	}

	public DateTime getPlpExpiryDate() {
		return plpExpiryDate;
	}

	public void setPlpExpiryDate(DateTime plpExpiryDate) {
		this.plpExpiryDate = plpExpiryDate;
	}

	public DateTime getWcpExpiryDate() {
		return wcpExpiryDate;
	}

	public void setWcpExpiryDate(DateTime wcpExpiryDate) {
		this.wcpExpiryDate = wcpExpiryDate;
	}

	public DateTime getHnmExpiryDate() {
		return hnmExpiryDate;
	}

	public void setHnmExpiryDate(DateTime hnmExpiryDate) {
		this.hnmExpiryDate = hnmExpiryDate;
	}

	public String getSavingCurrency() {
		return savingCurrency;
	}

	public void setSavingCurrency(String savingCurrency) {
		this.savingCurrency = savingCurrency;
	}

/*	public String getToList() {
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
	}*/

	public String getAdditionalCcList() {
		return additionalCcList;
	}

	public void setAdditionalCcList(String additionalCcList) {
		this.additionalCcList = additionalCcList;
	}

	public String getSaving() {
		return saving;
	}

	public void setSaving(String saving) {
		this.saving = saving;
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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public ContractHasStatusRequest getContractHasStatus() {
		return contractHasStatus;
	}

	public void setContractHasStatus(ContractHasStatusRequest contractHasStatus) {
		this.contractHasStatus = contractHasStatus;
	}

	public int getOfficerInChargeId() {
		return officerInChargeId;
	}

	public void setOfficerInChargeId(int officerInChargeId) {
		this.officerInChargeId = officerInChargeId;
	}

	public List<ContractReviewerRequest> getContractReviewer() {
		return contractReviewer;
	}

	public void setContractReviewer(List<ContractReviewerRequest> contractReviewer) {
		this.contractReviewer = contractReviewer;
	}

	public boolean isVerifiedRejected() {
		return isVerifiedRejected;
	}

	public void setVerifiedRejected(boolean isVerifiedRejected) {
		this.isVerifiedRejected = isVerifiedRejected;
	}

	public boolean isResubmit() {
		return isResubmit;
	}

	public void setResubmit(boolean isResubmit) {
		this.isResubmit = isResubmit;
	}

}
