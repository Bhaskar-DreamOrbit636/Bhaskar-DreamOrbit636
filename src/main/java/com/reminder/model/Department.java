package com.reminder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="department")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "department")
public class Department implements Serializable
{
    private static final long serialVersionUID = -1232395859408322328L;

    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="department_id")
    private int departmentId;
    
    @Column(name="department_name")
    private String departmentName;
	
    
	public Department()
    {
        super();
    }
    public Department(int departmentId, String departmentName)
    {
        super();
        this.departmentId = departmentId;
        this.departmentName = departmentName;
       
    }
    
    // ---------- setter/getter------------------------
    
    
    
	public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	
	@Override
    public String toString()
    {
        return "Department [departmentId=" + departmentId + ", departmentName=" + departmentName + "]";
    }

}
