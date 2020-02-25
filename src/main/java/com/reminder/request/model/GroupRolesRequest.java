package com.reminder.request.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GroupRolesRequest implements Serializable {
	private static final long serialVersionUID = -1232308999408322328L;

	private String groupRoleName;
	//private int roleId;
	private Set<ActionTypeRequest> actionType = new HashSet<>();
	private Set<GroupUserRequest> groupUsers = new HashSet<>();

	public String getGroupRoleName() {
		return groupRoleName;
	}

	public void setGroupRoleName(String groupRoleName) {
		this.groupRoleName = groupRoleName;
	}

	public Set<ActionTypeRequest> getGroupRoleActionType() {
		return actionType;
	}

	public void setGroupRoleActionType(Set<ActionTypeRequest> groupRoleActionType) {
		this.actionType = groupRoleActionType;
	}

	@Override
	public String toString() {
		return "GroupRoles ";
	}

	public Set<GroupUserRequest> getGroupUsers() {
		return groupUsers;
	}

	public void setGroupUsers(Set<GroupUserRequest> groupUsers) {
		this.groupUsers = groupUsers;
	}

	/*public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}*/
	private void writeObject(ObjectOutputStream stream)
	        throws IOException {
	    stream.defaultWriteObject();
	}

	private void readObject(ObjectInputStream stream)
	        throws IOException, ClassNotFoundException {
	    stream.defaultReadObject();
	}
		
}
