package com.reminder.dao;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.controller.FileUploadController;
import com.reminder.model.Contract;
import com.reminder.model.Contract_Reviewer;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.SimpleMail365;
import com.reminder.model.User;
import com.reminder.request.model.AssetAggregateReminder;
import com.reminder.request.model.ContractAggregateReminder;
import com.reminder.request.model.StaffAggregateReminder;
import com.reminder.response.model.ContractDropDownValue;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.service.ContractService;
import com.reminder.service.ReminderRecipients;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;
import com.reminder.utils.MyDate;

@Repository
@PropertySource("classpath:/application.properties")
public class NotificationDAOImpl implements NotificationDAO {

	private static final String REMINDER365 = "Reminder365";

	private Logger logger = Logger.getLogger(NotificationDAOImpl.class);

	@Autowired
	ReminderRecipients recipient;

	@Autowired
	ContractService contractService;

	@Autowired
	GroupDAO groupdao;

/*	@Value("${spring.setFromAddress}")
	private String fromAddress;

	@Value("${r365.applink}")
	private String appLink;

	@Value("${r365.alert.emailId}")
	private String alertMail;*/

	
	  private String appLink = System.getProperty("r365.applink");
	  
	  private String fromAddress = System.getProperty("spring.setFromAddress");
	  
	  private String alertMail = System.getProperty("r365.alert.emailId");
	 

	@Autowired
	ModuleTypeDao moduleTypeDao;

	@Autowired
	private ContractDAO contractDAO;

	// private String fromAddress = new
	// LoadPropertyFromJboss().getProperty("spring.setFromAddress");

	/*
	 * @Autowired private JavaMailSenderImpl javaMailSenderImpl;
	 */

	private static final String EXPIRING = "[Expiring] ";
	private static final String EXPIRED = "[Expired!!!] ";
	private static final String EXGSUB = " will expire on ";
	private static final String EXSUB = " has  <span style=" + "color:red;" + "> expired </span> on ";
	private static final String ATEXT = "<br>Please take action! <br>You may login to Reminder365 to inactivate this reminder.<br><br>";
	private static final String BTEXT = "Regards and Thanks, <br>Reminder365 <br> <br>";
	private static final String CTEXT = "This is an automatically generated email, please do not reply.";
	private static final String CONTRACT = "Please take note that Contract [Contract title] will expire on ";
	private static final String CONTRACTLINE2 = "You may login to link to inactivate this reminder";
	private static final String CONTRACTEXPIRED = "Please take note that Contract [Contract title] has <span style="
			+ "color:red;" + "> expired </span> on ";
	private static final String CONTRACTCREATION = " is pending your verification. <br>Please login to link to review and verify";
	private static final String CONTRACTCREATIONSUB = " is pending verification";
	private static final String CONTRACTVERIFICATIONSUB1 = "Creation of";
	private static final String CONTRACTVERIFICATIONSUB2 = " has been verified";
	private static final String CONTRACTVERIFICATIONTEXT1 = " has been verified by";
	private static final String CONTRACTVERIFICATIONTEXT2 = "<br>You may login to link to check the details";
	private static final String CONTRACTREJECTIONSUB1 = " has been rejected";
	private static final String CONTRACTUPDATIONSUB1 = "Update of";
	private static final String CONTRACTDELETIONSUB1 = "Deletion of";
	private static final String CONTRACT_SUBJECT = "Contract ";

	@PersistenceContext
	private EntityManager entityManager;

	private CurrentDate date = new CurrentDate();

	@Autowired
	private JavaMailSender mailSenderObj;

	@Autowired
	private UserService userService;

