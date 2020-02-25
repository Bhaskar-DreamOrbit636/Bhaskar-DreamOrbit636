package com.reminder.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.controller.StaffController;
import com.reminder.dao.GroupDAO;
import com.reminder.dao.StaffDAO;
import com.reminder.exception.DuplicateException;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.RecordType;
import com.reminder.model.Reminder;
import com.reminder.model.Staff;
import com.reminder.model.StaffRecord;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.ReminderRequest;
import com.reminder.request.model.StaffRequest;
import com.reminder.request.model.StaffSearchCriteria;
import com.reminder.response.model.StaffResponse;
import com.reminder.utils.CurrentDate;
import com.reminder.utils.NRICSecurity;



@Service
@Transactional(propagation = Propagation.REQUIRED)
public class StaffServiceImpl implements StaffService
{
	@Autowired
    private StaffDAO staffDAO;

	@Autowired
    private GroupDAO groupDAO;
	
	@Autowired
	private ReminderService reminderService;
	
	
	private CurrentDate date = new CurrentDate();
	
	private Logger logger = Logger.getLogger(StaffController.class);
	
	  @PersistenceContext
		private EntityManager entityManager;
	
	@Override
    public void createStaff(Staff staff) throws DuplicateException
    {		
    	staffDAO.createStaff(staff);
    }

    @Override
    public StaffResponse getStaffById(int staffId)
    {
        return staffDAO.getStaffById(staffId);
    }
    
    @Override
    public List<StaffResponse> getStaffByName(String staffName)
    {
        return staffDAO.getStaffByName(staffName);
    }

	@Override
	public List<StaffResponse> getStaffByNRIC(String nric) {
		return staffDAO.getStaffByNRIC(nric);
	}

    @Override
    public List<StaffResponse> getAllStaffs(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, int user_id)
    {
        return staffDAO.getAllStaffs(sort_by,order,searchCriteria,limit,page_no,user_id);
    }

    @Override
    public void updateStaff(Staff staff) throws DuplicateException
    {
    	staffDAO.updateStaff(staff);
    }

    @Override
    public void deleteStaff(int staffId)
    {
    	staffDAO.deleteStaff(staffId);
    }

	@Override
	public List<StaffRecord> getSaffRecordById(int staffid) {
		return staffDAO.getStaffRecordById(staffid);
		
	}

	@Override
	public void createRecordType(RecordType record) {
		staffDAO.createRecordType(record);
	}

	@Override
	public RecordType getRecord(int recordTypeId) {
		return staffDAO.getRecord(recordTypeId);
	}

	@Override
	public StaffResponse getAllRecord(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no,
			boolean active) {
		return staffDAO.getAllRecord(sort_by, order, searchCriteria, limit, page_no, active);
	}

	@Override
	public void updateRecord(RecordType record) {
		staffDAO.updateRecord(record);
		
	}

	@Override
	public void deleteRecord(int recordTypeId) {
		staffDAO.deleteRecord(recordTypeId);
		
	}

