package com.reminder.response.model;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import com.reminder.model.ModuleType;
import com.reminder.model.User;

public class GroupResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8510505913754764021L;
	/**
	 * 
	 */
	private Integer groupId;
	private String groupName;
	private String description;
	private Boolean active;
	private DateTime createdAt;
	private DateTime lastModifiedAt;
	private User createdBy;
	private UserResponse lastModifiedBy;
	private ModuleType moduleType;
	private List<String> actions;
	private String groupUsers;
/*	private Set<GroupRoles> groupRoles;
	private Set<GrouproleUser> groupUser;*/
	
	
	public Integer getGroupId() {
		return groupId;
	}
	public String getGroupUsers() {
		return groupUsers;
	}
	public void setGroupUsers(String groupUsers) {
		this.groupUsers = groupUsers;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
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
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public UserResponse getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(UserResponse lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public ModuleType getModuleType() {
		return moduleType;
	}
	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}
/*	public Set<GroupRoles> getGroupRoles() {
		return groupRoles;
	}
	public void setGroupRoles(Set<GroupRoles> groupRoles) {
		this.groupRoles = groupRoles;
	}
	public Set<GrouproleUser> getGroupUser() {
		return groupUser;
	}
	public void setGroupUser(Set<GrouproleUser> groupUser) {
		this.groupUser = groupUser;
	}*/
	
	
	
}
