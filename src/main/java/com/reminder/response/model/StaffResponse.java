package com.reminder.response.model;

import java.util.List;

import com.reminder.model.RecordType;
import com.reminder.model.Staff;
import com.reminder.model.StaffRecord;

public class StaffResponse {

	private StaffRecord staffRec;
	private int groupId;
	private String groupName;
	private Long count;
	private String LastModifiedBy;
	private String CreatedBy;
	private Staff staff;
	private List<RecordType> recordType;
	private List<Staff> listStaff;
	//private String nric_fin;
	private String refrence;
	private List<String> actions;
	private String DOB;
	private String decriptedDOB;
	private String decriptedNric;
	private String decriptedRefrenceNumber;
	
	
	public String getDecriptedRefrenceNumber() {
		return decriptedRefrenceNumber;
	}

	public void setDecriptedRefrenceNumber(String decriptedRefrenceNumber) {
		this.decriptedRefrenceNumber = decriptedRefrenceNumber;
	}

	public String getDecriptedDOB() {
		return decriptedDOB;
	}

	public void setDecriptedDOB(String decriptedDOB) {
		this.decriptedDOB = decriptedDOB;
	}

	public String getDecriptedNric() {
		return decriptedNric;
	}

	public void setDecriptedNric(String decriptedNric) {
		this.decriptedNric = decriptedNric;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String dOB) {
		DOB = dOB;
	}

	public String getRefrence() {
		return refrence;
	}

	public void setRefrence(String refrence) {
		this.refrence = refrence;
	}

/*	public String getNric_fin() {
		return nric_fin;
	}

	public void setNric_fin(String nric_fin) {
		this.nric_fin = nric_fin;
	}*/

	public List<Staff> getListStaff() {
		return listStaff;
	}

	public void setListStaff(List<Staff> listStaff) {
		this.listStaff = listStaff;
	}

	public List<RecordType> getRecordType() {
		return recordType;
	}

	public void setRecordType(List<RecordType> recordType) {
		this.recordType = recordType;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getLastModifiedBy() {
		return LastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		LastModifiedBy = lastModifiedBy;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public StaffRecord getStaffRec() {
		return staffRec;
	}

	public void setStaffRec(StaffRecord staffRec) {
		this.staffRec = staffRec;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

}
