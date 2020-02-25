package com.reminder.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.model.GroupRoles;
import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.User;
import com.reminder.request.model.GroupRequest;
import com.reminder.request.model.GroupSearchCriteria;
import com.reminder.request.model.MyGroupHome;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.response.model.MyGroupRoleDetails;
import com.reminder.service.GroupService;
import com.reminder.service.RoleService;


@Repository
public class GroupDAOImpl implements GroupDAO
{
	private Logger logger = Logger.getLogger(GroupDAOImpl.class);
	
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private GroupService groupService;
    
    @Override
    public void createGroup(Groups group)
    {
		try {
			logger.info("inside the createGroup() method");
			entityManager.merge(group);
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Group not created due to error : " , e);
			entityManager.flush();
			entityManager.close();
		}
    }

    @Override
    public Groups getGroupById(int groupId)
    {
    	
    	Groups group = null;
    	try {
			logger.info("fetching group details of groupId :"+groupId);
			group = entityManager.find(Groups.class,groupId);
		} catch (HibernateException e) {
			logger.error("No group fetched due to error : " + e);
		}
        return group;
    }
    
    /*@SuppressWarnings("unchecked")
	@Override
    public List<Group1> getAllGroups()
    {
    	List<Group1> groups = null;
    	try {
			logger.info("inside the getAllGroups() method");
			groups = entityManager.createQuery("select g from Group1 g").getResultList();
		} catch (HibernateException e) {
			logger.error("No contract found and exception ocurred : " + e);
		}
        return groups;
    }*/
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getAllGroups(String sort_by, String order, String searchC, Integer limit, Integer page_no,
			GroupSearchCriteria groupSearchCriteria) {
		StringBuilder searchCriteria = new StringBuilder();
		String sort = "";

		int offset = 0;
		int maxResult = 1000;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}