	@Override
	public StaffRecord createStaffReminder(StaffRequest staffReq, User user) {
		entityManager.clear();
		StaffRecord entity = new StaffRecord();
		
		Groups group = groupDAO.getGroupById(staffReq.getGroupId());
		
		ModuleType moduleType = entityManager.find(ModuleType.class,staffReq.getModuleTypeId()); 
    	entity.setModuleType(moduleType);
    	
    	Staff staff = entityManager.find(Staff.class,staffReq.getStaffId());
    	entity.setStaffs(staff);
    	
    	RecordType record = entityManager.find(RecordType.class,staffReq.getRecordId());
    	entity.setRecordType(record);
    	
    	//encrypting reference number
    	String refrence = staffReq.getRefrenceNumber();
    	if(refrence != null){
    	String encrypt = new NRICSecurity().encrypt(refrence);
    	entity.setReferenceNumber(encrypt);
    	}else{
    		entity.setReferenceNumber("");
    	}
    	entity.setValidityPeriod(staffReq.getValidPeriod());
    	entity.setAdditionalCcList(staffReq.getAdditionalCcList());
    	//entity.setCcList(staffReq.getCcList());
    	//entity.setToList(staffReq.getToList());
    
    	
    	ReminderRequest reminder = staffReq.getReminderReq();
	   	Reminder reminderEntity = new Reminder();
	   	
    	reminderEntity.setUserGroupId(group);
    	reminderEntity.setEffectiveStartDate(reminder.getEffectiveStartDate());;
    	reminderEntity.setEffectiveExpiryDate(reminder.getEffectiveExpiryDate());
    	reminderEntity.setRemarks(reminder.getRemarks());
    	reminderEntity.setFirstReminderDate(reminder.getFirstReminderDate());
    	reminderEntity.setSecondReminderDate(reminder.getSecondReminderDate());
    	reminderEntity.setThirdReminderDate(reminder.getThirdReminderDate());
    	reminderEntity.setActive(reminder.getActive());
    	reminderEntity.setCreatedById(user.getUserId());
    	reminderEntity.setLastModifiedById(user.getUserId());
    	reminderEntity.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
    	reminderEntity.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
    	reminderEntity.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
    	//reminderEntity.setStatusId(reminder.getStatusId());
    	reminderEntity.setActive(reminder.getActive());
    	reminderEntity.setAddCcListExpiryReminder(reminder.getAddCcListExpiryReminder());
    	reminderEntity.setAddCcListLastReminder(reminder.getAddCcListLastReminder());
    	//reminderEntity.setCcListExpiryReminder(reminder.getCcListExpiryReminder());
    	//reminderEntity.setCcListLastReminder(reminder.getCcListLastReminder());
    	Reminder reminderSaved = entityManager.merge(reminderEntity);
    	entity.setReminder(reminderSaved);
    	
		logger.info("createStaffReminder(staffReq=" + staffReq + ", user=" + user + ") - Staff reminder created at" + date.getCurrentDate() + " by " + user.getUserName());
    	
    	StaffRecord sr = staffDAO.createStaffReminder(entity);
    	return sr;
	}

	@Override
	public List<StaffResponse> getStaffReminder(String sort_by, String order, String searchCriteria, Integer limit,
			Integer page_no ,int userId) {
		return staffDAO.getStaffReminder(sort_by, order, searchCriteria, limit, page_no, userId);
	}

	@Override
	public List<StaffResponse> getStaffByPsId(String staffId) {
		return staffDAO.getStaffByPsId(staffId);
	}

