package com.reminder.response.model;

import java.io.Serializable;

public class UserPopUp implements Serializable{
 
	private static final long serialVersionUID = 1L;
	
	private String key;
	private String value;
	
	public UserPopUp(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
