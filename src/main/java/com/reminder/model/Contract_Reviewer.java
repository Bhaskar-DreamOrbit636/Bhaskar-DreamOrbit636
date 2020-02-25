package com.reminder.model;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name = "contract_reviewer")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contract_reviewer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract_Reviewer implements Serializable {
	private static final long serialVersionUID = -1232308999408322328L;

	// ----- all database columns in User table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "contract_reviewer_id")
	private int contractReviewerId;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_at")
	private DateTime lastModifedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User userId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by_id", referencedColumnName = "user_id")
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "last_modified_by_id", referencedColumnName = "user_id")
	private User lastModifiedBy;

	 @PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
		 lastModifedAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifedAt = DateTimeUtil.now();
	  }

	// ----- mapping with join table Contract_Has_Status ---------

	// private Set<Contract_Has_Status> contract_Has_Status = new
	// HashSet<Contract_Has_Status>();

	
	public Contract_Reviewer() {
		super();
	}

	public Contract_Reviewer(int contractReviewerId) {
		super();
		this.contractReviewerId = contractReviewerId;
	}

	// ---------- setter/getter------------------------

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
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public int getContractReviewerId() {
		return contractReviewerId;
	}

	public void setContractReviewerId(int contractReviewerId) {
		this.contractReviewerId = contractReviewerId;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public DateTime getLastModifedAt() {
		return lastModifedAt;
	}

	public void setLastModifedAt(DateTime lastModifedAt) {
		this.lastModifedAt = lastModifedAt;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

}
