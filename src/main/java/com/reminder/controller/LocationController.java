package com.reminder.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.model.Location;
import com.reminder.model.User;
import com.reminder.response.model.LocationResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.LocationService;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;

@RestController
public class LocationController
{
    @Autowired
    private LocationService locationService;
	@Autowired
	private UserService userService;

	 @Autowired
	private JwtTokenUtil jwtTokenUtil;
	 
    private Logger logger = Logger.getLogger(LocationController.class);
    
	CurrentDate curentDate = new CurrentDate();
	
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/createLocation", method=RequestMethod.POST, 
            produces="application/json", consumes="application/json")
    public void createLocation(@RequestBody Location location, HttpServletRequest request)
    {
		logger.info("createLocation(location=" + location + ", request=" + request + ") - start - Creating Location using /createLocation with Location  = " + location.getLocationName());
    	User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		logger.info("createLocation(location=" + location + ", request=" + request + ") -  Location created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		location.setCreatedById(createdUser.getUserId());
		//while creating location LastModifiedById will be createdById to avoid the default value (0)
		location.setLastModifiedById(createdUser.getUserId());
    	locationService.createLocation(location);
    }
    
    /*** Retrieve a single Location ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/location/{locationId}",produces="application/json",
            method=RequestMethod.GET)
    public Location getLocationById(@PathVariable("locationId") int locationId)
    { 
		logger.info("getLocationById(locationId=" + locationId + ") - start - Retrieving Location using /location/{locationId} api");
    	Location location = locationService.getLocationById(locationId);
        return location;
    }
    
   
    // --- list of Locations with pagination and search criteria -----------
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value = "/locations", method = RequestMethod.GET)
    public ResponseEntity<LocationResponse> getAllLocations(@RequestParam(required = false) String sort_by, 
		    		                                      @RequestParam(required = false) String order,  
		    		                                      @RequestParam(required = false) String searchCriteria,
		    		                                      @RequestParam(required = false) Integer limit,
		    		                                      @RequestParam(required = false) Integer page_no) {
    	
    	
    	LocationResponse locations = locationService.getAllLocations(sort_by,order,searchCriteria,limit,page_no);
        if(locations == null){
			ResponseEntity<LocationResponse> returnResponseEntity = new ResponseEntity<LocationResponse>(HttpStatus.NO_CONTENT);
			logger.info("getAllLocations(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - No Location created yet - return value=" + returnResponseEntity);
            return returnResponseEntity;
        }
ResponseEntity<LocationResponse> returnResponseEntity = new ResponseEntity<LocationResponse>(locations, HttpStatus.OK);
        logger.info("getAllLocations(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - Fetching data for all Locations - return value=" + returnResponseEntity);
        return returnResponseEntity;
    }
    
    
    /*** Update a Location ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/updateLocation", method=RequestMethod.PUT, 
            produces="application/json", consumes="application/json")
    public void updateLocation(@RequestBody Location location, HttpServletRequest request)
    {
    	
    	User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		logger.info("updateLocation(location=" + location + ", request=" + request + ") -  Location created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		location.setLastModifiedById(createdUser.getUserId());
    	locationService.updateLocation(location);
    }
    
    /*** Delete a Location ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/deleteLocation/{locationId}",method=RequestMethod.DELETE,produces="application/json")
    public void deleteLocation(@PathVariable("locationId") int locationId,HttpServletRequest request,
    		HttpServletResponse response)
    {
    	User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		try{
			logger.info("deleteLocation(locationId=" + locationId + ", request=" + request + ", response=" + response + ") -  Location created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
    	locationService.deleteLocation(locationId);
		}catch(Exception e){
			try {
				response.sendError(403, "Forbidden");
			} catch (IOException e1) {
				logger.error("Erro rin delete location" + e1);
			}
		}
    }
}

