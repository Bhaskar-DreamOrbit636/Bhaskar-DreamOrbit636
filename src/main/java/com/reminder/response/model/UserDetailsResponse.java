package com.reminder.response.model;

import java.util.List;

import com.reminder.model.User;

public class UserDetailsResponse {

	List<User> users ;
	long count ;
	
	public UserDetailsResponse(){
		
	}
	public UserDetailsResponse(List<User> users,  long count) {
		super();
		this.users = users;
		this.count = count;
	}
	

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public  long getCount() {
		return count;
	}

	public void setCount( long count) {
		this.count = count;
	}
	
	
}
