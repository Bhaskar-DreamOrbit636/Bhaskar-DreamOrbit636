package com.reminder.request.model;

import java.util.List;

public class FileRequest {

	private List<Integer> ids;
	private Integer reminderId;
	
	
	public List<Integer> getIds() {
		return ids;
	}
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	public Integer getReminderId() {
		return reminderId;
	}
	public void setReminderId(Integer reminderId) {
		this.reminderId = reminderId;
	}

	
}