	/**
	 * Sending Asset Notification
	 */
	@Override
	public void getAssetNotification(Date notification) {
		SimpleMail365 simpleMail = null;
		Date currentDate = notification == null ? new MyDate(new Date()) : new MyDate(notification);

		List<Object[]> allAsset = getAllAssets();
		ModuleType m = moduleTypeDao.getModuleType("Assets");

		SortedMap<Integer, List<AssetAggregateReminder>> expringMap = new TreeMap<>();

		for (Object[] as : allAsset) {
			String addCCList = "";
			int firstCount = 0;
			int secondCount = 0;
			int thirdCount = 0;
			int expireCount = 0;
			int id = (int) as[15];
			Groups group = groupdao.getGroupById(id);
			String groupName = group.getGroupName();

			ContractDropDownValue mailList = recipient.getRecipients(id, m.getModuleTypeId(), 1);
			Date secondReminderdate = null;
			Date thirdReminderdate = null;

			if (as[1] != null) {
				secondReminderdate = new MyDate(as[1]);
			}
			if (as[2] != null) {
				thirdReminderdate = new MyDate(as[2]);
			}

			User user = null;
			if ((int) as[4] != 0)
				user = userService.getUserById((int) as[4]);

			String assetRef = "";
			if (as[8] != null)
				assetRef = (String) as[8];

			Set<MyGroupDetails> setccEmail = mailList.getCcList();

			String ccEmail = "";
			if (setccEmail != null) {
				if (!setccEmail.isEmpty()) {
					for (MyGroupDetails groupDetails : setccEmail) {
						ccEmail += groupDetails.getEmailId();
						if (setccEmail.size() > 1)
							ccEmail += ",";
					}
				}
			}

			String addCC = (String) as[6];
			String addCCEmail = "";
			if (addCC != null)
				addCCEmail = replaceCharacters(addCC);

			if (!StringUtils.isEmpty(addCCEmail)) {
				if (addCCList.length() > 0)
					addCCList += ",";
				addCCList += addCCEmail;
			}

			Set<MyGroupDetails> setemail = mailList.getToList();

			String toEmail = "";
			if (setemail != null) {
				if (!setemail.isEmpty()) {
					for (MyGroupDetails groupDetails : setemail) {
						toEmail += groupDetails.getEmailId();
						if (setemail.size() > 1)
							toEmail += ",";
					}
				}
			}

			// getting username
			String[] tolist = getEmailArray(toEmail);
			StringBuffer sf = new StringBuffer();
			if (!toEmail.isEmpty()) {
				for (int i = 0; i < tolist.length; i++) {
					String toName = getUserByEmailId(tolist[i]);
					if (toName != null) {
						sf.append(toName);
						sf.append("," + " ");
					}
				}
			}

			Query queryfirstCount = null;
			Query querysecondCount = null;
			Query querythirdCount = null;
			Query queryExpiredCount = null;

			try {
				queryfirstCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, asset a where r.first_reminder_date <= '"
								+ currentDate
								+ "' and r.active = 1 and r.first_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				querysecondCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, asset a where r.second_reminder_date <= '"
								+ currentDate
								+ "' and r.active = 1 and r.second_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				querythirdCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, asset a where r.third_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.third_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				queryExpiredCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, asset a where r.effective_expiry_date <= '"
								+ currentDate + "'and r.active = 1 and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id ");

			} catch (Exception e) {
				logger.error("Error executing queries" + e);
			}

			firstCount = ((BigInteger) queryfirstCount.getSingleResult()).intValue();
			secondCount = ((BigInteger) querysecondCount.getSingleResult()).intValue();
			thirdCount = ((BigInteger) querythirdCount.getSingleResult()).intValue();
			expireCount = ((BigInteger) queryExpiredCount.getSingleResult()).intValue();

			if (firstCount != 0 || secondCount != 0 || thirdCount != 0) {
				simpleMail = new SimpleMail365();

				if (secondReminderdate == null || (thirdReminderdate == null && secondCount != 0)
						|| (thirdReminderdate != null && thirdCount != 0)) {

					Set<MyGroupDetails> setccLastRem = mailList.getCcLastReminderList();
					String ccLastRemEmail = "";
					if (setccLastRem != null) {
						if (!setccLastRem.isEmpty()) {
							for (MyGroupDetails groupDetails : setccLastRem) {
								ccLastRemEmail += groupDetails.getEmailId();
								if (ccLastRemEmail.length() > 0)
									ccLastRemEmail += ",";
							}
						}
					}

					if (!StringUtils.isEmpty(ccLastRemEmail)) {
						if (ccEmail.length() > 0)
							ccEmail += ",";
						ccEmail += ccLastRemEmail;
					}

					String addCCLastRem = (String) as[10];
					String addCCLastRemEmail = replaceCharacters(addCCLastRem).trim();

					if (!StringUtils.isEmpty(addCCLastRemEmail)) {
						if (addCCList.length() > 0)
							addCCList += ",";
						addCCList += addCCLastRemEmail;
					}
				}

				/*
				 * if (!StringUtils.isEmpty(toEmail))
				 * simpleMail.setTo(getEmailArray(toEmail));
				 */
				if (!StringUtils.isEmpty(addCCList))
					simpleMail.setCc(getEmailArray(addCCList));

				simpleMail.setSubject(EXPIRING + (String) as[16] + " [" + (String) as[5] + "] " + assetRef + " of "
						+ (String) as[9] + EXGSUB + convertDateToStringWithFormat((Date) as[3]));
				String text = "Hi " + sf.toString() + "<br><br>" + (String) as[16] + " [" + (String) as[5] + "] "
						+ assetRef + " of " + (String) as[9] + EXGSUB + convertDateToStringWithFormat((Date) as[3])
						+ "." + ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT;
				simpleMail.setText(text);

				AssetAggregateReminder aggregateReminder = new AssetAggregateReminder((String) as[16], (String) as[5],
						assetRef, (String) as[9], convertDateToStringWithFormat((Date) as[3]), toEmail, ccEmail,
						"Expiring", sf.toString(), groupName, firstCount, secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<AssetAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<AssetAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}

				boolean result = false;
				if (!StringUtils.isEmpty(addCCList)) {
					logger.info("sending Expiring email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", Asset Type = " + (String) as[16]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}

				// updateReminder(currentDate, firstCount, secondCount,
				// thirdCount, simpleMail, as);
			}
			if (expireCount != 0) {
				simpleMail = new SimpleMail365();
				logger.info("Including Expiry reminders");
				Set<MyGroupDetails> setccExpiry = mailList.getCcExpiryReminderList();
				String ccExpiry = "";
				if (setccExpiry != null) {
					if (!setccExpiry.isEmpty()) {
						for (MyGroupDetails groupDetails : setccExpiry) {
							ccExpiry += groupDetails.getEmailId();
							if (ccExpiry.length() > 0)
								ccExpiry += ",";
						}
					}
				}

				if (!StringUtils.isEmpty(ccExpiry)) {
					if (ccEmail.length() > 0)
						ccEmail += ",";
					ccEmail += ccExpiry;
				}
				String addCCExpiry = (String) as[11];
				if (addCCExpiry != null)
					addCCExpiry = replaceCharacters(addCCExpiry).trim();

				if (!StringUtils.isEmpty(addCCExpiry)) {
					if (addCCList.length() > 0)
						addCCList += ",";
					addCCList += addCCExpiry;
				}

				/*
				 * if (!StringUtils.isEmpty(toEmail))
				 * simpleMail.setTo(getEmailArray(toEmail));
				 */
				if (!StringUtils.isEmpty(addCCList))
					simpleMail.setCc(getEmailArray(addCCList));

				String subject = EXPIRED + (String) as[16] + " [" + (String) as[5] + "] " + assetRef + " of "
						+ (String) as[9] + " has expired " + convertDateToStringWithFormat((Date) as[3]);
				simpleMail.setSubject(subject);
				String text = "Hi " + sf.toString() + "<br><br>" + (String) as[16] + " [" + (String) as[5] + "] "
						+ assetRef + " of " + (String) as[9] + EXSUB + convertDateToStringWithFormat((Date) as[3]) + "."
						+ ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT;
				simpleMail.setText(text);

				AssetAggregateReminder aggregateReminder = new AssetAggregateReminder((String) as[16], (String) as[5],
						assetRef, (String) as[9], convertDateToStringWithFormat((Date) as[3]), toEmail, ccEmail,
						"expired", sf.toString(), groupName, firstCount, secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<AssetAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<AssetAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}

				boolean result = false;
				if (!StringUtils.isEmpty(addCCList)) {
					logger.info("sending Expired email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", Asset Type = " + (String) as[16]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}

			}

		}

		for (Integer ids : expringMap.keySet()) {
			List<AssetAggregateReminder> list = expringMap.get(ids);
			if (!list.isEmpty()) {
				SimpleMail365 mail = new SimpleMail365();
				mail.setSubject(REMINDER365);

				StringBuilder textHeader = new StringBuilder("Hi ");
				StringBuilder text = new StringBuilder("<h3> <u><font color=\"red\">" + "Expired Asset(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'><tr><th style='padding:5px;'>Asset Type</th><th style='padding:5px;'>Asset Sub Type</th><th style='padding:5px;'>Asset ID</th><th style='padding:5px;'>Location</th><th style='padding:5px;'>Group Name</th><th style='padding:5px;'>Expiry Date</th></tr>");
				StringBuilder textExpiring = new StringBuilder("<h3><u><font color=\"orange\">" + "Expiring Asset(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'> <tr><th style='padding:5px;'>Asset Type</th><th style='padding:5px;'>Asset Sub Type</th><th style='padding:5px;'>Asset ID</th><th style='padding:5px;'>Location</th><th style='padding:5px;'>Group Name</th><th style='padding:5px;'>Expiry Date</th></tr>");
				StringBuilder footer = new StringBuilder(
						ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT);

				int count = 0;
				boolean expiredFlag = false;
				boolean expiringFlag = false;
				for (AssetAggregateReminder assetAggregateReminder : list) {

					if (count == 0) {
						if (!StringUtils.isEmpty(assetAggregateReminder.getToList()))
							mail.setTo(getEmailArray(assetAggregateReminder.getToList()));
						if (!StringUtils.isEmpty(assetAggregateReminder.getCcList()))
							mail.setCc(getEmailArray(assetAggregateReminder.getCcList()));
						textHeader.append(assetAggregateReminder.getUserName() + "<br><br>");
						textHeader.append("I would like to draw your attention to the following asset(s)." + "<br>");
					}

					if (assetAggregateReminder.getType().equals("expired")) {
						expiredFlag = true;
						text.append("<tr>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getAssetType() + "</td>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getAssetSubType() + "</td>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getAssetId() + "</td>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getLocation() + "</td>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getGroupName() + "</td>");
						text.append("<td style='padding:5px;'>" + assetAggregateReminder.getExpiryDate() + "</td>");
						text.append("</tr>");
						logger.info("sending expired email for: " + assetAggregateReminder.getAssetType());
					}
					count++;
				}

				text.append("</table>");
				for (AssetAggregateReminder assetAggregateReminder : list) {
					if (assetAggregateReminder.getType().equals("Expiring")) {
						expiringFlag = true;
						textExpiring.append("<tr>");
						textExpiring
								.append("<td style='padding:5px;'>" + assetAggregateReminder.getAssetType() + "</td>");
						textExpiring.append(
								"<td style='padding:5px;'> " + assetAggregateReminder.getAssetSubType() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + assetAggregateReminder.getAssetId() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + assetAggregateReminder.getLocation() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + assetAggregateReminder.getGroupName() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + assetAggregateReminder.getExpiryDate() + "</td>");
						textExpiring.append("</tr>");
						logger.info("sending Expiring email for: " + assetAggregateReminder.getAssetType());
					}
				}
				textExpiring.append("</table>");
				String mailtext = textHeader.toString();
				if (expiredFlag) {
					mailtext += text.toString() + " ";
				}
				if (expiringFlag) {
					mailtext += textExpiring.toString() + " ";
				}
				boolean result = false;
				mail.setText(mailtext + footer.toString());
				logger.info("sending emails for all the Asset Reminders where reminder date is less then equal to: "
						+ currentDate);
				result = sendMail(mail);
				if (result) {
					for (AssetAggregateReminder assetAggregateReminder : list) {
						updateReminder(currentDate, assetAggregateReminder.getFirstCount(),
								assetAggregateReminder.getSecondCount(), assetAggregateReminder.getThirdCount(),
								assetAggregateReminder.getId());
					}
				}
				loggingMailDetails(mail, result);

			}
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getAllAssets() {
		List<Object[]> allAsset = entityManager.createNativeQuery(
				"select r.first_reminder_date,r.second_reminder_date,r.third_reminder_date,r.effective_expiry_date,r.created_by_id,"
						+ " atype.type,a.additional_cc_list,r.reminder_id,a.id,loc.location_name,r.addcc_list_lastreminder,r.addcc_list_expiryreminder"
						+ ",r.first_reminder_sent_at,r.second_reminder_sent_at,r.third_reminder_sent_at,r.user_group_id, (select type from asset_type where asset_type_id in (atype.parent_asset_type_id)) as Assettype"
						+ " from reminder r  join asset a  "
						+ " join asset_type atype on atype.asset_type_id=a.asset_type_id "
						+ " join location loc on loc.location_id=a.location_id " + " where  " + " r.active = 1 and "
						+ " r.reminder_id = a.reminder_id order by r.effective_expiry_date asc")
				.getResultList();
		return allAsset;
	}

	/**
	 * @param currentDate
	 * @param firstReminderdate
	 * @param secondReminderdate
	 * @param thirdReminderdate
	 * @param user
	 * @param result
	 * @param as
	 */
	private void updateReminder(Date currentDate, int firstReminderdate, int secondReminderdate, int thirdReminderdate,
			int id) {

		String date = new FileUploadController().getCurrentDateTime();
		// currentDate = new MyDate().appendTime(currentDate);
		if (firstReminderdate != 0) {
			entityManager
					.createNativeQuery(
							"update reminder set first_reminder_sent_at='" + date + "' where reminder_id=:reminder_id")
					.setParameter("reminder_id", id).executeUpdate();
			logger.info("updating the first_reminder_sent_at date to: " + date);
		}
		if (secondReminderdate != 0) {
			entityManager
					.createNativeQuery(
							"update reminder set second_reminder_sent_at='" + date + "' where reminder_id=:reminder_id")
					.setParameter("reminder_id", id).executeUpdate();
			logger.info("updating the second_reminder_sent_at date to: " + date);
		}
		if (thirdReminderdate != 0) {
			entityManager
					.createNativeQuery(
							"update reminder set third_reminder_sent_at='" + date + "' where reminder_id=:reminder_id")
					.setParameter("reminder_id", id).executeUpdate();
			logger.info("updating the third_reminder_sent_at date to: " + date);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.reminder.dao.NotificationDAO#getStaffNotification()
	 */
	@Override
	public void getStaffNotification(Date notification) {
		SimpleMail365 simpleMail = null;
		Date currentDate = notification == null ? new MyDate(new Date()) : new MyDate(notification);

		List<Object[]> allStaff = getStaffQuery();
		ModuleType m = moduleTypeDao.getModuleType("Staff");

		// Map<Integer,List<StaffAggregateReminder>> expringMap = new
		// HashMap<>();
		SortedMap<Integer, List<StaffAggregateReminder>> expringMap = new TreeMap<>();

		for (Object[] as : allStaff) {
			String addCCList = "";
			int id = (int) as[13];
			Groups group = groupdao.getGroupById(id);
			String groupName = group.getGroupName();
			ContractDropDownValue mailList = recipient.getRecipients(id, m.getModuleTypeId(), 1);
			MyDate secondReminderdate = null;
			MyDate thirdReminderdate = null;

			int firstCount = 0;
			int secondCount = 0;
			int thirdCount = 0;
			int expireCount = 0;

			String expiryDate = "";
			if (as[3] != null)
				expiryDate = convertDateToStringWithFormat((Date) as[3]);

			if (as[1] != null) {
				secondReminderdate = new MyDate(as[1]);
			}
			if (as[2] != null) {
				thirdReminderdate = new MyDate(as[2]);
			}

			User user = null;
			if ((int) as[4] != 0)
				user = userService.getUserById((int) as[4]);

			Set<MyGroupDetails> setccEmail = mailList.getCcList();

			String ccEmail = "";
			if (setccEmail != null) {
				if (!setccEmail.isEmpty()) {
					for (MyGroupDetails groupDetails : setccEmail) {
						ccEmail += groupDetails.getEmailId();
						if (setccEmail.size() > 1)
							ccEmail += ",";
					}
				}
			}

			String addCC = (String) as[5];
			String addCCEmail = "";
			if (addCC != null)
				addCCEmail = replaceCharacters(addCC);

			if (!StringUtils.isEmpty(addCCEmail)) {
				if (addCCList.length() > 0)
					addCCList += ",";
				addCCList += addCCEmail;
			}

			Set<MyGroupDetails> setemail = mailList.getToList();

			String toEmail = "";
			if (setemail != null) {
				if (!setemail.isEmpty()) {
					for (MyGroupDetails groupDetails : setemail) {
						toEmail += groupDetails.getEmailId();
						if (setemail.size() > 1)
							toEmail += ",";
					}
				}
			}

			// getting username
			String[] tolist = getEmailArray(toEmail);
			StringBuffer sf = new StringBuffer();
			if (!toEmail.isEmpty()) {
				for (int i = 0; i < tolist.length; i++) {
					String toName = getUserByEmailId(tolist[i]);
					if (toName != null) {
						sf.append(toName);
						sf.append("," + " ");
					}
				}
			}

			Query queryfirstCount = null;
			Query querysecondCount = null;
			Query querythirdCount = null;
			Query queryExpiredCount = null;

			try {
				queryfirstCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, staff_record a where r.first_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.first_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				querysecondCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, staff_record a where r.second_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.second_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				querythirdCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, staff_record a where r.third_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.third_reminder_sent_at is null and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id");

				queryExpiredCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, staff_record a where r.effective_expiry_date <= '"
								+ currentDate + "'and r.active = 1 and a.reminder_id =" + as[7]
								+ " and  a.reminder_id=r.reminder_id ");

			} catch (Exception e) {
				logger.error("Error executing queries" + e);
			}

			firstCount = ((BigInteger) queryfirstCount.getSingleResult()).intValue();
			secondCount = ((BigInteger) querysecondCount.getSingleResult()).intValue();
			thirdCount = ((BigInteger) querythirdCount.getSingleResult()).intValue();
			expireCount = ((BigInteger) queryExpiredCount.getSingleResult()).intValue();

			if (firstCount != 0 || secondCount != 0 || thirdCount != 0) {
				simpleMail = new SimpleMail365();

				if (secondReminderdate == null || (thirdReminderdate == null && secondCount != 0)
						|| (thirdReminderdate != null && thirdCount != 0)) {

					Set<MyGroupDetails> setccLastRem = mailList.getCcLastReminderList();
					String ccLastRemEmail = "";
					if (setccLastRem != null) {
						if (!setccLastRem.isEmpty()) {
							for (MyGroupDetails groupDetails : setccLastRem) {
								ccLastRemEmail += groupDetails.getEmailId();
								if (ccLastRemEmail.length() > 0)
									ccLastRemEmail += ",";
							}
						}
					}

					if (!StringUtils.isEmpty(ccLastRemEmail)) {
						if (ccEmail.length() > 0)
							ccEmail += ",";
						ccEmail += ccLastRemEmail;

						/*
						 * if (addCCEmail.length() > 0) addCCEmail += ",";
						 * addCCEmail += ccLastRemEmail;
						 */
					}
					String addCCLastRem = (String) as[8];
					String addCCLastRemEmail = replaceCharacters(addCCLastRem).trim();
					if (!StringUtils.isEmpty(addCCLastRemEmail)) {
						if (addCCEmail.length() > 0)
							addCCEmail += ",";
						addCCEmail += addCCLastRemEmail;
					}
				}

				/*
				 * if (!StringUtils.isEmpty(toEmail))
				 * simpleMail.setTo(getEmailArray(toEmail));
				 */
				if (!StringUtils.isEmpty(addCCEmail))
					simpleMail.setCc(getEmailArray(addCCEmail));
				if (as[3] != null) {
					simpleMail.setSubject(
							EXPIRING + (String) as[6] + EXGSUB + convertDateToStringWithFormat((Date) as[3]));
					String text = "Hi " + sf.toString() + "<br><br>" + (String) as[6] + EXGSUB
							+ convertDateToStringWithFormat((Date) as[3])
							+ ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT;
					simpleMail.setText(text.replace("link", appLink));
				} else {
					simpleMail.setSubject(EXPIRING + (String) as[6] + EXGSUB);
					String text = "Hi " + sf.toString() + "<br><br>" + (String) as[6] + EXGSUB
							+ ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT;
					simpleMail.setText(text.replace("link", appLink));
				}
				boolean result = false;
				if (!StringUtils.isEmpty(addCCEmail)) {
					logger.info("sending Expiring email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", for staff = " + (String) as[6]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}

				// updateReminder(currentDate, firstCount, secondCount,
				// thirdCount, simpleMail, as);
				StaffAggregateReminder aggregateReminder = new StaffAggregateReminder((String) as[6], (String) as[15],
						(String) as[14], toEmail, ccEmail, "Expiring", sf.toString(), expiryDate, groupName, firstCount,
						secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<StaffAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<StaffAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}

			}
			if (expireCount != 0) {
				simpleMail = new SimpleMail365();
				logger.info("including expired reminders");
				Set<MyGroupDetails> setccExpiry = mailList.getCcExpiryReminderList();
				String ccExpiry = "";
				if (setccExpiry != null) {
					if (!setccExpiry.isEmpty()) {
						for (MyGroupDetails groupDetails : setccExpiry) {
							ccExpiry += groupDetails.getEmailId();
							if (ccExpiry.length() > 0)
								ccExpiry += ",";
						}
					}
				}

				if (ccExpiry.length() > 0) {
					if (ccEmail.length() > 0)
						ccEmail += ",";
					ccEmail += ccExpiry;
				}

				String addCCExpiry = (String) as[9];
				if (addCCExpiry != null) {
					addCCExpiry = replaceCharacters(addCCExpiry);
					if (addCCExpiry.length() > 0) {
						if (addCCEmail.length() > 0)
							addCCEmail += ",";
						addCCEmail += addCCExpiry;
					}
				}

				/*
				 * if (!StringUtils.isEmpty(toEmail))
				 * simpleMail.setTo(getEmailArray(toEmail));
				 */
				if (!StringUtils.isEmpty(addCCEmail))
					simpleMail.setCc(getEmailArray(addCCEmail));

				simpleMail.setSubject(
						EXPIRED + (String) as[6] + " has expired " + convertDateToStringWithFormat((Date) as[3]));
				String text = "Hi " + sf.toString() + "<br><br>" + (String) as[6] + EXSUB
						+ convertDateToStringWithFormat((Date) as[3])
						+ ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT;
				simpleMail.setText(text.replace("link", appLink));

				StaffAggregateReminder aggregateReminder = new StaffAggregateReminder((String) as[6], (String) as[15],
						(String) as[14], toEmail, ccEmail, "Expired", sf.toString(), expiryDate, groupName, firstCount,
						secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<StaffAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<StaffAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}
				boolean result = false;
				if (!StringUtils.isEmpty(addCCEmail)) {
					logger.info("sending Expired email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", for Staff = " + (String) as[6]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}
			}

		}
		for (Integer ids : expringMap.keySet()) {
			List<StaffAggregateReminder> list = expringMap.get(ids);
			if (!list.isEmpty()) {
				SimpleMail365 mail = new SimpleMail365();
				mail.setSubject(REMINDER365);

				StringBuilder textHeader = new StringBuilder("Hi ");
				StringBuilder text = new StringBuilder("<h3> <u><font color=\"red\">"
						+ "Expired Staff Monitor Record(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'><tr><th style='padding:5px;'>Staff Name</th><th style='padding:5px;'>Staff User ID</th><th style='padding:5px;'>Record Type</th><th style='padding:5px;'>Group Name</th><th style='padding:5px;'>Expiry Date</th></tr>");
				StringBuilder textExpiring = new StringBuilder("<h3><u><font color=\"orange\">"
						+ "Expiring Staff Monitor Record(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'> <tr><th style='padding:5px;'>Staff Name</th><th style='padding:5px;'>Staff User ID</th><th style='padding:5px;'>Record Type</th><th style='padding:5px;'>Group Name</th><th style='padding:5px;'>Expiry Date</th></tr>");
				StringBuilder footer = new StringBuilder(
						ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT);
				boolean expiredFlag = false;
				boolean expiringFlag = false;
				int count = 0;
				for (StaffAggregateReminder staffAggregateReminder : list) {

					if (count == 0) {
						if (!StringUtils.isEmpty(staffAggregateReminder.getToList()))
							mail.setTo(getEmailArray(staffAggregateReminder.getToList()));
						if (!StringUtils.isEmpty(staffAggregateReminder.getCcList()))
							mail.setCc(getEmailArray(staffAggregateReminder.getCcList()));
						textHeader.append(staffAggregateReminder.getUserName() + "<br><br>");
						textHeader
								.append("I would like to draw your attention to the following staff monitor records(s)."
										+ "<br>");
					}

					if (staffAggregateReminder.getType().equals("Expired")) {
						expiredFlag = true;
						text.append("<tr>");
						text.append("<td style='padding:5px;'>" + staffAggregateReminder.getStaffName() + "</td>");
						text.append("<td style='padding:5px;'>" + staffAggregateReminder.getStaffCode() + "</td>");
						text.append("<td style='padding:5px;'>" + staffAggregateReminder.getRecordType() + "</td>");
						text.append("<td style='padding:5px;'>" + staffAggregateReminder.getGroupName() + "</td>");
						text.append("<td style='padding:5px;'>" + staffAggregateReminder.getExpiryDate() + "</td>");
						text.append("</tr>");
						logger.info("sending Expired emails for: " + staffAggregateReminder.getStaffName());
					}
					count++;
				}
				text.append("</table>");
				for (StaffAggregateReminder staffAggregateReminder : list) {
					if (staffAggregateReminder.getType().equals("Expiring")) {
						expiringFlag = true;
						textExpiring.append("<tr>");
						textExpiring
								.append("<td style='padding:5px;'>" + staffAggregateReminder.getStaffName() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + staffAggregateReminder.getStaffCode() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + staffAggregateReminder.getRecordType() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + staffAggregateReminder.getGroupName() + "</td>");
						textExpiring
								.append("<td style='padding:5px;'>" + staffAggregateReminder.getExpiryDate() + "</td>");
						textExpiring.append("</tr>");
						logger.info("sending Expiring emails for: " + staffAggregateReminder.getStaffName());
					}
				}
				textExpiring.append("</table>");
				String mailtext = textHeader.toString();
				if (expiredFlag) {
					mailtext += text.toString() + " ";
				}
				if (expiringFlag) {
					mailtext += textExpiring.toString() + " ";
				}
				boolean result = false;
				mail.setText(mailtext + footer.toString());
				logger.info("sending emails for all the Staff Reminders where reminder date is less then equal to: "
						+ currentDate);
				result = sendMail(mail);
				if (result) {
					for (StaffAggregateReminder staffAggregateReminder : list) {
						updateReminder(currentDate, staffAggregateReminder.getFirstCount(),
								staffAggregateReminder.getSecondCount(), staffAggregateReminder.getThirdCount(),
								staffAggregateReminder.getId());
					}
				}
				loggingMailDetails(mail, result);
			}
		}
	}

	/**
	 * @param simpleMail
	 * @param result
	 */
	private void loggingMailDetails(SimpleMail365 simpleMail, boolean result) {
		if (result == true) {
			logger.info("Email sent to " + Arrays.toString(simpleMail.getTo()) + ", "
					+ Arrays.toString(simpleMail.getCc()));
		} else {
			logger.info("Exception occured at sendMail() method " + Arrays.toString(simpleMail.getTo()) + ", "
					+ Arrays.toString(simpleMail.getCc()));
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getStaffQuery() {
		List<Object[]> allStaff = entityManager.createNativeQuery(
				"select r.first_reminder_date,r.second_reminder_date,r.third_reminder_date,r.effective_expiry_date,"
						+ " r.created_by_id,a.additional_cc_List,s.name,r.reminder_id,r.addcc_list_lastreminder,r.addcc_list_expiryreminder"
						+ ",r.first_reminder_sent_at,r.second_reminder_sent_at,r.third_reminder_sent_at,r.user_group_id,rt.type,s.psa_staff_id"
						+ " from reminder r " + " join staff_record a "
						+ " join staff s on s.staff_id= a.staff_id join record_type rt on rt.record_type_id=a.record_type_id"
						+ " where " + " r.active = 1 and "
						+ " r.reminder_id=a.reminder_id order by r.effective_expiry_date asc")
				.getResultList();
		return allStaff;
	}

	/**
	 * 
	 * @param body
	 * @return
	 * @throws IOException
	 */
	/*
	 * public File linkJavaHtml(String body) throws IOException{ File
	 * htmlTemplateFile = new File("C:\\SMC3\\EmailTemplate.html");
	 * Charset.forName("UTF-8"); String htmlString =
	 * FileUtils.readFileToString(htmlTemplateFile); htmlString =
	 * htmlString.replace("$body", body); File newHtmlFile = new
	 * File("C:\\SMC3\\NewEmailTemplate.html");
	 * FileUtils.writeStringToFile(newHtmlFile, htmlString); return newHtmlFile;
	 * }
	 */

	/**
	 * sending email
	 * 
	 * @param simpleEmail
	 */
	private boolean sendMail(SimpleMail365 simpleEmail) {
		MimeMessage message = mailSenderObj.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			String encodedSubject = new String(simpleEmail.getSubject().getBytes("UTF-8"), "UTF-8");
			helper.setSubject(encodedSubject);

			if (simpleEmail.getCc() != null && simpleEmail.getCc().length != 0) {
				helper.setCc(simpleEmail.getCc());
			}

			if (simpleEmail.getTo() != null && simpleEmail.getTo().length != 0) {
				helper.setTo(simpleEmail.getTo());
			} else {
				helper.setTo(new String[] {});
			}
			// message.setSubject(simpleEmail.getSubject(), "text/html;
			// charset=UTF-8");
			// message.setHeader("Content-Type", "text/html; charset=UTF-8");
			message.addHeader("Content-Transfer-Encoding", "7bit");
			message.setContent(simpleEmail.getText(), "text/html; charset=UTF-8");
			// helper.setFrom(InetAddress.getLocalHost().getHostName()+
			// "@globalpsa.com");
			// helper.setFrom(new
			// InternetAddress(InetAddress.getLocalHost().getHostName()+
			// "@globalpsa.com", "Reminder365"));
			helper.setFrom(fromAddress, "Reminder365");
			mailSenderObj.send(message);
			return true;
		} catch (MailSendException e) {
			if (simpleEmail.getTo() == null) {
				logger.error("MailSendException as To list is empty or invalid::" + e);
			}
			List<String> arrayToList = null;
			List<String> arrayCCList = null;
			if (simpleEmail.getTo() != null)
				arrayToList = new ArrayList<String>(Arrays.asList(simpleEmail.getTo()));
			if (simpleEmail.getCc() != null)
				arrayCCList = new ArrayList<String>(Arrays.asList(simpleEmail.getCc()));

			for (Exception exception : e.getMessageExceptions()) {
				if (exception instanceof SendFailedException) {
					// Address[] add = ((SendFailedException)
					// exception).getInvalidAddresses();
					if (((SendFailedException) exception).getInvalidAddresses() != null) {
						for (Address address : ((SendFailedException) exception).getInvalidAddresses()) {
							InternetAddress type = (InternetAddress) address;

							if (arrayToList != null && arrayToList.contains(type.getAddress())) {
								logger.error("MailSendException Invalid EmailID in To list::"+type.getAddress() +" : " + e);
								arrayToList.remove(type.getAddress());
							}
							if (arrayCCList != null && arrayCCList.contains(type.getAddress())) {
								logger.error("MailSendException Invalid EmailID in CC List::"+type.getAddress() +" : " + e);
								arrayCCList.remove(type.getAddress());
							}
						}
					}
				}
			}
			if (arrayToList != null)
				simpleEmail.setTo(arrayToList.toArray(new String[0]));
			if (arrayCCList != null)
				simpleEmail.setCc(arrayCCList.toArray(new String[0]));
			logger.error("Error in sending email to ::" + simpleEmail.getTo() == null ? ""
					: Arrays.toString(simpleEmail.getTo()), e);
			alertMail(e, simpleEmail);
			if (simpleEmail.getTo().length > 0)
				sendMail(simpleEmail);
			return false;
		} catch (MailAuthenticationException e) {
			logger.error("Mail Authentication fail in sending email to ::" + simpleEmail.getTo() == null ? ""
					: Arrays.toString(simpleEmail.getTo()), e);
			alertMail(e, simpleEmail);
			return false;
		} catch (Exception e) {
			logger.error("Error in sending email to ::" + simpleEmail.getTo() == null ? ""
					: Arrays.toString(simpleEmail.getTo()), e);
			alertMail(e, simpleEmail);
			return false;
		}
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@Override
	public boolean alertMail(Exception e, SimpleMail365 simpleEmail) {
		SimpleMail365 alertEmail = new SimpleMail365();
		String[] tolist = { alertMail };
		alertEmail.setTo(tolist);
		alertEmail.setSubject("Reminder365");
		alertEmail.setText(
				"Hi " + alertMail.substring(0, alertMail.indexOf('@')) + ",<br>Error while sending email to following :"
						+ simpleEmail.getText() + ". <br> Please check the below log: <br>" + e);
		logger.info("sending alert email with exception for notification");
		return sendMail(alertEmail);
	}

	/**
	 * convert date to string
	 * 
	 * @param date
	 * @return
	 */
	public String convertDateToString(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String strDate = dateFormat.format(c.getTime());
		return strDate;
	}

	private String convertDateToStringWithFormat(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String strDate = dateFormat.format(c.getTime());
		return strDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.reminder.dao.NotificationDAO#runCronJobContractNotification()
	 */
	@Override
	public void runCronJobContractNotification(Date notification) {
		SimpleMail365 simpleMail = null;
		Date currentDate = notification == null ? new MyDate(new Date()) : new MyDate(notification);

		List<Object[]> contractsObjectArray = getAllContracts();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// Map<Integer,List<ContractAggregateReminder>> expringMap = new
		// HashMap<>();
		SortedMap<Integer, List<ContractAggregateReminder>> expringMap = new TreeMap<>();

		for (Object[] as : contractsObjectArray) {
			String addCCList = "";
			int id = (int) as[13];
			Groups group = groupdao.getGroupById(id);
			String groupName = group.getGroupName();
			ContractDropDownValue mailList = recipient.getRecipients(id, m.getModuleTypeId(), 1);
			Date secondReminderdate = null;
			Date thirdReminderdate = null;

			int firstCount = 0;
			int secondCount = 0;
			int thirdCount = 0;
			int expireCount = 0;

			if (as[1] != null) {
				secondReminderdate = new MyDate(as[1]);
			}
			if (as[2] != null) {
				thirdReminderdate = new MyDate(as[2]);
			}

			User user = null;
			if ((int) as[4] != 0)
				user = userService.getUserById((int) as[4]);

			String ccEmail = "";
			if (!CollectionUtils.isEmpty(mailList.getCcList())) {
				for (MyGroupDetails groupDetails : mailList.getCcList()) {
					ccEmail += groupDetails.getEmailId();
					if (mailList.getCcList().size() > 0)
						ccEmail += ",";
				}
			}

			String addCC = (String) as[5];
			String addCCEmail = "";
			if (addCC != null)
				addCCEmail = replaceCharacters(addCC);

			if (!StringUtils.isEmpty(addCCEmail)) {
				if (addCCList.length() > 0)
					addCCList += ",";
				addCCList += addCCEmail;
			}

			Set<MyGroupDetails> setemail = mailList.getToList();
			String toEmail = "";
			if (setemail != null) {
				if (!setemail.isEmpty()) {
					for (MyGroupDetails groupDetails : setemail) {
						toEmail += groupDetails.getEmailId();
						if (setemail.size() > 0)
							toEmail += ",";
					}
				}
			}

			/*
			 * if(setemail != null){ if (!setemail.contains(ofc.getEmailId())) {
			 * if(setemail.size() ==1) toEmail +=","; toEmail +=
			 * ofc.getEmailId(); toEmail += ","; } }else{ toEmail +=
			 * ofc.getEmailId(); toEmail += ","; }
			 */

			User ofc = userService.getUserById((int) as[15]);
			String ofcEmailId = "";
			String ofcUserName = "";
			if (ofc != null) {
				ofcEmailId = ofc.getEmailId();
				ofcUserName = getUserByEmailId(ofcEmailId);
			}
			
			
			// getting username
			String[] tolist = getEmailArray(toEmail);
			StringBuffer sf = new StringBuffer();
			if (!toEmail.isEmpty()) {
				for (int i = 0; i < tolist.length; i++) {
					String toName = getUserByEmailId(tolist[i]);
					if (toName != null) {
						sf.append(toName);
						sf.append(",");
					}
				}
			}

			/*
			 * if (!Arrays.asList(tolist).contains(ofc.getEmailId())) {
			 * sf.append(ofcUserName); sf.append(","); }
			 */

			Query queryfirstCount = null;
			Query querysecondCount = null;
			Query querythirdCount = null;
			Query queryExpiredCount = null;

			try {
				queryfirstCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, contract c where r.first_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.first_reminder_sent_at is null and r.reminder_id =" + as[7]
								+ "  and r.reminder_id = c.reminder_id");

				querysecondCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, contract c where r.second_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.second_reminder_sent_at is null and r.reminder_id =" + as[7]
								+ "  and r.reminder_id = c.reminder_id");

				querythirdCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, contract c where r.third_reminder_date <= '"
								+ currentDate
								+ "'and r.active = 1 and r.third_reminder_sent_at is null and r.reminder_id =" + as[7]
								+ "  and r.reminder_id = c.reminder_id");

				queryExpiredCount = entityManager.createNativeQuery(
						"select count(r.reminder_id) from reminder r, contract c where r.effective_expiry_date <= '"
								+ currentDate + "'and r.active = 1 and r.reminder_id =" + as[7]
								+ " and r.reminder_id = c.reminder_id ");

			} catch (Exception e) {
				logger.error("Error executing queries" + e);
			}

			firstCount = ((BigInteger) queryfirstCount.getSingleResult()).intValue();
			secondCount = ((BigInteger) querysecondCount.getSingleResult()).intValue();
			thirdCount = ((BigInteger) querythirdCount.getSingleResult()).intValue();
			expireCount = ((BigInteger) queryExpiredCount.getSingleResult()).intValue();

			if (firstCount != 0 || secondCount != 0 || thirdCount != 0) {
				simpleMail = new SimpleMail365();

				if (secondReminderdate == null || (thirdReminderdate == null && secondCount != 0)
						|| (thirdReminderdate != null && thirdCount != 0)) {

					Set<MyGroupDetails> setccLastRem = mailList.getCcLastReminderList();
					String ccLastRemEmail = "";
					if (setccLastRem != null) {
						if (!setccLastRem.isEmpty()) {
							for (MyGroupDetails groupDetails : setccLastRem) {
								ccLastRemEmail += groupDetails.getEmailId();
								if (ccLastRemEmail.length() > 0)
									ccLastRemEmail += ",";
							}
						}
					}

					if (!StringUtils.isEmpty(ccLastRemEmail)) {
						/*
						 * if (ccEmail.length() > 0) ccEmail += ",";
						 */
						ccEmail += ccLastRemEmail;

						// Adding to add CC list
						/*
						 * if (addCCList.length() > 0) addCCList += ",";
						 * addCCList += ccLastRemEmail;
						 */
					}

					String addCCLastRem = (String) as[8];
					String addCCLastRemEmail = "";
					if (addCCLastRem != null)
						addCCLastRemEmail = replaceCharacters(addCCLastRem);

					if (!StringUtils.isEmpty(addCCLastRemEmail)) {
						if (addCCList.length() > 0)
							addCCList += ",";
						addCCList += addCCLastRemEmail;

					}

				}

				if (!StringUtils.isEmpty(addCCList))
					simpleMail.setCc(getEmailArray(addCCList));
				simpleMail.setSubject(CONTRACT_SUBJECT + (String) as[6]);
				String text = "Hi " + sf.toString() + "<br><br>" + CONTRACT
						+ convertDateToStringWithFormat((Date) as[3]) + ".<br><br>" + CONTRACTLINE2 + ".<br><br>"
						+ BTEXT + CTEXT;
				simpleMail.setText(
						text.replace("link", getAppLink(REMINDER365)).replace("[Contract title]", (String) as[6]));
				ContractAggregateReminder aggregateReminder = new ContractAggregateReminder((String) as[14],
						(String) as[6], toEmail, ccEmail, "Expiring", sf.toString(),
						convertDateToStringWithFormat((Date) as[3]), groupName, ofc.getUserName(), firstCount,
						secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<ContractAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<ContractAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}
				boolean result = false;
				if (!StringUtils.isEmpty(addCCList)) {
					logger.info("sending Expiring email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", for Contract = " + (String) as[6]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}

				if (ofc != null) {
					simpleMail = new SimpleMail365();
					if (!StringUtils.isEmpty(ofcEmailId))
						simpleMail.setTo(getEmailArray(ofcEmailId));
					simpleMail.setSubject(CONTRACT_SUBJECT + (String) as[6]);
					String ofctext = "Hi " + ofcUserName + "<br><br>" + CONTRACT
							+ convertDateToStringWithFormat((Date) as[3]) + ".<br><br>" + CONTRACTLINE2 + ".<br><br>"
							+ BTEXT + CTEXT;
					simpleMail.setText(ofctext.replace("link", getAppLink(REMINDER365)).replace("[Contract title]",
							(String) as[6]));

					boolean ofcresult = false;
					if (!StringUtils.isEmpty(ofcEmailId)) {
						logger.info("sending Expiring email to office in charge: " + Arrays.toString(simpleMail.getTo())
								+ ", for Contract = " + (String) as[6]);
						ofcresult = sendMail(simpleMail);
						loggingMailDetails(simpleMail, ofcresult);
					}
				}
				// updateReminder(currentDate, firstCount, secondCount,
				// thirdCount, simpleMail, as);
			}
			if (expireCount != 0) {
				simpleMail = new SimpleMail365();
				logger.info("including Expired reminders");
				Set<MyGroupDetails> setccExpiry = mailList.getCcExpiryReminderList();
				String ccExpiry = "";
				if (setccExpiry != null) {
					if (!setccExpiry.isEmpty()) {
						for (MyGroupDetails groupDetails : setccExpiry) {
							ccExpiry += groupDetails.getEmailId();
							if (ccExpiry.length() > 0)
								ccExpiry += ",";
						}
					}
				}

				if (!StringUtils.isEmpty(ccExpiry)) {
					/*
					 * if (ccEmail.length() > 0) ccEmail += ",";
					 */
					ccEmail += ccExpiry;
				}

				String addCCExpiry = (String) as[9];
				if (addCCExpiry != null)
					addCCExpiry = replaceCharacters(addCCExpiry).trim();

				if (!StringUtils.isEmpty(addCCExpiry)) {
					if (addCCList.length() > 0)
						addCCList += ",";
					addCCList += addCCExpiry;
				}

				/*
				 * if (!StringUtils.isEmpty(toEmail))
				 * simpleMail.setTo(getEmailArray(toEmail));
				 */
				if (!StringUtils.isEmpty(addCCList))
					simpleMail.setCc(getEmailArray(addCCList));

				simpleMail.setSubject(CONTRACT_SUBJECT + (String) as[6]);
				String text = "Hi " + sf.toString() + "<br><br>" + CONTRACTEXPIRED
						+ convertDateToStringWithFormat((Date) as[3]) + ".<br><br>" + CONTRACTLINE2 + ".<br><br>"
						+ BTEXT + CTEXT;
				simpleMail.setText(
						text.replace("link", getAppLink(REMINDER365)).replace("[Contract title]", (String) as[6]));

				ContractAggregateReminder aggregateReminder = new ContractAggregateReminder((String) as[14],
						(String) as[6], toEmail, ccEmail, "Expired", sf.toString(),
						convertDateToStringWithFormat((Date) as[3]), groupName, ofc.getUserName(), firstCount,
						secondCount, thirdCount, (int) as[7]);

				if (expringMap.get(id) == null) {
					List<ContractAggregateReminder> list = new ArrayList<>();
					list.add(aggregateReminder);
					expringMap.put(id, list);
				} else {
					List<ContractAggregateReminder> list = expringMap.get(id);
					list.add(aggregateReminder);
					expringMap.put(id, list);
				}
				boolean result = false;
				if (!StringUtils.isEmpty(addCCList)) {
					logger.info("sending Expired email to additional ccList users: "
							+ Arrays.toString(simpleMail.getCc()) + ", for Contract = " + (String) as[6]);
					result = sendMail(simpleMail);
					loggingMailDetails(simpleMail, result);
				}

				if (ofc != null) {
					simpleMail = new SimpleMail365();
					if (!StringUtils.isEmpty(ofcEmailId))
						simpleMail.setTo(getEmailArray(ofcEmailId));
					simpleMail.setSubject(CONTRACT_SUBJECT + (String) as[6]);
					String ofctext = "Hi " + ofcUserName + "<br><br>" + CONTRACTEXPIRED
							+ convertDateToStringWithFormat((Date) as[3]) + ".<br><br>" + CONTRACTLINE2 + ".<br><br>"
							+ BTEXT + CTEXT;
					simpleMail.setText(ofctext.replace("link", getAppLink(REMINDER365)).replace("[Contract title]",
							(String) as[6]));

					boolean ofcresult = false;
					if (!StringUtils.isEmpty(ofcEmailId)) {
						logger.info("sending Expired email to office in charge: " + Arrays.toString(simpleMail.getTo())
								+ ", for Contract = " + (String) as[6]);
						ofcresult = sendMail(simpleMail);
						loggingMailDetails(simpleMail, ofcresult);
					}
				}

			}
		}

		for (Integer ids : expringMap.keySet()) {
			List<ContractAggregateReminder> list = expringMap.get(ids);
			if (!list.isEmpty()) {
				StringBuilder textHeader = new StringBuilder("");
				StringBuilder text = new StringBuilder("<h3> <u><font color=\"red\">" + "Expired Contract(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'><tr><th style='padding:5px;' >Contract Ref</th><th style='padding:5px;'>Contract Title</th><th style='padding:5px;' >Officer In Charge</th><th style='padding:5px;' >Group Name</th><th style='padding:5px;' >Expiry Date</th></tr>");
				StringBuilder textExpiring = new StringBuilder("<h3><u><font color=\"orange\">"
						+ "Expiring Contract(s):"
						+ "</font></u></h3> <table border='1' style='border-spacing: unset;'> <tr><th style='padding:5px;'>Contract Ref</th><th style='padding:5px;'>Contract Title</th><th style='padding:5px;' >Officer In Charge</th><th style='padding:5px;' >Group Name</th><th style='padding:5px;'>Expiry Date</th></tr>");
				StringBuilder footer = new StringBuilder(
						ATEXT.replace(REMINDER365, getAppLink(REMINDER365)) + BTEXT + CTEXT);

				SimpleMail365 mail = new SimpleMail365();
				mail.setSubject(REMINDER365);
				boolean expiredFlag = false;
				boolean expiringFlag = false;
				StringBuilder sbtoList = new StringBuilder();
				StringBuilder sbccList = new StringBuilder();
				int count = 0;
				for (ContractAggregateReminder contractAggregateReminder : list) {
					textHeader.append(contractAggregateReminder.getUserName());
				}
				String[] name = getEmailArray(textHeader.toString());
				textHeader.setLength(0);
				textHeader.append("Hi ");
				for (String s : name)
					textHeader.append(s + ",");
				for (ContractAggregateReminder contractAggregateReminder : list) {
					// if (count == 0) {
					if (!StringUtils.isEmpty(contractAggregateReminder.getToList())) {
						// mail.setTo(getEmailArray(contractAggregateReminder.getToList()));
						sbtoList.append(contractAggregateReminder.getToList());
					}
					if (!StringUtils.isEmpty(contractAggregateReminder.getCcList())) {
						// mail.setCc(getEmailArray(contractAggregateReminder.getCcList()));
						sbccList.append(contractAggregateReminder.getCcList());
					}

					// textHeader.append(contractAggregateReminder.getUserName()
					// + "<br><br>");
					if (count == 0) {
						textHeader.append("<br><br>"
								+ "I would like to draw your attention to the following contract(s)." + "<br>");
					}

					if (contractAggregateReminder.getType().equals("Expired")) {
						expiredFlag = true;
						text.append("<tr>");
						text.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getContractRefNo() + "</td>");
						text.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getContractTitle() + "</td>");
						text.append("<td style='padding:5px;'>" + contractAggregateReminder.getOic() + "</td>");
						text.append("<td style='padding:5px;'>" + contractAggregateReminder.getGroupName() + "</td>");
						text.append("<td style='padding:5px;'>" + contractAggregateReminder.getExpiryDate() + "</td>");
						text.append("</tr>");
						logger.info("sending Expired emails for: " + contractAggregateReminder.getContractTitle());
					}
					count++;
				}
				text.append("</table>");

				for (ContractAggregateReminder contractAggregateReminder : list) {
					if (contractAggregateReminder.getType().equals("Expiring")) {
						expiringFlag = true;
						textExpiring.append("<tr>");
						textExpiring.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getContractRefNo() + "</td>");
						textExpiring.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getContractTitle() + "</td>");
						textExpiring.append("<td style='padding:5px;'>" + contractAggregateReminder.getOic() + "</td>");
						textExpiring.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getGroupName() + "</td>");
						textExpiring.append(
								"<td style='padding:5px;'>" + contractAggregateReminder.getExpiryDate() + "</td>");
						textExpiring.append("</tr>");
						logger.info("sending Expiring emails for: " + contractAggregateReminder.getContractTitle());
					}
				}

				textExpiring.append("</table>");
				String mailtext = textHeader.toString();
				if (expiredFlag) {
					mailtext += text.toString() + " ";
				}
				if (expiringFlag) {
					mailtext += textExpiring.toString() + " ";
				}
				boolean result = false;
				mail.setText(mailtext + footer.toString());
				logger.info("sending emails for all the Contract Reminders where reminder date is less then equal to: "
						+ currentDate);
				if (sbtoList.toString().isEmpty())
					mail.setTo(null);
				else
					mail.setTo(getEmailArray(sbtoList.toString()));
				if (sbccList.toString().isEmpty())
					mail.setCc(null);
				else
					mail.setCc(getEmailArray(sbccList.toString()));
				result = sendMail(mail);
				if (result) {
					for (ContractAggregateReminder contractAggregateReminder : list) {
						updateReminder(currentDate, contractAggregateReminder.getFirstCount(),
								contractAggregateReminder.getSecondCount(), contractAggregateReminder.getThirdCount(),
								contractAggregateReminder.getId());
					}
				}
				loggingMailDetails(mail, result);
			}
		}
	}

	/**
	 * @param ccLastRem
	 * @return
	 */
	private String replaceCharacters(String ccLastRem) {
		if (null == ccLastRem) {
			return "";
		}
		return ccLastRem.replace("[", "").replace("\"", "").replace("]", "").trim();
	}

	/**
	 * @param toEmail
	 * @return
	 */
	private String[] getEmailArray(String toEmail) {
		String[] array = toEmail.split(",");
		Set<String> mySet = new HashSet<String>(Arrays.asList(array));
		return mySet.stream().toArray(n -> new String[n]);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getAllContracts() {
		List<Object[]> allContract = entityManager
				.createNativeQuery("select r.first_reminder_date,r.second_reminder_date,r.third_reminder_date,"
						+ " r.effective_expiry_date,r.created_by_id,c.additional_cc_List,c.contract_title,r.reminder_id,r.addcc_list_lastreminder,"
						+ " r.addcc_list_expiryreminder"
						+ ",r.first_reminder_sent_at,r.second_reminder_sent_at,r.third_reminder_sent_at,r.user_group_id,c.contract_reference_number,c.officer_in_charge_id"
						+ " from reminder r " + " join contract c " + " where " + " r.active = 1 and "
						+ "  (c.version, c.parent_contract_id) in "
						+ " (select  max(c1.version) as max_version, c1.parent_contract_id from contract c1 where c1.is_verified=true or c1.is_deleted=true "
						+ "group by c1.parent_contract_id)"
						+ " and r.reminder_id = c.reminder_id order by r.effective_expiry_date asc")
				.getResultList();
		return allContract;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.dao.NotificationDAO#sentCreateContractEmail(com.reminder.
	 * model.Contract, com.reminder.model.User)
	 */
	@Override
	public void sendCreateContractEmail(Contract entity, User createdUser) {
		int groupId = entity.getReminder().getUserGroupId().getGroupId();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// ContractDropDownValue mailList =
		// contractService.getAllContractDropDownValue(groupId,
		// m.getModuleTypeId(), 1);
		// Set<MyGroupDetails> setemail = mailList.getToList();

		String toEmail = "";
		/*
		 * if (!CollectionUtils.isEmpty(setemail)) { for (MyGroupDetails
		 * groupDetails : setemail) { toEmail += groupDetails.getEmailId(); if
		 * (setemail.size() >= 1) toEmail += ","; } }
		 */
		// String toEmail = entity.getToList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// Set<MyGroupDetails> setccEmail = mailList.getCcList();

		String ccEmail = "";
		/*
		 * if(setccEmail!=null){ if (!setccEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setccEmail) { ccEmail +=
		 * groupDetails.getEmailId(); if (setccEmail.size() >= 1) ccEmail +=
		 * ","; } } }
		 */
		// String ccEmail = entity.getCcList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// String addCCEmail = entity.getAdditionalCcList().replace("[",
		// "").replace("\"", "").replace("]", "");

		List<Contract_Reviewer> contract_Reviewers = entity.getContractReviewer();

		String userName = "";
		int count = 0;
		List<String> userNameList = new ArrayList<>();

		for (Contract_Reviewer contract_Reviewer : contract_Reviewers) {
			if (count != 0)
				userName += "/";
			if (!userNameList.contains(contract_Reviewer.getUserId().getUserName())) {
				userName += contract_Reviewer.getUserId().getUserName();
				userNameList.add(contract_Reviewer.getUserId().getUserName());
			}
			toEmail += contract_Reviewer.getUserId().getEmailId();
			if (!StringUtils.isEmpty(toEmail))
				toEmail += ",";
			count++;
		}

		/*
		 * if (addCCEmail.length() > 0) { if (ccEmail.length() > 0) ccEmail +=
		 * ","; ccEmail += addCCEmail; }
		 */

		ccEmail += createdUser.getEmailId();
		if (ccEmail.length() > 0)
			ccEmail += ",";

		SimpleMail365 simpleMail = new SimpleMail365();
		if (!StringUtils.isEmpty(toEmail)) {
			String emailArray[] = getEmailArray(toEmail);
			emailArray = removeMakerEmailId(createdUser, emailArray);
			simpleMail.setTo(emailArray);
		}
		if (!StringUtils.isEmpty(ccEmail))
			simpleMail.setCc(getEmailArray(ccEmail));
		simpleMail.setSubject(entity.getContractTitle() + CONTRACTCREATIONSUB);
		String text = "Hi " + userName + ",<br><br>" + entity.getContractTitle() + " created by "
				+ createdUser.getUserName() + CONTRACTCREATION + ".<br><br>" + BTEXT + CTEXT;
		simpleMail.setText(text.replace("link", getAppLink(REMINDER365)));
		boolean result = sendMail(simpleMail);
		if (result)
			logger.info("Create contract reminder is sent to: " + userName + ", to group: "
					+ entity.getReminder().getUserGroupId().getGroupName() + ", Contrat: " + entity.getContractTitle());
		loggingMailDetails(simpleMail, result);
	}

	private String[] removeMakerEmailId(User createdUser, String[] emailArray) {
		for (String email : emailArray) {
			if (email.equals(createdUser.getEmailId())) {
				emailArray = (String[]) ArrayUtils.removeElement(emailArray, email);
			}
		}
		return emailArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.dao.NotificationDAO#sendRejectedContractEmail(com.reminder.
	 * model.Contract, com.reminder.model.User)
	 */

	public void sendVerifiedContractEmail(Contract entity, User createdUser, String contractStatus) {
		Contract contract = contractDAO.getContractById(entity.getContractId());

		int groupId = contract.getReminder().getUserGroupId().getGroupId();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// ContractDropDownValue mailList =
		// contractService.getAllContractDropDownValue(groupId,
		// m.getModuleTypeId(), 1);
		// Set<MyGroupDetails> setemail = mailList.getToList();

		@SuppressWarnings("unchecked")
		List<Contract_Reviewer> contract_Reviewers = entityManager
				.createQuery("select c from Contract_Reviewer c where c.contract.contractId=:contract_id")
				.setParameter("contract_id", entity.getContractId()).getResultList();
		// List<Contract_Reviewer> contract_Reviewers =
		// entity.getContractReviewer();

		String toEmail = "";
		/*
		 * Set<String> toEmailSet = new HashSet<>();
		 * 
		 * if (!CollectionUtils.isEmpty(setemail)) { for (MyGroupDetails
		 * groupDetails : setemail) { toEmailSet.add(groupDetails.getEmailId());
		 * toEmail += groupDetails.getEmailId(); if (setemail.size() >= 1)
		 * toEmail += ","; } }
		 */
		// String toEmail = entity.getToList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// Set<MyGroupDetails> setccEmail = mailList.getCcList();

		String ccEmail = "";
		/*
		 * if(setccEmail!=null){ if (!setccEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setccEmail) { ccEmail +=
		 * groupDetails.getEmailId(); if (setccEmail.size() >= 1) ccEmail +=
		 * ","; } } }
		 */
		// String ccEmail = entity.getCcList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// String addCCEmail = contract.getAdditionalCcList().replace("[",
		// "").replace("\"", "").replace("]", "");

		/*
		 * if (addCCEmail.length() > 0) { if (ccEmail.length() > 0) ccEmail +=
		 * ","; ccEmail += addCCEmail; }
		 */

		String userName = "";
		/*
		 * Set<MyGroupDetails> setReviewerEmail = mailList.getAllReviewers();
		 * 
		 * if(setReviewerEmail!=null){ if (!setReviewerEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setReviewerEmail) { if (ccEmail!="" ){
		 * ccEmail += ","; ccEmail += groupDetails.getEmailId(); }else ccEmail
		 * += groupDetails.getEmailId(); } } }
		 */

		int count = 0;
		// Checker emailId in cc list
		for (Contract_Reviewer contract_Reviewer : contract_Reviewers) {
			if (count != 0)
				userName += "/";
			userName += contract_Reviewer.getUserId().getUserName();
			ccEmail += contract_Reviewer.getUserId().getEmailId();
			if (!StringUtils.isEmpty(ccEmail))
				ccEmail += ",";
			count++;
		}
		// Maker emailId
		User u = userService.getUserById(contract.getReminder().getCreatedById());
		toEmail += u.getEmailId();
		if (!StringUtils.isEmpty(toEmail))
			toEmail += ",";

		SimpleMail365 simpleMail = new SimpleMail365();
		if (!StringUtils.isEmpty(toEmail)) {
			String emailArray[] = getEmailArray(toEmail);
			emailArray = removeMakerEmailId(createdUser, emailArray);
			simpleMail.setTo(emailArray);
		}
		if (!StringUtils.isEmpty(ccEmail))
			simpleMail.setCc(getEmailArray(ccEmail));

		StringBuilder subject = new StringBuilder();
		StringBuilder text = new StringBuilder();
		text.append("Hi " + u.getUserName() + ",<br><br>");
		if (contractStatus.equalsIgnoreCase("New")) {
			subject.append(CONTRACTVERIFICATIONSUB1);
			text.append(CONTRACTVERIFICATIONSUB1);
		} else if (contractStatus.equalsIgnoreCase("Updated")) {
			subject.append(CONTRACTUPDATIONSUB1);
			text.append(CONTRACTUPDATIONSUB1);
		} else if (contractStatus.equalsIgnoreCase("Deleted")) {
			subject.append(CONTRACTDELETIONSUB1);
			text.append(CONTRACTDELETIONSUB1);
		}
		subject.append(" " + entity.getContractTitle() + CONTRACTVERIFICATIONSUB2);
		simpleMail.setSubject(subject.toString());
		text.append(" " + entity.getContractTitle() + CONTRACTVERIFICATIONTEXT1 + " " + createdUser.getUserName()
				+ CONTRACTVERIFICATIONTEXT2 + ".<br><br>" + BTEXT + CTEXT);
		simpleMail.setText(text.toString().replace("link", getAppLink(REMINDER365)));
		boolean result = sendMail(simpleMail);
		if (result)
			logger.info("contract verification reminder is sent to: " + userName + ", to group: "
					+ entity.getReminder().getUserGroupId().getGroupName() + ", Contrat: " + entity.getContractTitle());
		loggingMailDetails(simpleMail, result);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.dao.NotificationDAO#sendRejectedContractEmail(com.reminder.
	 * model.Contract, com.reminder.model.User)
	 */
	@Override
	public void sendRejectedContractEmail(Contract entity, User createdUser, String contractStatus) {

		int groupId = entity.getReminder().getUserGroupId().getGroupId();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// ContractDropDownValue mailList =
		// contractService.getAllContractDropDownValue(groupId,
		// m.getModuleTypeId(), 1);
		// Set<MyGroupDetails> setemail = mailList.getToList();

		@SuppressWarnings("unchecked")
		List<Contract_Reviewer> contract_Reviewers = entityManager
				.createQuery("select c from Contract_Reviewer c where c.contract.contractId=:contract_id")
				.setParameter("contract_id", entity.getContractId()).getResultList();

		String toEmail = "";
		/*
		 * if (!CollectionUtils.isEmpty(setemail)) { for (MyGroupDetails
		 * groupDetails : setemail) { toEmail += groupDetails.getEmailId(); if
		 * (setemail.size() >= 1) toEmail += ","; } }
		 */
		// String toEmail = entity.getToList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// Set<MyGroupDetails> setccEmail = mailList.getCcList();

		String ccEmail = "";
		/*
		 * if(setccEmail!=null){ if (!setccEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setccEmail) { ccEmail +=
		 * groupDetails.getEmailId(); if (setccEmail.size() >= 1) ccEmail +=
		 * ","; } } }
		 */
		// String ccEmail = entity.getCcList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// String addCCEmail = entity.getAdditionalCcList().replace("[",
		// "").replace("\"", "").replace("]", "");

		/*
		 * if (addCCEmail.length() > 0) { if (ccEmail.length() > 0) ccEmail +=
		 * ","; ccEmail += addCCEmail; }
		 */

		String userName = "";
		int count = 0;

		// CC mail to checker
		for (Contract_Reviewer contract_Reviewer : contract_Reviewers) {
			if (count != 0)
				userName += "/";
			userName += contract_Reviewer.getUserId().getUserName();
			ccEmail += contract_Reviewer.getUserId().getEmailId();
			if (!StringUtils.isEmpty(ccEmail))
				ccEmail += ",";
			count++;
		}
		// TO mail to maker
		User u = userService.getUserById(entity.getReminder().getCreatedById());
		toEmail += u.getEmailId();
		if (!StringUtils.isEmpty(toEmail))
			toEmail += ",";

		SimpleMail365 simpleMail = new SimpleMail365();
		if (!StringUtils.isEmpty(toEmail)) {
			String emailArray[] = getEmailArray(toEmail);
			emailArray = removeMakerEmailId(createdUser, emailArray);
			simpleMail.setTo(emailArray);
		}
		if (!StringUtils.isEmpty(ccEmail))
			simpleMail.setCc(getEmailArray(ccEmail));
		StringBuilder subject = new StringBuilder();
		StringBuilder text = new StringBuilder();
		text.append("Hi " + u.getUserName() + ",<br><br>");
		if (contractStatus.equalsIgnoreCase("New")) {
			subject.append(CONTRACTVERIFICATIONSUB1);
			text.append(CONTRACTVERIFICATIONSUB1);
		} else if (contractStatus.equalsIgnoreCase("Updated")) {
			subject.append(CONTRACTUPDATIONSUB1);
			text.append(CONTRACTUPDATIONSUB1);
		} else if (contractStatus.equalsIgnoreCase("Deleted")) {
			subject.append(CONTRACTDELETIONSUB1);
			text.append(CONTRACTDELETIONSUB1);
		}
		subject.append(" " + entity.getContractTitle() + CONTRACTREJECTIONSUB1);
		simpleMail.setSubject(subject.toString());
		text.append(" " + entity.getContractTitle() + CONTRACTREJECTIONSUB1 + " by " + createdUser.getUserName()
				+ CONTRACTVERIFICATIONTEXT2 + ".<br><br>" + BTEXT + CTEXT);
		simpleMail.setText(text.toString().replace("link", getAppLink(REMINDER365)));
		boolean result = sendMail(simpleMail);
		if (result)
			logger.info("contract rejection reminder is sent to: " + userName + ", to group: "
					+ entity.getReminder().getUserGroupId().getGroupName() + ", Contrat: " + entity.getContractTitle());
		loggingMailDetails(simpleMail, result);

	}

	/*
	 * private void logMessages(String userName, boolean result) { if (result ==
	 * true) { logger.info("Email sent to " + userName + " at " + date); } else
	 * { logger.info("Error in sending email to " + userName + " at " + date); }
	 * }
	 */

	@Override
	public void sendUpdatedContractMail(Contract entity, User createdUser) {
		int groupId = entity.getReminder().getUserGroupId().getGroupId();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// ContractDropDownValue mailList =
		// contractService.getAllContractDropDownValue(groupId,
		// m.getModuleTypeId(), 1);
		// Set<MyGroupDetails> setemail = mailList.getToList();

		/*
		 * List<Contract_Reviewer> contract_Reviewers = entityManager.
		 * createQuery("select c from Contract_Reviewer c where c.contract.contractId=:contract_id"
		 * ). setParameter("contract_id",
		 * entity.getContractId()).getResultList();
		 */

		// Set<String> toEmailSet = new HashSet<>();
		String toEmail = "";
		/*
		 * if (!CollectionUtils.isEmpty(setemail)) { for (MyGroupDetails
		 * groupDetails : setemail) { toEmail += groupDetails.getEmailId();
		 * toEmailSet.add(groupDetails.getEmailId()); if (setemail.size() >= 1)
		 * toEmail += ","; } }
		 */
		// String toEmail = entity.getToList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// Set<MyGroupDetails> setccEmail = mailList.getCcList();

		String ccEmail = "";
		/*
		 * if(setccEmail!=null){ if (!setccEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setccEmail) {
		 * if(!toEmailSet.contains(groupDetails.getEmailId())){ ccEmail +=
		 * groupDetails.getEmailId(); if (setccEmail.size() >= 1) ccEmail +=
		 * ","; } } } }
		 */
		// String ccEmail = entity.getCcList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// String addCCEmail = entity.getAdditionalCcList().replace("[",
		// "").replace("\"", "").replace("]", "");

		/*
		 * if (addCCEmail.length() > 0) { if (ccEmail.length() > 0) ccEmail +=
		 * ","; ccEmail += addCCEmail;
		 * 
		 * }
		 */

		String userName = "";
		int count = 0;
		List<String> userNameList = new ArrayList<>();

		for (Contract_Reviewer contract_Reviewer : entity.getContractReviewer()) {
			if (!userNameList.contains(contract_Reviewer.getUserId().getUserName())) {
				if (count != 0)
					userName += "/";
				userName += contract_Reviewer.getUserId().getUserName();
				userNameList.add(contract_Reviewer.getUserId().getUserName());
			}
			toEmail += contract_Reviewer.getUserId().getEmailId();
			if (!StringUtils.isEmpty(toEmail))
				toEmail += ",";
			count++;
		}
		User u = userService.getUserById(entity.getReminder().getCreatedById());
		ccEmail += u.getEmailId();

		if (!StringUtils.isEmpty(ccEmail))
			ccEmail += ",";

		SimpleMail365 simpleMail = new SimpleMail365();
		if (!StringUtils.isEmpty(toEmail)) {
			String emailArray[] = getEmailArray(toEmail);
			emailArray = removeMakerEmailId(createdUser, emailArray);
			simpleMail.setTo(emailArray);
		}

		if (!StringUtils.isEmpty(ccEmail))
			simpleMail.setCc(getEmailArray(ccEmail));
		simpleMail.setSubject(entity.getContractTitle() + CONTRACTCREATIONSUB);
		String text = "Hi " + userName + ",<br><br>" + entity.getContractTitle() + " updated by "
				+ createdUser.getUserName() + CONTRACTCREATION + ".<br><br>" + BTEXT + CTEXT;
		simpleMail.setText(text.replace("link", getAppLink(REMINDER365)));
		boolean result = sendMail(simpleMail);
		if (result)
			logger.info("update contract reminder is sent to: " + userName + ", to group: "
					+ entity.getReminder().getUserGroupId().getGroupName() + ", Contrat: " + entity.getContractTitle());

		loggingMailDetails(simpleMail, result);
	}

	@Override
	public void sendDeletedContractMail(Contract entity, User createdUser) {
		Contract contractEntity = contractDAO.getContractById(entity.getContractId());

		int groupId = entity.getReminder().getUserGroupId().getGroupId();
		ModuleType m = moduleTypeDao.getModuleType("Contract");

		// ContractDropDownValue mailList =
		// contractService.getAllContractDropDownValue(groupId,
		// m.getModuleTypeId(), 1);
		// Set<MyGroupDetails> setemail = mailList.getToList();

		@SuppressWarnings("unchecked")
		List<Contract_Reviewer> contract_Reviewers = entity.getContractReviewer();

		String toEmail = "";
		/*
		 * if(setemail != null){ if (!setemail.isEmpty()) { for (MyGroupDetails
		 * groupDetails : setemail) { toEmail += groupDetails.getEmailId(); if
		 * (setemail.size() >= 1) toEmail += ","; } } }
		 */
		// String toEmail = entity.getToList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// Set<MyGroupDetails> setccEmail = mailList.getCcList();

		String ccEmail = "";
		/*
		 * if(setccEmail!=null){ if (!setccEmail.isEmpty()) { for
		 * (MyGroupDetails groupDetails : setccEmail) { ccEmail +=
		 * groupDetails.getEmailId(); if (setccEmail.size() >= 1) ccEmail +=
		 * ","; } } }
		 */
		// String ccEmail = entity.getCcList().replace("[", "").replace("\"",
		// "").replace("]", "");
		// String addCCEmail = contractEntity.getAdditionalCcList().replace("[",
		// "").replace("\"", "").replace("]", "");

		/*
		 * if (addCCEmail.length() > 0) { if (ccEmail.length() > 0) ccEmail +=
		 * ","; ccEmail += addCCEmail; }
		 */

		// createdUser is maker here and he should be cc list

		String userName = "";
		List<String> userNameList = new ArrayList<>();
		int count = 0;
		// Checker is reviewer and he should be in TO list
		for (Contract_Reviewer contract_Reviewer : contract_Reviewers) {
			if (!userNameList.contains(contract_Reviewer.getUserId().getUserName())) {
				if (count != 0)
					userName += "/";
				userName += contract_Reviewer.getUserId().getUserName();
				userNameList.add(contract_Reviewer.getUserId().getUserName());
			}
			toEmail += contract_Reviewer.getUserId().getEmailId();
			if (!StringUtils.isEmpty(toEmail))
				toEmail += ",";
			count++;
		}
		// old code
		// User u =
		// userService.getUserById(entity.getReminder().getCreatedById());
		// #3355, #336
		User u = userService.getUserById(createdUser.getUserId());
		ccEmail += u.getEmailId();
		if (!StringUtils.isEmpty(ccEmail))
			ccEmail += ",";

		SimpleMail365 simpleMail = new SimpleMail365();
		if (!StringUtils.isEmpty(toEmail)) {
			String emailArray[] = getEmailArray(toEmail);
			emailArray = removeMakerEmailId(createdUser, emailArray);
			simpleMail.setTo(emailArray);
		}
		if (!StringUtils.isEmpty(ccEmail))
			simpleMail.setCc(getEmailArray(ccEmail));
		simpleMail.setSubject(entity.getContractTitle() + CONTRACTCREATIONSUB);
		String text = "Hi " + userName + ",<br><br>" + entity.getContractTitle() + " deleted by "
				+ createdUser.getUserName() + CONTRACTCREATION + ".<br><br>" + BTEXT + CTEXT;
		simpleMail.setText(text.replace("link", getAppLink(REMINDER365)));
		boolean result = sendMail(simpleMail);
		if (result)
			logger.info("delete contract reminder is sent to: " + userName + ", to group: "
					+ entity.getReminder().getUserGroupId().getGroupName() + ", Contrat: " + entity.getContractTitle());

		loggingMailDetails(simpleMail, result);
	}

	private String getUserByEmailId(String emailId) {
		String userName = "";
		try {
			userName = (String) entityManager.createNativeQuery("select user_name from user where email_id = :emailId")
					.setParameter("emailId", emailId).getSingleResult();
		} catch (Exception e) {
			logger.error("Email id :- " + emailId + "is not foumd and userName is blank " + e);
			e.printStackTrace();
		}
		return userName;
	}

	private String getAppLink(String text) {
		return appLink + text + "</a>";
	}

}
