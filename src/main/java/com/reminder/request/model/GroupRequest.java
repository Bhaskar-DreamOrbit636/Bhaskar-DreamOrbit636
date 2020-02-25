package com.reminder.request.model;

import java.util.Set;

import org.joda.time.DateTime;

public class GroupRequest  {

	private String groupName;
	private String description;
	private Boolean active;
	private int createdById;
	private DateTime createdAt;
	private int moduleType;
	private DateTime lastModifiedAt;
	private int lastModifiedById;
	private Set<GroupRolesRequest> roles;
	
	
	public Set<GroupRolesRequest> getRoles() {
		return roles;
	}

	public void setRoles(Set<GroupRolesRequest> roles) {
		this.roles = roles;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public int getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(int lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	
	public int getModuleType() {
		return moduleType;
	}

	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	@Override
	public String toString() {
		return "Group1 [ groupName=" + groupName + "]";
	}

}
