package com.reminder.response.model;

import java.io.Serializable;

public class ActionType implements Serializable {

	private static final long serialVersionUID = 1L;
	private String actionType;
	private int actionTypeId;
	private String displayName;
	private boolean isAdminAction;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isAdminAction() {
		return isAdminAction;
	}

	public void setAdminAction(boolean isAdminAction) {
		this.isAdminAction = isAdminAction;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public int getActionTypeId() {
		return actionTypeId;
	}

	public void setActionTypeId(int actionTypeId) {
		this.actionTypeId = actionTypeId;
	}

}
