package com.reminder.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class CurrentDate {
	private static Logger logger = Logger.getLogger(CurrentDate.class);
	public static final TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
	
	public String getCurrentDate(){
		logger.info("getCurrentDate() - start");

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
		Date date = cal.getTime();
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        String strDate = dateFormat.format(date);  

		logger.info("getCurrentDate() - end - return value=" + strDate);
	    return strDate; 
	}
	
/*	public String getCurrentDate(Date date){
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        String strDate = dateFormat.format(date);  
	    return strDate; 
	}
	
	public Date getCurrentDateFormat(){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Date date = cal.getTime();
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
        String strDate = dateFormat.format(date);  
	    Date d = null;
		try {
			d = dateFormat.parse(strDate);
		} catch (ParseException e) {
			
		}
	    return d;
	}*/

}
