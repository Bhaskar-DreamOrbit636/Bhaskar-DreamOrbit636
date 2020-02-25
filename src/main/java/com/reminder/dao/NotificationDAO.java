package com.reminder.dao;

import java.util.Date;

import com.reminder.model.Contract;
import com.reminder.model.SimpleMail365;
import com.reminder.model.User;

public interface NotificationDAO {

	public void getAssetNotification(Date notification);

	public void getStaffNotification(Date notification);

	public void runCronJobContractNotification(Date notification);

	public void sendVerifiedContractEmail(Contract contractEntity, User createdUser, String contractStatus);

	public void sendRejectedContractEmail(Contract contractEntity, User createdUser, String contractStatus);

	public void sendUpdatedContractMail(Contract entity, User createdUser);

	public void sendDeletedContractMail(Contract entity, User createdUser);

	void sendCreateContractEmail(Contract entity, User createdUser);
	
	public boolean alertMail(Exception e, SimpleMail365 simpleMail);

}
