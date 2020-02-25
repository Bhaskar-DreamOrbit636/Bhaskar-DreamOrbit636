package com.reminder.dao;

import com.reminder.model.GrouproleUser;
import com.reminder.model.ModuleType;
import com.reminder.request.model.AssignRoleRequest;

public interface GroupUserDAO {
	
	public void createGroupUser(GrouproleUser user);
	
	public void updateGroupUser(GrouproleUser user);
	
	public ModuleType findModuleType(int moduleTypeId);

	public void updateGroupUser(AssignRoleRequest assignRoleRequest);

	public void deleteGroupUser(int id);
	
}
