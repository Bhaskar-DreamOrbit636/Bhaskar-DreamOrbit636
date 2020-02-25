package com.reminder.response.model;

import java.util.Comparator;

public class MyGroupDetails {

	private int userId;

	private String userName;

	private String emailId;

	private String moduleName;
	
	private String userGroupName;
	
	private String roleName;

	private int groupUserId;
	
	private int groupRoleId;
	
	private String adUserId;
	
	private String userRolePerGroup;
	
	
	
	public String getUserRolePerGroup() {
		return userRolePerGroup;
	}

	public void setUserRolePerGroup(String userRolePerGroup) {
		this.userRolePerGroup = userRolePerGroup;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getGroupUserId() {
		return groupUserId;
	}

	public void setGroupUserId(int groupUserId) {
		this.groupUserId = groupUserId;
	}

	public int getGroupRoleId() {
		return groupRoleId;
	}

	public void setGroupRoleId(int groupRoleId) {
		this.groupRoleId = groupRoleId;
	}

	public String getAdUserId() {
		return adUserId;
	}

	public void setAdUserId(String adUserId) {
		this.adUserId = adUserId;
	}
	
}

class SortbyUserIdAsc implements Comparator<MyGroupDetails>
{
    public int compare(MyGroupDetails a, MyGroupDetails b)
    {
        return a.getUserId() - b.getUserId();
    }
}

class SortbyUserIdDesc implements Comparator<MyGroupDetails>
{
    public int compare(MyGroupDetails a, MyGroupDetails b)
    {
        return b.getUserId() - a.getUserId();
    }
}

class SortbyUserNameDesc implements Comparator<MyGroupDetails>
{
    public int compare(MyGroupDetails a, MyGroupDetails b)
    {
        return b.getUserName().compareTo(a.getUserName());
    }
}
