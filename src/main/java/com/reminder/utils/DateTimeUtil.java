package com.reminder.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTimeUtil {
	
	//public static DateTimeZone dateTimeZone = DateTimeZone.UTC;
	private static Logger logger = Logger.getLogger(DateTimeUtil.class);
	
	public final static DateTimeZone currentTimeZone = setCurrentTimeZone();
	
	/*public static DateTime convertToUTC(Date date) {
		return new DateTime(date).withZone(DateTimeZone.forID("Asia/Singapore"));
	}*/
	
	public static Date convertToSGTWithDate(Date date) {
		logger.info("convertToSGTWithDate(date=" + date + ") - start");

		//DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'");
		DateFormat formatter = new SimpleDateFormat("dd/mmm/yyyy'T'hh:mm:ss'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		Date todayWithZeroTime = null;
		try {
			// todayWithZeroTime = formatter.format(date);
			    //java.util.TimeZone tz =  java.util.TimeZone.getTimeZone("UTC");
			//java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Singapore"));
			TimeZone.setDefault(TimeZone.getDefault());
			String timezone = TimeZone.getDefault().getDisplayName();
			logger.info("curreent TimeZone : "+timezone);
		        Calendar cal1 = Calendar.getInstance();
		        cal1.setTime(date);
		       // cal1.setTimeZone(tz);
		        todayWithZeroTime =cal1.getTime();
		} catch (Exception e) {
			logger.error("Error in converting Date to Asia/Singapore: "+e);
			e.printStackTrace();
		}

		logger.info("convertToSGTWithDate(date=" + date + ") - end - return value=" + todayWithZeroTime);
		return todayWithZeroTime;
	}
	
	public static DateTime now() {
		logger.info("now() - start");

		DateTime returnDateTime = new DateTime().withZone(DateTimeZone.forID("Asia/Singapore"));
		logger.info("now() - end - return value=" + returnDateTime);
		return returnDateTime;
	}
	
	public static String getCurrentTimeZoneName() {
		logger.info("getCurrentTimeZoneName() - start");

		Calendar cal = Calendar.getInstance();
		long milliDiff = cal.get(Calendar.ZONE_OFFSET);
		String[] ids = TimeZone.getAvailableIDs();
		String timeZoneId = null;
		for (String id : ids) {
			TimeZone tz = TimeZone.getTimeZone(id);
			if (tz.getRawOffset() == milliDiff) {
				timeZoneId = id;
				break;
			}
		}

		logger.info("getCurrentTimeZoneName() - end - return value=" + timeZoneId);
		return timeZoneId;
	}
	
	private static DateTimeZone setCurrentTimeZone() {
		logger.info("setCurrentTimeZone() - start");

		DateTimeZone returnDateTimeZone = DateTimeZone.forID(getCurrentTimeZoneName());
		logger.info("setCurrentTimeZone() - end - return value=" + returnDateTimeZone);
		return returnDateTimeZone;
	}
	
}
