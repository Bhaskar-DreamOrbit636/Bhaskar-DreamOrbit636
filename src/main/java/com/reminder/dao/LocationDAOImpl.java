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

import com.reminder.model.Location;
import com.reminder.response.model.LocationResponse;

@Repository
public class LocationDAOImpl implements LocationDAO
{
	private Logger logger = Logger.getLogger(LocationDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void createLocation(Location location)
    {
    	try {
			logger.info("inside the createLocation() method");
			entityManager.persist(location);
		} catch (HibernateException e) {
			logger.error("Location not created due to error : " + e);
		}
    }

    @Override
    public Location getLocationById(int locationId)
    {
    	Location location = null;
    	try {
			logger.info("fetching Location details of equipmentId :"+locationId);
			location = entityManager.find(Location.class,locationId);
		} catch (HibernateException e) {
			logger.error("No location fetched due to error : " + e);
		}
        return location;
    }

    /*@Override
    public List<Location> getAllLocations()
    {
    	List<Location> Location = null;
    	try {
			logger.info("inside the getAllAssetLocations() method");
			equipments = entityManager.createQuery("select c from Location c").getResultList();
		} catch (HibernateException e) {
			logger.error("No Location found and exception occurred : " + e);
		}
        return asset;
    }*/
    
    @SuppressWarnings("unchecked")
	@Override
    public LocationResponse getAllLocations(String sort_by, String order, String locationDescription, Integer limit, Integer page_no)
    {
    	
    	LocationResponse loc = new LocationResponse();
    	String searchCriteria="";
    	String sort ="";
    	int offset = 0;
    	int maxResult = 0;
    	
    	if(limit != null && page_no != null){
     		maxResult = limit;
     		offset=limit*(page_no-1);
    	}
    	else if(limit != null && page_no == null){
     		maxResult = limit;
     		offset=0;
    	}
    	if((locationDescription) != null)
    	searchCriteria = " WHERE LOWER(a.locationName) LIKE('%"+locationDescription.toLowerCase()+"%') OR"
    			+ " LOWER(a.active) LIKE('%"+locationDescription.toLowerCase()+"%')";
    	else
    		searchCriteria="";
    	System.out.println("********searchCriteria*******"+searchCriteria);
    	if((sort_by) != null)
    	sort =" ORDER BY "+sort_by+" "+order;
    	else
    		sort = "";
    	List<Location> locations = null;
    	Long count = (Long) entityManager.createQuery("select count(a.locationId) from Location a"+searchCriteria)
    			.getSingleResult();
    	
       	try {
       		logger.info("inside the getAllLocations() method");
       		if(maxResult==0){
       		locations = entityManager.createQuery("select a from Location a"+searchCriteria+sort)
   											   .setFirstResult(offset)
   					                           .getResultList();
       		}else{
       			locations = entityManager.createQuery("select a from Location a"+searchCriteria+sort)
						   .setFirstResult(offset)
                           .setMaxResults(maxResult)
                           .getResultList();
       		}
   		} catch (HibernateException e) {
   			logger.error("No Location found : " + e);
   		}
       loc.setLocation(locations);
       loc.setCount(count);
         return loc;
       }

    @Override
    public void updateLocation(Location location)
    {
        try {
			logger.info("inside the updateLocation() method");
			Location lc = getLocationById(location.getLocationId());
			location.setLastModifiedById(lc.getLastModifiedById());
			entityManager.merge(location);
		} catch (HibernateException e) {
			logger.error("Location not updated due to error : " + e);
		}
    }

    @Override
    public void deleteLocation(int locationId)
    {
		try {
			logger.info("inside the deleteLocation() method and deleting detail of LocationId : " + locationId);
			int loc = entityManager.createNativeQuery("delete from location  where location_id=:locationId")
					.setParameter("locationId",locationId)
					.executeUpdate();
		} catch (HibernateException e) {
			logger.error("Location details is not deleted, error : " + e);
		}
	}
}
