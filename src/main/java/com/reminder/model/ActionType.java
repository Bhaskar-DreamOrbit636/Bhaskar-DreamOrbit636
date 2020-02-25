package com.reminder.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "action_type")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "action_type")
public class ActionType implements Serializable {
	private static final long serialVersionUID = -1232308999408322328L;

	// ----- all database columns in User table--------
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "action_type_id")
	private int actionTypeId;

	@Column(name = "name")
	private String actionName;
	
	@Column(name = "display_name")
	private String displayName;
	
	@Column(name = "admin_action")
	private boolean isAdminAction;
	

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="actionType")
	private Set<GroupRoleActionType> actionTypes;

	public Set<GroupRoleActionType> getActionTypes() {
		return actionTypes;
	}

	public void setActionTypes(Set<GroupRoleActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

	public ActionType() {
		super();
	}

	public ActionType(int actionTypeId, String actionControllerName, String actionName) {
		super();
		this.actionTypeId = actionTypeId;

		this.actionName = actionName;

	}

	// ---------- setter/getter------------------------

	public int getActionTypeId() {
		return actionTypeId;
	}

	public void setActionTypeId(int actionTypeId) {
		this.actionTypeId = actionTypeId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

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
	
	@Override
	public String toString() {
		return "ActionType [actionTypeId=" + actionTypeId + ", actionName=" + actionName + "]";
	}

}