		List<String> conditionList = new ArrayList<>();
		if (groupSearchCriteria.getModuleName() != null) {
			if ((!groupSearchCriteria.getModuleName().equalsIgnoreCase("All"))
					&& ((groupSearchCriteria.getModuleName().equalsIgnoreCase("Contract")
							|| "contract".contains(groupSearchCriteria.getModuleName().toLowerCase()))
							|| (groupSearchCriteria.getModuleName().equalsIgnoreCase("Assets")
									|| "assets".contains(groupSearchCriteria.getModuleName().toLowerCase()))
							|| (groupSearchCriteria.getModuleName().equalsIgnoreCase("Staff")
									|| "staff".contains(groupSearchCriteria.getModuleName().toLowerCase()))))
				conditionList.add(" LOWER(m.moduletype) LIKE :module ");
		}
		if (groupSearchCriteria.getGroupName() != null) {
			conditionList.add(" LOWER(g.name) LIKE :groupName ");
		}
		if (groupSearchCriteria.getActive() != null) {
			if (groupSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" g.active = true ");
			} else if (groupSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" g.active = false ");
			} else if (groupSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (g.active = true OR g.active = false) ");
			}
		}
		if ((searchC) != null) {
			if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
				searchCriteria.append(" g.active = 1");
			else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
				searchCriteria.append(" g.active = 0");
			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			conditionList.add(" (" + searchCriteria.toString()
					+ "LOWER(g.name) LIKE :searchC OR  LOWER(m.moduletype) LIKE :searchC OR LOWER(g.description) LIKE :searchC) ");
		} else
			searchCriteria.append("");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < conditionList.size(); i++) {
		/*	if (i == 0)
				sb.append(" WHERE ");*/
			sb.append(conditionList.get(i));
			if (i != (conditionList.size() - 1))
				sb.append(" AND ");
		}
		if ((sort_by) != null){
			if(sort_by.equalsIgnoreCase("moduleType"))
				sort = " ORDER BY " + "m.moduletype" + " " + order;
			else
				sort = " ORDER BY " + sort_by + " " + order;
		}
		else
			sort = "";
		List<Object[]> groups = null;
		try {

			logger.info("inside the getAllGroups() method");
			Query query = entityManager.createNativeQuery("select g.name, g.group_id, g.description, g.active, m.moduletype, m.module_type_id, GROUP_CONCAT(u.user_name) "
					+ " from groups g join group_user gu on g.group_id = gu.group_id "
					+ " join group_role gr on gu.group_role_id = gr.group_role_id "
					+ " join module_type m on g.module_type_id = m.module_type_id  "
					+ " join user u on u.user_id = gu.user_id where gr.role like 'Group Admin' AND "
					+ sb.toString() + " group by g.group_id "+sort);
			if (searchC != null)
				query.setParameter("searchC", "%" + searchC + "%");
			if (groupSearchCriteria.getModuleName() != null && !(groupSearchCriteria.getModuleName().equalsIgnoreCase("All"))) {
				query.setParameter("module", "%" + groupSearchCriteria.getModuleName() + "%");
			}
			if (groupSearchCriteria.getGroupName() != null) {
				query.setParameter("groupName", "%" + groupSearchCriteria.getGroupName()+"%");
			}
			groups = query.setFirstResult(offset).setMaxResults(maxResult).getResultList();
		} catch (HibernateException e) {
			logger.error("No group found : " + e);
		}
		return groups;
	}

		@Override
	    public void updateGroup(Groups group ,GroupRequest groupRequest)
	    {/*
	    	try {
	    	  //  groups = entityManager.find(Groups.class,groups.getGroupId());
	    	    Groups  groups =(Groups) entityManager.createQuery("select gru from Groups gru where gru.groupId=:groupId ").
									setParameter("groupId", group.getGroupId()).
									getSingleResult();

	    		Groups groups =  group;
	    		entityManager.detach(groups);
	    		entityManager.detach(group.getLastModifiedBy());

	    		groups.setActive(groupRequest.isActive());
	    		groups.setDescription(groupRequest.getDescription());
	    		groups.setGroupName(groupRequest.getGroupName());
	    		groups = entityManager.merge(groups);
	    		
	    		List<GroupRoles>  GroupRolesSet =(List<GroupRoles>) entityManager.createQuery("select gru from GroupRoles gru where gru.group.groupId=:groupId ").
						setParameter("groupId", groups.getGroupId()).
						getResultList();
	    		
	    		 Set<Integer>  set = new HashSet<>();
	    		
	    		  for (GroupRolesRequest groupRoles : groupRequest.getRoles()) {
	    			  if(groupRoles.getRoleId()!=0){
	    				  set.add(groupRoles.getRoleId());
	    			  }
	    		  }
	    			  
	    			  if(!groupRequest.getRoles().contains(groupRoles.getGroupRoleId())){
	  					//entityManager.remove(groupRoles);
	  					String sql = "DELETE FROM group_role where group_role_id="+ groupRoles.getGroupRoleId();
	  				    entityManager.createNativeQuery(sql).executeUpdate();
	  				}
	    		
	    		for (GroupRoles groupRoles : GroupRolesSet) {
	    			  if(!set.contains(groupRoles.getGroupRoleId())){
		  					//entityManager.remove(groupRoles);
		  					String sql = "DELETE FROM group_role where group_role_id="+ groupRoles.getGroupRoleId();
		  				    entityManager.createNativeQuery(sql).executeUpdate();
		  				    
		  					String sql1 = "DELETE FROM group_role_has_action_type where group_role_action_type_id="+ groupRoles.getGroupRoleId();
		  				    entityManager.createNativeQuery(sql).executeUpdate();
		  				}
	    		}
	    		
	    		
	    		// groupRole
	    		Set<GroupRoles> groupRolesSet = new HashSet<>();

			    for (GroupRolesRequest groupRoles : groupRequest.getRoles()) {
					GroupRoles groupRole = null;
					if (groupRoles.getRoleId() != 0) {
						groupRole = entityManager.find(GroupRoles.class, groupRoles.getRoleId());
						groupRole.setGroupRoleName(groupRoles.getGroupRoleName());
					} else {
						groupRole = new GroupRoles();
						groupRole.setGroupRoleName(groupRoles.getGroupRoleName());
					}
					groupRole.setLastModifiedAtTime(new Date());
					groupRole.setGroup(groups);
					//entityManager.merge(groupRole);
					groupRolesSet.add(groupRole);
					
					
					List<GroupRoleActionType> roleAction	=  
    						(List<GroupRoleActionType>) entityManager.createQuery("select g from GroupRoleActionType g where g.groupRole.groupRoleId=:groupRoleId").
					setParameter("groupRoleId",groupRoles.getRoleId()).
					getResultList();
					
					
					  for (GroupRoleActionType actionType : roleAction) {
		    			  if(!groupRoles.getGroupRoleActionType().contains(actionType.getGroupRoleActionTypeId())){
		  					//entityManager.remove(groupRoles);
		  					String sql = "DELETE FROM group_role_has_action_type where group_role_action_type_id="+ actionType.getGroupRoleActionTypeId();
		  				    entityManager.createNativeQuery(sql).executeUpdate();
		  				    
		  				    
		  				}
		    		  }
					  
					
					// groupRoleAction
					Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
					Set<GrouproleUser> grouproleUserSet = new HashSet<>();

	    			for (ActionTypeRequest actions : groupRoles.getGroupRoleActionType()) {
	    				
	    				//ActionType newAction = entityManager.find(ActionType.class, actions.getActionTypeId());
	    				ActionType newAction = new ActionType();
	    				newAction.setActionTypeId(actions.getActionTypeId());

	    				GroupRoleActionType roleActionType = null;
						try {
							roleActionType = (GroupRoleActionType) 
									entityManager.createQuery("select g from GroupRoleActionType g where g.actionType.actionTypeId=:actionTypeId and g.groupRole.groupRoleId=:groupRoleId").
									setParameter("groupRoleId",groupRoles.getRoleId()).
									setParameter("actionTypeId", actions.getActionTypeId()).
									getSingleResult();
						} catch (Exception e) {
						}
	    				
						if (roleActionType == null) {
							roleActionType = new GroupRoleActionType();
	    				}
						roleActionType.setGroupRole(groupRole);
						roleActionType.setActionType(newAction);
						
		    			entityManager.merge(roleActionType);

						groupRoleActions.add(roleActionType);
				
					}
	    			
	    			
	    			if (groupRole.getGroupRoleName().equals("Group Admin")) {
						
	        			for (GroupUserRequest groupuser : groupRoles.getGroupUsers()){
	        				GrouproleUser grouproleUser = null ;
	        			
	    	    				// grouproleUser = entityManager.find(GrouproleUser.class,groupuser.getGroupUserId());
	        					List <GrouproleUser> grouproleUserList = entityManager.createQuery("select gru from GrouproleUser gru where gru.user.userId=:userId and gru.roleUser.groupRoleId=:groupRoleId").
	        					setParameter("groupRoleId", groupRole.getGroupRoleId()).
	        					setParameter("userId", groupuser.getUserId())
	        					.getResultList();
	        					
	        					if(!CollectionUtils.isEmpty(grouproleUserList)){
	        						grouproleUser = grouproleUserList.get(0);
	        					}
	        					
	    					if (grouproleUser == null) {
	    	    					grouproleUser = new GrouproleUser();
	    							User  user = entityManager.find(User.class,groupuser.getUserId());
	    							grouproleUser.setUser(user);
	        				}
	    					
	    					//entityManager.detach(groups);
	    					grouproleUser.setRoleUser(groupRole);
	    					grouproleUser.setGroup(groups);
	    					//entityManager.merge(grouproleUser);
	        				grouproleUserSet.add(grouproleUser);
	        			  }
	        			groups.setGroupUser(grouproleUserSet);
	        			
	        			}
	    			groupRole.setGroupRolesActionType(groupRoleActions);
	    			
	    			
	    			entityManager.merge(groupRole);
			     }
	    		
			} catch (Exception e) {
				logger.error("Group not updated due to Hibernate-exception : " + e);
			}
	    */}
	 

    /*@SuppressWarnings("unchecked")
	@Override
    public void updateGroup(Groups group ,GroupRequest groupRequest)
    {
    	try {
    	  //  groups = entityManager.find(Groups.class,groups.getGroupId());
    	    Groups  groups =(Groups) entityManager.createQuery("select gru from Groups gru where gru.groupId=:groupId ").
								setParameter("groupId", group.getGroupId()).
								getSingleResult();

    		Groups groups =  group;
    		
    		entityManager.detach(groups);
    		
    		entityManager.detach(group.getLastModifiedBy());

    		groups.setActive(groupRequest.isActive());
    		groups.setDescription(groupRequest.getDescription());
    		groups.setGroupName(groupRequest.getGroupName());
    		
			Set<GroupRoles> groupRolesSet = new HashSet<>();
			Set<GrouproleUser> grouproleUserSet = new HashSet<>();

    		for(GroupRolesRequest groupRoles: groupRequest.getRoles()){
    			
    			GroupRoles groupRole = entityManager.find(GroupRoles.class,groupRoles.getRoleId());
    			if(groupRole!= null){
	    			groupRole.setGroupRoleName(groupRoles.getGroupRoleName());
    			} else {
    				groupRole = new GroupRoles();
    				groupRole.setGroupRoleName(groupRoles.getGroupRoleName());
    			}
    			groupRole.setGroup(groups);
    		
    			
				if (groupRole.getGroupRoleName().equals("Group Admin")) {
					
    			for (GroupUserRequest groupuser : groupRoles.getGroupUsers()){
    				GrouproleUser grouproleUser = null ;
    			
	    				// grouproleUser = entityManager.find(GrouproleUser.class,groupuser.getGroupUserId());
    					List <GrouproleUser> grouproleUserList = entityManager.createQuery("select gru from GrouproleUser gru where gru.user.userId=:userId and gru.roleUser.groupRoleId=:groupRoleId").
    					setParameter("groupRoleId", groupRole.getGroupRoleId()).
    					setParameter("userId", groupuser.getUserId())
    					.getResultList();
    					
    					if(!CollectionUtils.isEmpty(grouproleUserList)){
    						grouproleUser = grouproleUserList.get(0);
    					}
    					
					if (grouproleUser == null) {
	    					grouproleUser = new GrouproleUser();
							User  user = entityManager.find(User.class,groupuser.getUserId());
							grouproleUser.setUser(user);
    				}
					
					entityManager.detach(groups);
					grouproleUser.setRoleUser(groupRole);
					grouproleUser.setGroup(groups);
    				grouproleUserSet.add(grouproleUser);
    			  }
    			groups.setGroupUser(grouproleUserSet);
    			
    			}
    			//groups.getGroupUser().add(grouproleUser);
    			groupRole.setRoleUsers(grouproleUserSet);
    			
				Set<GroupRoleActionType> groupRoleActions = new HashSet<>();

    			for (ActionTypeRequest actions : groupRoles.getGroupRoleActionType()) {
    				
    				ActionType newAction = entityManager.find(ActionType.class, actions.getActionTypeId());
    				
    				GroupRoleActionType roleAction	=(GroupRoleActionType) 
    						entityManager.createQuery("select g from GroupRoleActionType g where g.actionType.actionTypeId=:actionTypeId and g.groupRole.groupRoleId=:groupRoleId").
    						setParameter("groupRoleId",groupRoles.getRoleId()).
    						setParameter("actionTypeId", actions.getActionTypeId()).
    						getSingleResult();
    				
					if (roleAction == null) {
						roleAction = new GroupRoleActionType();
    				}
					roleAction.setGroupRole(groupRole);
					roleAction.setActionType(newAction);
					
	    		//	entityManager.merge(roleAction);

					groupRoleActions.add(roleAction);
			
				}
    			groupRole.setGroupRolesActionType(groupRoleActions);
    			groupRolesSet.add(groupRole);
    			
    		//	entityManager.merge(groupRole);
    		}
    	
    		groups.setGroupRoles(groupRolesSet);
    		
			entityManager.merge(groups);
		} catch (HibernateException e) {
			logger.error("Group not updated due to Hibernate-exception : " + e);
		}
    }*/

    @Override
    @Transactional
    public void deleteGroup(int groupId)
    {
    	try {
			String deleteQuery = "DELETE FROM groups where group_id="+ groupId;
		    entityManager.createNativeQuery(deleteQuery).executeUpdate();
		    
		/*    String sql = "DELETE FROM group_user where group_id="+ groupId;
		    entityManager.createNativeQuery(sql).executeUpdate();*/
		    entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Group details is not deleted : " + e);
			entityManager.flush();
			entityManager.close();
		}
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public List<ModuleType> getAllModule() {
		List<ModuleType> moduleTypeList = new ArrayList<>();

		List<Object[]> objList = entityManager
				.createNativeQuery("select t.module_type_id,t.moduletype from module_type t").getResultList();

		for (Object[] obj : objList) {
			ModuleType moduleType = new ModuleType();
			moduleType.setModuleTypeId((int) obj[0]);
			moduleType.setModuleType((String) obj[1]);
			moduleTypeList.add(moduleType);
		}

		return moduleTypeList;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Groups> getAllModuleByGroups(Integer moduleId,Integer loginId) {
		List<Groups> moduleByGroups=null;
		moduleByGroups=entityManager.createQuery("select m FROM Groups  m  JOIN m.groupUser  bps  where "
				    + "m.moduleType.moduleTypeId=:moduleId and bps.user.userId=:loginId").
				      setParameter("moduleId", moduleId).
				      setParameter("loginId", loginId).getResultList();
		return moduleByGroups;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupDropDown> getAllModuleByRole(Integer groupId, Integer moduleId) {
	
		List<Object[]> moduleByRoles= 	(List<Object[]>)entityManager.createNativeQuery("select r.role,r.group_role_id FROM group_role r join groups g on g.group_id = r.group_id "
				+ "where r.group_id=:groupId and g.module_type_id=:moduleId").
				setParameter("groupId", groupId).
				setParameter("moduleId", moduleId).
				getResultList();
		
		List<MyGroupDropDown> groupName = new ArrayList<>();
		
		for (Object[] groupRoles : moduleByRoles) {
			
			StringBuilder str = new StringBuilder("["); 
			int count = 0 ;
			
			List<Object> actions = (List<Object> )entityManager.createNativeQuery("SELECT name FROM action_type  a "+
					"join group_role_has_action_type g on a.action_type_id=g.action_type_id " +
					"where g.group_role_id=:roleId").
					 setParameter("roleId", (int)groupRoles[1]).
				     getResultList();

			
			for (Object groupRoleActionType : actions) {
				
				if(count !=0){
					str.append(",");
				}
				
				str.append((String)groupRoleActionType);
				count++;
			}
			str.append("]");
			MyGroupDropDown down = new MyGroupDropDown((String)groupRoles[0]+ " " + str, (int)groupRoles[1]);
			groupName.add(down);
		}
		
		return groupName;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupDetails> getMyGroupDetailsByUserId(Integer userId,Integer pageNo,Integer pageSize) {

		
		List<GrouproleUser> moduleByGroups = null;
		try {
			moduleByGroups = entityManager.createQuery("select gru FROM GrouproleUser gru JOIN gru.user  u"
					+ " where gru.createdBy.userId=:userId")
					.setParameter("userId", userId)
					.setFirstResult(pageNo)
			        .setMaxResults(pageSize)
			        .getResultList();
		} catch (Exception e) {
			logger.error("Error in getMyGroupDetailsByUserId method {} ",e);
			entityManager.flush();
			entityManager.close();
		}
		
		List <MyGroupDetails>  groupDetails = new ArrayList<>();

		if(moduleByGroups!= null ){
			mapToResponseDTO(moduleByGroups, groupDetails);
		}
		
		return groupDetails;
	}

	private void mapToResponseDTO(List<GrouproleUser> moduleByGroups, List<MyGroupDetails> groupDetails) {
		for(GrouproleUser  group: moduleByGroups){
			
			MyGroupDetails myGroupDetails = new MyGroupDetails();
			myGroupDetails.setUserId(group.getUser().getUserId());
			myGroupDetails.setUserName(group.getUser().getUserName());
			myGroupDetails.setEmailId(group.getUser().getEmailId());
			if(group.getRoleUser() != null && group.getRoleUser().getGroup()!=null){
				myGroupDetails.setUserGroupName(group.getRoleUser().getGroup().getGroupName());
			}
			if(group.getRoleUser()!= null && group.getRoleUser().getGroupRoleName()!=null){
				myGroupDetails.setRoleName(group.getRoleUser().getGroupRoleName());
			}
			if(group.getRoleUser()!= null &&
					 group.getRoleUser().getGroup()!=null && group.getRoleUser().getGroup().getModuleType().getModuleType()!=null){
				myGroupDetails.setModuleName(group.getRoleUser().getGroup().getModuleType().getModuleType());
			}
			
			groupDetails.add(myGroupDetails);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupDropDown> getAllGroupByModuleId(Integer moduleId, Integer loginId, boolean isadmin) {
 
		StringBuilder sb = new StringBuilder("SELECT distinct g.name,g.group_id FROM group_user as gu "
				+ " join   groups g on g.group_id= gu.group_id "
				+ " join   group_role  gr on gr.group_role_id = gu.group_role_id "
				+ " where gu.user_id=:loginId and g.module_type_id=:moduleId and g.active=1 ");
		/*if(isadmin){
			sb.append(" and role=:role ");
		}*/
		
		List<Object[]> moduleByGroups = null ;
		Query q = entityManager.createNativeQuery(sb.toString())
					 .setParameter("moduleId", moduleId)
				     .setParameter("loginId", loginId);
			/*if(isadmin){
			  q.setParameter("role", "Group Admin");
			}*/
			moduleByGroups = q.getResultList();
		 
		
	    List<MyGroupDropDown> groupName = new ArrayList<>();
		
		for (Object[] mList : moduleByGroups) {
			MyGroupDropDown down = new MyGroupDropDown((String)mList[0],(int) mList[1]);
			groupName.add(down);
		}

		return groupName;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupDetails> getMyGroupDetailsByUserId(MyGroupHome myGroupHome, String searchCriteria) {
		List<Object[]> objectList = null;
		String role="";
		StringBuilder sb = new StringBuilder();
		if (searchCriteria != null) {
			sb.append(
					" AND (CAST(u.ad_user_id AS CHAR(50)) LIKE :searchCriteria OR LOWER(u.user_name) LIKE :searchCriteria OR LOWER(u.email_id) LIKE :searchCriteria OR LOWER(gr.role) LIKE :searchCriteria) ");
		}
		String sort = "";
		if (!StringUtils.isEmpty(myGroupHome.getSortBy())) {
			sort = " ORDER BY " + myGroupHome.getSortBy() + " " + myGroupHome.getOrderBy();
		}
		try {
			if(myGroupHome.getModuleId() == 0 && myGroupHome.getGroupId() == 0 &&  myGroupHome.getRoleId() == 0 ){
				Query query = entityManager.createNativeQuery(
						"SELECT distinct u.ad_user_id,u.user_name,u.email_id,gu.group_user_id,g.name,gr.role"
								+ ",GROUP_CONCAT(distinct at.name order by at.name, '')" + " FROM   group_user as gu"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   user u on u.user_id = gu.user_id "
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " join   group_role_has_action_type  grat on grat.group_role_id = gu.group_role_id"
								+ " join   action_type at on at.action_type_id=grat.action_type_id "
								+ " where g.group_id in (select group_id from group_user where user_id = :userId)"
								+ sb.toString()
								+ " group by gu.group_user_id " + sort);
							query.setParameter("userId", myGroupHome.getAdminUser());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				objectList = query.setFirstResult(myGroupHome.getPageSize() * (myGroupHome.getPageNo() - 1))
						.setMaxResults(myGroupHome.getPageSize()).getResultList();
				
			}
			if (myGroupHome.getRoleId() != 0) {
				Query query = entityManager.createNativeQuery(
						"SELECT distinct u.ad_user_id,u.user_name,u.email_id,gu.group_user_id,g.name,gr.role"
								+ ",GROUP_CONCAT(at.name order by at.name, '')" + " FROM   group_user as gu"
								+ " join   user u on u.user_id= gu.user_id"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   module_type m on m.Module_Type_Id= g.module_type_id"
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " join   group_role_has_action_type  grat on grat.group_role_id = gu.group_role_id"
								+ " join   action_type at on at.action_type_id=grat.action_type_id"
								+ " where  g.group_id=:groupId and m.Module_Type_Id=:moduleTyId and gr.group_role_id=:roleId"
								+ sb.toString() + " group by u.ad_user_id " + sort)
						.setParameter("groupId", myGroupHome.getGroupId())
						.setParameter("moduleTyId", myGroupHome.getModuleId())
						.setParameter("roleId", myGroupHome.getRoleId());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				objectList = query.setFirstResult(myGroupHome.getPageSize() * (myGroupHome.getPageNo() - 1))
						.setMaxResults(myGroupHome.getPageSize()).getResultList();
			} if (myGroupHome.getRoleId() == 0 && myGroupHome.getModuleId() != 0 && myGroupHome.getGroupId() !=0) {
				Query query = entityManager.createNativeQuery(
						"SELECT distinct u.ad_user_id,u.user_name,u.email_id,gu.group_user_id,g.name,gr.role"
								+ ",GROUP_CONCAT(distinct at.name order by at.name, '')" + " FROM   group_user as gu"
								+ " join   user u on u.user_id= gu.user_id"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   module_type m on m.Module_Type_Id= g.module_type_id"
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " join   group_role_has_action_type  grat on grat.group_role_id = gu.group_role_id"
								+ " join   action_type at on at.action_type_id=grat.action_type_id"
								+ " where  g.group_id=:groupId and m.Module_Type_Id=:moduleTyId"
								+ sb.toString()
								+ " group by u.ad_user_id " + sort);
							query.setParameter("groupId", myGroupHome.getGroupId());
							query.setParameter("moduleTyId", myGroupHome.getModuleId());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				objectList = query.setFirstResult(myGroupHome.getPageSize() * (myGroupHome.getPageNo() - 1))
						.setMaxResults(myGroupHome.getPageSize()).getResultList();
			}
			
			if(myGroupHome.getGroupId() !=0){
				Query qu = entityManager.createNativeQuery("select role from group_role r join group_user u on r.group_role_id = u.group_role_id "
						+ " where u.group_id = :groupId and u.user_id = :userId")
						.setParameter("groupId", myGroupHome.getGroupId())
						.setParameter("userId", myGroupHome.getAdminUser());
				role = (String) qu.getSingleResult();	
			}
			
			
		} catch (Exception e) {
			logger.error("Error in getMyGroupDetailsByUserId method {} ", e);
			entityManager.flush();
			entityManager.close();
		}

		List<MyGroupDetails> groupDetails = new ArrayList<>();

		mapToResponseModel(objectList, groupDetails, role);

		sortMyGroup(myGroupHome, groupDetails);
		return groupDetails;
	}

	private void mapToResponseModel(List<Object[]> objectList, List<MyGroupDetails> groupDetails, String role) {
		for (Object[] objArray : objectList) {
			MyGroupDetails myGroupDetails = new MyGroupDetails();
			myGroupDetails.setAdUserId((String) objArray[0]);
			if (objArray[1] != null)
				myGroupDetails.setUserName((String) objArray[1]);
			if (objArray[2] != null)
				myGroupDetails.setEmailId((String) objArray[2]);
			if (objArray[3] != null)
				myGroupDetails.setGroupUserId((int) objArray[3]);
			myGroupDetails.setUserGroupName((String) objArray[4]);
			myGroupDetails.setRoleName((String) objArray[5] + "[" + (String) objArray[6] + "]");
			myGroupDetails.setUserRolePerGroup(role);
			groupDetails.add(myGroupDetails);
		}
	}

	
	
	/**
	 * @param myGroupHome
	 * @param groupDetails
	 */
	private void sortMyGroup(MyGroupHome myGroupHome, List<MyGroupDetails> groupDetails) {
		if (!StringUtils.isEmpty(myGroupHome.getSortBy()) && myGroupHome.getSortBy().equals("userId")  ) {
			if(myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc")) {
				groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserId).reversed());
			} else{
				groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserId));
			}
		}
		
		
		if (myGroupHome.getSortBy()!=null && myGroupHome.getSortBy().equals("groupName"))  {
			if ( myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc")){
					groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserGroupName).reversed());
		    } else{
			      groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserGroupName));
		  }
	    }
		
		if (myGroupHome.getSortBy()!=null && myGroupHome.getSortBy().equals("roleName")){  
				
			if(	myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc")) {
			  groupDetails.sort(Comparator.comparing(MyGroupDetails::getRoleName).reversed());
		    } else{
			  groupDetails.sort(Comparator.comparing(MyGroupDetails::getRoleName));
		   }
		}
		
		if (myGroupHome.getSortBy()!=null && myGroupHome.getSortBy().equals("moduleName")) {
				if (myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc") ) {
			groupDetails.sort(Comparator.comparing(MyGroupDetails::getModuleName).reversed());
		      } else{
			groupDetails.sort(Comparator.comparing(MyGroupDetails::getModuleName));
		     }
	   }
	 	
		if (myGroupHome.getSortBy()!=null && myGroupHome.getSortBy().equals("emailId") ){ 
			if(	myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc") ) {
			  groupDetails.sort(Comparator.comparing(MyGroupDetails::getEmailId).reversed());
		    } else{
			 groupDetails.sort(Comparator.comparing(MyGroupDetails::getEmailId));
		    }
		}
		
		
		if (myGroupHome.getSortBy()!=null && myGroupHome.getSortBy().equals("userName")) {
				if(myGroupHome.getOrderBy()!=null && myGroupHome.getOrderBy().equals("desc") ) {
			groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserName).reversed());
				} else{
			groupDetails.sort(Comparator.comparing(MyGroupDetails::getUserName));
				}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUserDetailsNotInGroup(MyGroupHome myGroupHome, String searchCriteria) {
		List<Integer> moduleByGroups = null;
		List<User> u = null;
		try {
			moduleByGroups = entityManager
					.createQuery("select gru.user.userId FROM GrouproleUser gru JOIN gru.group  u"
							+ " where gru.group.groupId=:groupId and gru.group.moduleType.moduleTypeId=:moduleTyId ")
					.setParameter("groupId", myGroupHome.getGroupId())
					.setParameter("moduleTyId", myGroupHome.getModuleId())
					.getResultList();

			StringBuilder sb = new StringBuilder();
			if (!StringUtils.isEmpty(searchCriteria)) {
				sb.append(
						" AND (CAST(u.adUserId AS string) LIKE :searchC OR LOWER(u.emailId) LIKE :searchC OR LOWER(u.userName) LIKE :searchC OR u.mobileNumber LIKE :searchC OR LOWER(u.remark) LIKE :searchC) ");
			}
			
			String sort = "";
			if (!StringUtils.isEmpty(myGroupHome.getSortBy())) {
				sort = " ORDER BY " + myGroupHome.getSortBy() + " " + myGroupHome.getOrderBy();
			}
			
			Query query = null;
			if (CollectionUtils.isEmpty(moduleByGroups)) {
				query = entityManager.createQuery("SELECT u FROM User u where u.active=1 " + sb.toString() + sort)
						.setFirstResult(myGroupHome.getPageSize() * (myGroupHome.getPageNo() - 1))
						.setMaxResults(myGroupHome.getPageSize());
			} else {
				query = entityManager
						.createQuery("SELECT u FROM User u where u.userId NOT IN:ids and u.active=1 " + sb.toString() + sort)
						.setParameter("ids", moduleByGroups)
						.setFirstResult(myGroupHome.getPageSize() * (myGroupHome.getPageNo() - 1))
						.setMaxResults(myGroupHome.getPageSize());
			}

			if (!StringUtils.isEmpty(searchCriteria)) {
				query.setParameter("searchC", "%" + searchCriteria + "%");
			}
			u = query.getResultList();

		} catch (Exception e) {
			logger.error("Error in getMyGroupDetailsByUserId method {} ", e);
			entityManager.flush();
			entityManager.close();
		}
		return u;
	}

	@Override
	public long getAllUsersCountNotInGroup(MyGroupHome myGroupHome, String searchCriteria) {

		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(searchCriteria)) {
			sb.append(
					" AND (CAST(uu.ad_user_id AS char) LIKE :searchC OR LOWER(uu.email_id) LIKE :searchC OR LOWER(uu.user_name) LIKE :searchC OR uu.mobile_number LIKE :searchC OR LOWER(uu.remark) LIKE :searchC) ");
		}

		Query query = entityManager.createNativeQuery(
				"SELECT count(uu.user_id) FROM user uu where uu.active=1 and uu.user_id NOT IN (SELECT distinct user_id FROM  group_user  u"
						+ " join groups g on g.group_id=u.group_id "
						+ " join module_type module on g.module_type_id = module.module_type_id "
						+ " where u.group_id=:groupId and module.module_type_id=:moduleId )" + sb.toString())
				.setParameter("moduleId", myGroupHome.getModuleId()).setParameter("groupId", myGroupHome.getGroupId());
		if (!StringUtils.isEmpty(searchCriteria)) {
			query.setParameter("searchC", "%" + searchCriteria + "%");
		}
		BigInteger count = (BigInteger) query.getSingleResult();
		return count.longValue();
	}
	
