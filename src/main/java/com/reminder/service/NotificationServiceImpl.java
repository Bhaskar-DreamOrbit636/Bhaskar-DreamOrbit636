package com.reminder.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.NotificationDAO;
import com.reminder.model.SimpleMail365;	

@Service
@Transactional(propagation = Propagation.REQUIRED)
@EnableScheduling
@PropertySource("classpath:/application.properties")
public class NotificationServiceImpl implements NotificationService{

	private Logger logger = Logger.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	private NotificationDAO notificationDao;
	
	
/*    @Value("${spring.mail.host}")
	private String host;
	
	@Value("${spring.mail.port}")
	private int port;
	
	@Value("${spring.mail.username}")
	private String username;
	
	@Value("${spring.mail.password}")
	private String password;

	@Value("${spring.mail.smtp.ssl.trust}")
	private String ssl;
	
	@Value("${r365.host.file}")
	private String  hostFile;*/
	
	private String host = System.getProperty("spring.mail.host");
	private String portNumber = System.getProperty("spring.mail.port");
	private int port = Integer.valueOf(portNumber);
	private String username = System.getProperty("spring.mail.username");
	private String password = System.getProperty("spring.mail.password");
	private String ssl = System.getProperty("spring.mail.smtp.ssl.trust");
	private String hostFile = System.getProperty("r365.host.file");
	//private static final String crontime = System.getProperty("scheduler.i18n.crontime");
	
		
/*	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}*/
	 
	/* (non-Javadoc)
	 * @see com.reminder.service.NotificationService#getAssetNotification()
	 * Every day at 1 AM
	 */
	@Scheduled(cron = "${r365.notification.scheduler}")
	public void getAssetNotification() {
		try {
			if(isMaster())
			notificationDao.getAssetNotification(null);
		} catch (Exception e) {
			logger.error("getAssetNotification() - error in getAssetNotification", e);
		}
	}
	

	@Override
	public void getAssetNotification(Date notification) {
		try {
			if(isMaster())
			notificationDao.getAssetNotification(notification);
		} catch (Exception e) {
			logger.error("getAssetNotification(notification=" + notification + ") - error in getAssetNotification", e);
		}
	}
	
	
	@Scheduled(cron = "${r365.notification.scheduler}")
	public void getStaffNotification() {
		try {
			if(isMaster())
			notificationDao.getStaffNotification(null);
		} catch (Exception e) {
			logger.error("getStaffNotification() - error in getStaffNotification", e);
		}
	}
	
	@Override
	public void getStaffNotification(Date notification) {
		try {
			if(isMaster())
			notificationDao.getStaffNotification(notification);
		} catch (Exception e) {
			logger.error("getStaffNotification(notification=" + notification + ") - error in getStaffNotification", e);
		}
	}
	
	@Scheduled(cron = "${r365.notification.scheduler}")
	public void runCronJobContractNotification() {
		try {
			if(isMaster())
			notificationDao.runCronJobContractNotification(null);
		} catch (Exception e) {
			logger.error("runCronJobContractNotification() - error in runCronJobContractNotification", e);
		}
	}
	
	@Override
	public void runCronJobContractNotification(Date notification) {
		try {
			if(isMaster())
			notificationDao.runCronJobContractNotification(notification);
		} catch (Exception e) {
			logger.error("runCronJobContractNotification(notification=" + notification + ") - error in runCronJobContractNotification", e);
		}
	}
	
	private boolean isMaster() {
		String line=null;
		boolean isMaster = false;
		try(BufferedReader br = new BufferedReader(new FileReader(hostFile));){
			logger.info("host file locatin: "+hostFile);
			while((line = br.readLine()) != null){
				if(line.contains("r365master")){
					logger.info("mode is master tiggering the notification: "+line);
					isMaster=true;
				}else{
					logger.info("mode is slave, so not tiggering any notifications.");
				}
			}
		}catch(IOException e){
			logger.error("Error in reading host file: "+e);
			isMaster = false;
		}catch(Exception e){
			logger.error("Exception in isMaster() "+e);
			isMaster = false;
		}
		return isMaster;
	}
	
	@Bean
	public JavaMailSenderImpl getJavaMailSender() {
	 
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		SimpleMail365 simpleMail = new SimpleMail365();
		simpleMail.setText("host: "+host+", port: "+port+", userName: "+username);
		try{
		sender.setHost(host);
		sender.setPort(port);
	    
		sender.setUsername(username);
	    // set the password 
		sender.setPassword(password);
	    Properties props = sender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");//false
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
		}catch(Exception e){
			logger.error("Exception in JavMailSender bean:  "+e);
			notificationDao.alertMail(e,simpleMail);
		}
	    return sender;
	}
	

}
