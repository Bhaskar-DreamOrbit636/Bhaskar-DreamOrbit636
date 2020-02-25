package com.reminder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;


@Entity
@Table(name = "record_type")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "record_type")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordType implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	// ----- all database columns in Staff_Record table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "record_type_id")
	private int recordTypeId;

	@Column(name = "type")
	private String recordType;
	
	@Column(name="active")
	private Boolean active;

	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "created_by_id", referencedColumnName = "user_id")
	@Column (name="created_by_id")
	private int createdBy;

	//@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "last_modified_by_id", referencedColumnName = "user_id")
	@Column (name="last_modified_by_id")
	private int lastModifiedBy;

	@Column(name = "first_reminder_day")
	private int firstReminderDay;

	@Column(name = "second_reminder_day")
	private int secondReminderDay;

	@Column(name = "third_reminder_day")
	private int thirdReminderDay;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;
	
//	@JsonIgnore
//	@OneToMany(mappedBy="rocrdType", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
//	List<StaffRecord> staffRecords;
 
//	public List<StaffRecord> getStaffRecords() {
//		return staffRecords;
//	}
//
//	public void setStaffRecords(List<StaffRecord> staffRecords) {
//		this.staffRecords = staffRecords;
//	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public RecordType() {
		super();
	}
	
	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
		 lastModifiedAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }

	public RecordType(int recordTypeId, String recordType, Boolean active, int createdBy, int lastModifiedBy, int firstReminderDay,
			int secondReminderDay,int thirdReminderDay,DateTime createdAt,DateTime lastModifiedAt){
		super();
		this.recordTypeId=recordTypeId;
		this.recordType=recordType;
		this.createdBy=createdBy;
		this.createdAt=createdAt;
		this.lastModifiedAt=lastModifiedAt;
		this.lastModifiedBy=lastModifiedBy;
		this.firstReminderDay=firstReminderDay;
		this.secondReminderDay=secondReminderDay;
		this.thirdReminderDay=thirdReminderDay;
		this.active=active;
		
	}
	/** Getter/Setter **/
	

	public int getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(int lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public int getFirstReminderDay() {
		return firstReminderDay;
	}

	public void setFirstReminderDay(int firstReminderDay) {
		this.firstReminderDay = firstReminderDay;
	}

	public int getSecondReminderDay() {
		return secondReminderDay;
	}

	public void setSecondReminderDay(int secondReminderDay) {
		this.secondReminderDay = secondReminderDay;
	}

	public int getThirdReminderDay() {
		return thirdReminderDay;
	}

	public void setThirdReminderDay(int thirdReminderDay) {
		this.thirdReminderDay = thirdReminderDay;
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


	

}