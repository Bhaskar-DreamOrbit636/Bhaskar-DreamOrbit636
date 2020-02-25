package com.reminder.service;

import java.util.Date;

public interface NotificationService {

	public void getAssetNotification(Date notification);
	
	public void getStaffNotification(Date notification);
	
	void runCronJobContractNotification(Date notification);


}
