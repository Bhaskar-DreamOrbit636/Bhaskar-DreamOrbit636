package com.reminder.service;

import java.util.Set;

import com.reminder.model.GroupRoleActionType;

public interface GroupRoleActionTypeService {
	
	public void createGroup(GroupRoleActionType group);

	public void deleteGroupActions(GroupRoleActionType groupRoleActionType);

	public void deleteGroupActions(Set<GroupRoleActionType> groupRolesActionType);

}
