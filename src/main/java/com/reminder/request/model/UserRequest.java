package com.reminder.request.model;

import java.io.Serializable;

import org.joda.time.DateTime;

public class UserRequest implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;
    
    private int userId;
    private String adUserId;
    private String emailId;
    private String userName;
    private Integer mobileNumber;
    private String remark;
    private Boolean active;
    private Boolean userAdmin;
    private Boolean groupAdmin;
    private Integer createdById;
    private DateTime createdAt;
    private Integer lastModifiedById;
    private DateTime lastModifiedAt;
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
	public Integer getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(Integer mobileNumber) {
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
	public Integer getCreatedById() {
		return createdById;
	}
	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}
	public DateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	public Integer getLastModifiedById() {
		return lastModifiedById;
	}
	public void setLastModifiedById(Integer lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
}
    
