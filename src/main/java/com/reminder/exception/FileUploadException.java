package com.reminder.exception;

public class FileUploadException extends RuntimeException {
	
	private String msg;
	public FileUploadException(){
		super();
	}
	
	public FileUploadException(String msg){
		this.msg = msg;
	}

}
