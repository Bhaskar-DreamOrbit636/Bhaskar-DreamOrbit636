package com.reminder.dao;

import java.util.Set;

import com.reminder.model.GroupRoleActionType;

public interface GroupRoleActionTypeDAO {
	public void createGroup(GroupRoleActionType group);

	void deleteGroupActions(GroupRoleActionType groupRoleType);

	public void deleteGroupActions(Set<GroupRoleActionType> groupRolesActionType);
}
