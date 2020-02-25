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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "group_role")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "group_role")
public class GroupRoles implements Serializable {
	private static final long serialVersionUID = -1232308999408322328L;

	// ----- all database columns in User table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "group_role_id")
	private int groupRoleId;

	@Column(name = "role")
	private String groupRoleName;

	@Column(name = "created_at")
	private DateTime createdAtTime;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAtTime;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	@JoinColumn(name="created_by_id", referencedColumnName="user_id")
	private User createdBy;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	@JoinColumn(name="last_modified_by_id", referencedColumnName="user_id")
	private User lastModifiedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="group_id", referencedColumnName="group_id")
	private Groups group;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy="groupRole")
	private Set<GroupRoleActionType> groupRolesActionType;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "roleUser")
	private Set<GrouproleUser> roleUsers;

	public Set<GrouproleUser> getRoleUsers() {
		return roleUsers;
	}

	public void setRoleUsers(Set<GrouproleUser> roleUsers) {
		this.roleUsers = roleUsers;
	}


	@PrePersist
	  protected void onCreate() {
		createdAtTime = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAtTime = DateTimeUtil.now();
	  }
	  
	public GroupRoles() {
		super();
	}

	public GroupRoles(int groupRoleId, String groupRoleName, int groupId, DateTime createdAtTime, DateTime lastModifiedAtTime) {
		super();
		this.groupRoleId = groupRoleId;
		this.groupRoleName = groupRoleName;
		this.createdAtTime = createdAtTime;
		this.lastModifiedAtTime = lastModifiedAtTime;
		// this.roleId = roleId;
	}

	// ---------- setter/getter------------------------

	public int getGroupRoleId() {
		return groupRoleId;
	}

	public void setGroupRoleId(int groupRoleId) {
		this.groupRoleId = groupRoleId;
	}

	public String getGroupRoleName() {
		return groupRoleName;
	}

	public void setGroupRoleName(String groupRoleName) {
		this.groupRoleName = groupRoleName;
	}

	

	public DateTime getCreatedAtTime() {
		return createdAtTime;
	}

	public void setCreatedAtTime(DateTime createdAtTime) {
		this.createdAtTime = new DateTime();
	}

	

	public DateTime getLastModifiedAtTime() {
		return lastModifiedAtTime;
	}

	public void setLastModifiedAtTime(DateTime lastModifiedAtTime) {
		this.lastModifiedAtTime = new DateTime();
	}

	public Groups getGroup() {
		return group;
	}

	public void setGroup(Groups group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "GroupRoles [" + ", groupRoleName=" + groupRoleName + "]";
	}

	public Set<GroupRoleActionType> getGroupRolesActionType() {
		return groupRolesActionType;
	}

	public void setGroupRolesActionType(Set<GroupRoleActionType> groupRolesActionType) {
		this.groupRolesActionType = groupRolesActionType;
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

}
