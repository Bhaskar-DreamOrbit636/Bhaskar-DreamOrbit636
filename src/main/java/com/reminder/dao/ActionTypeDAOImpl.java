package com.reminder.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.ActionType;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ActionTypeDAOImpl implements ActionTypeDAO {
	
	private Logger logger = Logger.getLogger(ActionTypeDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;

	@Override
	public ActionType find(int id) {
		ActionType actionType = null;
    	try {
			logger.info("fetching group details of ActionTypeId :"+id);
			actionType = entityManager.find(ActionType.class,id);
		} catch (HibernateException e) {
			logger.error("No group fetched due to error : " + e);
		}
        return actionType;
	}

}
