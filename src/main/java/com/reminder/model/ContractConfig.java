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
@Table(name="contract_config")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contract_config")
public class ContractConfig implements Serializable
{
    private static final long serialVersionUID = -1232308999408322328L;

    
    //----- all database columns in User table--------
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="contract_config_id")
    private int contractConfigId;
    
    @Column(name="first_reminder_day")
    private int firstReminderDate;
    
    @Column(name="second_reminder_day")
    private int secondReminderDate;
    
    @Column(name="third_reminder_day")
    private int thirdReminderDate;
    
    @Column(name="created_by_id")
    private int createdById;
    
     @Column(name="created_at")
    private DateTime createdAt;
     
     @Column(name="last_modified_by_id")
     private int lastModifiedById;
     
     @Column(name="last_modified_at")
     private DateTime lastModifiedAt;
    
	 @PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
  
	public ContractConfig()
    {
        super();
    }
    public ContractConfig(int contractConfigId,int firstReminderDate,int secondReminderDate, int thirdReminderDate)
    {
        super();
        this.contractConfigId = contractConfigId;
        this.firstReminderDate = firstReminderDate;
        this.secondReminderDate = secondReminderDate;
        this.thirdReminderDate = thirdReminderDate;
    }
    
    // ---------- setter/getter------------------------
    
    
    public int getContractConfigId() {
		return contractConfigId;
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

	public void setContractConfigId(int contractConfigId) {
		this.contractConfigId = contractConfigId;
	}
	public int getFirstReminderDate() {
		return firstReminderDate;
	}
	public void setFirstReminderDate(int firstReminderDate) {
		this.firstReminderDate = firstReminderDate;
	}
	public int getSecondReminderDate() {
		return secondReminderDate;
	}
	public void setSecondReminderDate(int secondReminderDate) {
		this.secondReminderDate = secondReminderDate;
	}
	public int getThirdReminderDate() {
		return thirdReminderDate;
	}
	public void setThirdReminderDate(int thirdReminderDate) {
		this.thirdReminderDate = thirdReminderDate;
	}
	
//	public Boolean getActive() {
//		return active;
//	}
//	public void setActive(Boolean active) {
//		this.active = active;
//	}
   
	

	@Override
	public String toString() {
		return "Contract_Config [contractConfigId=" + contractConfigId + "]";
	}
	


}

