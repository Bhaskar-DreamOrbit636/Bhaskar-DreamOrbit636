package com.reminder.response.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DashboardResponse {
	
	private DashboardModule assetExpiring;
	
	private DashboardModule staffExpiring;
	
	private DashboardModule contractExpiring;
	
	private List<GroupRecordExpiring> groupRecordExpiringList;
	
	private Set<Date> expiryCalendar;

	public DashboardModule getAssetExpiring() {
		return assetExpiring;
	}

	public void setAssetExpiring(DashboardModule assetExpiring) {
		this.assetExpiring = assetExpiring;
	}

	public DashboardModule getStaffExpiring() {
		return staffExpiring;
	}

	public void setStaffExpiring(DashboardModule staffExpiring) {
		this.staffExpiring = staffExpiring;
	}

	public DashboardModule getContractExpiring() {
		return contractExpiring;
	}

	public void setContractExpiring(DashboardModule contractExpiring) {
		this.contractExpiring = contractExpiring;
	}

	public List<GroupRecordExpiring> getGroupRecordExpiringList() {
		return groupRecordExpiringList;
	}

	public void setGroupRecordExpiringList(List<GroupRecordExpiring> groupRecordExpiringList) {
		this.groupRecordExpiringList = groupRecordExpiringList;
	}

	public Set<Date> getExpiryCalendar() {
		return expiryCalendar;
	}

	public void setExpiryCalendar(Set<Date> expiryCalendar) {
		this.expiryCalendar = expiryCalendar;
	}
	
}
