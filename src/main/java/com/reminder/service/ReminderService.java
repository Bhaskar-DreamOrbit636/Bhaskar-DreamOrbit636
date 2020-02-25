package com.reminder.service;

import java.util.List;

import com.reminder.model.Groups;
import com.reminder.model.Reminder;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ReminderRequest;


public interface ReminderService
{
    public void createReminder(Reminder reminder);
    
    public Reminder getReminderById(int reminderId);
    
    public List<Reminder> getAllReminder(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no);
    
    public void updateReminder(Reminder reminder);
    
    public void deleteReminder(int reminderId);
    
    public Reminder getContractReminderById(int reminderId);
    
    public Reminder createReminder(ReminderRequest reminder);
    		
    public Reminder createReminder(ReminderRequest reminder, Groups group);

	public Reminder createReminder(ContractRequest contract);
}
