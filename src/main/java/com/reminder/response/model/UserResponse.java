package com.reminder.response.model;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

public class UserResponse implements Serializable{

	private static final long serialVersionUID = -1232395859408322328L;
	
	private int userId;
    private String adUserId;
    private String emailId;
    private String userName;
    private String mobileNumber;
    private String remark;
    private Boolean active;
    private Boolean userAdmin;
    private Boolean groupAdmin;
    private String createdById;
    private DateTime createdAt;
    private String lastModifiedById;
    private DateTime lastModifiedAt;
    private List<MyGroupRoleDetails> userGroupResponse;
    private DateTime lastUnSuccesFullLoggedIn;
    private DateTime lastSuccesFullLoggedIn;
	private int departmentId;
	private String departmentName;
    
    public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public DateTime getLastSuccesFullLoggedIn() {
		return lastSuccesFullLoggedIn;
	}
	public void setLastSuccesFullLoggedIn(DateTime lastSuccesFullLoggedIn) {
		this.lastSuccesFullLoggedIn = lastSuccesFullLoggedIn;
	}
	public DateTime getLastUnSuccesFullLoggedIn() {
		return lastUnSuccesFullLoggedIn;
	}
	public void setLastUnSuccesFullLoggedIn(DateTime lastUnSuccesFullLoggedIn) {
		this.lastUnSuccesFullLoggedIn = lastUnSuccesFullLoggedIn;
	}
    

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getAdUserId() {
		return adUserId;
	}
	public void setAdUserId(String adUserId) {
		this.adUserId = adUserId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Boolean getUserAdmin() {
		return userAdmin;
	}
	public void setUserAdmin(Boolean userAdmin) {
		this.userAdmin = userAdmin;
	}
	public Boolean getGroupAdmin() {
		return groupAdmin;
	}
	public void setGroupAdmin(Boolean groupAdmin) {
		this.groupAdmin = groupAdmin;
	}
	public String getCreatedById() {
		return createdById;
	}
	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}
	public DateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getLastModifiedById() {
		return lastModifiedById;
	}
	public void setLastModifiedById(String lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	public List<MyGroupRoleDetails> getUserGroupResponse() {
		return userGroupResponse;
	}
	public void setUserGroupResponse(List<MyGroupRoleDetails> userGroupResponse) {
		this.userGroupResponse = userGroupResponse;
	}
    
    
}
