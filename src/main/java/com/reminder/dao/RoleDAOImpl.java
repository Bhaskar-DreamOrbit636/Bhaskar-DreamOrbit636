package com.reminder.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.GroupRoles;
import com.reminder.model.Groups;
import com.reminder.response.model.ActionType;
import com.reminder.service.GroupService;

@Repository
public class RoleDAOImpl implements RoleDAO
{
	private Logger logger = Logger.getLogger(RoleDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private GroupService groupService;
    
    
    
    //----------------- for Group_Role Api -----------------------------
    
    @Override
    public GroupRoles createGroupRole(GroupRoles groupRole)
    {
    	try {
			logger.info("inside the createGroupRole() method");
			entityManager.merge(groupRole);
		} catch (HibernateException e) {
			logger.error("Role not created due to error : " + e);
		}
    	return groupRole;
    }

    @Override
    public GroupRoles getGroupRoleById(int groupRoleId)
    {
    	GroupRoles groupRole = null;
    	try {
			logger.info("fetching role details of groupRoleId :"+groupRoleId);
			groupRole = entityManager.find(GroupRoles.class,groupRoleId);
		} catch (HibernateException e) {
			logger.error("No role fetched due to error : " + e);
		}
        return groupRole;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<GroupRoles> getRoleByGroup(int groupId)
    {
    	List<GroupRoles> groupRoles = new ArrayList<>();
    	try {
    		Groups group = groupService.getGroupByID(groupId);
    		groupRoles = entityManager.createQuery("select r from GroupRoles r where r.group=:group").setParameter("group", group).getResultList();
    		
    	} catch (HibernateException ex) {
    		logger.error(ex);
    	}
    	return groupRoles;
    	
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<GroupRoles> getAllGroupRoles()
    {
    	List<GroupRoles> group_Roles = null;
    	try {
			logger.info("inside the getAllGroupRoles() method");
			group_Roles = entityManager.createQuery("select r from Group_Roles r").getResultList();
		} catch (HibernateException e) {
			logger.error("No role found and exception ocurred : " + e);
		}
        return group_Roles;
    }

    
    @Override
    public void updateGroupRole(GroupRoles groupRole)
    {
    	try {
			logger.info("inside the updateRole() method");
			entityManager.merge(groupRole);
		} catch (HibernateException e) {
			logger.error("Role not updated due to Hibernate-exception : " + e);
		}
    }
    
    @Override
    public void flushGroupRole() {
    	try {
    		entityManager.flush();
    	}catch(HibernateException e) {
    		logger.error(e);
    	}
    }
    
    @Override
    public void deleteGroupRole(int groupRoleId)
    {
    	try {
			logger.info("inside the deleteRole() method and deleting detail of roleid : " + groupRoleId);
			//GroupRoles gr = entityManager.find(GroupRoles.class, groupRoleId);
			entityManager.createNativeQuery("delete from group_role_has_action_type where group_role_id = ?").setParameter(1, groupRoleId).executeUpdate();
			entityManager.createNativeQuery("delete from group_user where group_role_id = ?").setParameter(1, groupRoleId).executeUpdate();
			entityManager.createNativeQuery("delete from group_role where group_role_id = ?").setParameter(1, groupRoleId).executeUpdate();
		} catch (HibernateException e) {
			logger.error("Group details is not deleted : " + e);
		}
    }

	@Override
	public void deleteRoleUser(GroupRoles newGroupRole) {
		
		entityManager.createNativeQuery("delete from group_user where group_role_id = ?").setParameter(1, newGroupRole.getGroupRoleId()).executeUpdate();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActionType> getGroupRoleActions() {
		List<Object[]> actionTypes = null;
		try {
			logger.info("inside the getGroupRoleActions() method");
			actionTypes = entityManager.createNativeQuery("select c.action_type_id,c.name,c.display_name,c.admin_action from action_type c").getResultList();
		} catch (HibernateException e) {
			logger.error("No actions found and exception ocurred : " + e);
		}
		List<ActionType> types = new ArrayList<>();

		for (Object[] objects : actionTypes) {
			ActionType actionType = new ActionType();
			actionType.setActionTypeId((int) objects[0]);
			actionType.setActionType((String) objects[1]);
			actionType.setDisplayName((String) objects[2]);
			actionType.setAdminAction((boolean) objects[3]);
			types.add(actionType);
		}
		return types;
	}


}
