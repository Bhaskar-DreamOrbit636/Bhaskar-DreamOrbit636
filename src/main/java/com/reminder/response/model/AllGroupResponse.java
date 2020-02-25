package com.reminder.response.model;

import java.io.Serializable;
import java.util.List;

public class AllGroupResponse implements Serializable{

	private static final long serialVersionUID = 8559045560967555526L;
	List<GroupResponse> groups ;
	long count;
	
	public AllGroupResponse(){
		
	}
	
	public AllGroupResponse(List<GroupResponse> groups, long count) {
		super();
		this.groups = groups;
		this.count = count;
	}
	
	public List<GroupResponse> getGroups() {
		return groups;
	}
	public void setGroups(List<GroupResponse> groups) {
		this.groups = groups;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
}
