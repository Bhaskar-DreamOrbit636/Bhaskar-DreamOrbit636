package com.reminder.controller;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.service.NotificationService;


@RestController
public class NotificationController {
	
	private Logger logger = Logger.getLogger(NotificationController.class);

	@Autowired 
	private NotificationService notificationService;
	
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/runAssetNotification", method = RequestMethod.GET)
	public ResponseEntity<String> runAssetNotification(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date notification){
		logger.info("runAssetNotification(notification=" + notification + ") - start - running Asset Notification");
		try {
			notificationService.getAssetNotification(notification);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/runStaffNotification", method = RequestMethod.GET)
	public ResponseEntity<String> runStaffNotification(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date notification){
		logger.info("runStaffNotification(notification=" + notification + ") - start - running Staff Notification");
		try {
			notificationService.getStaffNotification(notification);;
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/runContractNotification", method = RequestMethod.GET)
	public ResponseEntity<String> runContractNotification(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date notification){
		logger.info("runContractNotification(notification=" + notification + ") - start - running Contract Notification");
		try {
			notificationService.runCronJobContractNotification(notification);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	} 
}
