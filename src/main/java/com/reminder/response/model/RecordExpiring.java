package com.reminder.response.model;

import java.math.BigInteger;

public class RecordExpiring {
	
	private BigInteger assetExpiring = BigInteger.ZERO;
	
	private BigInteger contractExpiring = BigInteger.ZERO;
	
	private BigInteger staffExpiring = BigInteger.ZERO;
	
	private int groupId;
	
	private String groupName;
	
	private String moduleName;
	
	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public BigInteger getAssetExpiring() {
		return assetExpiring;
	}

	public void setAssetExpiring(BigInteger assetExpiring) {
		this.assetExpiring = assetExpiring;
	}

	public BigInteger getContractExpiring() {
		return contractExpiring;
	}

	public void setContractExpiring(BigInteger contractExpiring) {
		this.contractExpiring = contractExpiring;
	}

	public BigInteger getStaffExpiring() {
		return staffExpiring;
	}

	public void setStaffExpiring(BigInteger staffExpiring) {
		this.staffExpiring = staffExpiring;
	}
	
}
