package com.reminder.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "contract")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contract")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract implements Serializable {
	private static final long serialVersionUID = -1232395859408322328L;

	// ----- all database columns in Contract table--------
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "contract_id")
	private int contractId;
	
	@Column(name = "contract_reference_number")
	private String contractReferenceNumber;

	@Column(name = "contract_title")
	private String contractTitle;

	@Column(name = "description")
	private String description;

	@Column(name = "ba_po_number")
	private int baPoNumber;

	@Column(name = "supplier")
	private String supplier;

	@Column(name = "contract_value_currency")
	private String contractValueCurrency;

	@Column(name = "contract_value")
	private String contractValue;

	@Column(name = "performance_bond_submission")
	private String performanceBondSubmission;

	@Column(name = "option_year")
	private DateTime optionYear;

	@Column(name = "plp_expiry_date")
	private DateTime plpExpiryDate;

	@Column(name = "wcp_expiry_date")
	private DateTime wcpExpiryDate;

	@Column(name = "hnm_expiry_date")
	private DateTime hnmExpiryDate;

	@Column(name = "saving_currency")
	private String savingCurrency;

/*	@Column(name = "to_list")
	private String toList;

	@Column(name = "cc_list")
	private String ccList;*/

	@Column(name = "additional_cc_list")
	private String additionalCcList;
	
	
	@Column(name = "saving")
	private String saving;

	@Column(name = "version")
	private int version;
	
	@Column(name="is_verified")
	private Boolean isVerified;
	
	@Column(name="is_deleted")
	private Boolean isDeleted;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	@JsonIgnore
    @JoinColumn(name="parent_contract_id")
	private Contract parentContractId;

	@Column(name = "module_type_id")
	private int moduleTypeId;

	@Column(name="contract_status")
	private String contractStatus;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "reminder_id")
	private Reminder reminder;

	@JsonIgnore	
	@OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Contract_Reviewer> contractReviewer;

	@JsonIgnore
	@OneToMany(mappedBy = "contract",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Contract_Has_Status> contractHasStatus;
	
	@JsonIgnore
	@JoinColumn(name = "officer_in_charge_id")
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private User officerInChargeId;
	
	
	
	public Boolean getIsVerified() {
		return isVerified;
	}

	// ---------- setter/getter------------------------

	public List<Contract_Reviewer> getContractReviewer() {
		return contractReviewer;
	}
	public void setContractReviewer(List<Contract_Reviewer> contractReviewer) {
		this.contractReviewer = contractReviewer;
	}
	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	public String getContractTitle() {
		return contractTitle;
	}

	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle.replaceAll("\\r\\n|\\r|\\n", " ");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getBaPoNumber() {
		return baPoNumber;
	}

	public void setBaPoNumber(int baPoNumber) {
		this.baPoNumber = baPoNumber;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getContractValue() {
		return contractValue;
	}

	public void setContractValue(String contractValue) {
		this.contractValue = contractValue;
	}

	public String getPerformanceBondSubmission() {
		return performanceBondSubmission;
	}

	public void setPerformanceBondSubmission(String performanceBondSubmission) {
		this.performanceBondSubmission = performanceBondSubmission;
	}

	public DateTime getOptionYear() {
		return optionYear;
	}

	public void setOptionYear(DateTime optionYear) {
		this.optionYear = optionYear;
	}

	public User getOfficerInChargeId() {
		return officerInChargeId;
	}

	public void setOfficerInChargeId(User officerInChargeId) {
		this.officerInChargeId = officerInChargeId;
	}

	public DateTime getPlpExpiryDate() {
		return plpExpiryDate;
	}

	public void setPlpExpiryDate(DateTime plpExpiryDate) {
		this.plpExpiryDate = plpExpiryDate;
	}

	public DateTime getWcpExpiryDate() {
		return wcpExpiryDate;
	}

	public void setWcpExpiryDate(DateTime wcpExpiryDate) {
		this.wcpExpiryDate = wcpExpiryDate;
	}

	public DateTime getHnmExpiryDate() {
		return hnmExpiryDate;
	}

	public void setHnmExpiryDate(DateTime hnmExpiryDate) {
		this.hnmExpiryDate = hnmExpiryDate;
	}

	public String getSavingCurrency() {
		return savingCurrency;
	}

	public void setSavingCurrency(String savingCurrency) {
		this.savingCurrency = savingCurrency;
	}

	public String getSaving() {
		return saving;
	}

	public void setSaving(String saving) {
		this.saving = saving;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/*
	 * public int getReminderId() { return reminderId; } public void
	 * setReminderId(int reminderId) { this.reminderId = reminderId; }
	 */
	public int getModuleTypeId() {
		return moduleTypeId;
	}

	public void setModuleTypeId(int moduleTypeId) {
		this.moduleTypeId = moduleTypeId;
	}

	public String getContractValueCurrency() {
		return contractValueCurrency;
	}

	public void setContractValueCurrency(String contractValueCurrency) {
		this.contractValueCurrency = contractValueCurrency;
	}


	public Reminder getReminder() {
		return reminder;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}


	
	public void linkContractReviewer() {

		// System.out.println("*******Inside linkContractHasStatus
		// object**********"+contract_Has_Status.toString());
		if (this.contractId != 0)
			System.out.println("*******contractId >>>>>>>>>>>>>> ");
		for (Contract_Reviewer contractReviewer : contractReviewer) {
			contractReviewer.setContract(this);
		}
	}
	
	public void linkContractHasStatus() {
			
			//System.out.println("*******Inside linkContractHasStatus object**********"+contract_Has_Status.toString());
			if(this.contractId != 0)
				System.out.println("*******contractId >>>>>>>>>>>>>> ");
		    for (Contract_Has_Status contractHasStatus : contractHasStatus) {
		    	System.out.println("*******contractHasStatus********** "+contractHasStatus.toString());
		    	contractHasStatus.setContract(this);
		    }
		}

	@Override
	public String toString() {
		return "Contract [ContractNumber=" + contractId + "]";
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

	public Contract getParentContractId() {
		return parentContractId;
	}

	public void setParentContractId(Contract parentContractId) {
		this.parentContractId = parentContractId;
	}



	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public List<Contract_Has_Status> getContractHasStatus() {
		return contractHasStatus;
	}

	public void setContractHasStatus(List<Contract_Has_Status> contractHasStatus) {
		this.contractHasStatus = contractHasStatus;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	

}