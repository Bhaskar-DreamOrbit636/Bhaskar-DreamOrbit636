package com.reminder.request.model;

import java.io.Serializable;

public class GroupUserRequest implements Serializable{
	
	private static final long serialVersionUID = -1232395859408322328L;
	
	private int userId;
	//private int groupUserId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

/*	public int getGroupUserId() {
		return groupUserId;
	}

	public void setGroupUserId(int groupUserId) {
		this.groupUserId = groupUserId;
	}*/

}