	@Override
	public StaffRecord updateStaffReminder(StaffRequest staffReq, int id, User user) {
		entityManager.clear();
		StaffRecord entity = staffDAO.getStaffreminderByIdLocal(id);
		
		Groups group = groupDAO.getGroupById(staffReq.getGroupId());
		
		ModuleType moduleType = entityManager.find(ModuleType.class,staffReq.getModuleTypeId()); 
    	entity.setModuleType(moduleType);
    	
    	Staff staff = entityManager.find(Staff.class,staffReq.getStaffId());
    	entity.setStaffs(staff);
    	
    	RecordType record = entityManager.find(RecordType.class,staffReq.getRecordId());
    	entity.setRecordType(record);
    	
    	String reference = staffReq.getRefrenceNumber();
    	if(reference != null){
    	String encrypt = new NRICSecurity().encrypt(reference);
    	entity.setReferenceNumber(encrypt);
    	}else{
    		String ref = entity.getReferenceNumber();
    		entity.setReferenceNumber(ref);
    	}
    	//entity.setReferenceNumber(reference);
    	entity.setValidityPeriod(staffReq.getValidPeriod());
    	entity.setAdditionalCcList(staffReq.getAdditionalCcList());
    	//entity.setCcList(staffReq.getCcList());
    	//entity.setToList(staffReq.getToList());
    	
    	if(staffReq.getReminderReq() != null){
        	Reminder rm = reminderService.getReminderById(entity.getReminder().getReminderId());
        	rm.setUserGroupId(group);
        	rm.setLastModifiedById(user.getUserId());
        	rm.setActive(staffReq.getReminderReq() .getActive());
        	rm.setEffectiveStartDate(staffReq.getReminderReq() .getEffectiveStartDate());;
        	rm.setRemarks(staffReq.getReminderReq() .getRemarks());
        	rm.setFirstReminderDate(staffReq.getReminderReq() .getFirstReminderDate());
        	rm.setSecondReminderDate(staffReq.getReminderReq() .getSecondReminderDate());
        	rm.setThirdReminderDate(staffReq.getReminderReq() .getThirdReminderDate());
        	//rm.setStatusId(staffReq.getReminderReq().getStatusId());
        	rm.setAddCcListExpiryReminder(staffReq.getReminderReq().getAddCcListExpiryReminder());
        	rm.setAddCcListLastReminder(staffReq.getReminderReq().getAddCcListLastReminder());
        	//rm.setCcListExpiryReminder(staffReq.getReminderReq().getCcListExpiryReminder());
        	//rm.setCcListLastReminder(staffReq.getReminderReq().getCcListLastReminder());
        	
        	if( rm.getEffectiveExpiryDate() != null && staffReq.getReminderReq().getEffectiveExpiryDate() != null){
        	if((staffReq.getReminderReq().getEffectiveExpiryDate()).compareTo(rm.getEffectiveExpiryDate())!=0){
        		rm.setFirstReminderSentAt(null);
        		rm.setSecondReminderSentAt(null);
        		rm.setThirdReminderSentAt(null);
        	}
        	}
        	if(staffReq.getReminderReq().getEffectiveExpiryDate() == null){
        		rm.setFirstReminderSentAt(null);
        		rm.setSecondReminderSentAt(null);
        		rm.setThirdReminderSentAt(null);
        	}
        	
        	rm.setEffectiveExpiryDate(staffReq.getReminderReq().getEffectiveExpiryDate());
        	//rm.setStatusId(staffReq.getReminderReq() .getStatusId());
        	reminderService.updateReminder(rm);
        	} 
        	
		StaffRecord returnStaffRecord = staffDAO.updateStaffReminder(entity);
		logger.info("updateStaffReminder(staffReq=" + staffReq + ", id=" + id + ", user=" + user + ") - end - Staff reminder updated at" + date.getCurrentDate() + " by " + user.getAdUserId() + "-" + user.getUserName() + " - return value=" + returnStaffRecord);
    	
        	return returnStaffRecord;
	}

	@Override
	public StaffResponse getStaffreminderById(int staffRecId) {
		
		return staffDAO.getStaffreminderById(staffRecId);
	}

	@Override
	public void deleteStaffReminder(int staffId, User user) {
		staffDAO.deleteStaffReminder(staffId, user);
		
	}

	@Override
	public List<Summary> getExpiryCalendar(int userId, Date date) {
		return staffDAO.getExpiryCalendar(userId,  date);
	}
	
	@Override
    public List<StaffResponse> searchStaff(String sort_by, String order, Integer limit, Integer page_no, StaffSearchCriteria staffSearchCriteria, int userId, String searchCriteria)
    {
		List<StaffResponse> staffResponseList = staffDAO.searchStaff(sort_by, order, limit, page_no, staffSearchCriteria, userId, searchCriteria);
	/*	if (staffResponseList != null) {
			for (StaffResponse staffResponse : staffResponseList) {
				List<String> actions = groupDAO.getGroupRolesAction(userId, staffResponse.getGroupId());
				staffResponse.setActions(actions);
			}
		}*/
        return staffResponseList;
    }

	@Override
	public List<StaffResponse> getStaffByRefrenceNumber(String ref) {
		return staffDAO.getStaffByRefrenceNumber(ref);
	}

    
  
}

