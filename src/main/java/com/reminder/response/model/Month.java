package com.reminder.response.model;

public class Month {
	
	private int groupId;
	
	private int month;
	
	private int year;
	
	private RecordExpiring recordExpiring;
	
	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public RecordExpiring getRecordExpiring() {
		return recordExpiring;
	}

	public void setRecordExpiring(RecordExpiring recordExpiring) {
		this.recordExpiring = recordExpiring;
	}
	
}
