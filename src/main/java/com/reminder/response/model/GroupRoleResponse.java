package com.reminder.response.model;

public class GroupRoleResponse extends Object {
	
	private int groupId;
	
	private String actionName;
	
	public GroupRoleResponse() {
		
	}

	public GroupRoleResponse(int groupId, String actionName) {
		this.groupId = groupId;
		this.actionName = actionName;
	}


	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	@Override
	public int hashCode() {
		return groupId*23;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (!(obj instanceof GroupRoleResponse)) {
            return false;
        }

        GroupRoleResponse grr = (GroupRoleResponse) obj;

        return grr.groupId == groupId;
	}

}
