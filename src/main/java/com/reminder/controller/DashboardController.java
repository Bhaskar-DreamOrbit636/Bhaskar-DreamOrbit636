package com.reminder.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.response.model.DashboardResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.DashboardService;
import com.reminder.service.UserService;

@RestController
public class DashboardController {
	
	private Logger logger = Logger.getLogger(DashboardController.class);
	
	@Autowired
	DashboardService dashboardService;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	UserService userService;
	
	
	/*** Retrieve module based reminders Expiring next month ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/dashboardContent", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<DashboardResponse> getDashboardContent(HttpServletRequest request) {
		logger.info("getDashboardContent(request=" + request + ") - start - Fetching dash board contents");
		/*	logger.info(System.getProperty("----before------"));
		System.out.println(System.getProperty("upload_path"));
		logger.info(System.getProperty("upload_path"));
		logger.info(System.getProperty("-----after-----"));*/
		
		int createdUser = getUserDetails(request);
		DashboardResponse dashboardResponse = dashboardService.getDashboardContent(createdUser);
		if (dashboardResponse == null) {
			 return new ResponseEntity<DashboardResponse>(HttpStatus.NO_CONTENT);
		}
		 return new ResponseEntity<DashboardResponse>(dashboardResponse, HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/assetExpiringConunt", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<DashboardResponse> getAssetExpiringCount(HttpServletRequest request){
		logger.info("getAssetExpiringCount(request=" + request + ") - start - getting Asset Expiring Count");
		int createdUser = getUserDetails(request);
		DashboardResponse dashboardResponse = dashboardService.getAssetExpiringCount(createdUser);
		if(dashboardResponse == null){
			return new ResponseEntity<DashboardResponse>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<DashboardResponse>(dashboardResponse, HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contractExpiringConunt", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<DashboardResponse> getContractExpiringCount(HttpServletRequest request){
		logger.info("getContractExpiringCount(request=" + request + ") - start - getting Asset Expiring Count");
		int createdUser = getUserDetails(request);
		DashboardResponse dashboardResponse = dashboardService.getContractExpiringCount(createdUser);
		if(dashboardResponse == null){
			return new ResponseEntity<DashboardResponse>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<DashboardResponse>(dashboardResponse, HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffExpiringConunt", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<DashboardResponse> getStaffExpiringCount(HttpServletRequest request){
		logger.info("getStaffExpiringCount(request=" + request + ") - start - getting Asset Expiring Count");
		int createdUser = getUserDetails(request);
		DashboardResponse dashboardResponse = dashboardService.getStaffExpiringCount(createdUser);
		if(dashboardResponse == null){
			return new ResponseEntity<DashboardResponse>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<DashboardResponse>(dashboardResponse, HttpStatus.OK);
	}
	
	private int getUserDetails(HttpServletRequest request) {
		int createdUser = 0;
		String header = request.getHeader("Authorization");
		if(!StringUtils.isEmpty(header) && header.startsWith("Bearer ")){
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserIdByName(username);
		}
		return createdUser;
	}
}
