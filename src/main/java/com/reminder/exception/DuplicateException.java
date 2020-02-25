package com.reminder.exception;

public class DuplicateException extends RuntimeException {
	
	private String msg;
	
	public DuplicateException(){
		super();
	}
	
	public DuplicateException(String msg){
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
	
}
