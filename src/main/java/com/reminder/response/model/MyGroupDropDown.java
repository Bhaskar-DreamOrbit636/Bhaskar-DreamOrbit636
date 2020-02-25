package com.reminder.response.model;

public class MyGroupDropDown {

	private String name;
	private Integer id;
	
	public MyGroupDropDown(){
		
	}
	public MyGroupDropDown(String name, Integer id) {
		super();
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

	
}
