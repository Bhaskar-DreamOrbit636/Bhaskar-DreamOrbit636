package com.reminder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.ReminderDAO;
import com.reminder.model.Groups;
import com.reminder.model.Reminder;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ReminderRequest;


@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ReminderServiceImpl implements ReminderService
{

	@Autowired
    private ReminderDAO reminderDAO;
    
    @Override
    public void createReminder(Reminder reminder)
    {
    	reminderDAO.createReminder(reminder);
    }

    @Override
    public Reminder getReminderById(int reminderId)
    {
        return reminderDAO.getReminderById(reminderId);
    }

    /*@Override
    public List<Group1> getAllGroups(){
        return groupDAO.getAllGroups();
    }*/
    
    @Override
    public List<Reminder> getAllReminder(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no){
        return reminderDAO.getAllReminder(sort_by,order,searchCriteria,limit,page_no);
    }

    @Override
    public void updateReminder(Reminder reminder)
    {
    	reminderDAO.updateReminder(reminder);
    }

    @Override
    public void deleteReminder(int reminderId)
    {
    	reminderDAO.deleteReminder(reminderId);
    }
    
   
    @Override
    public Reminder getContractReminderById(int reminderId)
    {
        return reminderDAO.getContractReminderById(reminderId);
    }
    @Override
    public Reminder createReminder(ReminderRequest reminder )
    {
    	
    	Reminder entity = new Reminder();
    	entity.setEffectiveStartDate(reminder.getEffectiveStartDate());;
    	entity.setEffectiveExpiryDate(reminder.getEffectiveExpiryDate());
    	entity.setRemarks(reminder.getRemarks());
    	entity.setFirstReminderDate(reminder.getFirstReminderDate());
    	entity.setSecondReminderDate(reminder.getSecondReminderDate());
    	entity.setThirdReminderDate(reminder.getThirdReminderDate());
    	entity.setActive(reminder.getActive());
    	//entity.setCreatedById(reminder.get);
    	//entity.setCreatedAt(reminder.get);
    	//entity.setLastModifiedById(reminder.get);
    	//entity.setLastModifiedAt(reminder.get);
    	entity.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
    	entity.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
    	entity.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
    	//entity.setStatusId(reminder.getStatusId());
		reminderDAO.createReminder(entity);
		return entity;
    }
    
    
    @Override
    public Reminder createReminder(ReminderRequest reminder ,Groups group)
    {
    	
    	Reminder entity = new Reminder();
    	//entity.setUserGroupId(group);
    	entity.setEffectiveStartDate(reminder.getEffectiveStartDate());;
    	entity.setEffectiveExpiryDate(reminder.getEffectiveExpiryDate());
    	entity.setRemarks(reminder.getRemarks());
    	entity.setFirstReminderDate(reminder.getFirstReminderDate());
    	entity.setSecondReminderDate(reminder.getSecondReminderDate());
    	entity.setThirdReminderDate(reminder.getThirdReminderDate());
    	entity.setActive(reminder.getActive());
    	//entity.setCreatedById(reminder.get);
    	//entity.setCreatedAt(reminder.get);
    	//entity.setLastModifiedById(reminder.get);
    	//entity.setLastModifiedAt(reminder.get);
    	entity.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
    	entity.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
    	entity.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
    	//entity.setStatusId(reminder.getStatusId());
		reminderDAO.createReminder(entity);
		return entity;
    }

	@Override
	public Reminder createReminder(ContractRequest contract) {
		ReminderRequest reminder = contract.getReminder();
	   	Reminder entity = new Reminder();
	   	
	   	Groups group = new Groups();
	   	group.setGroupId(contract.getGroupId());
	   	
    	//entity.setUserGroupId(group);
    	entity.setEffectiveStartDate(reminder.getEffectiveStartDate());;
    	entity.setEffectiveExpiryDate(reminder.getEffectiveExpiryDate());
    	entity.setRemarks(reminder.getRemarks());
    	entity.setFirstReminderDate(reminder.getFirstReminderDate());
    	entity.setSecondReminderDate(reminder.getSecondReminderDate());
    	entity.setThirdReminderDate(reminder.getThirdReminderDate());
    	entity.setActive(reminder.getActive());
    	//entity.setCreatedById(reminder.get);
    	//entity.setCreatedAt(reminder.get);
    	//entity.setLastModifiedById(reminder.get);
    	//entity.setLastModifiedAt(reminder.get);
    	entity.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
    	entity.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
    	entity.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
    	//entity.setStatusId(reminder.getStatusId());
		reminderDAO.createReminder(entity);
		return entity;
	}
}
