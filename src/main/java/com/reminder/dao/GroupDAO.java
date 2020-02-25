package com.reminder.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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


public interface GroupDAO
{
    public void createGroup(Groups group);
    
    public Groups getGroupById(int groupId);
    
    public List<Object[]> getAllGroups(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, GroupSearchCriteria groupSearchCriteria);
    
    public void deleteGroup(int groupId);
    
	public List<ModuleType> getAllModule();

	public List<Groups> getAllModuleByGroups(Integer moduleId,Integer loginId);
	
	public List<MyGroupDropDown> getAllGroupByModuleId(Integer moduleId,Integer loginId, boolean isadmin);
	
	public List<MyGroupDropDown> getAllModuleByRole(Integer groupId, Integer moduleId);

	List<MyGroupDetails> getMyGroupDetailsByUserId(Integer userId, Integer pageNo,Integer pageSize);

	public List<MyGroupDetails> getMyGroupDetailsByUserId(MyGroupHome myGroupHome, String searchCriteria);

	List<User> getAllUserDetailsNotInGroup(MyGroupHome myGroupHome, String searchCriteria);

	public Boolean groupNameExists(int moduleId, String groupName);

	public long getAllUsersCountNotInGroup(MyGroupHome myGroupHome, String searchCriteria);

	public BigInteger getMyGroupDetailsCountByUserId(MyGroupHome myGroupHome, String searchCriteria);

	public long getAllGroupsCount(String searchCriteria, GroupSearchCriteria groupSearchCriteria);

	public void updateGroup(Groups group, GroupRequest groupRequest);

	public void updateGroup(Groups group);

	List<GrouproleUser> getGroupUsersByGroupId(int groupId);

	public List<GrouproleUser> getUserByGroupRole(int groupId, int groupRoleId);
	
	public List<Object> getGroupUserDetailByGroupId(int groupId, int moduleTypeId);
	
	public List<Object> getGroupUserDetailByGroupId(Set<Integer> groupId, int moduleTypeId);

	public List<String> getActionsByGroupRoleId(int groupRoleId);

	public List<MyGroupRoleDetails> getMyGroupsRoleDetails(int userId);

	public boolean checkforActiveReminder(Integer id);

	public List<Object[]> getGroupRolesAction(int userId, Set<Integer> groupId);
	
	public List<String> getGroupRolesAction(int userId, int groupId);

	public List<MyGroupRoleDetails> groupByName(String groupName);

	public List<ModuleType> getModuleNameByUser(int userId);

	public boolean isAdmin(int userId, int moduleId);
	
}
