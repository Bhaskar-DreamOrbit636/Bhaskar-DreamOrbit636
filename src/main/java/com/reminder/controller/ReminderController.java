package com.reminder.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.reminder.model.Reminder;
import com.reminder.request.model.ReminderRequest;
import com.reminder.service.ReminderService;
import com.reminder.utils.CsvUtils;

@RestController
public class ReminderController
{
    @Autowired
    private ReminderService reminderService;
    
    private Logger logger = Logger.getLogger(ReminderController.class);
         
        
    
    /*** Creating a new Reminder ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/createReminder", method=RequestMethod.POST, 
            produces="application/json", consumes="application/json")
    public void createReminder(@RequestBody ReminderRequest reminder)
    {
		logger.info("createReminder(reminder=" + reminder + ") - start - Creating Reminder using createReminder()");
    	reminderService.createReminder(reminder);
    }
    
    /*** Retrieve a single Reminder ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/reminder/{reminderId}",produces="application/json",
            method=RequestMethod.GET)
    public Reminder getReminderById(@PathVariable("reminderId") int reminderId)
    {  
		logger.info("getReminderById(reminderId=" + reminderId + ") - start - Fetching data for single Reminder");
    	Reminder reminder = reminderService.getReminderById(reminderId);
        return reminder;
    }
    
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value = "/reminders", method = RequestMethod.GET)
    public ResponseEntity<List<Reminder>> getAllReminders(@RequestParam(required = false) String sort_by, 
    		                                      @RequestParam(required = false) String order,  
    		                                      @RequestParam(required = false) String searchCriteria,
    		                                      @RequestParam(required = false) Integer limit,
    		                                      @RequestParam(required = false) Integer page_no) {
    	
    	
        List<Reminder> reminders = reminderService.getAllReminder(sort_by,order,searchCriteria,limit,page_no);
        if(reminders.isEmpty()){
			ResponseEntity<List<Reminder>> returnResponseEntity = new ResponseEntity<List<Reminder>>(HttpStatus.NO_CONTENT);
			logger.info("getAllReminders(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - No Reminder created yet - return value=" + returnResponseEntity);
            return returnResponseEntity;
        }
ResponseEntity<List<Reminder>> returnResponseEntity = new ResponseEntity<List<Reminder>>(reminders, HttpStatus.OK);
        logger.info("getAllReminders(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - Fetching data for all Reminder... - return value=" + returnResponseEntity);
        return returnResponseEntity;
    }
    
    
    /*** Update a Reminder ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/updateReminder", method=RequestMethod.PUT, 
            produces="application/json", consumes="application/json")
    public void updateReminder(@RequestBody Reminder reminder)
    {
		logger.info("updateReminder(reminder=" + reminder + ") - start - Updating Reminder ...");
    	reminderService.updateReminder(reminder);
    }
    
    /*** Delete a Reminder ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/deleteReminder/{reminderId}",method=RequestMethod.DELETE,produces="application/json")
    public void deleteUser(@PathVariable("userId") int reminderId)
    {
		logger.info("deleteUser(reminderId=" + reminderId + ") - start - Deleting Reminder with particular id");
    	reminderService.deleteReminder(reminderId);
    }
    
    
    /*** Reminders to CSV 
     * @throws IOException ***/
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/writeRemindersToCSV",method=RequestMethod.GET,produces="application/json")
    public void getDataForCsv(@RequestParam(required = false) String sort_by, 
					          @RequestParam(required = false) String order,  
					          @RequestParam(required = false) String searchCriteria,
					          @RequestParam(required = false) Integer limit,
					          @RequestParam(required = false) Integer page_no) throws IOException
    {
    	String csvFile = "D:/Reminder365_AllCsvs/Reminder.csv";
        FileWriter writer = new FileWriter(csvFile);
        try{
    	List<Reminder> reminders = reminderService.getAllReminder(sort_by,order,searchCriteria,limit,page_no);
    	CsvUtils.writeLine(writer, Arrays.asList("ReminderId"));
    	
    	for(Reminder reminder : reminders){
    		
    		List<String> list = new ArrayList<>();
            
    		list.add(String.valueOf(reminder.getReminderId()));
          
            System.out.println("******************"+list.toString());
           
    		CsvUtils.writeLine(writer,list);
				logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - CSV file created at location : " + csvFile);
    	    }
        } catch(IOException e){
			logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - CSV file couldn't be created for error : " + e);
        }
    	writer.flush();
        writer.close();
    }
    
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/inactiveReminder", method=RequestMethod.PUT, produces="application/json")
    public void inactiveReminder(@PathVariable int reminderId){
    	//if()
    }
    
    //---------------------------------nested object fetch -----------------------------------
    
    /*** Retrieve all Contract-Reminder ***/
   /* @RequestMapping(value="/contractReminder/{reminderId}", method=RequestMethod.GET, 
            produces="application/json")
    public void getAllContractReminders(@PathVariable("reminderId") int reminderId)
    {
    	logger.info("Fetching Reminder for id = "+reminderId);
    	reminderService.getContractReminderById(reminderId);
    }*/
    
    
}

