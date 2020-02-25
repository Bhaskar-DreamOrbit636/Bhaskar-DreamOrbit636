package com.reminder.response.model;

import java.math.BigInteger;
import java.util.List;

public class MyGroupDetailsResponse {

	List<MyGroupDetails> groupDetails ;
	BigInteger count ;
	String roleForGroup;
	
	public MyGroupDetailsResponse(){
		
	}
	
	
	public MyGroupDetailsResponse(List<MyGroupDetails> groupDetails, BigInteger count, String roleForGroup) {
		super();
		this.groupDetails = groupDetails;
		this.count = count;
		this.roleForGroup = roleForGroup;
	}
	
	
	public List<MyGroupDetails> getGroupDetails() {
		return groupDetails;
	}
	public void setGroupDetails(List<MyGroupDetails> groupDetails) {
		this.groupDetails = groupDetails;
	}
	public BigInteger getCount() {
		return count;
	}
	public void setCount(BigInteger count) {
		this.count = count;
	}

	public String getRoleForGroup() {
		return roleForGroup;
	}


	public void setRoleForGroup(String roleForGroup) {
		this.roleForGroup = roleForGroup;
	}
	
	
}
