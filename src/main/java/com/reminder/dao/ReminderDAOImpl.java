package com.reminder.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.Reminder;


@Repository
public class ReminderDAOImpl implements ReminderDAO
{
	private Logger logger = Logger.getLogger(DepartmentDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void createReminder(Reminder reminder)
    {
    	try {
			logger.info("inside the createReminder() method");
			   
			//entityManager.clear();
	/*		
			Object obj =entityManager.createNativeQuery("select gru.group_id from groups gru where gru.group_id=:groupId ").
			setParameter("groupId", reminder.getUserGroupId().getGroupId()).getSingleResult();
			
			Groups groups = new Groups();
			groups.setGroupId((int)obj);*/
		/*	Groups groups = entityManager.find(Groups.class,reminder.getUserGroupId().getGroupId());
			
			reminder.setUserGroupId(groups);*/
			
			reminder = entityManager.merge(reminder);
		
		} catch (Exception e) {
			logger.error("Reminder not created due to error : " + e);
		}
    }

    @Override
    public Reminder getReminderById(int reminderId)
    {
    	Reminder reminder = null;
    	try {
			logger.info("fetching Department details of roleId :"+reminderId);
			reminder = entityManager.find(Reminder.class,reminderId);
		} catch (HibernateException e) {
			logger.error("No Department fetched due to error : " + e);
		}
        return reminder;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Reminder> getAllReminder(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no)
    {
    	List<Reminder> reminders = null;
    	try {
			logger.info("inside the getAllReminders() method");
			reminders = entityManager.createQuery("select d from Reminder d").getResultList();
		} catch (HibernateException e) {
			logger.error("No Reminder found and exception ocurred : " + e);
		}
        return reminders;
    }

    @Override
    public void updateReminder(Reminder reminder)
    {
    	try {
			logger.info("inside the updateDepartment() method");
			entityManager.merge(reminder);
		} catch (HibernateException e) {
			logger.error("reminder not updated due to Hibernate-exception : " + e);
			entityManager.flush();
			entityManager.close();
		}
    }

    @Override
    public void deleteReminder(int reminderId)
    {
    	try {
			logger.info("inside the deleteReminder() method and deleting detail of Reminderid : " + reminderId);
			Reminder d = entityManager.find(Reminder.class, reminderId);
			entityManager.remove(d);
		} catch (HibernateException e) {
			logger.error("Reminder details is not deleted : " + e);
		}
    }
    
    
    @Override
    public Reminder getContractReminderById(int reminderId)
    {
    	Reminder reminder = null;
    	try {
			logger.info("fetching Department details of roleId :"+reminderId);
			reminder = entityManager.find(Reminder.class,reminderId);
		} catch (HibernateException e) {
			logger.error("No Department fetched due to error : " + e);
		}
        return reminder;
    }
    
    
   
}

