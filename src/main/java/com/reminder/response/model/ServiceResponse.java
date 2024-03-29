package com.reminder.response.model;

public class ServiceResponse {

	private String status;
	private Object data;
	
	public ServiceResponse(){
		
	}
	
	public ServiceResponse(String status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}
	
	public ServiceResponse(String status) {
		super();
		this.status = status;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
}
