package com.reminder.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.reminder.response.model.ServiceResponse;

@EnableWebMvc
@ControllerAdvice("com.reminder")
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseBody
	public ServiceResponse handleUserException(HttpServletRequest request, Exception ex){
		ServiceResponse sr = new ServiceResponse("error","User not found in Reminder365");
		return sr;
	}
	
	@ExceptionHandler(DuplicateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ServiceResponse handleDuplicateException(DuplicateException ex){
		ServiceResponse sr = new ServiceResponse(ex.toString());
		return sr;
	}
	
	@ExceptionHandler(UserInactiveException.class)
	@ResponseBody
	public ServiceResponse handleUserInactiveException(HttpServletRequest request, HttpServletResponse response, Exception ex){
		response.setStatus(403);
		ServiceResponse sr = new ServiceResponse("error","Inactive User");
		sr.setStatus("403");
		return sr;
	}
}