/*	@Override
	public long getAllUsersCountNotInGroup(MyGroupHome myGroupHome) {
	
		BigInteger count = (BigInteger) entityManager.createNativeQuery("SELECT  count(distinct u.user_id) FROM  group_user  u"
				+ " join groups g on g.group_id=u.group_id "
				+ " join module_type module on g.module_type_id = module.module_type_id "
				+ " where u.group_id!=:groupId and module.module_type_id!=:moduleId ")
				    .setParameter("moduleId", myGroupHome.getModuleId())
					.setParameter("groupId", myGroupHome.getGroupId())
					.getSingleResult();
		
		return count.longValue();
	}*/
	
	@Override
	public Boolean groupNameExists(int moduleId, String groupName) {
		Groups groups = (Groups) entityManager.createQuery("select gru  FROM Groups gru"
						+ " where gru.groupName=:groupName and gru.moduleType.moduleTypeId=:moduleTyId ")
						.setParameter("moduleTyId", moduleId)
						.setParameter("groupName", groupName)
				        .getSingleResult();
	 	return groups == null ? Boolean.FALSE : Boolean.TRUE ;
	}



	@Override
	public BigInteger getMyGroupDetailsCountByUserId(MyGroupHome myGroupHome, String searchCriteria) {

		BigInteger moduleByGroups = BigInteger.ZERO;
		StringBuilder sb = new StringBuilder();
		if (searchCriteria != null) {
			sb.append(
					" AND (CAST(u.ad_user_id AS CHAR(50)) LIKE :searchCriteria OR LOWER(u.user_name) LIKE :searchCriteria OR LOWER(u.email_id) LIKE :searchCriteria OR LOWER(gr.role) LIKE :searchCriteria) ");
		}
		try {
			if(myGroupHome.getModuleId() == 0 && myGroupHome.getGroupId() == 0 &&  myGroupHome.getRoleId() == 0 ){
				Query query = entityManager.createNativeQuery(
						"SELECT count(*)" + " FROM   group_user as gu"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   user u on u.user_id = gu.user_id "
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " where g.group_id in (select group_id from group_user where user_id = :userId) "
								+ sb.toString());
							query.setParameter("userId", myGroupHome.getAdminUser());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				moduleByGroups = (BigInteger) query.getSingleResult();
				
			}
			if (myGroupHome.getRoleId() != 0) {
				Query query = entityManager.createNativeQuery("SELECT count(*)" + " FROM   group_user as gu"
								+ " join   user u on u.user_id= gu.user_id"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   module_type m on m.Module_Type_Id= g.module_type_id"
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " where  g.group_id=:groupId and m.Module_Type_Id=:moduleTyId and gr.group_role_id=:roleId "
								+ sb.toString())
						.setParameter("groupId", myGroupHome.getGroupId())
						.setParameter("moduleTyId", myGroupHome.getModuleId())
						.setParameter("roleId", myGroupHome.getRoleId());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				moduleByGroups = (BigInteger) query.getSingleResult();
			} if (myGroupHome.getRoleId() == 0 && myGroupHome.getModuleId() != 0 && myGroupHome.getGroupId() !=0) {
				Query query = entityManager
						.createNativeQuery("SELECT count(*)" + " FROM   group_user as gu"
								+ " join   user u on u.user_id= gu.user_id"
								+ " join   groups g on g.group_id= gu.group_id"
								+ " join   module_type m on m.Module_Type_Id= g.module_type_id"
								+ " join   group_role  gr on gr.group_role_id = gu.group_role_id"
								+ " where  g.group_id=:groupId and m.Module_Type_Id=:moduleTyId "
								+ sb.toString())
						.setParameter("groupId", myGroupHome.getGroupId())
						.setParameter("moduleTyId", myGroupHome.getModuleId());
				if (searchCriteria != null) {
					query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				}
				moduleByGroups = (BigInteger) query.getSingleResult();
			}
		} catch (Exception e) {
			logger.error("Error in getMyGroupDetailsByUserId method {} ", e);
			entityManager.flush();
			entityManager.close();
		}
		return moduleByGroups;
	}

	@Override
	public long getAllGroupsCount(String searchC, GroupSearchCriteria groupSearchCriteria) {
		StringBuilder searchCriteria = new StringBuilder();

		List<String> conditionList = new ArrayList<>();
		if (groupSearchCriteria.getModuleName() != null) {
			if ((!groupSearchCriteria.getModuleName().equalsIgnoreCase("All"))
					&& ((groupSearchCriteria.getModuleName().equalsIgnoreCase("Contract")
							|| "contract".contains(groupSearchCriteria.getModuleName().toLowerCase()))
							|| (groupSearchCriteria.getModuleName().equalsIgnoreCase("Assets")
									|| "assets".contains(groupSearchCriteria.getModuleName().toLowerCase()))
							|| (groupSearchCriteria.getModuleName().equalsIgnoreCase("Staff")
									|| "staff".contains(groupSearchCriteria.getModuleName().toLowerCase()))))
				conditionList.add(" LOWER(g.moduleType.moduleType) LIKE :module ");
		}
		if (groupSearchCriteria.getGroupName() != null) {
			conditionList.add(" LOWER(g.groupName) LIKE :groupName ");
		}
		if (groupSearchCriteria.getActive() != null) {
			if (groupSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" g.active = true ");
			} else if (groupSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" g.active = false ");
			} else if (groupSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (g.active = true OR g.active = false) ");
			}
		}
		if ((searchC) != null) {
			if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
				searchCriteria.append(" g.active = 1");
			else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
				searchCriteria.append(" g.active = 0");
			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			conditionList.add(" (" + searchCriteria.toString()
					+ "LOWER(g.groupName) LIKE :searchC OR  LOWER(g.moduleType.moduleType) LIKE :searchC OR LOWER(g.description) LIKE :searchC) ");
		} else
			searchCriteria.append("");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < conditionList.size(); i++) {
			if (i == 0)
				sb.append(" WHERE ");
			sb.append(conditionList.get(i));
			if (i != (conditionList.size() - 1))
				sb.append(" AND ");
		}
		long count = 0;
		try {

			logger.info("inside the getAllGroupsCount() method");
			Query query = entityManager.createQuery("SELECT COUNT(g) FROM Groups g" + sb.toString());
			if (searchC != null)
				query.setParameter("searchC", "%" + searchC + "%");
			if (groupSearchCriteria.getModuleName() != null && !(groupSearchCriteria.getModuleName().equalsIgnoreCase("All"))) {
				query.setParameter("module", "%" + groupSearchCriteria.getModuleName() + "%");
			}
			if (groupSearchCriteria.getGroupName() != null) {
				query.setParameter("groupName", groupSearchCriteria.getGroupName());
			}
			count = (long) query.getSingleResult();
		} catch (HibernateException e) {
			logger.error("No group found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return count;
	}

	@Override
	public void updateGroup(Groups group) {
		entityManager.merge(group);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GrouproleUser> getGroupUsersByGroupId(int groupId) {
		List<GrouproleUser> grouproleUsers = null;
		try {
			logger.info("fetching group details of groupId :" + groupId);
			grouproleUsers = (List<GrouproleUser>) entityManager
					.createQuery("SELECT gu FROM GrouproleUser gu where gu.group.groupId = :groupId")
					.setParameter("groupId", groupId).getResultList();
		} catch (HibernateException e) {
			logger.error("No groupUser fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return grouproleUsers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GrouproleUser> getUserByGroupRole(int groupId, int groupRoleId) {
		List<GrouproleUser> grouproleUsers = null;
		try {
			logger.info("fetching group details of groupId :" + groupId);
			Groups group = groupService.getGroupByID(groupId);
			GroupRoles groupRole = roleService.getGroupRoleById(groupRoleId);
			grouproleUsers = entityManager.createQuery("select u from GrouproleUser u where u.group=:group and u.roleUser= :roleUser")
					.setParameter("group", group)
					.setParameter("roleUser", groupRole)
					.getResultList();
		} catch (HibernateException e) {
			logger.error("No groupUser fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return grouproleUsers;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Object> getGroupUserDetailByGroupId(int groupId, int moduleTypeId) {
		List<Object> result = null;
		try {
			logger.info("fetching group details of groupId :" + groupId);

			result = entityManager.createNativeQuery(
					"SELECT u.user_id, u.user_name, u.email_id, g.name, gr.role, m.moduletype, gr.group_role_id, gu.group_user_id,u.ad_user_id "
							+ "FROM group_user gu " + "INNER JOIN groups g ON gu.group_id = g.group_id "
							+ "INNER JOIN user u ON u.user_id = gu.user_id "
							+ "INNER JOIN group_role gr ON gr.group_role_id = gu.group_role_id "
							+ "INNER JOIN module_type m ON m.module_type_id = g.module_type_id "
							+ "AND gu.group_id = :groupId " + "AND m.module_type_id = :moduleTypeId and u.active=1")
					.setParameter("groupId", groupId).setParameter("moduleTypeId", moduleTypeId).getResultList();

		} catch (HibernateException e) {
			logger.error("No groupUser fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return result;
	}
	
	/**
	 * for ReviewerList #321
	 * @param groupId
	 * @param moduleTypeId
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Object> getGroupUserDetailByGroupId(Set<Integer> groupId, int moduleTypeId) {
		List<Object> result = null;
		try {
			logger.info("fetching group details of groupId :" + groupId);

			result = entityManager.createNativeQuery(
					"SELECT u.user_id, u.user_name, u.email_id, g.name, gr.role, m.moduletype, gr.group_role_id, gu.group_user_id,u.ad_user_id "
							+ "FROM group_user gu " + "INNER JOIN groups g ON gu.group_id = g.group_id "
							+ "INNER JOIN user u ON u.user_id = gu.user_id "
							+ "INNER JOIN group_role gr ON gr.group_role_id = gu.group_role_id "
							+ "INNER JOIN module_type m ON m.module_type_id = g.module_type_id "
							+ "AND gu.group_id in (:groupId) " + "AND m.module_type_id = :moduleTypeId and u.active=1")
					.setParameter("groupId", groupId).setParameter("moduleTypeId", moduleTypeId).getResultList();

		} catch (HibernateException e) {
			logger.error("No groupUser fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getActionsByGroupRoleId(int groupRoleId) {
		List<String> result = null;
		try {
			logger.info("fetching Actions of groupRoleId :" + groupRoleId);

			result = (List<String>) entityManager
					.createNativeQuery("SELECT DISTINCT at.name from group_role_has_action_type grhat "
							+ "INNER JOIN action_type at ON grhat.action_type_id = at.action_type_id "
							+ "AND grhat.group_role_id = :groupRoleId")
					.setParameter("groupRoleId", groupRoleId).getResultList();

		} catch (HibernateException e) {
			logger.error("No Actions fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupRoleDetails> getMyGroupsRoleDetails(int userId) {
		List<Object[]> result = null;
		try {
			logger.info("fetching group of user :" + userId);

			result = (List<Object[]> ) entityManager
					.createNativeQuery("SELECT distinct g.name,gr.role,m.ModuleType"
									+  " FROM  group_user as gu"
									+  " join  groups g on g.group_id= gu.group_id"
									+  " join  module_type m on m.Module_Type_Id= g.module_type_id"
									+  " join  group_role  gr on gr.group_role_id = gu.group_role_id"
									+  " where gu.user_id=:userId")
					.setParameter("userId", userId).getResultList();

		} catch (HibernateException e) {
			logger.error("No Actions fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		
		List<MyGroupRoleDetails> myGroupRoleDetailsList = new ArrayList<>();
		for (Object[] objArray : result) {
			MyGroupRoleDetails myGroupRoleDetails = new MyGroupRoleDetails();
			myGroupRoleDetails.setGroupName((String) objArray[0]);
			myGroupRoleDetails.setRoleName((String) objArray[1]);
			myGroupRoleDetails.setModuleName((String) objArray[2]);
			myGroupRoleDetailsList.add(myGroupRoleDetails);
		}
		return myGroupRoleDetailsList;
	}

	@Override
	public boolean checkforActiveReminder(Integer id) {
		BigInteger count = new BigInteger("-1");
		try{
		Groups g = getGroupById(id);
		if (g != null && !g.getActive()) {
			return Boolean.FALSE;
		}
		count = (BigInteger) entityManager
				.createNativeQuery(
						"select count(r.reminder_id) from reminder r where r.user_group_id=:groupId and r.active=1")
				.setParameter("groupId", id).getSingleResult();
		if (count == null)
			return Boolean.FALSE;
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Error while checking for active reminder: "+e);
		}
		return count.longValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getGroupRolesAction(int userId, Set<Integer> groupId) {
		
		/*List<String>  strings = (List<String>)entityManager.createNativeQuery("SELECT name FROM action_type where "
				+ "action_type_id in(SELECT action_type_id FROM  group_role_has_action_type "
				+ "where group_role_id in(SELECT group_role_id FROM  group_user where user_id=:userId  and group_Id=:groupId))")
	 			    .setParameter("userId", userId)
	 				.setParameter("groupId", groupId)
	 				.getResultList();*/
		List<Object[]> groupAction = entityManager.createNativeQuery("select a.name, gu.group_id from action_type a "
				+ "inner join group_role_has_action_type  gra on gra.action_type_id = a.action_type_id "
				+ "inner join group_user  gu on gu.group_role_id = gra.group_role_id "
				+ "where gu.user_id=:userId and gu.group_Id in (:groupId)")
				.setParameter("userId", userId)
				.setParameter("groupId", groupId)
				.getResultList();
		
		return groupAction;
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getGroupRolesAction(int userId, int groupId) {
		
		List<String>  strings = (List<String>)entityManager.createNativeQuery("SELECT name FROM action_type where "
				+ "action_type_id in(SELECT action_type_id FROM  group_role_has_action_type "
				+ "where group_role_id in(SELECT group_role_id FROM  group_user where user_id=:userId  and group_Id=:groupId))")
	 			    .setParameter("userId", userId)
	 				.setParameter("groupId", groupId)
	 				.getResultList();
		
		return strings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MyGroupRoleDetails> groupByName(String groupName) {
		List<MyGroupRoleDetails> grpName = new ArrayList<>();
		List<String> obj = null;
		MyGroupRoleDetails mygrpdetails = null;
		try{
			String whereCond = " lower(g.name) like '%"+groupName+"%'";
			obj = entityManager.createNativeQuery("select g.name from groups g where "+whereCond)
					.getResultList();
			for(String object: obj){
				mygrpdetails = new MyGroupRoleDetails();
				mygrpdetails.setGroupName(object);
				grpName.add(mygrpdetails);
			}
		}catch(Exception e){
			logger.error("error fetching group name: "+e);
		}
		return grpName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ModuleType> getModuleNameByUser(int userId) {
		List<ModuleType> moduleName= new ArrayList<>();
		try{
			List<Object[]> obj = entityManager.createNativeQuery("select distinct(m.moduletype),m.Module_Type_Id from module_type m, groups g, group_user gu,  group_role gr "
					+ "where g.group_id = gu.group_id and m.Module_Type_Id=g.module_type_id and gr.group_role_id = gu.group_role_id and gu.user_id=:userId ")
					//+ " and gr.role like 'Group%'")
					.setParameter("userId", userId)
					.getResultList();
			for(Object[] object:obj){
				ModuleType mt = new ModuleType();
				mt.setModuleTypeId((int)object[1]);
				mt.setModuleType((String)object[0]);
				moduleName.add(mt);
			}
		}catch(HibernateException e){
			logger.error("Error fetching module name: "+e);
			entityManager.flush();
			entityManager.close();
		}
		return moduleName;
	}

	@Override
	public boolean isAdmin(int userId, int moduleId) {
		try{
			@SuppressWarnings("unchecked")
			List<String> obj = entityManager.createNativeQuery("select distinct(gr.role), g.module_type_id from group_role gr right join group_user gu "
					+ "on gr.group_role_id = gu.group_role_id right join groups g on gu.group_id = g.group_id "
					+ "where gu.user_id=:userId and g.module_type_id=:moduleId ")
					//+ " and gr.role like '%Group%'")
					.setParameter("userId", userId)
					.setParameter("moduleId", moduleId)
					.getResultList();
			if(obj.isEmpty())
				return false;
			else
				return true;
		}catch(HibernateException e){
			logger.error("Error while getting if user isAdmin(): "+e);
			return false;
		}catch(Exception e){
			logger.error("Error while getting if user isAdmin(): "+e);
			return false;
		}
	}
	
	
}

