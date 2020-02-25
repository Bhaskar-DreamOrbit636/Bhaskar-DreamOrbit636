package com.reminder.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;


public class UserRequest implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in User table--------
    
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
    
    // -------must use for actual SQL type of the column
    private DateTime lastModifiedAt;
    
    // ----- mapping with join table Grouprole_User ---------
 
    private Set<ActionType> actionType = new HashSet<>();
    

    
    //-------code for join table Group_User -----------------
    
   
	
	
    
    // ---------- setter/getter------------------------
    
    public void addGrouprole(ActionType grouprole_User) {
        this.actionType.add(grouprole_User);
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
	public Boolean isActive() {
		return active;
	}
	
	public Boolean isUserAdmin() {
		return userAdmin;
	}

	public Boolean isGroupAdmin() {
		return groupAdmin;
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
	

    
    // ----- mapping with join table Grouprole_User ---------
	
    public Set<ActionType> getGrouprole_User() {
		return actionType;
	}
	public void setGrouprole_User(Set<ActionType> grouprole_User) {
		this.actionType = grouprole_User;
	}
	
	


	
    @Override
    public String toString()
    {
        return "User [userid=" + userId + ", username=" + userName + "]";
    }
}
