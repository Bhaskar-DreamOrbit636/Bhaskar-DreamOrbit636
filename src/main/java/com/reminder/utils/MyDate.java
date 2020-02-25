package com.reminder.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class MyDate extends Date{
	
	   private Logger logger = Logger.getLogger(MyDate.class);
	   private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	    /*
	     * additional constructors
	     */
	   	Date date;
	   	public MyDate(Date date){
	   		this.date = date;
	   	}
	   	
	   	boolean flag = false;
	   	Object date1;
		public MyDate(Object date1){
			flag = true;
	   		this.date1 = date1;
	   	}
	   	
	   	public MyDate() {
	   		
		}
	   	
/*	   	public Date appendTime(Date currentDate){
	   		
	   		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = df.format(new Date()).substring(10); // 10 is the beginIndex of time here

			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String startUserDateString = df2.format(currentDate);
			startUserDateString = startUserDateString+""+timeString;
			try {
				currentDate = df.parse(startUserDateString);
			} catch (ParseException e) {
				logger.error("Erro in appendTime() in mydate util: " +e); 
				e.printStackTrace();
			}
			return currentDate;
	   	}*/
	   	
	    @Override
	    public String toString() {
	    	Calendar c = Calendar.getInstance();
	    	if(flag){
	    		c.setTime((Date)date1);
	    	}
	    	else{
	    		c.setTime(date);
	    	}
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
	        return dateFormat.format(c.getTime());
	    }

}
