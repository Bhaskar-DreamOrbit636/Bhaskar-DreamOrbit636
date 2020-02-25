package com.reminder.response.model;

import java.util.List;

public class GroupRecordExpiring {
	
/*	private int groupId;
	
	private String groupName;*/
	
	private List<Month> recordExpiring;
	
	private String moduleName;

/*	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}*/

	public List<Month> getRecordExpiring() {
		return recordExpiring;
	}

	public void setRecordExpiring(List<Month> recordExpiring) {
		this.recordExpiring = recordExpiring;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
}
