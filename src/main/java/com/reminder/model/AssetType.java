package com.reminder.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
@Table(name="asset_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "asset_type")
public class AssetType implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in Equipment table--------
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="asset_type_id")
    private int assetTypeId;
    
    @Column(name="type")
    private String assetType;
    
//    @Column(name="parent_asset_type_id")
//    private int parentAssetTypeId;
    
    @Column(name="created_by_id")
    private int createdById;
    
     @Column(name="created_at")
    private DateTime createdAt;
     
     @Column(name="last_modified_by_id")
     private int lastModifiedById;
     
     @Column(name="last_modified_at")
     private DateTime lastModifiedAt;
     
     @Column(name="active")
     private Boolean active;
     
 	@Column(name = "first_reminder_day")
 	private int firstReminderDay;

 	@Column(name = "second_reminder_day")
 	private int secondReminderDay;

 	@Column(name = "third_reminder_day")
 	private int thirdReminderDay;
     
	/*@JsonIgnore
 	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="assetType")
 	private Set<Asset> asset;*/
	
 	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="parent_asset_type_id")
    private AssetType parentAssetType;
	
	//@JsonIgnore
	@OneToMany(mappedBy="parentAssetType", cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	Set<AssetType> assettypes = new HashSet<>();
	
	 @PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
   
	public AssetType()
    {
        super();
    }
    public AssetType(int assetTypeId, String assetType, int parentAssetTypeId, int createdById,DateTime createdAt,int lastModifiedById,DateTime lastModifiedAt,Boolean active)
    {
        super();
        this.assetTypeId = assetTypeId;
        this.assetType = assetType;
       // this.parentAssetTypeId = parentAssetTypeId;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.lastModifiedById = lastModifiedById;
        this.lastModifiedAt = lastModifiedAt;
        this.active = active;
    }
    
    /**      Getter/Setter     **/

	public Set<AssetType> getAssettypes() {
		return assettypes;
	}
	public void setAssettypes(Set<AssetType> assettypes) {
		this.assettypes = assettypes;
	}
	public AssetType getParentAssetType() {
		return parentAssetType;
	}
	public void setParentAssetType(AssetType parentAssetType) {
		this.parentAssetType = parentAssetType;
	}
    public int getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
//	public int getParentAssetTypeId() {
//		return parentAssetTypeId;
//	}
//	public void setParentAssetTypeId(int parentAssetTypeId) {
//		this.parentAssetTypeId = parentAssetTypeId;
//	}
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
	
/*	public Set<Asset> getAsset() {
		return asset;
	}
	public void setAsset(Set<Asset> asset) {
		this.asset = asset;
	}*/
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
	
	@Override
    public String toString()
    {
        return "AssetType [assetTypeId=" + assetTypeId + "assetType=" + assetType  + "]";
    }
	
}
