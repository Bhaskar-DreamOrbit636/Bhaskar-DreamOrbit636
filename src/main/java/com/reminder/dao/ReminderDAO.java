package com.reminder.dao;

import java.util.List;

import com.reminder.model.Reminder;

public interface ReminderDAO
{
    public void createReminder(Reminder reminder);
    
    public Reminder getReminderById(int reminderId);
    
    public void updateReminder(Reminder reminder);
    
    public void deleteReminder(int reminderId);
    
    public List<Reminder> getAllReminder(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no);
    
    
    public Reminder getContractReminderById(int reminderId);
    
}

