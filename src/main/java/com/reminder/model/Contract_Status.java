package com.reminder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="contract_status")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Contract_Status")
public class Contract_Status implements Serializable
{
    private static final long serialVersionUID = -1232308999408322328L;

    
    //----- all database columns in Contract_Has_Status table--------
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="contract_status_id")
    private int contractStatusId;
    
    @Column(name="status")
    private String status;
    
    @OneToMany(mappedBy = "contractStatus")
    private List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
    
    
   
	public Contract_Status()
    {
        super();
    }
    public Contract_Status(int contractStatusId,String status)
    {
        super();
        this.contractStatusId = contractStatusId;
        this.status = status;
    }
    
    // ---------- setter/getter------------------------
	public int getContractStatusId() {
		return contractStatusId;
	}
	public void setContractStatusId(int contractStatusId) {
		this.contractStatusId = contractStatusId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Contract_Status [contractStatusId=" + contractStatusId + "]";
	}


	


}


