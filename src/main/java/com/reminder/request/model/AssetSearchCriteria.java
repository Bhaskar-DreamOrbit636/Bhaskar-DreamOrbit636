package com.reminder.request.model;

import java.io.Serializable;
import java.util.Date;

public class AssetSearchCriteria implements Serializable {
	
	private static final long serialVersionUID = -1232395859408322328L;

	private String location;

	private String assetType;

	private String assetSubType;

	private String assetId;

	private Date expiryDateFrom;

	private Date expiryDateTo;

	private int userGroupId;

	private String active;

	private String status;
	
	private String assetDescription;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetSubType() {
		return assetSubType;
	}

	public void setAssetSubType(String assetSubType) {
		this.assetSubType = assetSubType;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
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

	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
	
}
