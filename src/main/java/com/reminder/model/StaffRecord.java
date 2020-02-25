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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name="staff_record")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "staff_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffRecord implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in Staff_Record table--------
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="staff_record_id")
    private int staffRecordId; 
    
    @Column(name="reference_number")
    private String referenceNumber;
    
    @Column(name="validity_period")
    private String validityPeriod;
    
/*	@Column(name = "to_list")
	private String toList;

	@Column(name = "cc_list")
	private String ccList;*/

	@Column(name = "additional_cc_list")
	private String additionalCcList;
	
	//@JsonBackReference
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "reminder_id")
	private Reminder reminder;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="staff_id", referencedColumnName="staff_id")
    private Staff staffs;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="module_type_id", referencedColumnName="module_type_id")
    private ModuleType moduleType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="record_type_id", referencedColumnName="record_type_id")
    private RecordType recordType;


	
    
//    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.REMOVE)
//	@JoinColumn(name = "record_type_id", referencedColumnName = "record_type_id")
//	private RecordType rocrdType;
    
	public StaffRecord()
    {
        super();
    }
    public StaffRecord(int staffRecordId, String referenceNumber, String validityPeriod, Staff staffs)
    {
        super();
        this.staffRecordId = staffRecordId;
        this.referenceNumber = referenceNumber;
        this.validityPeriod = validityPeriod;
        this.staffs = staffs;
    
    }
    
    /**      Getter/Setter     **/

    
	public int getStaffRecordId() {
		return staffRecordId;
	}
/*	public String getToList() {
		return toList;
	}
	public void setToList(String toList) {
		this.toList = toList;
	}
	public String getCcList() {
		return ccList;
	}
	public void setCcList(String ccList) {
		this.ccList = ccList;
	}*/
	public String getAdditionalCcList() {
		return additionalCcList;
	}
	public void setAdditionalCcList(String additionalCcList) {
		this.additionalCcList = additionalCcList;
	}
	public ModuleType getModuleType() {
		return moduleType;
	}
	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}
	public RecordType getRecordType() {
		return recordType;
	}
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
 
	public void setStaffRecordId(int staffRecordId) {
		this.staffRecordId = staffRecordId;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getValidityPeriod() {
		return validityPeriod;
	}
	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	public Staff getStaffs() {
		return staffs;
	}
	public void setStaffs(Staff staffs) {
		this.staffs = staffs;
	}
	public Reminder getReminder() {
		return reminder;
	}
	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}


	@Override
    public String toString()
    {
        return "StaffRecord [staffRecordId=" + staffRecordId + "referenceNumber=" + referenceNumber  + "validityPeriod=" + validityPeriod + "]";
    }
}
