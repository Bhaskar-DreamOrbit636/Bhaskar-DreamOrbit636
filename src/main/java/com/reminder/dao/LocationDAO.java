package com.reminder.dao;

import com.reminder.model.Location;
import com.reminder.response.model.LocationResponse;



public interface LocationDAO
{
    public void createLocation(Location location);
    
    public Location getLocationById(int locationId);
    
    //public List<Location> getAllLocations();
    
    public LocationResponse getAllLocations(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no);
    
    public void updateLocation(Location location);
    
    public void deleteLocation(int locationId);
}

