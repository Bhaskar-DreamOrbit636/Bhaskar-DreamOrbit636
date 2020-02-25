package com.reminder.response.model;

import java.util.Set;

public class ContractDropDownValue {
	
	private Set<MyGroupDetails> officerInChargeList;
	
	private Set<MyGroupDetails> reviewers;
	
	private Set<MyGroupDetails> toList;
	
	private Set<MyGroupDetails> ccList;
	
	private Set<MyGroupDetails> ccLastReminderList;
	
	private Set<MyGroupDetails> ccExpiryReminderList;
	
	//private Set<MyGroupDetails> allReviewers;
	
/*	private Set<MyGroupDetails> addccExpiryReminderList;
	
	private Set<MyGroupDetails> addccLastReminderList;*/

	public Set<MyGroupDetails> getOfficerInChargeList() {
		return officerInChargeList;
	}

	public void setOfficerInChargeList(Set<MyGroupDetails> officerInChargeList) {
		this.officerInChargeList = officerInChargeList;
	}

	public Set<MyGroupDetails> getReviewers() {
		return reviewers;
	}

	public void setReviewers(Set<MyGroupDetails> reviewers) {
		this.reviewers = reviewers;
	}

	public Set<MyGroupDetails> getToList() {
		return toList;
	}

	public void setToList(Set<MyGroupDetails> toList) {
		this.toList = toList;
	}

	public Set<MyGroupDetails> getCcList() {
		return ccList;
	}

	public void setCcList(Set<MyGroupDetails> ccList) {
		this.ccList = ccList;
	}

	public Set<MyGroupDetails> getCcLastReminderList() {
		return ccLastReminderList;
	}

	public void setCcLastReminderList(Set<MyGroupDetails> ccLastReminderList) {
		this.ccLastReminderList = ccLastReminderList;
	}

	public Set<MyGroupDetails> getCcExpiryReminderList() {
		return ccExpiryReminderList;
	}

	public void setCcExpiryReminderList(Set<MyGroupDetails> ccExpiryReminderList) {
		this.ccExpiryReminderList = ccExpiryReminderList;
	}

/*	public Set<MyGroupDetails> getAllReviewers() {
		return allReviewers;
	}

	public void setAllReviewers(Set<MyGroupDetails> allReviewers) {
		this.allReviewers = allReviewers;
	}*/

/*	public Set<MyGroupDetails> getAddccExpiryReminderList() {
		return addccExpiryReminderList;
	}

	public void setAddccExpiryReminderList(Set<MyGroupDetails> addccExpiryReminderList) {
		this.addccExpiryReminderList = addccExpiryReminderList;
	}

	public Set<MyGroupDetails> getAddccLastReminderList() {
		return addccLastReminderList;
	}

	public void setAddccLastReminderList(Set<MyGroupDetails> addccLastReminderList) {
		this.addccLastReminderList = addccLastReminderList;
	}
	*/
	
}
