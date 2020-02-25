package com.reminder.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.ActionTypeDAO;
import com.reminder.dao.GroupDAO;
import com.reminder.dao.GroupUserDAO;
import com.reminder.model.ActionType;
import com.reminder.model.GroupRoleActionType;
import com.reminder.model.GroupRoles;
import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.ResponseModel;
import com.reminder.model.User;
import com.reminder.request.model.ActionTypeRequest;
import com.reminder.request.model.AssignRoleRequest;
import com.reminder.request.model.GroupRequest;
import com.reminder.request.model.GroupRolesRequest;
import com.reminder.request.model.GroupSearchCriteria;
import com.reminder.request.model.GroupUserRequest;
import com.reminder.request.model.MyGroupHome;
import com.reminder.response.model.GroupResponse;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.response.model.MyGroupRoleDetails;
import com.reminder.response.model.UserResponse;
import com.reminder.utils.DateTimeUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class GroupServiceImpl implements GroupService {

	private Logger logger = Logger.getLogger(GroupServiceImpl.class);

	
	@Autowired
	private GroupDAO groupDAO;

	@Autowired
	private GroupUserDAO groupUserDAO;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	GroupRoleActionTypeService groupRoleActionTypeService;
	
	@Autowired
	ActionTypeDAO actionDAO;
	
	@Autowired
	UserService userService;
	
	@Override
	public Groups createGroup( Groups group) {
		try {
			groupDAO.createGroup(group);
		} catch (Exception e) {
			logger.error("createGroup(group=" + group + ") - Group not created due to error", e);
		}
		return group;
	}

	@Override
	public GroupResponse getGroupById(int userId, int groupId) {
		GroupResponse response = new GroupResponse();
		Groups group = groupDAO.getGroupById(groupId);
		if (group == null) {
			return response;
		}
		List<String> actions = groupDAO.getGroupRolesAction(userId, groupId);
		response.setActions(actions);
		response.setGroupId(group.getGroupId());
		response.setGroupName(group.getGroupName());
		response.setDescription(group.getDescription());
		response.setActive(group.getActive());
		response.setCreatedAt(group.getCreatedAt());
		response.setCreatedBy(group.getCreatedBy());
		if (group.getLastModifiedBy() != null) {
			UserResponse userResponse = new UserResponse();
			BeanUtils.copyProperties(group.getLastModifiedBy(), userResponse);
			response.setLastModifiedBy(userResponse);
		}
		response.setModuleType(group.getModuleType());
		response.setLastModifiedAt(group.getLastModifiedAt());
		return response;
	}

	/*
	 * @Override public List<Group1> getAllGroups(){ return
	 * groupDAO.getAllGroups(); }
	 */

	@Override
	public List<GroupResponse> getAllGroups(String sort_by, String order, String searchCriteria, Integer limit,
			Integer page_no, GroupSearchCriteria groupSearchCriteria) {
		List<GroupResponse> groupResponses = new ArrayList<>();
		List<Object[]> groups = groupDAO.getAllGroups(sort_by, order, searchCriteria, limit, page_no, groupSearchCriteria);
		for(Object[] group: groups) {
			GroupResponse groupResponse = new GroupResponse();
			groupResponse.setActive((boolean)group[3]);
			//groupResponse.setCreatedAt(group.getCreatedAt());
			//groupResponse.setCreatedBy(group.getCreatedBy());
			groupResponse.setDescription((String)group[2]);
			groupResponse.setGroupId((int)group[1]);
			groupResponse.setGroupName((String)group[0]);
/*			groupResponse.setGroupRoles(group.getGroupRoles());
			groupResponse.setGroupUser(group.getGroupUser());*/
			//groupResponse.setLastModifiedAt(group.getLastModifiedAt());
			//UserResponse response = new UserResponse();
			//BeanUtils.copyProperties(group.getLastModifiedBy(), response);
			//String name = userService.getUserName(group.getCreatedBy().getCreatedById());
			//response.setCreatedById(name);
			//response.setLastModifiedById(userService.getUserName(group.getLastModifiedBy().getLastModifiedById()));
		//	groupResponse.setLastModifiedBy(response);
			groupResponse.setModuleType(new ModuleType((int)group[5],(String)group[4]));
			groupResponse.setGroupUsers((String)group[6]);
			groupResponses.add(groupResponse);
		}
		return groupResponses;
	}

	@Override
	public void updateGroup(Groups group ,GroupRequest groupRequest ) {
		groupDAO.updateGroup(group ,groupRequest);
	}

	@Override
	public void deleteGroup(int groupId) {
		groupDAO.deleteGroup(groupId);
	}
	@Override
	public Groups getGroupByID(int groupId) {
		Groups group = groupDAO.getGroupById(groupId);
		return group;
	}

	@Override
	public List<ModuleType> getAllModule() {
		return groupDAO.getAllModule();
	}

	@Override
	public List<GroupResponse>  getAllModuleByGroups(Integer moduleId,Integer loginId) {
		List<Groups> groups = groupDAO.getAllModuleByGroups(moduleId,loginId);
		List<GroupResponse> getGroupResponses = new ArrayList<>();
		for (Groups groupResponse : groups) {
			GroupResponse getResponse = new GroupResponse();
			getResponse.setGroupId(groupResponse.getGroupId());
			getResponse.setGroupName(groupResponse.getGroupName());
			getResponse.setDescription(groupResponse.getDescription());
			getResponse.setActive(groupResponse.getActive());
			getResponse.setCreatedAt(groupResponse.getCreatedAt());
			getResponse.setCreatedBy(groupResponse.getCreatedBy());
			UserResponse response = new UserResponse();
			BeanUtils.copyProperties(groupResponse.getLastModifiedBy(), response);
			//String name = userService.getUserName(group.getCreatedBy().getCreatedById());
			//response.setCreatedById(name);
			//response.setLastModifiedById(userService.getUserName(group.getLastModifiedBy().getLastModifiedById()));
			getResponse.setLastModifiedBy(response);
			getResponse.setModuleType(groupResponse.getModuleType());
/*			getResponse.setGroupUser(groupResponse.getGroupUser());*/
			getGroupResponses.add(getResponse);

		}
		return getGroupResponses;
	}

	@Override
	public List<MyGroupDropDown> getAllModuleByRoles(Integer groupId, Integer moduleId) {
		return groupDAO.getAllModuleByRole(groupId, moduleId);
	}

	@Override
	public List<MyGroupDetails> getMyGroupDetailsByUserId(Integer userId ,Integer pageNo ,Integer pageSize) {
		return groupDAO.getMyGroupDetailsByUserId(userId,pageNo, pageSize);
	}

	@Override
	public void saveUserGroupUsersRole(AssignRoleRequest assignRoleRequest) {
		groupUserDAO.updateGroupUser(assignRoleRequest);
	}

	@Override
	public void deleteGroupUser(int id) {
		groupUserDAO.deleteGroupUser(id);	
	}

	@Override
	public List<MyGroupDropDown> getAllGroupByModuleId(Integer moduleId, int userId,boolean isadmin) {
		return groupDAO.getAllGroupByModuleId(moduleId,userId,isadmin);
	}

	@Override
	public List<MyGroupDetails> getMyGroupDetailsByUserId(MyGroupHome myGroupHome, String searchCriteria) {
		return  groupDAO.getMyGroupDetailsByUserId(myGroupHome, searchCriteria);
	}
	
	@Override
	public List<User> getAllUserDetailsNotInGroup(MyGroupHome myGroupHome, String searchCriteria){
		return groupDAO.getAllUserDetailsNotInGroup(myGroupHome, searchCriteria);
	}

	@Override
	public Boolean groupNameExists(int moduleId, String groupName) {
		return groupDAO.groupNameExists( moduleId,groupName);
	}

	@Override
	public long getAllUsersCountNotInGroup(MyGroupHome myGroupHome, String searchCriteria) {
		return groupDAO.getAllUsersCountNotInGroup(myGroupHome, searchCriteria);
	}

	@Override
	public BigInteger getMyGroupDetailsCountByUserId(MyGroupHome myGroupHome, String searchCriteria) {
		return groupDAO.getMyGroupDetailsCountByUserId(myGroupHome, searchCriteria) ;
	}

	@Override
	public long getAllGroupsCount(String searchCriteria, GroupSearchCriteria groupSearchCriteria) {
		return groupDAO.getAllGroupsCount(searchCriteria, groupSearchCriteria);
	}

	@Override
	public void updateGroup(Groups group) {
		groupDAO.updateGroup( group);		
	}

	@Override
	public List<GrouproleUser> getAllUserByGroupRole(int groupId, int groupRoleId) {
		return groupDAO.getUserByGroupRole(groupId, groupRoleId);
	}
	
	@Override
	public ResponseModel updateGroupRole(GroupRolesRequest groupRolesRequest, User createdUser, int groupId,
			int groupRoleId) {
		ResponseModel response = new ResponseModel();
		GroupRoles groupRoles = roleService.getGroupRoleById(groupRoleId);
		groupRoles.setGroupRoleName(groupRolesRequest.getGroupRoleName());
		Set<ActionTypeRequest> actionTypeRequest = groupRolesRequest.getGroupRoleActionType();
		Set<GrouproleUser> groupRoleUserSet = new HashSet<>();
		GrouproleUser groupRoleUser = null;
		Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
		try {
			roleService.updateGroupRole(groupRoles);
			GroupRoles newGroupRole = roleService.findGroupRole(groupRoles.getGroupRoleId());
			for (GroupRoleActionType groupAction : newGroupRole.getGroupRolesActionType()) {
				groupRoleActionTypeService.deleteGroupActions(groupAction);
			}
			roleService.flushGroupRole();
			newGroupRole.getGroupRolesActionType().clear();
			for (ActionTypeRequest actions : actionTypeRequest) {
				ActionType newAction = actionDAO.find(actions.getActionTypeId());
				GroupRoleActionType roleAction = new GroupRoleActionType();
				roleAction.setActionType(newAction);
				roleAction.setGroupRole(newGroupRole);
				roleAction.setCreatedAt(DateTimeUtil.now());
				roleAction.setCreatedBy(createdUser);
				roleAction.setLastModifiedAt(DateTimeUtil.now());
				roleAction.setLastModifiedBy(createdUser);
				groupRoleActions.add(roleAction);
			}
			newGroupRole.setGroupRolesActionType(groupRoleActions);
			Groups group = getGroupByID(groupId);
			for (GroupUserRequest userRequest : groupRolesRequest.getGroupUsers()) {
				groupRoleUser = new GrouproleUser();
				User user = userService.getUserById(userRequest.getUserId());
				groupRoleUser.setUser(user);
				groupRoleUser.setCreatedAt(DateTimeUtil.now());
				groupRoleUser.setCreatedBy(createdUser);
				groupRoleUser.setLastModifiedAt(DateTimeUtil.now());
				groupRoleUser.setLastModifiedBy(createdUser);
				groupRoleUser.setGroup(group);
				groupRoleUser.setRoleUser(groupRoles);
				groupRoleUserSet.add(groupRoleUser);
			}
			newGroupRole.setRoleUsers(groupRoleUserSet);
			roleService.updateGroupRole(newGroupRole);
			groupDAO.updateGroup(group);
			response.setSuccess("Record Updated Successfully");
			response.setRecordNumber(newGroupRole.getGroupRoleId());
			response.setFailure("No Exception");
			return response;
		}

		catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("Error while Updating record" + e.getMessage());
			return response;
		}
	}

	@Override
	public List<MyGroupRoleDetails> getMyGroupsRoleDetails(int userId) {
		return groupDAO.getMyGroupsRoleDetails(userId);
	}

	@Override
	public boolean checkforActiveReminder(Integer id) {
		return groupDAO.checkforActiveReminder(id);
	}

	@Override
	public List<MyGroupRoleDetails> groupByName(String groupName) {
		return groupDAO.groupByName(groupName);
	}

	@Override
	public List<ModuleType> getModuleNameByUser(int userId) {
		return groupDAO.getModuleNameByUser(userId);
	}

	@Override
	public boolean isAdmin(int userId, int moduleId ) {
		return groupDAO.isAdmin(userId, moduleId);
	}
}
