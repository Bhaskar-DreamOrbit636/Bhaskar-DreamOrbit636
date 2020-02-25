package com.reminder.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.reminder.exception.UserInactiveException;
import com.reminder.model.GroupRoles;
import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.User;
import com.reminder.request.model.AssignRoleRequest;

@Repository
public class GroupUserDAOImpl implements GroupUserDAO{
	
	private Logger logger = Logger.getLogger(GroupUserDAOImpl.class);
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public void createGroupUser(GrouproleUser user) {
		try {
			logger.info("inside the createGroup() method");
			entityManager.merge(user);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Group not created due to error : " , e);
			entityManager.flush();
			entityManager.close();
		}
	}
	
	@Override
	public ModuleType findModuleType(int moduleTypeId) {
		ModuleType module = null;
		try {
			logger.info("inside the createModuleType() method");
			module = entityManager.find(ModuleType.class, moduleTypeId);
			logger.info("Details fetch from the database for module");
		} catch (HibernateException e) {
			logger.error("Group not updated due to Hibernate-exception : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return module;
	}
	

	@Override
	public void updateGroupUser(GrouproleUser user) {
		try {
			logger.info("inside the createGroup() method");
			entityManager.merge(user);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Group not created due to error : " , e);
			entityManager.flush();
			entityManager.close();
		}
		
	}

	@Override
	public void updateGroupUser(AssignRoleRequest assignRoleRequest) {
		User createdUser = entityManager.find(User.class, assignRoleRequest.getCreatedUserId());
		
		try {
			for( int id : assignRoleRequest.getUserIds()){
				
				GrouproleUser groupRoleUser = new GrouproleUser();
				
				GroupRoles gp = entityManager.find(GroupRoles.class, assignRoleRequest.getRoleId());
				
				Groups groups = entityManager.find(Groups.class, assignRoleRequest.getGroupId());
				
				User user = entityManager.find(User.class, id);
				
				groupRoleUser.setUser(user);
				groupRoleUser.setCreatedBy(createdUser);
				
				groups.setLastModifiedBy(createdUser);
				
				ModuleType moduleType = entityManager.find(ModuleType.class, assignRoleRequest.getModuleTypeId());
				
				groups.setModuleType(moduleType);
				
				groupRoleUser.setGroup(groups);
				groupRoleUser.setRoleUser(gp);
				
				entityManager.merge(groupRoleUser);
			}
			entityManager.close();
		} catch (Exception e) {
			logger.error("Group not created due to error : " , e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteGroupUser(int id) {
		try {
			GrouproleUser grouproleUser = entityManager.find(GrouproleUser.class, id);
			
			String sql1 = "select contract_reviewer_id from  contract_reviewer where contract_id in "
					+ " (select contract_id from  contract where reminder_id in "
					+ " (select reminder_id from reminder where user_group_id in "
					+ " (SELECT group_id FROM group_user where group_user_id="+ id +")) "
					+ " and user_id ="+grouproleUser.getUser().getUserId()+" )";

		    List<Integer> ids =  entityManager.createNativeQuery(sql1).getResultList();
		    
		    Query query = entityManager
					.createQuery("select gru.roleUser.groupRoleName FROM GrouproleUser gru JOIN gru.group  u"
							+ " where gru.group.groupId=:userId and gru.group.moduleType.moduleTypeId=:moduleTyId ")
					.setParameter("userId", grouproleUser.getGroup().getGroupId())
					.setParameter("moduleTyId",grouproleUser.getGroup().getModuleType().getModuleTypeId());
		    		//.setParameter("roleId", grouproleUser.getRoleUser().getGroupRoleId());
		    
			List<String> moduleByGroups = query.getResultList();
			
			long rolecount = moduleByGroups.stream()
	    			.filter(name ->  name.startsWith("Group"))
	    			.count();
			
		   // if(moduleByGroups > 1 ){
		    	long count = 0 ;
		    	logger.info("-----delete group user for one id---");
		    	List<String> roleName = entityManager.createQuery("select g.groupRoleName from GroupRoles g where g.groupRoleId = "+ grouproleUser.getRoleUser().getGroupRoleId())
		    	.getResultList();
		   
		    	count = roleName.stream()
		    			.filter(name ->  name.startsWith("Group"))
		    			.count();
		    
		    	if(count == 1 && rolecount ==1){
		    		 throw new UserInactiveException("Last Group Admin");
		    	}
		   // }
		    
		    if(!CollectionUtils.isEmpty(ids)){
				String sql2 = "DELETE FROM contract_reviewer where contract_reviewer_id in (:ids)";
			    entityManager.createNativeQuery(sql2).setParameter("ids", ids).executeUpdate();
		    }
		    
			String sql = "DELETE FROM group_user where group_user_id="+ id;
		    entityManager.createNativeQuery(sql).executeUpdate();
		    
		} catch (HibernateException e) {
			logger.error("Error in updateGroupUser" , e);
			entityManager.close();
		}/*catch (Exception e) {
			logger.error("Error in updateGroupUser" , e);
		}*/
	}
    
}
