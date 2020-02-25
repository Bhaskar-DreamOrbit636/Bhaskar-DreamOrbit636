package com.reminder.service;

import java.math.BigInteger;
import java.util.List;

import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.ResponseModel;
import com.reminder.model.User;
import com.reminder.request.model.AssignRoleRequest;
import com.reminder.request.model.GroupRequest;
import com.reminder.request.model.GroupRolesRequest;
import com.reminder.request.model.GroupSearchCriteria;
import com.reminder.request.model.MyGroupHome;
import com.reminder.response.model.GroupResponse;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.response.model.MyGroupRoleDetails;


public interface GroupService
{
    public Groups createGroup(Groups group);
    
    public GroupResponse getGroupById(int userId,int groupId);
    
    //public List<Group1> getAllGroups();
    
    public List<GroupResponse> getAllGroups(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, GroupSearchCriteria groupSearchCriteria);
    
    public void updateGroup(Groups group, GroupRequest groupRequest);
    
    public void deleteGroup(int groupId);
	public List<ModuleType> getAllModule();

	public List<GroupResponse> getAllModuleByGroups(Integer moduleId,Integer loginId);
	
	public List<MyGroupDropDown> getAllModuleByRoles(Integer moduleId,Integer loginId);

	public List<MyGroupDetails> getMyGroupDetailsByUserId(Integer userId, Integer pageNo,Integer pageSize);

	public void saveUserGroupUsersRole(AssignRoleRequest assignRoleRequest);

	public void deleteGroupUser(int id);

	Groups getGroupByID(int groupId);

	public List<MyGroupDropDown>  getAllGroupByModuleId(Integer moduleId, int userId, boolean isadmin);

	public List<User> getAllUserDetailsNotInGroup(MyGroupHome myGroupHome, String searchCriteria);
	
	public List<GrouproleUser> getAllUserByGroupRole(int groupId, int groupRoleId);

	public List<MyGroupDetails> getMyGroupDetailsByUserId(MyGroupHome myGroupHome, String searchCriteria);

	public Boolean groupNameExists(int moduleId, String groupName);

	public long getAllUsersCountNotInGroup(MyGroupHome myGroupHome, String searchCriteria);

	public BigInteger getMyGroupDetailsCountByUserId(MyGroupHome myGroupHome, String searchCriteria);

	public long getAllGroupsCount(String searchCriteria, GroupSearchCriteria groupSearchCriteria);

	public void updateGroup(Groups group);

	public ResponseModel updateGroupRole(GroupRolesRequest groupRolesRequest, User createdUser, int groupId, int groupRoleId);

	public List<MyGroupRoleDetails> getMyGroupsRoleDetails(int userId);

	public boolean checkforActiveReminder(Integer id);

	public List<MyGroupRoleDetails> groupByName(String groupName);

	public List<ModuleType> getModuleNameByUser(int userId);

	public boolean isAdmin(int userId, int moduleId);


}

