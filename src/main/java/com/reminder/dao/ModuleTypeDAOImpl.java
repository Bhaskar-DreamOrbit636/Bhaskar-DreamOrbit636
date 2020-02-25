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

import com.reminder.model.ModuleType;


@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ModuleTypeDAOImpl implements ModuleTypeDao
{
	private Logger logger = Logger.getLogger(ModuleTypeDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public void createModuleType(ModuleType moduleType)
    {
		try {
			logger.info("inside the createGroup() method");
			entityManager.persist(moduleType);
		} catch (HibernateException e) {
			logger.error("Group not created due to error : " , e);
		}
    }

    @Override
    public ModuleType getModuleType(String moduleName) {
    	
    	ModuleType moduleType = null;
    	try {
			logger.info("fetching group details of moduleTypeId :"+ moduleName);
			moduleType = (ModuleType) entityManager.createQuery("select m from ModuleType m where m.moduleType=:moduleName")
					    .setParameter("moduleName", moduleName).getSingleResult();
		} catch (HibernateException e) {
			logger.error("No module fetched due to error : " + e);
		}catch (Exception e) {
			logger.error("No module fetched due to error : " + e);
		}
        return moduleType;
    }
    
    @Override
    public ModuleType getModuleTypeId(int moduleTypeId)
    {
    	
    	ModuleType moduleType = null;
    	try {
			logger.info("fetching group details of moduleTypeId :"+ moduleTypeId);
			moduleType = entityManager.find(ModuleType.class,moduleTypeId);
		} catch (HibernateException e) {
			logger.error("No group fetched due to error : " + e);
		}
        return moduleType;
    }
    
    
    /*@SuppressWarnings("unchecked")
	@Override
    public List<Group1> getAllGroups()
    {
    	List<Group1> groups = null;
    	try {
			logger.info("inside the getAllGroups() method");
			groups = entityManager.createQuery("select g from Group1 g").getResultList();
		} catch (HibernateException e) {
			logger.error("No contract found and exception ocurred : " + e);
		}
        return groups;
    }*/
    
    @SuppressWarnings({ "unchecked", "null" })
	@Override
    public List<ModuleType> getAllModuleType(String sort_by, String order, String type, Integer limit, Integer page_no)
    {
        String searchCriteria="";
    	String sort ="";
    	
    	int offset = 0;
    	int maxResult = 10;
    	
    	if(limit != null && page_no != null){
     		maxResult = limit;
     		offset=limit*(page_no-1);
    	}
    	else if(limit != null && page_no == null){
     		maxResult = limit;
     		offset=0;
    	}
    	
    	if((type) != null )
    	searchCriteria = " WHERE LOWER(groupName) LIKE('%"+type.toLowerCase()+"%')";
    	else
    		searchCriteria="";
    	if((sort_by) != null)
    	sort =" ORDER BY "+sort_by+" "+order;
    	else
    		sort = "";
        List<ModuleType> moduleTypes = null;
       	try {
       		
   			logger.info("inside the getAllModuleType() method");
   			moduleTypes = entityManager.createQuery("select g from Groups g"+searchCriteria+sort)
   											   .setFirstResult(offset)
   					                           .setMaxResults(maxResult)
   					                           .getResultList();
   		} catch (HibernateException e) {
   			logger.error("No contract found : " + e);
   		}
           return moduleTypes;
       }


    @Override
    public void updateModuleType(ModuleType moduleType)
    {
    	try {
			logger.info("inside the updateGroup() method");
			entityManager.merge(moduleType);
		} catch (HibernateException e) {
			logger.error("Group not updated due to Hibernate-exception : " + e);
		}
    }

    @Override
    public void deleteModuleType(int moduleTypeId)
    {
    	try {
			logger.info("inside the deleteGroup() method and deleting detail of contract : " + moduleTypeId);
			ModuleType g = entityManager.find(ModuleType.class, moduleTypeId);
			entityManager.remove(g);
		} catch (HibernateException e) {
			logger.error("Group details is not deleted : " + e);
		}
    }

}

