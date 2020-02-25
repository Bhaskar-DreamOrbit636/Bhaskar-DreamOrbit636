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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name = "staff", uniqueConstraints = @UniqueConstraint(columnNames = { "psa_staff_id" }))
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "staff")
public class Staff implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	// ----- all database columns in Equipment table--------
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "staff_id")
	private int staff_Id;

	@Column(name = "name")
	private String staffName;

	@Column(name = "designation")
	private String designation;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "ofo")
	private String ofo;

	@Column(name = "psa_staff_id")
	private String staffId;

	@Column(name = "date_of_joining")
	private DateTime dateOfJoining;

	@Column(name = "created_by_id")
	private int createdById;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_by_id")
	private int lastModifiedById;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;

	@Column(name = "active")
	private Boolean active;

	/*@Column(name = "NRIC_FIN")
	private String NRIC_FIN;*/

	@Type(type="boolean")
	@Column(name = "local_crew")
	private Boolean localCrew;

	@Transient
	private int groupId;

	// @JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "department_id", nullable = true)
	private Department departments;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "section_id", nullable = true)
	private Section sections;

	/*
	 * @JsonIgnore
	 * 
	 * @JoinColumn(name = "user_group_id")
	 * 
	 * @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE) private
	 * Groups userGroupId;
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = DateTimeUtil.now();
	}

	@PreUpdate
	protected void onUpdate() {
		lastModifiedAt = DateTimeUtil.now();
	}

	public Staff() {
		super();
	}

	public Staff(int staff_Id, String staffName, String designation, String dateOfBirth, String ofo, String staffId,
			DateTime dateOfJoining, int createdById, DateTime createdAt, int lastModifiedById, DateTime lastModifiedAt,
			Boolean active) {
		super();
		this.staff_Id = staff_Id;
		this.staffName = staffName;
		this.designation = designation;
		this.dateOfBirth = dateOfBirth;
		this.ofo = ofo;
		this.staffId = staffId;
		this.dateOfJoining = dateOfJoining;
		this.createdById = createdById;
		this.createdAt = createdAt;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedAt = lastModifiedAt;
		this.active = active;
	}

	/** Getter/Setter **/

	public int getStaff_Id() {
		return staff_Id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/*
	 * public Groups getUserGroupId() { return userGroupId; }
	 * 
	 * public void setUserGroupId(Groups userGroupId) { this.userGroupId =
	 * userGroupId; }
	 */

	public Section getSections() {
		return sections;
	}

	public void setSections(Section sections) {
		this.sections = sections;
	}

	public Boolean getLocalCrew() {
		return localCrew;
	}

	public void setLocalCrew(Boolean localCrew) {
		this.localCrew = localCrew;
	}

/*	public String getNRIC_FIN() {
		return NRIC_FIN;
	}

	public void setNRIC_FIN(String nRIC_FIN) {
		NRIC_FIN = nRIC_FIN;
	}*/

	public void setStaff_Id(int staff_Id) {
		this.staff_Id = staff_Id;
	}

	public String getStaffName() {
		return staffName;
	}

	/*
	 * public Set<StaffRecord> getStaffRecords() { return staffRecords; } public
	 * void setStaffRecords(Set<StaffRecord> staffRecords) { this.staffRecords =
	 * staffRecords; }
	 */
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getOfo() {
		return ofo;
	}

	public void setOfo(String ofo) {
		this.ofo = ofo;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public DateTime getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(DateTime dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Department getDepartments() {
		return departments;
	}

	public void setDepartments(Department departments) {
		this.departments = departments;
	}

	@Override
	public String toString() {
		return "Staff [Staff_Id=" + staffId + "staffName=" + staffName + "designation=" + designation + "]";
	}
}