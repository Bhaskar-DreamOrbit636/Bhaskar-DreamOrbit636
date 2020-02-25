package com.reminder.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="module_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Module_Type")
@Cacheable
public class ModuleType implements Serializable
{
    private static final long serialVersionUID = -1232308999408322328L;

    
    //----- all database columns in User table--------
    @Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="module_type_id")
    private int moduleTypeId;
    
    @Column(name="moduletype")
    private String moduleType;
    
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy="moduleType" )
	private Set<Groups> groups;
    
	public Set<Groups> getGroups() {
		return groups;
	}
	public void setGroups(Set<Groups> groups) {
		this.groups = groups;
	}
	public ModuleType()
    {
        super();
    }
    public ModuleType(int moduleTypeId,String moduleType)
    {
        super();
        this.moduleTypeId = moduleTypeId;
        this.moduleType = moduleType;
    }
    
    // ---------- setter/getter------------------------
    
	public int getModuleTypeId() {
		return moduleTypeId;
	}
	public void setModuleTypeId(int moduleTypeId) {
		this.moduleTypeId = moduleTypeId;
	}
	public String getModuleType() {
		return moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	
   
	

	@Override
	public String toString() {
		return "Module_Type [moduleTypeId=" + moduleTypeId + ", moduleType=" + moduleType + "]";
	}


}
