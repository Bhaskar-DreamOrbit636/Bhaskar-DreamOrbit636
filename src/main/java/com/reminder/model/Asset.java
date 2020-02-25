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

@Entity
@Table(name="asset")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "asset")
public class Asset implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in Equipment table--------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="asset_id")
    private int assetId;

	@Column(name="description")
    private String assetDescription;
	
	@Column(name="id")
	private String id;
	
/*	@Column(name = "to_list")
	private String toList;

	@Column(name = "cc_list")
	private String ccList;*/

	@Column(name = "additional_cc_list")
	private String additionalCcList;

	@Column(name = "remarks")
	private String remarks;
    
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="module_type_id", referencedColumnName="module_type_id")
    private ModuleType moduleType;


	//@JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="asset_type_id", referencedColumnName="asset_type_id")
	private AssetType assetType;
    
	//@JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="location_id", referencedColumnName="location_id")
   	private Location location;
	
//	@JsonIgnore
//	@JoinColumn(name = "user_group_id")
//	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	private Groups userGroupId;
	
	//@JsonBackReference
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "reminder_id")
	private Reminder reminder;
	

	
	public Asset()
    {
        super();
    }
    public Asset(int assetId, String assetDescription, int assetLocationId, int assetTypeId)
    {
        super();
        this.assetId = assetId;
        this.assetDescription = assetDescription;
        
    }
    
    /**      Getter/Setter     **/

	public int getAssetId() {
		return assetId;
	}
/*	public String getToList() {
		return toList;
	}
	public void setToList(String toList) {
		this.toList = toList;
	}*/
	public String getAdditionalCcList() {
		return additionalCcList;
	}
	public void setAdditionalCcList(String additionalCcList) {
		this.additionalCcList = additionalCcList;
	}
	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}
	public String getAssetDescription() {
		return assetDescription;
	}
	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	public Reminder getReminder() {
		return reminder;
	}
	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}
	
	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}
	public ModuleType getModuleType() {
		return moduleType;
	}
	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}	
/*	public String getCcList() {
		return ccList;
	}
	public void setCcList(String ccList) {
		this.ccList = ccList;
	}*/
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	

	@Override
    public String toString()
    {
        return "Asset [AssetId=" + assetId + "AssetDescription=" + assetDescription  +  "ModuleId="+ "]";
    }
}