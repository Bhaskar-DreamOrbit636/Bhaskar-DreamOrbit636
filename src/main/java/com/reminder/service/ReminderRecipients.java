package com.reminder.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.GroupDAO;
import com.reminder.response.model.ContractDropDownValue;
import com.reminder.response.model.MyGroupDetails;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ReminderRecipients {

	@Autowired
	public GroupDAO groupDao;

	public ContractDropDownValue getRecipients(int groupId, int moduleTypeId, int userId) {
		ContractDropDownValue contractDropDownValue = new ContractDropDownValue();
		List<Object> grouproleUsers = groupDao.getGroupUserDetailByGroupId(groupId, moduleTypeId);
		Set<MyGroupDetails> officerInCharge = null;
		Set<MyGroupDetails> reviewers = null;
		Set<MyGroupDetails> toList = null;
		Set<MyGroupDetails> ccList = null;
		Set<MyGroupDetails> ccLastReminderList = null;
		Set<MyGroupDetails> ccExpiryReminderList = null;
		// Set<MyGroupDetails> allReviewers = new HashSet<>();

		// Set<MyGroupDetails> addccExpiryReminderList = null;
		// Set<MyGroupDetails> addccLastReminderList = null;
		if (grouproleUsers != null) {
			for (Object grouproleUserObj : grouproleUsers) {
				Object[] grouproleUserObjArray = (Object[]) grouproleUserObj;
				MyGroupDetails myGroupDetails = mapToResponse(grouproleUserObjArray);
				List<String> groupRoleActionTypes = groupDao.getActionsByGroupRoleId(myGroupDetails.getGroupRoleId());
				if (groupRoleActionTypes != null) {
					for (String groupRoleActionType : groupRoleActionTypes) {
						if (groupRoleActionType.equalsIgnoreCase("Verify")) {
							if (reviewers == null)
								reviewers = new HashSet<>();
							if (userId != myGroupDetails.getUserId())
								reviewers.add(myGroupDetails);
							// allReviewers.add(myGroupDetails);
						}
						if (groupRoleActionType.equalsIgnoreCase("NotificationTO")) {
							if (toList == null)
								toList = new HashSet<>();
							toList.add(myGroupDetails);
						}
						if (groupRoleActionType.equalsIgnoreCase("NotificationCC")) {
							if (ccList == null)
								ccList = new HashSet<>();
							ccList.add(myGroupDetails);
						}
						if (groupRoleActionType.equalsIgnoreCase("NotificationCC(Last Reminder)")) {
							if (ccLastReminderList == null)
								ccLastReminderList = new HashSet<>();
							ccLastReminderList.add(myGroupDetails);
						}
						if (groupRoleActionType.equalsIgnoreCase("NotificationCC(Expiry Reminder)")) {
							if (ccExpiryReminderList == null)
								ccExpiryReminderList = new HashSet<>();
							ccExpiryReminderList.add(myGroupDetails);
						}
						/*
						 * if (groupRoleActionType.
						 * equalsIgnoreCase("AddtionalCC(Expiry Reminder)")) {
						 * if (addccExpiryReminderList == null)
						 * addccExpiryReminderList = new HashSet<>();
						 * addccExpiryReminderList.add(myGroupDetails); } if
						 * (groupRoleActionType.
						 * equalsIgnoreCase("AddtionalCC(Last Reminder)")) { if
						 * (addccLastReminderList == null) addccLastReminderList
						 * = new HashSet<>();
						 * addccLastReminderList.add(myGroupDetails); }
						 */
					}
				}
				if (officerInCharge == null)
					officerInCharge = new HashSet<>();
				officerInCharge.add(myGroupDetails);
			}
		}
		contractDropDownValue.setOfficerInChargeList(officerInCharge);
		contractDropDownValue.setReviewers(reviewers);
		contractDropDownValue.setToList(toList);
		contractDropDownValue.setCcList(ccList);
		contractDropDownValue.setCcLastReminderList(ccLastReminderList);
		contractDropDownValue.setCcExpiryReminderList(ccExpiryReminderList);
		// contractDropDownValue.setAddccLastReminderList(addccLastReminderList);
		// contractDropDownValue.setAddccExpiryReminderList(addccExpiryReminderList);
		return contractDropDownValue;
	}

	private MyGroupDetails mapToResponse(Object[] grouproleUserObjArray) {
		MyGroupDetails myGroupDetails = new MyGroupDetails();
		if (grouproleUserObjArray[0] != null)
			myGroupDetails.setUserId((int) grouproleUserObjArray[0]);
		if (grouproleUserObjArray[1] != null)
			myGroupDetails.setUserName((String) grouproleUserObjArray[1]);
		if (grouproleUserObjArray[2] != null)
			myGroupDetails.setEmailId((String) grouproleUserObjArray[2]);
		if (grouproleUserObjArray[3] != null)
			myGroupDetails.setUserGroupName((String) grouproleUserObjArray[3]);
		if (grouproleUserObjArray[4] != null)
			myGroupDetails.setRoleName((String) grouproleUserObjArray[4]);
		if (grouproleUserObjArray[5] != null)
			myGroupDetails.setModuleName((String) grouproleUserObjArray[5]);
		if (grouproleUserObjArray[6] != null)
			myGroupDetails.setGroupRoleId((int) grouproleUserObjArray[6]);
		if (grouproleUserObjArray[7] != null)
			myGroupDetails.setGroupUserId((int) grouproleUserObjArray[7]);
		if (grouproleUserObjArray[8] != null)
			myGroupDetails.setAdUserId((String) grouproleUserObjArray[8]);
		return myGroupDetails;
	}
}