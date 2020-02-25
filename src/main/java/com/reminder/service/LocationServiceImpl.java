package com.reminder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.LocationDAO;
import com.reminder.model.Location;
import com.reminder.response.model.LocationResponse;



@Service
@Transactional(propagation = Propagation.REQUIRED)
public class LocationServiceImpl implements LocationService
{
	@Autowired
    private LocationDAO locationDAO;
    
	@Override
    public void createLocation(Location location)
    {
    	locationDAO.createLocation(location);
    }

    @Override
    public Location getLocationById(int locationId)
    {
        return locationDAO.getLocationById(locationId);
    }

    /*@Override
    public List<Location> getAllLocations()
    {
        return locationDAO.getAllLocations();
    }*/
    
    @Override
    public LocationResponse getAllLocations(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no)
    {
        return locationDAO.getAllLocations(sort_by,order,searchCriteria,limit,page_no);
    }

    @Override
    public void updateLocation(Location location)
    {
    	locationDAO.updateLocation(location);
    }

    @Override
    public void deleteLocation(int locationId)
    {
    	locationDAO.deleteLocation(locationId);
    }
}
