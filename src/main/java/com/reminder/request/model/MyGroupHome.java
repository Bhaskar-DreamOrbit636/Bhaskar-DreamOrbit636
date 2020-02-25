package com.reminder.request.model;

public class MyGroupHome {
	
private int moduleId;
private int groupId;
private int roleId;
private int pageNo;
private int pageSize;
private int adminUser;
private String sortBy;
private String orderBy;


public String getSortBy() {
	return sortBy;
}
public void setSortBy(String sortBy) {
	this.sortBy = sortBy;
}
public String getOrderBy() {
	return orderBy;
}
public void setOrderBy(String orderBy) {
	this.orderBy = orderBy;
}
public int getModuleId() {
	return moduleId;
}
public void setModuleId(int moduleId) {
	this.moduleId = moduleId;
}
public int getGroupId() {
	return groupId;
}
public void setGroupId(int groupId) {
	this.groupId = groupId;
}
public int getRoleId() {
	return roleId;
}
public void setRoleId(int roleId) {
	this.roleId = roleId;
}
public int getPageNo() {
	return pageNo;
}
public void setPageNo(int pageNo) {
	this.pageNo = pageNo;
}
public int getPageSize() {
	return pageSize;
}
public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
}
public int getAdminUser() {
	return adminUser;
}
public void setAdminUser(int adminUser) {
	this.adminUser = adminUser;
}


}
