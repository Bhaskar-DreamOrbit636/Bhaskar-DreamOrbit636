package com.reminder.exception;

public class NoContentException extends Exception
{

	private String msg;
	public NoContentException(){
		super();
	}
	
	public NoContentException(String msg){
		this.msg = msg;
	}
}
