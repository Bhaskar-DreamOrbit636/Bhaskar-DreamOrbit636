package com.reminder.response.model;

import java.util.List;

import com.reminder.model.Location;

public class LocationResponse {

	private Long count;
	private List<Location> location;
	
	
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Location>  getLocation() {
		return location;
	}
	public void setLocation(List<Location>  location) {
		this.location = location;
	}
	
	
	
}
