package com.reminder.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
@Table(name = "user",
uniqueConstraints=
@UniqueConstraint(columnNames={"email_id"}))
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	// ----- all database columns in User table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "user_id")
	private int userId;

	@Column(name = "ad_user_id")
	private String adUserId;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "remark")
	private String remark;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "user_admin")
	private Boolean userAdmin;

	@Column(name = "group_admin")
	private Boolean groupAdmin;

	@Column(name = "created_by_id")
	private Integer createdById;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_by_id")
	private Integer lastModifiedById;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;

	@Column(name = "last_unsucessfull_login_at")
	private DateTime lastUnSuccessfullLoginDate;
	
	
	@Column(name = "current_login_at")
	private DateTime currentLoginDate;
	
	@Column(name = "last_sucessfull_login_at")
	private DateTime lastSuccessfullLoginDate;

	@JsonIgnore
	@Access(AccessType.PROPERTY)
	@OneToMany(mappedBy = "userId", fetch = FetchType.EAGER)
	private Set<Contract_Reviewer> contract_Reviewer;
	
	@ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.ALL}, optional = false)
	@JoinColumn(name = "department_id")
	private Department departments;

	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
		 //lastModifiedAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }

	public User() {
		super();
	}

	public User(int userId, String adUserId, String emailId, String userName, String mobileNumber, String remark,
			Boolean active, Boolean userAdmin, Boolean groupAdmin, int createdById, DateTime createdAt,
			int lastModifiedById, DateTime lastModifiedAt) {
		super();
		this.userId = userId;
		this.adUserId = adUserId;
		this.emailId = emailId;
		this.userName = userName;
		this.mobileNumber = mobileNumber;
		this.remark = remark;
		this.active = active;
		this.userAdmin = userAdmin;
		this.groupAdmin = groupAdmin;
		this.createdById = createdById;
		this.createdAt = createdAt;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedAt = lastModifiedAt;
	}

	// ---------- setter/getter------------------------

	/*
	 * public void addGrouprole(Grouprole_User grouprole_User) {
	 * this.grouprole_User.add(grouprole_User); }
	 */

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAdUserId() {
		return adUserId;
	}

	public void setAdUserId(String adUserId) {
		this.adUserId = adUserId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean isActive() {
		return active;
	}

	public Boolean isUserAdmin() {
		return userAdmin;
	}

	public Boolean isGroupAdmin() {
		return groupAdmin;
	}

	public Integer getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(Integer lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
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

	public Boolean getUserAdmin() {
		return userAdmin;
	}

	public void setUserAdmin(Boolean userAdmin) {
		this.userAdmin = userAdmin;
	}

	public Boolean getGroupAdmin() {
		return groupAdmin;
	}

	public void setGroupAdmin(Boolean groupAdmin) {
		this.groupAdmin = groupAdmin;
	}

	public Set<Contract_Reviewer> getContract_Reviewer() {
		return contract_Reviewer;
	}

	public void setContract_Reviewer(Set<Contract_Reviewer> contract_Reviewer) {
		this.contract_Reviewer = contract_Reviewer;
	}

	public DateTime getLastUnSuccessfullLoginDate() {
		return lastUnSuccessfullLoginDate;
	}
	
	public void setLastUnSuccessfullLoginDate(DateTime lastUnSuccessfullLoginDate) {
		this.lastUnSuccessfullLoginDate = lastUnSuccessfullLoginDate;
	}
	
	public DateTime getCurrentLoginDate() {
		return currentLoginDate;
	}
	
	public void setCurrentLoginDate(DateTime currentLoginDate) {
		this.currentLoginDate = currentLoginDate;
	}
	
	public DateTime getLastSuccessfullLoginDate() {
		return lastSuccessfullLoginDate;
	}
	
	public void setLastSuccessfullLoginDate(DateTime lastSuccessfullLoginDate) {
		this.lastSuccessfullLoginDate = lastSuccessfullLoginDate;
	}

	public Department getDepartments() {
		return departments;
	}

	public void setDepartments(Department departments) {
		this.departments = departments;
	}

}