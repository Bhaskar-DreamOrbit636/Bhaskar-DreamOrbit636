package com.reminder.response.model;

import java.util.List;

import org.joda.time.DateTime;

import com.reminder.model.Contract_Has_Status;
import com.reminder.model.Contract_Reviewer;
import com.reminder.model.User;

public class ContractResponse {

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
	private User officerInChargeId;
	private DateTime plpExpiryDate;
	private DateTime wcpExpiryDate;
	private DateTime hnmExpiryDate;
	private String savingCurrency;
	//private String toList;
	//private String ccList;
	private String additionalCcList;
	private String saving;
	private int moduleTypeId;
	private Boolean isDeleted;
	private Boolean isVerified;
	private ReminderResponse reminder;
	private String contractStatus;
	private List<ContractReviewer> contract_Reviewer;
	private List<Contract_Has_Status> contractHasStatus;
	private List<String> actions;
	private String createdUser;
	private int parentContractId;
	private String groupName;
	private List<String> contractStatusList;
	private String rejectedRemarks;
	private boolean isUpdated;
	private List<Contract_Reviewer> reviewer;
	private List<String> userModifiedContract;
	
	
	public List<String> getUserModifiedContract() {
		return userModifiedContract;
	}

	public void setUserModifiedContract(List<String> userModifiedContract) {
		this.userModifiedContract = userModifiedContract;
	}

	public List<Contract_Reviewer> getReviewer() {
		return reviewer;
	}

	public void setReviewer(List<Contract_Reviewer> list) {
		this.reviewer = list;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public User getOfficerInChargeId() {
		return officerInChargeId;
	}

	public void setOfficerInChargeId(User officerInChargeId) {
		this.officerInChargeId = officerInChargeId;
	}

	public ReminderResponse getReminder() {
		return reminder;
	}

	public void setReminder(ReminderResponse reminder) {
		this.reminder = reminder;
	}

	public List<ContractReviewer> getContractReviewer() {
		return contract_Reviewer;
	}

	public void setContractReviewer(List<ContractReviewer> contract_Reviewer) {
		this.contract_Reviewer = contract_Reviewer;
	}



	public List<Contract_Has_Status> getContractHasStatus() {
		return contractHasStatus;
	}

	public void setContractHasStatus(List<Contract_Has_Status> contractHasStatus) {
		this.contractHasStatus = contractHasStatus;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public int getParentContractId() {
		return parentContractId;
	}

	public void setParentContractId(int parentContractId) {
		this.parentContractId = parentContractId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getContractStatusList() {
		return contractStatusList;
	}

	public void setContractStatusList(List<String> contractStatusList) {
		this.contractStatusList = contractStatusList;
	}

	public String getRejectedRemarks() {
		return rejectedRemarks;
	}

	public void setRejectedRemarks(String rejectedRemarks) {
		this.rejectedRemarks = rejectedRemarks;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

 
	
}
