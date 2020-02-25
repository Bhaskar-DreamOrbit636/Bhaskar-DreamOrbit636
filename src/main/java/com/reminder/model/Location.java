package com.reminder.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name="location",
uniqueConstraints=
@UniqueConstraint(columnNames={"location_name"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "location")
public class Location implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    
    //----- all database columns in Equipment table--------
    
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="location_id")
    private int locationId;
    
    @Column(name="location_name")
    private String locationName;
    
    @Column(name="created_by_id")
    private int createdById;
    
    @Column(name="created_at")
    private DateTime createdAt;
    
	@Column(name = "last_modified_by_id")
	private int lastModifiedById;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;

	@Column(name = "active")
	private Boolean active;
	
	@JsonIgnore
 	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="location")
 	private Set<Asset> asset;
   
	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
    
    public Location()
    {
        super();
    }
    public Location(int locationId, String locationName, int createdById, DateTime createdAt,int lastModifiedById,DateTime lastModifiedAt,Boolean active)
    {
        super();
        this.locationId = locationId;
        this.locationName = locationName;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.lastModifiedById = lastModifiedById;
        this.lastModifiedAt = lastModifiedAt;
        this.active = active;
    }
    
	  
    
    /**      Getter/Setter     **/
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
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

	
	
	@Override
    public String toString()
    {
        return "Location [locationId=" + locationId + "locationName=" + locationName  + "createdById=" + createdById +  "]";
    }

}