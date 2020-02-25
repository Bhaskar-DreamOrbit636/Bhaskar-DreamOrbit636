package com.reminder.request.model;


import java.io.Serializable;

import org.joda.time.DateTime;

import com.reminder.model.GroupRoles;
import com.reminder.model.User;


public class GrouproleUserRequest implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    private int id;
    private User user;
    private GroupRoles grouprole;
    private int createdById;
    private DateTime createdAt;
    private int lastModifiedById;
    private DateTime lastModifiedAt;
    
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
 
    public void setUser(User user) {
        this.user = user;
    }
 
    
    public GroupRoles getGrouprole() {
  		return grouprole;
  	}

  	public void setGrouprole(GroupRoles grouprole) {
  		this.grouprole = grouprole;
  	}

	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}
	
	
	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	
	public int getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(int lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	
	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
	@Override
    public String toString()
    {
        return " successful return ";
    }

	
	
}

