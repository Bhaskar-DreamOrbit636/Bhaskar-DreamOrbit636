package com.reminder.response.model;

import java.util.List;

public class UserGroupResponse {

	private String groupName;
	private List<String> groupRoles;
	
	
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getGroupRoles() {
		return groupRoles;
	}
	public void setGroupRoles(List<String> groupRoles) {
		this.groupRoles = groupRoles;
	}

	
}
