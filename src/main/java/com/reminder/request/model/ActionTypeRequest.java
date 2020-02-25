package com.reminder.request.model;

import java.io.Serializable;

public class ActionTypeRequest implements Serializable{
	
	private static final long serialVersionUID = -1232395859408322328L;

	private int actionTypeId;
	// private String actionNam

	public int getActionTypeId() {
		return actionTypeId;
	}

	public void setActionTypeId(int actionTypeId) {
		this.actionTypeId = actionTypeId;
	}
}
