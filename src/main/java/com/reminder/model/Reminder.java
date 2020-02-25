package com.reminder.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "reminder")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reminder")
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class Reminder implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "reminder_id")
	private int reminderId;

	@Column(name = "effective_startd_date")
	private Date effectiveStartDate;

	@Column(name = "effective_expiry_date")
	private Date effectiveExpiryDate;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "first_reminder_date")
	private Date firstReminderDate;

	@Column(name = "second_reminder_date")
	private Date secondReminderDate;

	@Column(name = "third_reminder_date")
	private Date thirdReminderDate;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "created_by_id")
	private int createdById;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_by_id")
	private int lastModifiedById;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;

	@Column(name = "first_reminder_sent_at")
	private Date firstReminderSentAt;

	@Column(name = "second_reminder_sent_at")
	private Date secondReminderSentAt;

	@Column(name = "third_reminder_sent_at")
	private Date thirdReminderSentAt;

	/*@Column(name = "status_id")
	private int statusId;*/

	// @JsonIgnore
	// @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, fetch =
	// FetchType.EAGER, optional = true)
	// private Contract contract;

	@JsonIgnore
	@JoinColumn(name = "user_group_id")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Groups userGroupId;

	/*
	 * @JsonIgnore
	 * 
	 * @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, fetch =
	 * FetchType.LAZY, optional = true) private Contract contract;
	 */

	// @JsonIgnore
	// @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, fetch =
	// FetchType.LAZY, optional = true)
	// private Asset asset;
	/*
	 * @JsonIgnore
	 * 
	 * @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, fetch =
	 * FetchType.LAZY, optional = true) private StaffRecord staffRecords;
	 */

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "reminder_id")
	private List<FileUpload> fileuploads;

