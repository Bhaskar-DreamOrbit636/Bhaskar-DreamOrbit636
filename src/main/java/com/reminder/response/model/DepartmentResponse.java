package com.reminder.response.model;

import java.util.List;

import com.reminder.model.Section;

public class DepartmentResponse {
	
	private List<Section> section;
	
	private String status;

	

	public List<Section> getSection() {
		return section;
	}

	public void setSection(List<Section> section) {
		this.section = section;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
