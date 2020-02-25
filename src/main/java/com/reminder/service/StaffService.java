package com.reminder.service;

import java.util.Date;
import java.util.List;

import com.reminder.exception.DuplicateException;
import com.reminder.model.RecordType;
import com.reminder.model.Staff;
import com.reminder.model.StaffRecord;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.StaffRequest;
import com.reminder.request.model.StaffSearchCriteria;
import com.reminder.response.model.StaffResponse;

public interface StaffService {
	public void createStaff(Staff staff) throws DuplicateException;

	public StaffResponse getStaffById(int staffId);

	public void updateStaff(Staff staff) throws DuplicateException;

	public void deleteStaff(int staffId);

	public List<StaffRecord> getSaffRecordById(int staffid);

	public List<StaffResponse> getStaffByName(String staffName);

	public void createRecordType(RecordType record);

	public RecordType getRecord(int recordTypeId);

	public StaffResponse getAllRecord(String sort_by, String order, String searchCriteria, Integer limit,
			Integer page_no, boolean active);

	public void updateRecord(RecordType record);

	public void deleteRecord(int recordTypeId);

	public List<StaffResponse> getAllStaffs(String sort_by, String order, String searchCriteria, Integer limit,
			Integer page_no, int user_id);

	public StaffRecord createStaffReminder(StaffRequest r, User user);

	public List<StaffResponse> getStaffReminder(String sort_by, String order, String searchCriteria, Integer limit,
			Integer page_no, int userId);

	public List<StaffResponse> getStaffByPsId(String staffId);

	public StaffRecord updateStaffReminder(StaffRequest staffReq, int id, User createdUser);

	public StaffResponse getStaffreminderById(int staffRecId);

	public void deleteStaffReminder(int staffId, User createdUser);

	public List<Summary> getExpiryCalendar(int userId, Date date);

	public List<StaffResponse> searchStaff(String sort_by, String order, Integer limit, Integer page_no,
			StaffSearchCriteria staffSearchCriteria, int userId, String searchCriteria);

	public List<StaffResponse> getStaffByNRIC(String nric);

	public List<StaffResponse> getStaffByRefrenceNumber(String ref);

}