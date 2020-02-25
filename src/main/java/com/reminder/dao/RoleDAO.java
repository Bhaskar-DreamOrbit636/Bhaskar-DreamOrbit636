package com.reminder.dao;

import java.util.List;

import com.reminder.model.GroupRoles;
import com.reminder.response.model.ActionType;

public interface RoleDAO
{
    //--------------- for Group_Role api ---------------------
    
    public GroupRoles createGroupRole(GroupRoles groupRole);
    
    public GroupRoles getGroupRoleById(int groupRoleId);
    
    public List<GroupRoles> getAllGroupRoles();
    
    public void updateGroupRole(GroupRoles groupRole);
    
    public void deleteGroupRole(int groupRoleId);

	void flushGroupRole();

	public List<GroupRoles> getRoleByGroup(int groupId);

	public void deleteRoleUser(GroupRoles newGroupRole);

	public List<ActionType> getGroupRoleActions();

}
