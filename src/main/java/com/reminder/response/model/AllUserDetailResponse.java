package com.reminder.response.model;

import java.util.List;

public class AllUserDetailResponse {
	List<UserResponse> users ;
	long count ;
	
	
	public AllUserDetailResponse(List<UserResponse> users, long count) {
		super();
		this.users = users;
		this.count = count;
	}
	public AllUserDetailResponse(){
		
	}
	
	public List<UserResponse> getUsers() {
		return users;
	}
	public void setUsers(List<UserResponse> users) {
		this.users = users;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
}
