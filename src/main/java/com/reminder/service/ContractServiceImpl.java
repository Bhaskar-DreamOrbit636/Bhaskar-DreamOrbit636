package com.reminder.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.reminder.dao.ContractDAO;
import com.reminder.dao.GroupDAO;
import com.reminder.dao.ModuleTypeDao;
import com.reminder.dao.NotificationDAO;
import com.reminder.dao.UserDAO;
import com.reminder.model.Contract;
import com.reminder.model.ContractConfig;
import com.reminder.model.Contract_Has_Status;
import com.reminder.model.Contract_Reviewer;
import com.reminder.model.Contract_Status;
import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.Reminder;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ContractReviewerRequest;
import com.reminder.request.model.ContractSearchCriteria;
import com.reminder.request.model.ReminderRequest;
import com.reminder.response.model.ContractDropDownValue;
import com.reminder.response.model.ContractResponse;
import com.reminder.response.model.ContractReviewer;
import com.reminder.response.model.Contracts;
import com.reminder.response.model.MyContractResponse;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.response.model.ReminderResponse;
import com.reminder.response.model.UserResponse;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ContractServiceImpl implements ContractService {

	Logger logger = Logger.getLogger(ContractServiceImpl.class);
	@Autowired
	private ContractDAO contractDAO;
	@Autowired
	private GroupDAO groupDao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private ReminderService reminderService;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private NotificationDAO notificationDao;
	@Autowired
	private ModuleTypeDao moduleTypeDao;
	
	@Autowired
	ReminderRecipients recipient;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.service.ContractService#createContract(com.reminder.request.
	 * model.ContractRequest, com.reminder.model.User)
	 */
	@Override
	public int createContract(ContractRequest contract, User createdUser) {
		int reminderId=0;
		Contract entity = new Contract();
		try{
		BeanUtils.copyProperties(contract, entity);

		entity.setContractReferenceNumber(contract.getContractReferenceNumber());
		entity.setContractTitle(contract.getContractTitle());
		entity.setDescription(contract.getDescription());
		entity.setBaPoNumber(contract.getBaPoNumber());
		entity.setSupplier(contract.getSupplier());
		entity.setContractValueCurrency(contract.getContractValueCurrency());
		entity.setContractValue(contract.getContractValue());
		entity.setPerformanceBondSubmission(contract.getPerformanceBondSubmission());
		entity.setOptionYear(contract.getOptionYear());

		User user = userDao.getUserById(contract.getOfficerInChargeId());
		entity.setOfficerInChargeId(user);

		entity.setPlpExpiryDate(contract.getPlpExpiryDate());
		entity.setWcpExpiryDate(contract.getWcpExpiryDate());
		entity.setHnmExpiryDate(contract.getHnmExpiryDate());
		entity.setSavingCurrency(contract.getSavingCurrency());
		// entity.setToList(contract.getToList());
		// entity.setCcList(contract.getCcList());
		entity.setAdditionalCcList(contract.getAdditionalCcList());
		entity.setSaving(contract.getSaving());
		Integer versionNo = entity.getVersion() + 1;
		entity.setVersion(versionNo);
		entity.setModuleTypeId(contract.getModuleTypeId());
		entity.setIsDeleted(false);
		entity.setIsVerified(false);
		entity.setParentContractId(entity);

		Contract_Status contractStatus = entityManager.find(Contract_Status.class,
				entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
						.setParameter("status", "New").getResultList().get(0));
		Contract_Has_Status contractHasStatus = new Contract_Has_Status();
		contractHasStatus.setContractStatus(contractStatus);
		List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
		contractHasStatus.setComment("New contract created");
		contractHasStatus.setUserId(createdUser);
		entity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
		contractHasStatuses.add(contractHasStatus);
		entity.setContractHasStatus(contractHasStatuses);
		entity.linkContractHasStatus();

		List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
		List<Contract_Reviewer> contractReviewers = new ArrayList<>();

		for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
			Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
			contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
			contractReviwerEntity.setCreatedBy(createdUser);
			contractReviwerEntity.setLastModifiedBy(createdUser);
			// contractReviwerEntity.setContract(entity);
			contractReviewers.add(contractReviwerEntity);
		}
		entity.setContractReviewer(contractReviewers);
		entity.linkContractReviewer();

		reminderId = contractDAO.createContract(entity, contract, createdUser);
		}catch(Exception e){
			logger.error("error in creatingContractService: " + e);
		}
		try {
			if (contract.getReminder().getActive() && reminderId !=0)
				notificationDao.sendCreateContractEmail(entity, createdUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error in sentCreateContractEmail: " + e);
		}

		return reminderId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.reminder.service.ContractService#getContractById(int)
	 */
	@Override
	public Contract getContractById(int contractNumber) {
		return contractDAO.getContractById(contractNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.service.ContractService#getAllContracts(java.lang.String,
	 * boolean, java.lang.String, java.lang.String, java.lang.Integer,
	 * java.lang.Integer, int)
	 */
	@Override
	public MyContractResponse getAllContracts(String sortBy, boolean isVerified, String referenceNumber, String title,
			Integer offset, Integer numberOfRecords, int userId) {
		List<Contract> contracts = contractDAO.getAllContracts(sortBy, isVerified, referenceNumber, title, offset,
				numberOfRecords, userId);
		MyContractResponse myContractResponse = null;
		if (contracts != null) {
			myContractResponse = setActions(contracts, userId);
		}
		return myContractResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.service.ContractService#updateContract(com.reminder.request.
	 * model.ContractRequest, java.lang.Integer, com.reminder.model.User)
	 */
	@Override
	public Integer updateContract(ContractRequest contract, Integer id, User createdUser) {

		Contract contractEntity = contractDAO.getContractById(id);
		if (contractEntity.isVerified()) {

			if (contract.isReviewerChanged()) {
				contractDAO.deleteContractReview(id);
				List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
				List<Contract_Reviewer> contractReviewers = new ArrayList<>();
				for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
					Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
					User createdBy = entityManager.find(User.class, contractEntity.getReminder().getCreatedById());
					contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
					contractReviwerEntity.setLastModifiedBy(createdUser);
					contractReviwerEntity.setCreatedBy(createdBy);
					contractReviewers.add(contractReviwerEntity);
				}
				contractEntity.setContractReviewer(contractReviewers);
				contractEntity.linkContractReviewer();
				Reminder reminder = contractEntity.getReminder();
				
				mapToReminder(createdUser, reminder, contract);
				contractEntity.setReminder(reminder);
				contractEntity.setAdditionalCcList(contract.getAdditionalCcList());
				contractDAO.updateContract(contractEntity);
				/*
				 * if(contract.getReminder().getActive()){ Contract saved =
				 * contractDAO.getContractById(id);
				 * sendUpdatedContractMail(saved, createdUser); }
				 */
				return reminder.getReminderId();
			}

			Contract entity = new Contract();

			// Groups group = groupDao.getGroupById(contract.getGroupId());
			mapToModel(contract, entity);
			entity.setModuleTypeId(contractEntity.getModuleTypeId());

			User user = userDao.getUserById(contract.getOfficerInChargeId());
			entity.setOfficerInChargeId(user);
			entity.setPlpExpiryDate(contract.getPlpExpiryDate());
			entity.setWcpExpiryDate(contract.getWcpExpiryDate());
			entity.setHnmExpiryDate(contract.getHnmExpiryDate());
			entity.setSavingCurrency(contract.getSavingCurrency());
			// entity.setToList(contract.getToList());
			// entity.setCcList(contract.getCcList());
			entity.setAdditionalCcList(contract.getAdditionalCcList());
			entity.setSaving(contract.getSaving());
			entity.setIsDeleted(false);
			entity.setIsVerified(false);
			entity.setParentContractId(contractEntity.getParentContractId());
			
			/*
			 * if(contract.getReminder()!=null){ Reminder reminder =
			 * reminderService.getReminderById(contractEntity.getReminder().
			 * getReminderId()); reminder.setUserGroupId(group);
			 * reminder.setActive(contract.getReminder().getActive());
			 * reminder.setAddccListExpiryReminder(contractEntity.getReminder().
			 * getAddccListExpiryReminder());
			 * reminder.setAddCcListLastReminder(contractEntity.getReminder().
			 * getAddCcListLastReminder());
			 * reminder.setCcListExpiryReminder(contractEntity.getReminder().
			 * getCcListExpiryReminder());
			 * reminder.setCcListLastReminder(contractEntity.getReminder().
			 * getCcListLastReminder());
			 * reminderService.updateReminder(reminder); }
			 */

			entity.setVersion(contractEntity.getVersion() + 1);
			entity.setModuleTypeId(contract.getModuleTypeId());

			// entity.setReminder(contractEntity.getReminder());

			Contract_Status contractStatus = entityManager.find(Contract_Status.class,
					entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
							.setParameter("status", "Updated").getResultList().get(0));
			Contract_Has_Status contractHasStatus = new Contract_Has_Status();
			contractHasStatus.setContractStatus(contractStatus);
			contractHasStatus.setContract(entity);
			List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
			contractHasStatus.setComment("Approved contract updated");
			contractHasStatus.setUserId(createdUser);
			entity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
			contractHasStatuses.add(contractHasStatus);
			entity.setContractHasStatus(contractHasStatuses);
			entity.linkContractHasStatus();
			
			// deleting the old data and updating the new
			entityManager.createNativeQuery("delete from contract_reviewer where contract_id =:id")
			.setParameter("id", id).executeUpdate();
			List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
			List<Contract_Reviewer> contractReviewers = new ArrayList<>();
			for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
				Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
				// User userEntity = entityManager.find(User.class,
				// contractReviewer.getUserId());
				contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
				contractReviwerEntity.setLastModifiedBy(createdUser);
				contractReviwerEntity.setCreatedBy(createdUser);
				contractReviwerEntity.setContract(entity);
				contractReviewers.add(contractReviwerEntity);
			}
			entity.setContractReviewer(contractReviewers);
			entity.linkContractReviewer();
			logger.info("deleted old contract reviewer data and updating the new for contract = "+contract.getContractTitle());
			int reminderId = contractDAO.createContract(entity, contract, createdUser);
			//if (contract.getReminder().getActive())
			sendUpdatedContractMail(entity, createdUser);
			return reminderId;
		} else {

			Reminder reminder = contractEntity.getReminder();

			mapToModel(contract, contractEntity);
			User user = userDao.getUserById(contract.getOfficerInChargeId());
			contractEntity.setOfficerInChargeId(user);

			contractEntity.setPlpExpiryDate(contract.getPlpExpiryDate());
			contractEntity.setWcpExpiryDate(contract.getWcpExpiryDate());
			contractEntity.setHnmExpiryDate(contract.getHnmExpiryDate());
			contractEntity.setSavingCurrency(contract.getSavingCurrency());
			// contractEntity.setToList(contract.getToList());
			// contractEntity.setCcList(contract.getCcList());
			contractEntity.setAdditionalCcList(contract.getAdditionalCcList());
			contractEntity.setSaving(contract.getSaving());

			mapToReminder(createdUser, reminder, contract);
			contractEntity.setReminder(reminder);

			/*
			 * setReminder(createdUser, reminder, reminderRequest);
			 * contractEntity.setReminder(reminder);
			 */

			String status = "New";

			if (contract.isResubmit()) {
				status = "Updated";
			}

			Contract_Status contractStatus = entityManager.find(Contract_Status.class,
					entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
							.setParameter("status", status).getResultList().get(0));
			Contract_Has_Status contractHasStatus = new Contract_Has_Status();
			contractHasStatus.setContractStatus(contractStatus);
			List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
			contractHasStatus.setComment("Contract updated");
			contractHasStatus.setUserId(createdUser);
			contractEntity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
			contractHasStatuses.add(contractHasStatus);
			contractEntity.setContractHasStatus(contractHasStatuses);
			contractEntity.linkContractHasStatus();
			
			entityManager.createNativeQuery("delete from contract_reviewer where contract_id =:id")
			.setParameter("id", id).executeUpdate();
			List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
			List<Contract_Reviewer> contractReviewers = new ArrayList<>();
			for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
				Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
				contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
				contractReviwerEntity.setLastModifiedBy(createdUser);
				contractReviwerEntity.setCreatedBy(createdUser);
				contractReviewers.add(contractReviwerEntity);
			}
			contractEntity.setContractReviewer(contractReviewers);
			contractEntity.linkContractReviewer();
			logger.info("deleted old contract reviewer data and updating the new for contract = "+contract.getContractTitle());

			contractDAO.updateContract(contractEntity);
			//if (contract.getReminder().getActive())
			sendUpdatedContractMail(contractEntity, createdUser);
			return reminder.getReminderId();
		}
	}

	private void mapToReminder(User createdUser, Reminder reminder, ContractRequest contract) {
		ReminderRequest reminderRequest = contract.getReminder();
		reminder.setEffectiveStartDate(reminderRequest.getEffectiveStartDate());
		reminder.setRemarks(reminderRequest.getRemarks());
		reminder.setFirstReminderDate(reminderRequest.getFirstReminderDate());
		reminder.setSecondReminderDate(reminderRequest.getSecondReminderDate());
		reminder.setThirdReminderDate(reminderRequest.getThirdReminderDate());
		reminder.setActive(reminderRequest.getActive());
		reminder.setCreatedById(createdUser.getUserId());
		reminder.setLastModifiedById(createdUser.getUserId());
		if (reminder != null && (contract.isReviewerChanged() || 
				(contract.getReminder().getEffectiveExpiryDate()).compareTo(reminder.getEffectiveExpiryDate())==0)) {
			reminder.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
			reminder.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
			reminder.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
		}
		if(reminder != null &&
				((contract.getReminder().getEffectiveExpiryDate()).compareTo(reminder.getEffectiveExpiryDate())!=0)){
			reminder.setFirstReminderSentAt(null);
			reminder.setSecondReminderSentAt(null);
			reminder.setThirdReminderSentAt(null);
		}
		reminder.setEffectiveExpiryDate(reminderRequest.getEffectiveExpiryDate());
		/// reminder.setStatusId(reminderRequest.getStatusId());
		//reminder.setActive(reminderRequest.getActive());
		reminder.setAddCcListExpiryReminder(reminderRequest.getAddCcListExpiryReminder());
		reminder.setAddCcListLastReminder(reminderRequest.getAddCcListLastReminder());
		// reminder.setCcListExpiryReminder(reminderRequest.getCcListExpiryReminder());
		// reminder.setCcListLastReminder(reminderRequest.getCcListLastReminder());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.service.ContractService#verifyContract(com.reminder.request.
	 * model.ContractRequest, java.lang.Integer, com.reminder.model.User)
	 */
	@Override
	@Transactional
	public boolean verifyContract(ContractRequest contract, Integer id, User createdUser) {
		boolean status = true;
		Contract contractEntity = contractDAO.getContractById(id);
		String contractStatusForMail = contractEntity.getContractStatus();

		if (!contractStatusForMail.equalsIgnoreCase("Verified")) {

			mapToModel(contract, contractEntity);

			User user = userDao.getUserById(contract.getOfficerInChargeId());
			contractEntity.setOfficerInChargeId(user);
			contractEntity.setPlpExpiryDate(contract.getPlpExpiryDate());
			contractEntity.setWcpExpiryDate(contract.getWcpExpiryDate());
			contractEntity.setHnmExpiryDate(contract.getHnmExpiryDate());
			contractEntity.setSavingCurrency(contract.getSavingCurrency());
			// contractEntity.setToList(contract.getToList());
			// contractEntity.setCcList(contract.getCcList());
			contractEntity.setAdditionalCcList(contract.getAdditionalCcList());
			contractEntity.setSaving(contract.getSaving());
			contractEntity.setIsVerified(true);

			Reminder reminderEntity = contractEntity.getReminder();
			reminderEntity.setLastModifiedById(createdUser.getUserId());

			contractEntity.setReminder(reminderEntity);

			Contract_Status contractStatus = entityManager.find(Contract_Status.class,
					entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
							.setParameter("status", "Verified").getResultList().get(0));
			Contract_Has_Status contractHasStatus = new Contract_Has_Status();
			contractHasStatus.setContractStatus(contractStatus);
			List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
			contractHasStatus.setComment(contract.getContractHasStatus().getComments());
			contractHasStatus.setUserId(createdUser);
			contractEntity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
			contractHasStatuses.add(contractHasStatus);
			contractEntity.setContractHasStatus(contractHasStatuses);
			contractEntity.linkContractHasStatus();

			List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
			List<Contract_Reviewer> contractReviewers = new ArrayList<>();
			for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
				Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
				contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
				contractReviwerEntity.setLastModifiedBy(createdUser);
				contractReviwerEntity
						.setCreatedBy(entityManager.find(User.class, contractEntity.getReminder().getCreatedById()));
				contractReviewers.add(contractReviwerEntity);
			}
			contractEntity.setContractReviewer(contractReviewers);
			contractEntity.linkContractReviewer();

			contractDAO.updateContract(contractEntity);

			Contract contractSaved = contractDAO.getContractById(id);

			try {
				notificationDao.sendVerifiedContractEmail(contractSaved, createdUser, contractStatusForMail);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error in sendVerifiedContractEmail: " + e);
			}

		} else {
			status = false;
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.reminder.service.ContractService#rejectContract(com.reminder.request.
	 * model.ContractRequest, java.lang.Integer, com.reminder.model.User)
	 */
	@Override
	public boolean rejectContract(ContractRequest contract, Integer id, User createdUser) {
		boolean status = true;
		Contract contractEntity = contractDAO.getContractById(id);
		String contractStatusForMail = contractEntity.getContractStatus();

		if (!contractStatusForMail.equalsIgnoreCase("Verified")) {

			mapToModel(contract, contractEntity);

			ReminderRequest reminderRequest = contract.getReminder();
			Reminder reminder = contractEntity.getReminder();
			reminder.setRemarks(reminderRequest.getRemarks());
			contractEntity.setReminder(reminder);

			User user = userDao.getUserById(contract.getOfficerInChargeId());
			contractEntity.setOfficerInChargeId(user);
			contractEntity.setPlpExpiryDate(contract.getPlpExpiryDate());
			contractEntity.setWcpExpiryDate(contract.getWcpExpiryDate());
			contractEntity.setHnmExpiryDate(contract.getHnmExpiryDate());
			contractEntity.setSavingCurrency(contract.getSavingCurrency());
			// contractEntity.setToList(contract.getToList());
			// contractEntity.setCcList(contract.getCcList());
			contractEntity.setAdditionalCcList(contractEntity.getAdditionalCcList());
			contractEntity.setSaving(contract.getSaving());
			contractEntity.setIsVerified(false);
			Reminder reminderEntity = contractEntity.getReminder();
			reminderEntity.setLastModifiedById(createdUser.getUserId());

			contractEntity.setReminder(reminderEntity);
			String lastStatus = (String) entityManager
					.createNativeQuery("select c.status from contract_has_status ch join contract_status c "
							+ "on ch.contract_status_id=c.Contract_Status_Id where ch.contract_id=:contractId order by ch.contract_has_status_id desc limit 1")
					.setParameter("contractId", id).getSingleResult();
			String contractLastStatus = "";
			if ("New".equalsIgnoreCase(lastStatus))
				contractLastStatus = "Create Rejected";
			if ("Updated".equalsIgnoreCase(lastStatus))
				contractLastStatus = "Update Rejected";
			if ("Deleted".equalsIgnoreCase(lastStatus))
				contractLastStatus = "Delete Rejected";

			Contract_Status contractStatus = entityManager.find(Contract_Status.class,
					entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
							.setParameter("status", contractLastStatus).getResultList().get(0));
			Contract_Has_Status contractHasStatus = new Contract_Has_Status();
			contractHasStatus.setContractStatus(contractStatus);
			List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
			contractHasStatus.setComment(contract.getContractHasStatus().getComments());
			contractHasStatus.setUserId(createdUser);
			contractEntity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
			contractHasStatuses.add(contractHasStatus);
			contractEntity.setContractHasStatus(contractHasStatuses);
			contractEntity.linkContractHasStatus();

			List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
			List<Contract_Reviewer> contractReviewers = new ArrayList<>();
			for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
				Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
				contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
				contractReviwerEntity.setLastModifiedBy(createdUser);
				contractReviwerEntity
						.setCreatedBy(entityManager.find(User.class, contractEntity.getReminder().getCreatedById()));
				contractReviewers.add(contractReviwerEntity);
			}
			contractEntity.setContractReviewer(contractReviewers);
			contractEntity.linkContractReviewer();

			contractDAO.updateContract(contractEntity);

			Contract contractSaved = contractDAO.getContractById(id);

			try {
				notificationDao.sendRejectedContractEmail(contractSaved, createdUser, contractStatusForMail);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error in sendRejectedContractEmail: " + e);
			}
		} else {
			status = false;
		}

		return status;
	}

	/**
	 * @param contract
	 * @param contractEntity
	 */
	private void mapToModel(ContractRequest contract, Contract contractEntity) {
		contractEntity.setContractReferenceNumber(contract.getContractReferenceNumber());
		contractEntity.setContractTitle(contract.getContractTitle());
		contractEntity.setDescription(contract.getDescription());
		contractEntity.setBaPoNumber(contract.getBaPoNumber());
		contractEntity.setSupplier(contract.getSupplier());
		contractEntity.setContractValueCurrency(contract.getContractValueCurrency());
		contractEntity.setContractValue(contract.getContractValue());
		contractEntity.setPerformanceBondSubmission(contract.getPerformanceBondSubmission());
		contractEntity.setOptionYear(contract.getOptionYear());
		/*
		 * contractEntity.setAddccListExpiryReminder(contract.
		 * getAddccListExpiryReminder());
		 * contractEntity.setAddCcListLastReminder(contract.
		 * getAddCcListLastReminder());
		 * contractEntity.setCcListExpiryReminder(contract.
		 * getCcListExpiryReminder());
		 * contractEntity.setCcListLastReminder(contract.getCcListLastReminder()
		 * );
		 */

	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean deleteContract(int contractNumber, User createdUser) {
		Contract contractEntity = contractDAO.getContractById(contractNumber);

		List<Integer> userIds = entityManager
				.createNativeQuery(
						"select user_id from contract_reviewer where contract_id=:contractNumber and user_id!=:userId")
				.setParameter("userId", createdUser.getUserId()).setParameter("contractNumber", contractNumber)
				.getResultList();
		if (CollectionUtils.isEmpty(userIds))
			return true;

		/*
		 * for (Integer id : userIds) { //actions =
		 * groupDao.getGroupRolesAction(id, groupId); List<String> strings =
		 * (List<String>)entityManager.
		 * createNativeQuery("SELECT name FROM action_type where " +
		 * "action_type_id in(SELECT action_type_id FROM  group_role_has_action_type "
		 * +
		 * "where group_role_id in(SELECT group_role_id FROM  group_user where user_id=:userId  and group_Id=:groupId))"
		 * ) .setParameter("userId", id) .setParameter("groupId",
		 * contractEntity.getReminder().getUserGroupId().getGroupId())
		 * .getResultList(); }
		 */
		/*
		 * Long count = 0L; for (Integer id : userIds) { //actions =
		 * groupDao.getGroupRolesAction(id, groupId); count = (Long)
		 * entityManager.
		 * createNativeQuery("SELECT count(a.action_type_id) FROM action_type a where a.name='delete' and "
		 * +
		 * "a.action_type_id in(SELECT action_type_id FROM  group_role_has_action_type "
		 * +
		 * "where group_role_id in(SELECT group_role_id FROM  group_user where user_id in (:userId ) and group_Id=:groupId))"
		 * ) .setParameter("userId", id) .setParameter("groupId",
		 * contractEntity.getReminder().getUserGroupId().getGroupId())
		 * .getSingleResult(); } if(count == 0){ return
		 * "Reviewers does not have "; }
		 */

		Contract_Status contractStatus = entityManager.find(Contract_Status.class,
				entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
						.setParameter("status", "Deleted").getResultList().get(0));
		Contract_Has_Status contractHasStatus = new Contract_Has_Status();
		contractHasStatus.setContractStatus(contractStatus);
		List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
		contractHasStatus.setComment("Contract Deleted");
		contractHasStatus.setUserId(createdUser);
		contractEntity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
		contractHasStatuses.add(contractHasStatus);
		contractEntity.setContractHasStatus(contractHasStatuses);
		contractEntity.linkContractHasStatus();
		contractEntity.setIsVerified(false);
		contractEntity.setDeleted(true);

		Reminder reminderEntity = contractEntity.getReminder();

		reminderEntity.setLastModifiedById(createdUser.getUserId());

		contractEntity.setReminder(reminderEntity);

		contractDAO.updateContract(contractEntity);

		notificationDao.sendDeletedContractMail(contractEntity, createdUser);

		return false;
	}

	@Override
	public void createContractHasStatus(Contract_Has_Status contractHasStatus) {
		contractDAO.createContractHasStatus(contractHasStatus);
	}

	@Override
	public List<ContractResponse> getSingleContract(int contractId) {
		List<ContractResponse> contractResponseList = null;
		try {
			List<Contract> contractsList = contractDAO.getSingleContract(contractId);
			if (contractsList != null) {
				contractResponseList = setContractResponse(contractsList);
			}
		} catch (Exception e) {
			logger.error("Error getting contract " + contractId);
		}
		return contractResponseList;
	}

	private List<ContractResponse> setContractResponse(List<Contract> contracts) {
		List<ContractResponse> contractResponseList = null;
		for (Object contractUserObj : contracts) {
			Object[] contractUserObjArray = (Object[]) contractUserObj;
			Contract contract = (Contract) contractUserObjArray[0];
			User officerInCharge = (User) contractUserObjArray[1];
			ContractResponse contractResponse = null;
			// List<String> actions = null;
			int groupId = 0;
			if (contract.getReminder() != null && contract.getReminder().getUserGroupId() != null) {
				groupId = contract.getReminder().getUserGroupId().getGroupId();
			}
			contractResponse = convertToContractResponse(contract);
			contractResponse.setGroupId(groupId);
			contractResponse.setOfficerInChargeId(officerInCharge);
			User userEntity = getCreatedUser(contract);
			contractResponse.setCreatedUser(userEntity.getAdUserId());

			List<ContractReviewer> contractReviewList = new ArrayList<>();

			Groups groups = entityManager.find(Groups.class, groupId);
			Set<GrouproleUser> usersSet = groups.getGroupUser();

			Set<Integer> userIds = new HashSet<>();
			for (GrouproleUser grouproleUser : usersSet) {
				userIds.add(grouproleUser.getUser().getUserId());
			}

			/*
			 * if (!userIds.contains(contractResponse.getOfficerInChargeId())) {
			 * Contract c =
			 * entityManager.find(Contract.class,contract.getContractId());
			 * c.setOfficerInChargeId(null); contractDAO.updateContract(c);
			 * contractResponse.setOfficerInChargeId(null); }
			 */

			for (Contract_Reviewer contract_review : contract.getContractReviewer()) {
				ContractReviewer dto = new ContractReviewer();
				dto.setContractReviewerId(contract_review.getContractReviewerId());
				UserResponse userResponse = userResponse(contract_review);
				dto.setUserId(userResponse);
				if (userIds.contains(userResponse.getUserId())) {
					contractReviewList.add(dto);
				} else {
					deleteContractReviewer(contract_review);
				}
			}
			List<String> contractStatusList = new ArrayList<>();
			List<String> contractStatusUserList = new ArrayList<>();
			if (contract.getParentContractId().getContractId() != contract.getContractId()) {
				Contract parentContract = contractDAO.getContractById(contract.getParentContractId().getContractId());

				for (Contract_Has_Status status : parentContract.getContractHasStatus()) {
					contractStatusList.add(status.getContractStatus().getStatus());
					contractStatusUserList.add(status.getUserId().getAdUserId());
				}
			}
			for (Contract_Has_Status status : contract.getContractHasStatus()) {
				contractStatusList.add(status.getContractStatus().getStatus());
				if (status.getUserId() != null)
					contractStatusUserList.add(status.getUserId().getAdUserId());
				contractResponse.setRejectedRemarks(status.getComment());
			}

			// contractResponse.setContractHasStatus(contract.getContractHasStatus());
			contractResponse.setContractStatusList(contractStatusList);
			contractResponse.setContractReviewer(contractReviewList);
			contractResponse.setUserModifiedContract(contractStatusUserList);
			if (contractResponseList == null)
				contractResponseList = new ArrayList<>();
			if (contractResponse != null)
				contractResponseList.add(contractResponse);
		}
		return contractResponseList;
	}

	private void deleteContractReviewer(Contract_Reviewer contract_review) {

		try {
			String sql2 = "DELETE FROM contract_reviewer where contract_reviewer_id =:ids";
			entityManager.createNativeQuery(sql2).setParameter("ids", contract_review.getContractReviewerId())
					.executeUpdate();
		} catch (Exception e) {
			logger.error(
					"Error in deleteContractReviewer contract_reviewer ::" + contract_review.getContractReviewerId());
		}
	}

	private UserResponse userResponse(Contract_Reviewer contract_review) {
		UserResponse userResponse = new UserResponse();
		if(contract_review.getUserId()!=null){
		userResponse.setUserName(contract_review.getUserId().getUserName());
		userResponse.setUserId(contract_review.getUserId().getUserId());
		userResponse.setAdUserId(contract_review.getUserId().getAdUserId());
		}
		return userResponse;
	}

	private User getCreatedUser(Contract contract) {
		return entityManager.find(User.class, contract.getReminder().getCreatedById());
	}

	@Override
	public Contract_Has_Status contractHasStatus(Contract_Has_Status contractHasStatus) {
		contractDAO.createContractHasStatus(contractHasStatus);
		return contractHasStatus;
	}

	@Override
	public ContractDropDownValue getAllContractDropDownValue(int groupId, int moduleTypeId, int userId) {
		return recipient.getRecipients(groupId, moduleTypeId, userId);
	}

	/**
	 * Getting Reviewer List #321
	 * 
	 * @param groupId
	 * @param moduleTypeId
	 * @param userId
	 * @return
	 */
	public ContractDropDownValue getReviewerDropDownValue(Set<Integer> groupId, int moduleTypeId, int userId) {
		ContractDropDownValue contractDropDownValue = new ContractDropDownValue();
		List<Object> grouproleUsers = groupDao.getGroupUserDetailByGroupId(groupId, moduleTypeId);
		Set<MyGroupDetails> reviewers = null;
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
						}
					}
				}
			}
		}
		contractDropDownValue.setReviewers(reviewers);
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

	public int getContractCount(String sortBy, boolean isVerified, String referenceNumber, String title, int userId) {
		return contractDAO.getContractCount(sortBy, isVerified, referenceNumber, title, userId);
	}

	@Override
	public void deleteContractPerm(Integer contractId, boolean deleteParent, User createdUser) {
		Contract contract = contractDAO.getContractById(contractId);
		String contractStatus = contract.getContractStatus();
		if (!contract.getContractStatus().contains("Rejected"))
			notificationDao.sendVerifiedContractEmail(contract, createdUser, contractStatus);
		contractDAO.deleteContractPerm(contractId, deleteParent);
	}

	@Override
	public MyContractResponse getContractWithActions(int userId, String sort_by, String order, boolean isVerified,
			Integer limit, Integer page_no, ContractSearchCriteria contractSearchCriteria, String searchCriteria) {
		Contracts contracts = contractDAO.getAllContracts(userId, sort_by, order, isVerified, limit, page_no,
				contractSearchCriteria, searchCriteria);

		MyContractResponse myContractResponse = null;
		if (contracts != null && contracts.getContracts() != null) {
			myContractResponse = setActions(contracts.getContracts(), userId);
			myContractResponse.setCount(contracts.getCount());
		}
		return myContractResponse;
	}

	private ContractResponse convertToContractResponse(Contract contract) {
		ContractResponse contractResponse = new ContractResponse();
		contractResponse.setContractId(contract.getContractId());
		contractResponse.setContractReferenceNumber(contract.getContractReferenceNumber());
		contractResponse.setContractTitle(contract.getContractTitle());
		contractResponse.setDescription(contract.getDescription());
		contractResponse.setBaPoNumber(contract.getBaPoNumber());
		contractResponse.setSupplier(contract.getSupplier());
		contractResponse.setContractValueCurrency(contract.getContractValueCurrency());
		contractResponse.setContractValue(contract.getContractValue());
		contractResponse.setPerformanceBondSubmission(contract.getPerformanceBondSubmission());
		contractResponse.setOptionYear(contract.getOptionYear());
		contractResponse.setPlpExpiryDate(contract.getPlpExpiryDate());
		contractResponse.setWcpExpiryDate(contract.getWcpExpiryDate());
		contractResponse.setHnmExpiryDate(contract.getHnmExpiryDate());
		contractResponse.setSavingCurrency(contract.getSavingCurrency());
		// contractResponse.setToList(contract.getToList());
		// contractResponse.setCcList(contract.getCcList());
		contractResponse.setAdditionalCcList(contract.getAdditionalCcList());
		contractResponse.setSaving(contract.getSaving());
		contractResponse.setModuleTypeId(contract.getModuleTypeId());
		contractResponse.setIsDeleted(contract.getIsDeleted());
		contractResponse.setIsVerified(contract.getIsVerified());

		List<ContractReviewer> contractReviewList = new ArrayList<>();

		Groups groups = entityManager.find(Groups.class, contract.getReminder().getUserGroupId().getGroupId());
		Set<GrouproleUser> usersSet = groups.getGroupUser();

		Set<Integer> userIds = new HashSet<>();
		for (GrouproleUser grouproleUser : usersSet) {
			userIds.add(grouproleUser.getUser().getUserId());
		}

		for (Contract_Reviewer contract_review : contract.getContractReviewer()) {
			ContractReviewer dto = new ContractReviewer();
			dto.setContractReviewerId(contract_review.getContractReviewerId());
			UserResponse userResponse = userResponse(contract_review);
			dto.setUserId(userResponse);
			if (userIds.contains(userResponse.getUserId())) {
				contractReviewList.add(dto);
			} else {
				deleteContractReviewer(contract_review);
			}
		}
		contractResponse.setContractReviewer(contractReviewList);
		// BeanUtils.copyProperties(contract, contractResponse);
		// if(contract.getParentContractId().getContractId()!=
		// contract.getContractId()){
		BigInteger count = (BigInteger) entityManager
				.createNativeQuery(
						"SELECT count(c.contract_id) FROM contract c where c.parent_contract_id=:contractId and c.is_verified=0")
				.setParameter("contractId", contract.getParentContractId().getContractId()).getSingleResult();
		// }
		// True means not allowed to update
		// count greater than 0 means not allowed to update
		if (count != null && count.intValue() != 0) {
			contractResponse.setUpdated(Boolean.TRUE);
		}

		Reminder reminder = contract.getReminder();
		ReminderResponse reminderResponse = new ReminderResponse();

		BeanUtils.copyProperties(reminder, reminderResponse);

		reminderResponse.setAddCcListExpiryReminder(reminder.getAddCcListExpiryReminder());

		User userEntity = entityManager.find(User.class, reminder.getCreatedById());
		User lastModifiedByUser = entityManager.find(User.class, reminder.getLastModifiedById());
		if (userEntity != null) {
			contractResponse.setCreatedUser(userEntity.getAdUserId());
			reminderResponse.setCreatedByUser(userEntity.getAdUserId());
		}
		if (lastModifiedByUser != null) {
			reminderResponse.setLastModifiedByUser(lastModifiedByUser.getAdUserId());
		}
		contractResponse.setReminder(reminderResponse);
		// contractResponse.setActions(actions);
		contractResponse.setContractStatus(contract.getContractStatus());
		contractResponse.setParentContractId(contract.getParentContractId().getContractId());
		return contractResponse;
	}

	private MyContractResponse setActions(List<Contract> contracts, int userId) {
		List<ContractResponse> contractResponseList = null;
		Map<Integer, List<String>> map = new HashMap<>();
		Set<Integer> setOfGroupId = new HashSet<>();

		for (Object contractUserObj : contracts) {
			Object[] contractUserObjArray = (Object[]) contractUserObj;
			Contract contract = (Contract) contractUserObjArray[0];
			User officerInCharge = (User) contractUserObjArray[1];
			ContractResponse contractResponse = null;
			setOfGroupId.add(contract.getReminder().getUserGroupId().getGroupId());
			List<String> roleActions = null;
			int groupId = 0;
			String groupName = "";
			if (contract.getReminder() != null && contract.getReminder().getUserGroupId() != null) {
				setOfGroupId.add(contract.getReminder().getUserGroupId().getGroupId());
				groupId = contract.getReminder().getUserGroupId().getGroupId();
				groupName = contract.getReminder().getUserGroupId().getGroupName();

				/*
				 * if (map.get(groupId) == null) { roleActions =
				 * groupDao.getGroupRolesAction(userId, groupId);
				 * map.put(groupId, roleActions); contractResponse =
				 * convertToContractResponse(contract, roleActions); } else {
				 * contractResponse = convertToContractResponse(contract,
				 * map.get(groupId)); }
				 */
				contractResponse = convertToContractResponse(contract);
			}
			contractResponse.setGroupId(groupId);
			contractResponse.setGroupName(contract.getReminder().getUserGroupId().getGroupName());
			contractResponse.setOfficerInChargeId(officerInCharge);
			if (contract.getReminder().getCreatedById() != 0) {
				User userEntity = getCreatedUser(contract);
				contractResponse.setCreatedUser(userEntity.getAdUserId());
			}
			if (contractResponseList == null)
				contractResponseList = new ArrayList<>();
			if (contractResponse != null) {
				contractResponseList.add(contractResponse);
			}
		}

		if (!setOfGroupId.isEmpty()) {
			ModuleType m = moduleTypeDao.getModuleType("Contract");
			ContractDropDownValue reviewerList = getReviewerDropDownValue(setOfGroupId, m.getModuleTypeId(), userId);

			List<Object[]> roleActions = groupDao.getGroupRolesAction(userId, setOfGroupId);
			List<String> actionName = null;
			for (Object[] obj : roleActions) {
				if (!map.containsKey((Integer) obj[1])) {
					actionName = new ArrayList<>();
					actionName.add((String) obj[0]);
					map.put((Integer) obj[1], actionName);
				} else {
					actionName = map.get((Integer) obj[1]);
					actionName.add((String) obj[0]);
					map.put((Integer) obj[1], actionName);
				}
			}
			for (ContractResponse cr : contractResponseList) {
				cr.setActions(map.get(cr.getGroupId()));
			}
		}

		MyContractResponse myContractResponse = new MyContractResponse();
		myContractResponse.setContact(contractResponseList);
		return myContractResponse;
	}

	private MyContractResponse setActionsToResponse(List<Contract> contracts, int userId) {
		List<ContractResponse> contractResponseList = null;
		MyContractResponse myContractResponse = null;
		Map<Integer, List<String>> map = new HashMap<>();
		Set<Integer> setOfGroupId = new HashSet<>();

		for (Contract contract : contracts) {
			User officerInCharge = contract.getOfficerInChargeId();
			ContractResponse contractResponse = null;
			// List<String> actions = null;
			int groupId = 0;
			String groupName = "";
			if (contract.getReminder() != null && contract.getReminder().getUserGroupId() != null) {
				setOfGroupId.add(contract.getReminder().getUserGroupId().getGroupId());
				groupId = contract.getReminder().getUserGroupId().getGroupId();
				groupName = contract.getReminder().getUserGroupId().getGroupName();

				/*
				 * if (map.get(groupId) == null) { actions =
				 * groupDao.getGroupRolesAction(userId, groupId);
				 * map.put(groupId, actions); contractResponse =
				 * convertToContractResponse(contract, actions); } else {
				 * contractResponse = convertToContractResponse(contract,
				 * map.get(groupId)); }
				 */
				contractResponse = convertToContractResponse(contract);
			}
			contractResponse.setGroupId(groupId);
			contractResponse.setOfficerInChargeId(officerInCharge);
			contractResponse.setGroupName(groupName);
			if (contract.getReminder().getCreatedById() != 0) {
				User userEntity = getCreatedUser(contract);
				contractResponse.setCreatedUser(userEntity.getAdUserId());
			}

			if (contractResponseList == null)
				contractResponseList = new ArrayList<>();
			if (contractResponse != null) {
				contractResponseList.add(contractResponse);
			}
		}
		if (!setOfGroupId.isEmpty()) {
			List<Object[]> roleActions = groupDao.getGroupRolesAction(userId, setOfGroupId);

			List<String> actionName = null;
			for (Object[] obj : roleActions) {
				if (!map.containsKey((Integer) obj[1])) {
					actionName = new ArrayList<>();
					actionName.add((String) obj[0]);
					map.put((Integer) obj[1], actionName);
				} else {
					actionName = map.get((Integer) obj[1]);
					actionName.add((String) obj[0]);
					map.put((Integer) obj[1], actionName);
				}
			}
			for (ContractResponse cr : contractResponseList) {
				cr.setActions(map.get(cr.getGroupId()));
			}
		}
		if (contractResponseList != null) {
			myContractResponse = new MyContractResponse();
			myContractResponse.setContact(contractResponseList);
		}
		return myContractResponse;
	}

	@Override
	public void createContractConfig(ContractConfig config) {
		contractDAO.createContractConfig(config);

	}

	@Override
	public List<ContractConfig> getContractConfig() {

		return contractDAO.getContractConfig();
	}

	@Override
	public List<Summary> getContractExpireCalendar(int userId, Date Date) {
		return contractDAO.getContractExpireCalendar(userId, Date);
	}

	@Override
	public MyContractResponse getContractReviewData(String sortBy, String order, boolean isVerified, Integer limit,
			Integer page_no, String searchCriteria, int userId) {
		Contracts contracts = contractDAO.getContractReviewData(sortBy, order, isVerified, limit, page_no,
				searchCriteria, userId);
		if (contracts == null || CollectionUtils.isEmpty(contracts.getContracts()))
			return null;
		MyContractResponse myContractResponse = setActionsToResponse(contracts.getContracts(), userId);
		myContractResponse.setCount(contracts.getCount());
		return myContractResponse;
	}

	@Override
	public int deleteContractReminder(ContractRequest contract, User createdUser) {

		// Contract contractEntity = contractDAO.getContractById(id);

		Contract entity = new Contract();
		mapToModel(contract, entity);

		User user = userDao.getUserById(contract.getOfficerInChargeId());
		entity.setOfficerInChargeId(user);

		entity.setPlpExpiryDate(contract.getPlpExpiryDate());
		entity.setWcpExpiryDate(contract.getWcpExpiryDate());
		entity.setHnmExpiryDate(contract.getHnmExpiryDate());
		entity.setSavingCurrency(contract.getSavingCurrency());
		// entity.setToList(contract.getToList());
		// entity.setCcList(contract.getCcList());
		entity.setAdditionalCcList(contract.getAdditionalCcList());
		entity.setSaving(contract.getSaving());
		Integer versionNo = entity.getVersion() + 1;
		entity.setVersion(versionNo);
		entity.setModuleTypeId(contract.getModuleTypeId());
		entity.setIsDeleted(false);
		entity.setIsVerified(false);
		entity.setParentContractId(entity);

		Contract_Status contractStatus = entityManager.find(Contract_Status.class,
				entityManager.createQuery("select c.contractStatusId from Contract_Status c where Status= :status")
						.setParameter("status", "Deleted").getResultList().get(0));
		Contract_Has_Status contractHasStatus = new Contract_Has_Status();
		contractHasStatus.setContractStatus(contractStatus);
		List<Contract_Has_Status> contractHasStatuses = new ArrayList<>();
		contractHasStatus.setComment("contract deleted");
		contractHasStatus.setUserId(createdUser);
		entity.setContractStatus(contractHasStatus.getContractStatus().getStatus());
		contractHasStatuses.add(contractHasStatus);
		entity.setContractHasStatus(contractHasStatuses);
		entity.linkContractHasStatus();

		List<ContractReviewerRequest> contractReviewerEntity = contract.getContractReviewer();
		List<Contract_Reviewer> contractReviewers = new ArrayList<>();

		for (ContractReviewerRequest contractReviewer : contractReviewerEntity) {
			Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
			contractReviwerEntity.setUserId(entityManager.find(User.class, contractReviewer.getUserId()));
			contractReviwerEntity.setCreatedBy(createdUser);
			contractReviwerEntity.setLastModifiedBy(createdUser);
			contractReviewers.add(contractReviwerEntity);
		}
		entity.setContractReviewer(contractReviewers);
		entity.linkContractReviewer();
		return contractDAO.createContract(entity, contract, createdUser);

	}

	@Override
	public void revertContract(Integer contractId) {
		Contract contractEntity = contractDAO.getContractById(contractId);
		contractEntity.setIsDeleted(Boolean.FALSE);
		contractEntity.setVerified(Boolean.TRUE);
		contractEntity.setContractStatus("Verified");
		contractDAO.updateContract(contractEntity);
	}

	public void sendUpdatedContractMail(Contract entity, User createdUser) {
		notificationDao.sendUpdatedContractMail(entity, createdUser);
	}

	public void sendDeletedContractMail(Contract entity, User createdUser) {
		notificationDao.sendDeletedContractMail(entity, createdUser);

	}

	@Override
	public List<Contract> contractAdvanceSearch(String search, String column) {

		return contractDAO.contractAdvanceSearch(search, column);

	}

	@Override
	public List<MyContractResponse> searchByOfficerInCharge(String name) {
		return contractDAO.searchByOfficerInCharge(name);
	}

}