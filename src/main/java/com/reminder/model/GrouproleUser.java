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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name="group_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "group_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrouproleUser implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in Group table--------
    
    //----------------------------------------------------------------------------------------------------------
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "group_user_id")
    private int id;
        
	// additional fields

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName="user_id")
    private User user;
 
 
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_role_id")
    private GroupRoles roleUser;
    
    @JsonIgnore
   	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
  	@JoinColumn(name = "group_id", referencedColumnName="group_id")
	private Groups group;

	@Column(name="created_at")
	private DateTime createdAt;

	@Column(name="last_modified_at")
    private DateTime lastModifiedAt;
		
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	@JoinColumn(name="created_by_id", referencedColumnName="user_id")
	private User createdBy;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE )
	@JoinColumn(name="last_modified_by_id", referencedColumnName="user_id")
	private User lastModifiedBy;

	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public GroupRoles getRoleUser() {
		return roleUser;
	}



	public void setRoleUser(GroupRoles roleUser) {
		this.roleUser = roleUser;
	}



	public Groups getGroup() {
		return group;
	}



	public void setGroup(Groups group) {
		this.group = group;
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



	@Override
    public String toString()
    {
        return " successful return ";
    }
}

