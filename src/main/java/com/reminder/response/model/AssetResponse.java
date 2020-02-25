package com.reminder.response.model;

import java.util.List;

import com.reminder.model.Asset;
import com.reminder.model.AssetType;

public class AssetResponse {
	
	private Asset asset;
	private Long count;
	private Integer parentId;
	private String parentname;
	private int groupId;
	private String groupName;
	private List<String> actions;
	//private List<AssetTypeResponse> assettype;
	private List<AssetType> assettype;
	private String createdBy;
	private String lastModifiedBy;
	
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public List<AssetType> getAssettype() {
		return assettype;
	}

	public void setAssettype(List<AssetType> assettype) {
		this.assettype = assettype;
	}

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

	public String getParentname() {
		return parentname;
	}

	public void setParentname(String parentname) {
		this.parentname = parentname;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	
	

}
