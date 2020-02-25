package com.reminder.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.ActionTypeDAO;
import com.reminder.dao.GroupDAO;
import com.reminder.dao.RoleDAO;
import com.reminder.model.GroupRoles;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class RoleServiceImpl implements RoleService
{

	private Logger logger = Logger.getLogger(RoleServiceImpl.class);
	
	@Autowired
    private RoleDAO roleDAO;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private ActionTypeDAO actionTypeDAO;
    
    @Override
    public GroupRoles createRole(GroupRoles role)
    {
    	GroupRoles newRole = roleDAO.createGroupRole(role);
    	return newRole;
    }

        
    //--------------------- for Group Roles -----------------------
    
    @Override
    public void createGroupRole(int groupid,GroupRoles groupRole)
    {
    	try {
    		groupDAO.getGroupById(groupid);
    		roleDAO.createGroupRole(groupRole);
		} catch (Exception e) {
			logger.error("Error in create GropRole: "+e);
			e.printStackTrace();
		}
    	
    }

    @Override
    public GroupRoles getGroupRoleById(int groupRoleId)
    {
        return roleDAO.getGroupRoleById(groupRoleId);
    }
    
    @Override
    public List<GroupRoles> getRolesByGroup(int groupId) {
    	return roleDAO.getRoleByGroup(groupId);
    }

    @Override
    public List<GroupRoles> getAllGroupRoles()
    {
        return roleDAO.getAllGroupRoles();
    }

    @Override
    public void updateGroupRole(GroupRoles groupRole)
    {
    	roleDAO.updateGroupRole(groupRole);
    }

    @Override
    public void deleteGroupRole(int groupRoleId)
    {
    	roleDAO.deleteGroupRole(groupRoleId);
    }

	@Override
	public GroupRoles findGroupRole(int groupRoleId) {
		
		return roleDAO.getGroupRoleById(groupRoleId);
	}
	
	@Override
	public void flushGroupRole() {
		roleDAO.flushGroupRole();
	}


	@Override
	public void deleteRoleUser(GroupRoles newGroupRole) {
		roleDAO.deleteRoleUser(newGroupRole);		
	}


	@Override
	public List<com.reminder.response.model.ActionType> getGroupRoleActions() {
		return roleDAO.getGroupRoleActions();
	}
}