/*	@Column(name = "cc_list_lastreminder")
	private String ccListLastReminder;

	@Column(name = "cc_expiry_reminder")
	private String ccListExpiryReminder;*/

	@Column(name = "addcc_list_lastreminder")
	private String addCcListLastReminder;

	@Column(name = "addcc_list_expiryreminder")
	private String addCcListExpiryReminder;

	@PrePersist
	protected void onCreate() {
		createdAt = DateTimeUtil.now();
	}

	@PreUpdate
	protected void onUpdate() {
		lastModifiedAt = DateTimeUtil.now();
		//effectiveStartDate = new LocalDateTime(effectiveStartDate).toDateTime(DateTimeUtil.currentTimeZone);
		//effectiveExpiryDate = new LocalDateTime(effectiveExpiryDate).toDateTime(DateTimeUtil.currentTimeZone);
	}

	/*
	 * public Contract getContract() { return contract; }
	 * 
	 * public void setContract(Contract contract) { this.contract = contract; }
	 */

	/*
	 * public Asset getAsset() { return asset; }
	 * 
	 * public void setAsset(Asset asset) { this.asset = asset; }
	 */

	public List<FileUpload> getFileuploads() {
		return fileuploads;
	}

	public void setFileuploads(List<FileUpload> fileuploads) {
		this.fileuploads = fileuploads;
	}

	public Reminder() {
		super();
	}

	public Reminder(int reminderId, Date effectiveStartDate, Date effectiveExpiryDate, String remarks,
			Date firstReminderDate, Date secondReminderDate, Date thirdReminderDate, Boolean active,
			int createdById, DateTime createdAt, int lastModifiedById, DateTime lastModifiedAt,
			Date firstReminderSentAt, Date secondReminderSentAt, Date thirdReminderSentAt, int moduleTypeId,
			int statusId) {
		super();
		this.reminderId = reminderId;
		this.effectiveStartDate = effectiveStartDate;
		this.effectiveExpiryDate = effectiveExpiryDate;
		this.remarks = remarks;
		this.firstReminderDate = firstReminderDate;
		this.secondReminderDate = secondReminderDate;
		this.thirdReminderDate = thirdReminderDate;
		this.active = active;
		this.createdById = createdById;
		this.createdAt = createdAt;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedAt = lastModifiedAt;
		this.firstReminderSentAt = firstReminderSentAt;
		this.secondReminderSentAt = secondReminderSentAt;
		this.thirdReminderSentAt = thirdReminderSentAt;

		//this.statusId = statusId;
	}

	// ---------- setter/getter------------------------

	public Groups getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Groups userGroupId) {
		this.userGroupId = userGroupId;
	}
	/*
	 * public Group1 getGroup() { return group; } public void setGroup(Group1 group)
	 * { this.group = group; }
	 */

	public int getReminderId() {
		return reminderId;
	}

	public void setReminderId(int reminderId) {
		this.reminderId = reminderId;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveExpiryDate() {
		return effectiveExpiryDate;
	}

	public void setEffectiveExpiryDate(Date effectiveExpiryDate) {
		this.effectiveExpiryDate = effectiveExpiryDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getFirstReminderDate() {
		return firstReminderDate;
	}

	public void setFirstReminderDate(Date firstReminderDate) {
		this.firstReminderDate = firstReminderDate;
	}

	public Date getSecondReminderDate() {
		return secondReminderDate;
	}

	public void setSecondReminderDate(Date secondReminderDate) {
		this.secondReminderDate = secondReminderDate;
	}

	public Date getThirdReminderDate() {
		return thirdReminderDate;
	}

	public void setThirdReminderDate(Date thirdReminderDate) {
		this.thirdReminderDate = thirdReminderDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(int lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public Date getFirstReminderSentAt() {
		return firstReminderSentAt;
	}

	public void setFirstReminderSentAt(Date firstReminderSentAt) {
		this.firstReminderSentAt = firstReminderSentAt;
	}

	public Date getSecondReminderSentAt() {
		return secondReminderSentAt;
	}

	public void setSecondReminderSentAt(Date secondReminderSentAt) {
		this.secondReminderSentAt = secondReminderSentAt;
	}

	public Date getThirdReminderSentAt() {
		return thirdReminderSentAt;
	}

	public void setThirdReminderSentAt(Date thirdReminderSentAt) {
		this.thirdReminderSentAt = thirdReminderSentAt;
	}

	/*public int getStatusId() {
		return statusId;
	}*/

	// public Contract getContract() {
	// return contract;
	// }
	//
	// public void setContract(Contract contract) {
	// this.contract = contract;
	// }

	// public Asset getAsset() {
	// return asset;
	// }
	//
	// public void setAsset(Asset asset) {
	// this.asset = asset;
	// }

/*	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}*/

	/*
	 * public StaffRecord getStaffRecords() { return staffRecords; }
	 * 
	 * public void setStaffRecords(StaffRecord staffRecords) { this.staffRecords =
	 * staffRecords; }
	 */

	/*
	 * public void linkContract() {
	 * 
	 * contract.setReminder(this); contract.linkContractReviewers();
	 * contract.linkContractHasStatus();
	 * 
	 * }
	 */

	// public void linkAsset() {
	//
	// asset.setReminder(this);
	//
	// }

	// public void linkStaff(){
	//
	// staffRecord.setReminder(this);
	//
	// }

	/*
	 * public Contract getContract() { return contract; } public void
	 * setContract(Contract contract) { this.contract = contract; }
	 */

/*	public String getCcListLastReminder() {
		return ccListLastReminder;
	}

	public void setCcListLastReminder(String ccListLastReminder) {
		this.ccListLastReminder = ccListLastReminder;
	}

	public String getCcListExpiryReminder() {
		return ccListExpiryReminder;
	}

	public void setCcListExpiryReminder(String ccListExpiryReminder) {
		this.ccListExpiryReminder = ccListExpiryReminder;
	}*/

	public String getAddCcListLastReminder() {
		return addCcListLastReminder;
	}

	public void setAddCcListLastReminder(String addCcListLastReminder) {
		this.addCcListLastReminder = addCcListLastReminder;
	}

	public String getAddCcListExpiryReminder() {
		return addCcListExpiryReminder;
	}

	public void setAddCcListExpiryReminder(String addCcListExpiryReminder) {
		this.addCcListExpiryReminder = addCcListExpiryReminder;
	}

	@Override
	public String toString() {
		return "Reminder [ReminderId=" + reminderId + ", Remarks=" + remarks + "]";
	}
}
