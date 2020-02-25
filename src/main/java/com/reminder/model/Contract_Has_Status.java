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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="contract_has_status")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Contract_Has_Status")
public class Contract_Has_Status implements Serializable {
	private static final long serialVersionUID = -1232308999408322328L;

	// ----- all database columns in Contract_Has_Status table--------
//	@EmbeddedId
//	private ContractHasStatusId id;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "contract_has_status_id")
	private int ContractHasStatusId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="contract_id")
	private Contract contract;
	
	// --------- mapping with Contract_Status -----------
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name= "contract_status_id")
	private Contract_Status contractStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User userId;
	
	/*@Column(name="contact_reviewer_id")
	private int userId;*/
	
	@Column(name="comment")
	private String comment;
	
	

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Contract_Has_Status() {
		super();
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	/*public ContractHasStatusId getId() {
		return id;
	}*/

	public Contract_Status getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(Contract_Status contractStatus) {
		this.contractStatus = contractStatus;
	}

/*	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}*/

	@Override
	public String toString() {
		return "contract_status returned";
	}
	
	/*public Contract_Has_Status(Contract Contract_Id,Contract_Status Contract_Status_Id){
    	this.contract=Contract_Id;
    	this.contractStatus=Contract_Status_Id;
    //	this.ContractHasStatusId = ContractHasStatusId;
    }*/

	public int getContractHasStatusId() {
		return ContractHasStatusId;
	}

	public void setContractHasStatusId(int contractHasStatusId) {
		ContractHasStatusId = contractHasStatusId;
	}
	
}
