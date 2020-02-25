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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name = "groups", 
uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "module_type_id" } ) })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "groups")
public class Groups implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	// ----- all database columns in Group table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "group_id")
	private int groupId;

	@Column(name = "name")
	private String groupName;

	@Column(name = "description")
	private String description;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST )
	@JoinColumn(name="created_by_id", referencedColumnName="user_id")
	private User createdBy;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
	@JoinColumn(name="last_modified_by_id", referencedColumnName="user_id")
	private User lastModifiedBy;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, optional=false)
	@JoinColumn(name="module_type_id", referencedColumnName="module_type_id")
    private ModuleType moduleType;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY,  mappedBy="group", cascade = CascadeType.ALL)
	private Set<GroupRoles> groupRoles;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy="group")
	private Set<GrouproleUser> groupUser;
	
	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
	  


	public Set<GrouproleUser> getGroupUser() {
		return groupUser;
	}

	public void setGroupUser(Set<GrouproleUser> groupUser) {
		this.groupUser = groupUser;
	}

	public Groups() {
		super();
	}

	public Groups(int groupId, String groupName, String description, Boolean active, DateTime createdAt,
			DateTime lastModifiedAt) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.description = description;
		this.active = active;
		this.createdAt = new DateTime();
		this.lastModifiedAt = new DateTime();

	}

	// ---------- setter/getter------------------------

	public int getGroupId() {
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
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	

	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public ModuleType getModuleType() {
		return moduleType;
	}

	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}

	public Set<GroupRoles> getGroupRoles() {
		return groupRoles;
	}

	public void setGroupRoles(Set<GroupRoles> groupRoles) {
		this.groupRoles = groupRoles;
	}


	public Set<GrouproleUser> getGroup() {
		return groupUser;
	}

	public void setGroup(Set<GrouproleUser> groupUser) {
		this.groupUser = groupUser;
	}

	@Override
	public String toString() {
		return "Group1 [groupId=" + groupId + ", groupName=" + groupName + "]";
	}

}
