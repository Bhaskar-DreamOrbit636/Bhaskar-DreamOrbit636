package com.reminder.service;

import java.util.List;

import com.reminder.model.GroupRoles;
import com.reminder.response.model.ActionType;

public interface RoleService
{
    public GroupRoles createRole(GroupRoles role);
    
 
    //------- for group role api ---------------
    
    public void createGroupRole(int groupid,GroupRoles groupRole);
    
    public GroupRoles getGroupRoleById(int groupRoleId);
    
    public List<GroupRoles> getAllGroupRoles();
    
    public void updateGroupRole(GroupRoles groupRole);
    
    public void deleteGroupRole(int groupRoleId);
    
    //-------for Action Type check -------------
    
    public GroupRoles findGroupRole(int actionTypeId);


	void flushGroupRole();


	public List<GroupRoles> getRolesByGroup(int groupId);


	public void deleteRoleUser(GroupRoles newGroupRole);


	public List<ActionType> getGroupRoleActions();

}
