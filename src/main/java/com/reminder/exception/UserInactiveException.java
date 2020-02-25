package com.reminder.exception;


public class UserInactiveException extends RuntimeException {

	private String msg;
	public UserInactiveException(){
		super();
	}
	
	public UserInactiveException(String msg){
		this.msg = msg;
	
	}	
}
