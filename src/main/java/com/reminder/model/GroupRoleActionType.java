package com.reminder.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name = "group_role_has_action_type")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "group_role_has_action_type")
public class GroupRoleActionType implements Serializable {


	private static final long serialVersionUID = -1232395859408322328L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
    @Column(name="group_role_action_type_id")
    private int groupRoleActionTypeId;
	
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY,cascade =CascadeType.ALL)
	@JoinColumn(name="group_role_id", nullable=false)
	private GroupRoles groupRole;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="action_type_id", referencedColumnName="action_type_id",nullable=false)
	private ActionType actionType;

	@Column(name = "created_at")
	private DateTime createdAt;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="created_by_id", referencedColumnName="user_id")
	private User createdBy;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="last_modified_by_id", referencedColumnName="user_id")
	private User lastModifiedBy;
	
	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;



	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
	  
	@Override
	public String toString() {
		return " successful return ";
	}
	
	
	public int getGroupRoleActionTypeId() {
		return groupRoleActionTypeId;
	}


	public void setGroupRoleActionTypeId(int groupRoleActionTypeId) {
		this.groupRoleActionTypeId = groupRoleActionTypeId;
	}


	public GroupRoles getGroupRole() {
		return groupRole;
	}


	public void setGroupRole(GroupRoles groupRole) {
		this.groupRole = groupRole;
	}


	public ActionType getActionType() {
		return actionType;
	}


	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}


	public DateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}


	public User getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


	public User getLastModifiedBy() {
		return lastModifiedBy;
	}


	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}


	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}


	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}


	public GroupRoleActionType() {}


	public GroupRoleActionType(int groupRoleActionTypeId, GroupRoles groupRole, ActionType actionType, DateTime createdAt,
			User createdBy, User lastModifiedBy, DateTime lastModifiedAt) {
		super();
		this.groupRoleActionTypeId = groupRoleActionTypeId;
		this.groupRole = groupRole;
		this.actionType = actionType;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedAt = lastModifiedAt;
	}
	
	

}
