package com.reminder.dao;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.model.Contract;
import com.reminder.model.ContractConfig;
import com.reminder.model.Contract_Has_Status;
import com.reminder.model.Contract_Reviewer;
import com.reminder.model.Contract_Status;
import com.reminder.model.Groups;
import com.reminder.model.Reminder;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ContractSearchCriteria;
import com.reminder.request.model.ReminderRequest;
import com.reminder.response.model.Contracts;
import com.reminder.response.model.MyContractResponse;
import com.reminder.utils.DateTimeUtil;

@Repository
public class ContractDAOImpl implements ContractDAO {

	private Logger logger = Logger.getLogger(ContractDAOImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	FileDAO fileDao;

	@Override
	public int createContract(Contract contract , ContractRequest contractRequest,User createdUser) {
		try {
			logger.info("inside the ContractDAO.createContract() method");
			Groups groups = entityManager.find(Groups.class,contractRequest.getGroupId());
		
			
			Contract contractEntity  = null ;
			Reminder reminder = null;
			Reminder entity = new Reminder();
			if (contractRequest != null && contractRequest.getContractId() != 0) {
				contractEntity = getContractById(contractRequest.getContractId());
			    reminder = contractEntity.getReminder();
			    entity.setCreatedAt(reminder.getCreatedAt());
			    entity.setLastModifiedById(createdUser.getUserId());
			    entity.setLastModifiedAt(DateTime.now());
			}

			ReminderRequest reminderRequest = contractRequest.getReminder();
		   	
	    	entity.setUserGroupId(groups);
	    	entity.setEffectiveStartDate(reminderRequest.getEffectiveStartDate());
	    	entity.setRemarks(reminderRequest.getRemarks());
	    	entity.setFirstReminderDate(reminderRequest.getFirstReminderDate());
	    	entity.setSecondReminderDate(reminderRequest.getSecondReminderDate());
	    	entity.setThirdReminderDate(reminderRequest.getThirdReminderDate());
	    	entity.setActive(reminderRequest.getActive());
	    	entity.setCreatedById(createdUser.getUserId());
	    	//entity.setLastModifiedById(createdUser.getUserId());
			if (reminder != null && ((contractRequest.getReminder().getEffectiveExpiryDate()).compareTo(reminder.getEffectiveExpiryDate())!=0)) {
				entity.setFirstReminderSentAt(null);
				entity.setSecondReminderSentAt(null);
				entity.setThirdReminderSentAt(null);
			}
			if (reminder != null && ((contractRequest.getReminder().getEffectiveExpiryDate()).compareTo(reminder.getEffectiveExpiryDate())==0)) {
				entity.setFirstReminderSentAt(reminder.getFirstReminderSentAt());
				entity.setSecondReminderSentAt(reminder.getSecondReminderSentAt());
				entity.setThirdReminderSentAt(reminder.getThirdReminderSentAt());
			}
			entity.setEffectiveExpiryDate(reminderRequest.getEffectiveExpiryDate());
	    	//entity.setStatusId(reminderRequest.getStatusId());
	    	entity.setAddCcListExpiryReminder(reminderRequest.getAddCcListExpiryReminder());
	    	entity.setAddCcListLastReminder(reminderRequest.getAddCcListLastReminder());
	    	//entity.setCcListExpiryReminder(reminderRequest.getCcListExpiryReminder());
	    	//entity.setCcListLastReminder(reminderRequest.getCcListLastReminder());
	    	if(reminder == null)
	    		entity.setCreatedAt(DateTimeUtil.now());
	    	
			contract.setReminder(entity);
			
			contract = entityManager.merge(contract);
		} catch (Exception e) {
			logger.error("contract not created due to error : " + e);
			entityManager.close();
		}
		return contract.getReminder().getReminderId();
	}
	
	@Override
	public int createContract(Contract contract,User createdUser) {
		int reminderId = 0;
		try {
			logger.info("inside the createContract() method");
			
	    	Reminder reminderSaved = entityManager.merge(contract.getReminder());
	    	reminderId = reminderSaved.getReminderId();
			contract.setReminder(reminderSaved);
			
			contract = entityManager.merge(contract);
		} catch (Exception e) {
			logger.error("User not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return reminderId;
	}

	@Override
	public Contract getContractById(int contractNumber) {
		Contract contract = null;
		try {
			logger.info("fetching contract details of contractNumber :" + contractNumber);
			contract = entityManager.find(Contract.class, contractNumber);
		} catch (HibernateException e) {
			logger.error("No contract fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return contract;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> getAllContracts(String sortBy, boolean isVerified, String referenceNumber, String title, Integer offset,
			Integer numberOfRecords ,int userId) {
		
		List<Contract> contracts = null;
		List<Integer> reminderIds = null;
		try {
			logger.info("inside the getAllContracts() method");
			
			if(!isVerified)
		    	 reminderIds = getReminderIdsByUser(userId);
			else
				 reminderIds = getNonVerifiedReminderIdsByUser(userId);
			
			
			if(CollectionUtils.isEmpty(reminderIds)){
				return contracts;
			}

			List<String> wconditions = new ArrayList<>();
			
				wconditions.add("c.reminder.reminderId in (:ids)"); 
			if (title != null)
				wconditions.add("c.contractTitle=:title");
			
			if (referenceNumber != null)
				wconditions.add("c.contractReferenceNumber=:referenceNumber");
			
			StringBuilder sb = new StringBuilder();
			for (String s : wconditions)
			{
			    sb.append(s);
			    sb.append(" and ");
			}
			String sortByString = "";
			if (sortBy != null)
				sortByString = sortBy;
			
			Query query = entityManager.createQuery(
							 "Select c, oc from Contract c join c.reminder r join c.officerInChargeId oc where " 
						     + sb.toString()
						     + " (c.isVerified= :isVerified or c.isDeleted=TRUE) and (c.version, c.parentContractId) in "
						     + "(select  max(c1.version) as max_version, c1.parentContractId from Contract c1 where c1.isVerified=:isVerified group by parentContractId) "
							 + sortByString);
			
			query.setParameter("isVerified", isVerified);
			query.setParameter("ids", reminderIds);
			if (title != null)
			  query.setParameter("title", title );
			if (referenceNumber != null)
			  query.setParameter("referenceNumber", referenceNumber);
			
			if (numberOfRecords!= null && numberOfRecords != 0) {
				query.setFirstResult(offset * (offset - 1));
				query.setMaxResults(numberOfRecords);
			}
			contracts = query.getResultList();
			String sql = query.toString();
			System.out.println(sql);
		} catch (HibernateException e) {
			logger.error("No contract found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return contracts;
	}

	private List<Integer> getNonVerifiedReminderIdsByUser(int userId) {
		@SuppressWarnings("unchecked")
		List<Integer> reminderIds = (List<Integer>) entityManager.createNativeQuery("select reminder_id from  reminder where user_group_id in (select group_id from groups  where group_id in "
				+ "(SELECT group_id FROM group_user where user_id=:userId) and module_type_id in (select Module_Type_Id from  module_type where ModuleType='Contract'))").
		 setParameter("userId", userId).getResultList();
		return reminderIds;
	}

	@Override
	public int getContractCount(String sortBy, boolean isVerified, String referenceNumber, String title ,int userId) {
		long count = 0;
		List<Integer> reminderIds = null;

		try {
			
			if(!isVerified)
		    	 reminderIds = getReminderIdsByUser(userId);
			else
				 reminderIds = getNonVerifiedReminderIdsByUser(userId);
			
			if(CollectionUtils.isEmpty(reminderIds)){
				return 0;
			}
			
	        List<String> wconditions = new ArrayList<>();
			
		    wconditions.add("c.reminder.reminderId in (:ids)"); 
		
			if (title != null)
				wconditions.add("c.contractTitle=:title");
			
			if (referenceNumber != null)
				wconditions.add("c.contractReferenceNumber=:referenceNumber");
			
			StringBuilder sb = new StringBuilder();
			for (String s : wconditions)
			{
			    sb.append(s);
			    sb.append(" and ");
			}
			String sortByString = "";
			if (sortBy != null)
				sortByString = sortBy;
			
			Query query = entityManager.createQuery(
							 "Select count(c) from Contract c join c.reminder r join c.officerInChargeId oc where " 
						     + sb.toString()
						     + " c.isVerified= :isVerified and (c.version, c.parentContractId) in "
						     + "(select  max(c1.version) as max_version, c1.parentContractId from Contract c1 where c1.isVerified=:isVerified group by c1.parentContractId) "
							 + sortByString);
			
			query.setParameter("ids", reminderIds);
			query.setParameter("isVerified", isVerified);
			if (title != null)
			  query.setParameter("title", title );
			if (referenceNumber != null)
			  query.setParameter("referenceNumber", referenceNumber);
			
			count = (long) query.getSingleResult();

		} catch (HibernateException e) {
			logger.error("Contract not updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return (int) count;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getReminderIdsByUser(int userId) {
		return (List<Integer>) entityManager.createNativeQuery("select reminder_id from  reminder where user_group_id in (select group_id from groups  where group_id in "
				+ "(SELECT group_id FROM group_user where user_id in(select user_id from contract_reviewer where user_id=:userId)) and module_type_id in (select Module_Type_Id from  module_type where ModuleType='Contract'))").
		 setParameter("userId", userId).getResultList();
	}
	
	@Override
	public void updateContract(Contract contract) {
		try {
			logger.info("Inside the updateContract() method");
			entityManager.merge(contract);
			//entityManager.flush();
			entityManager.close();
		} catch (Exception e) {
			logger.error("Contract not Updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void deleteContract(int contractNumber) {
		try {
			logger.info("inside the deleteContract() method and deleting detail of contract : " + contractNumber);
			Contract c = entityManager.find(Contract.class, contractNumber);
			entityManager.remove(c);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Contract details is not deleted : " + e);
			entityManager.flush();
			entityManager.close();
		}catch ( Exception e) {
			logger.error("Contract details is not deleted : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}


	@Override
	public void createContractHasStatus(Contract_Has_Status contractHasStatus) {
		try {
			logger.info("inside the contractHasStatus() method");
			entityManager.persist(contractHasStatus);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("User not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> getSingleContract(int contractId) {
		List<Contract> contracts = null;
		try {
			contracts = entityManager.createQuery(
					"select c ,c.officerInChargeId from Reminder r, Contract c where r.reminderId = c.reminder and c.contractId="
							+ contractId)
					.getResultList();
		} catch (HibernateException e) {
			e.printStackTrace();
			logger.error("No contract found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return contracts;
	}

	/*	@Override
	public Contract_Has_Status createContractStatus(Contract_Has_Status contractStatus) {
		
		SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		
		//entityManager.persist(contractStatus);
		return (Contract_Has_Status) session.save(contractStatus);
	}*/

	@Override
	public Contract_Status getContractStatus(Integer contractStatusId) {
		Contract_Status status = entityManager.find(Contract_Status.class, contractStatusId);
		return status;
	}

	
/*	@Override
	public Contract contractCreate(Contract contract) {
		logger.info("inside the createContract() method");
		 entityManager.persist(contract); 
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.save(contract);
		return (Contract) session.save(contract);
	}*/

	@Override
	public void deleteContractPerm(Integer contractId , boolean deleteParent) {
		try {
			Contract c = getContractById(contractId);
			Contract pc=null; List<Contract_Reviewer> cr = null; List<Contract_Reviewer> newCr = null;
			if(!deleteParent && c.getContractStatus().equalsIgnoreCase("Update Rejected")){
				 cr = c.getContractReviewer();
				logger.info("saving the reviewer of contract before detleting it: ");
				int pcId = c.getParentContractId().getContractId();
				 int cId = (Integer) entityManager.createNativeQuery("select c.contract_id from contract c where c.parent_contract_id = :pcId "
						+ "and c.version = (select max(c1.version)-1 as max_version from contract c1 where c1.parent_contract_id = :pcId)")
						.setParameter("pcId", pcId)
						.getSingleResult();
				 pc = getContractById(cId);
				 newCr = new ArrayList<>();
					for(Contract_Reviewer cre: cr){
						Contract_Reviewer contractReviwerEntity = new Contract_Reviewer();
						contractReviwerEntity.setUserId(cre.getUserId());
						contractReviwerEntity.setLastModifiedBy(cre.getLastModifiedBy());
						contractReviwerEntity.setCreatedBy(cre.getCreatedBy());
						contractReviwerEntity.setContract(pc);
						newCr.add(contractReviwerEntity);
					}
			}
			if (deleteParent) {

				int childparentContractId = c.getParentContractId().getContractId();

				if (childparentContractId != contractId) {
					int parentContractId = c.getParentContractId().getParentContractId().getContractId();

					String deleteQuery1 = "DELETE FROM contract where contract_id="+ childparentContractId;
					entityManager.createNativeQuery(deleteQuery1).executeUpdate();

					/*String sql = "DELETE FROM reminder where reminder_id="+ c.getParentContractId().getReminder().getReminderId();
					entityManager.createNativeQuery(sql).executeUpdate();*/
					
					String deleteQuery2 = "DELETE FROM contract where contract_id="+ parentContractId;
					entityManager.createNativeQuery(deleteQuery2).executeUpdate();
					
					@SuppressWarnings("unchecked")
					List<Integer> fileId = entityManager.createNativeQuery("select file_id from files where reminder_id = "+c.getParentContractId().getReminder().getReminderId())
							   .getResultList();

					for(int fid: fileId){
						fileDao.deleteFile(fid);
					}
					
					@SuppressWarnings("unchecked")
					List<Integer> pfileId = entityManager.createNativeQuery("select file_id from files where reminder_id = "+
											c.getParentContractId().getParentContractId().getReminder().getReminderId())
							   .getResultList();

					for(int fid: pfileId){
						fileDao.deleteFile(fid);
					}
					
					String sql = "DELETE FROM reminder where reminder_id="+ c.getParentContractId().getReminder().getReminderId();
					entityManager.createNativeQuery(sql).executeUpdate();
					
					/*String deleteFiles = "delete from files where reminder_id = " + c.getReminder().getReminderId();
					entityManager.createNativeQuery(deleteFiles).executeUpdate()*/;
					
					String sqlDelete = "DELETE FROM reminder where reminder_id="+ c.getParentContractId().getParentContractId().getReminder().getReminderId();
					entityManager.createNativeQuery(sqlDelete).executeUpdate();
			 
				}
				
			}
		    
			String deleteQuery3 = "DELETE FROM contract where contract_id="+ contractId;
		    entityManager.createNativeQuery(deleteQuery3).executeUpdate();
		    
			if (c != null) {
				@SuppressWarnings("unchecked")
				List<Integer> fileId = entityManager.createNativeQuery("select file_id from files where reminder_id = "+c.getReminder().getReminderId())
						   .getResultList();

				for(int fid: fileId){
					fileDao.deleteFile(fid);
				}
				/*String deleteFiles = "delete from files where reminder_id = " + c.getReminder().getReminderId();
				entityManager.createNativeQuery(deleteFiles).executeUpdate();*/
					
				String sql = "DELETE FROM reminder where reminder_id="+c.getReminder().getReminderId();
				entityManager.createNativeQuery(sql).executeUpdate();
			}
			if(pc != null && newCr != null){
				
				pc.setContractReviewer(newCr);
				pc.linkContractReviewer();
				updateContract(pc);
			}
		    
		} catch (HibernateException e) {
			logger.error("Error in deleting contract : "+ contractId + e.getMessage());
			entityManager.flush();
			entityManager.close();
		}
		catch(Exception ex){
			logger.error("Error in deleting contract"+ex);
			entityManager.flush();
			entityManager.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Contracts getAllContracts(int userId, String sort_by, String order, boolean isVerified, Integer limit, Integer page_no,
			ContractSearchCriteria contractSearchCriteria, String searchC) {
		Contracts contracts = null;
		List<Contract> contractList = null;
		List<Integer> userBasedContractIds = getUserBasedContractIds(userId);

		if (userBasedContractIds == null || userBasedContractIds.isEmpty())
			return contracts;

		List<Integer> reminderIdsByDates = (List<Integer>) entityManager
				.createNativeQuery(" SELECT r.reminder_id FROM reminder r WHERE "
						+ "(DATE_FORMAT(r.effective_startd_date, '%d/%m/%Y') LIKE :searchC OR DATE_FORMAT(r.effective_expiry_date, '%d/%m/%Y') LIKE :searchC) ")
				.setParameter("searchC", "%" + searchC + "%").getResultList();

		StringBuilder searchCriteria = new StringBuilder();
		String sort = "";
		int offset = 0;
		int maxResult = 10;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}

		try {
			logger.info("inside the getAllContracts() method");

			List<String> wconditions = new ArrayList<>();

			if (contractSearchCriteria.getTitle() != null)
				wconditions.add(" LOWER(c.contractTitle) LIKE :title ");

			if (contractSearchCriteria.getReferenceNumber() != null) {
				wconditions.add(" c.contractReferenceNumber LIKE :referenceNumber ");
			}

			if (contractSearchCriteria.getStartDateFrom() != null) {
				wconditions.add(" r.effectiveStartDate >= :startDateFrom ");
			}

			if (contractSearchCriteria.getStartDateTo() != null) {
				wconditions.add(" r.effectiveStartDate <= :startDateTo ");
			}

			if (contractSearchCriteria.getExpiryDateFrom() != null) {
				wconditions.add(" r.effectiveExpiryDate >= :expiryDateFrom ");
			}

			if (contractSearchCriteria.getExpiryDateTo() != null) {
				wconditions.add(" r.effectiveExpiryDate <= :expiryDateTo ");
			}

			if (contractSearchCriteria.getSupplier() != null) {
				wconditions.add(" LOWER(c.supplier) LIKE :supplier ");
			}

			if (contractSearchCriteria.getOfficerInCharge() != null) {
				wconditions.add(" LOWER(oc.userName) LIKE :officerInCharge ");
			}

			if (contractSearchCriteria.getUserGroupId() != 0) {
				wconditions.add(" c.reminder.userGroupId.groupId=:userGroupId ");
			}

			if (contractSearchCriteria.getActive() != null) {
				if (contractSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
					wconditions.add(" r.active = TRUE ");
				} else if (contractSearchCriteria.getActive().equalsIgnoreCase("No")) {
					wconditions.add(" r.active = FALSE ");
				} else if (contractSearchCriteria.getActive().equalsIgnoreCase("All")) {
					wconditions.add(" (r.active = TRUE OR r.active = FALSE) ");
				}
			}

			if (contractSearchCriteria.getStatus() != null) {
				if (contractSearchCriteria.getStatus().equalsIgnoreCase("Expiring")) {
					wconditions.add(" (r.firstReminderDate < CURDATE() AND r.effectiveExpiryDate > CURDATE())");
				} else if (contractSearchCriteria.getStatus().equalsIgnoreCase("Expired")) {
					wconditions.add(" r.effectiveExpiryDate < CURDATE() ");
				} else if (contractSearchCriteria.getStatus().equalsIgnoreCase("Outside")) {
					wconditions.add(" r.firstReminderDate > CURDATE() ");
				}
			}

			if (!StringUtils.isEmpty(searchC)) {
				if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
					searchCriteria.append(" c.active = 1 ");
				else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
					searchCriteria.append(" c.active = 0 ");

				if (searchCriteria.length() > 0) {
					searchCriteria.append(" OR ");
				}
				wconditions.add(" ( " + searchCriteria.toString()
						+ " LOWER(c.contractTitle) LIKE :searchC OR LOWER(c.contractReferenceNumber) LIKE :searchC OR LOWER(c.supplier) LIKE :searchC OR LOWER(oc.userName) LIKE :searchC OR r.reminderId in (:reminderIds)) ");
			}
			StringBuilder sb = new StringBuilder();
			for (String s : wconditions) {
				sb.append(s);
				sb.append(" and ");
			}

			if ((sort_by) != null && !sort_by.isEmpty()) {
				if (sort_by.equalsIgnoreCase("active")) {
					sort_by = "r." + sort_by;
				} else if (sort_by.equalsIgnoreCase("group_name")) {
					sort_by = "r.userGroupId.groupName";
				} else if (sort_by.equalsIgnoreCase("reminder_sent")) {
					sort_by = " CASE when (r.firstReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
							+ " END " + order + ","
							+ " CASE when (r.secondReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
							+ " END " + order + ","
							+ " CASE when (r.thirdReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
							+ " END " + order + "," + " r.effectiveExpiryDate ";
				}
				sort = " ORDER BY " + sort_by + " " + order;
			} else
				sort = "";

			Query query = entityManager.createQuery(
					"Select c, oc from Contract c join c.reminder r left join c.officerInChargeId oc where "
							+ sb.toString()
							+ " ( c.isVerified= :isVerified or c.isDeleted=TRUE) and c.contractId in (:userBasedContractIds) AND (c.version, c.parentContractId) in "
							+ "(select  max(c1.version) as max_version, c1.parentContractId from Contract c1 where (c1.isVerified=:isVerified  or c1.isDeleted=TRUE ) group by c1.parentContractId) "
							+ sort);
			Query countQuery = entityManager.createQuery(
					"Select COUNT(c.contractId) from Contract c join c.reminder r left join c.officerInChargeId oc where "
							+ sb.toString()
							+ " ( c.isVerified= :isVerified or c.isDeleted=TRUE) and c.contractId in (:userBasedContractIds) AND (c.version, c.parentContractId) in "
							+ "(select  max(c1.version) as max_version, c1.parentContractId from Contract c1 where (c1.isVerified=:isVerified  or c1.isDeleted=TRUE ) group by c1.parentContractId) "
							+ sort);

			if (!StringUtils.isEmpty(searchC)) {
				query.setParameter("searchC", "%" + searchC + "%").setParameter("reminderIds", reminderIdsByDates);
				countQuery.setParameter("searchC", "%" + searchC + "%").setParameter("reminderIds", reminderIdsByDates);
			}
			query.setParameter("isVerified", isVerified);
			countQuery.setParameter("isVerified", isVerified);
			query.setParameter("userBasedContractIds", userBasedContractIds);
			countQuery.setParameter("userBasedContractIds", userBasedContractIds);

			if (contractSearchCriteria.getTitle() != null) {
				query.setParameter("title", "%" + contractSearchCriteria.getTitle() + "%");
				countQuery.setParameter("title", "%" + contractSearchCriteria.getTitle() + "%");
			}

			if (contractSearchCriteria.getReferenceNumber() != null) {
				query.setParameter("referenceNumber", "%" + contractSearchCriteria.getReferenceNumber() + "%");
				countQuery.setParameter("referenceNumber", "%" + contractSearchCriteria.getReferenceNumber() + "%");
			}

			if (contractSearchCriteria.getStartDateFrom() != null) {
				query.setParameter("startDateFrom",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getStartDateFrom()));
				countQuery.setParameter("startDateFrom",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getStartDateFrom()));
			}

			if (contractSearchCriteria.getStartDateTo() != null) {
				query.setParameter("startDateTo", DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getStartDateTo()));
				countQuery.setParameter("startDateTo",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getStartDateTo()));
			}

			if (contractSearchCriteria.getExpiryDateFrom() != null) {
				query.setParameter("expiryDateFrom",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getExpiryDateFrom()));
				countQuery.setParameter("expiryDateFrom",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getExpiryDateFrom()));
			}

			if (contractSearchCriteria.getExpiryDateTo() != null) {
				query.setParameter("expiryDateTo", DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getExpiryDateTo()));
				countQuery.setParameter("expiryDateTo",
						DateTimeUtil.convertToSGTWithDate(contractSearchCriteria.getExpiryDateTo()));
			}

			if (contractSearchCriteria.getSupplier() != null) {
				query.setParameter("supplier", "%" + contractSearchCriteria.getSupplier() + "%");
				countQuery.setParameter("supplier", "%" + contractSearchCriteria.getSupplier() + "%");
			}

			if (contractSearchCriteria.getOfficerInCharge() != null) {
				query.setParameter("officerInCharge", "%" + contractSearchCriteria.getOfficerInCharge() + "%");
				countQuery.setParameter("officerInCharge", "%" + contractSearchCriteria.getOfficerInCharge() + "%");
			}

			if (contractSearchCriteria.getUserGroupId() != 0) {
				query.setParameter("userGroupId", contractSearchCriteria.getUserGroupId());
				countQuery.setParameter("userGroupId", contractSearchCriteria.getUserGroupId());
			}

			if (maxResult != 0) {
				query.setFirstResult(offset).setMaxResults(maxResult);
			}
			contractList = query.getResultList();
			long count = (long) countQuery.getSingleResult();
			contracts = new Contracts();
			contracts.setContracts(contractList);
			contracts.setCount(count);

		} catch (HibernateException e) {
			logger.error("No contract found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return contracts;
	}
	
	@Override
	public void createContractConfig(ContractConfig contractConfig) {
		try {
			logger.info("inside the createContractConfig() method");
			entityManager.merge(contractConfig);
//			id = contract.getContractId();
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("User not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractConfig> getContractConfig() {
		List<ContractConfig>  cc = entityManager.createQuery("select c from ContractConfig c" )
				.getResultList();
		return cc;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Summary> getContractExpireCalendar(int userId,Date date) {
		
	BigInteger expiredCount = new BigInteger("-1");
	BigInteger thisMonthCount = new BigInteger("-1");
	BigInteger nextmonthcount = new BigInteger("-1");
	BigInteger toBeVerifiedCount = new BigInteger("-1");
	List<Summary> l = new ArrayList<>();
	Summary as = new Summary();
	List<java.sql.Date> dat = new ArrayList<>();

	String pattern = "yyyy-MM-dd";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String dateStr = simpleDateFormat.format(date);
	String currentDate = simpleDateFormat.format(new Date());
	
	List<Integer> userBasedContractIds = getUserBasedContractIds(userId);
	List<Integer> userBasedNotVerifiedContractIds = getUserBasedUnVerifiedContractIds(userId);
	
	if (!CollectionUtils.isEmpty(userBasedNotVerifiedContractIds)) {
		toBeVerifiedCount = (BigInteger) entityManager
				.createNativeQuery(" SELECT COUNT(DISTINCT c.contract_id) from contract c "
						+ " join reminder r on r.reminder_id = c.reminder_id "
						+ " join contract_reviewer cr on cr.contract_id = c.contract_id "
						+ " LEFT JOIN user createdUser ON createdUser.user_id = r.created_by_id "
						+ " LEFT JOIN user officerInCharge ON officerInCharge.user_id = c.officer_in_charge_id "
						+ " LEFT JOIN groups userGroup ON r.user_group_id = userGroup.group_id "
						+ " where (cr.last_modified_by_id = :userId or cr.user_id = :userId)  AND "
						+ " c.is_verified= FALSE AND c.contract_id in (:userBasedContractIds) "
						+ " AND (c.version, c.parent_contract_id) IN (SELECT max(c1.version) as max_version, c1.parent_contract_id FROM contract c1 GROUP BY c1.parent_contract_id)")
				.setParameter("userId", userId)
				.setParameter("userBasedContractIds", userBasedNotVerifiedContractIds).getSingleResult();
		as.setToBeVerifiedCount(toBeVerifiedCount);
	}
	try {
		if (!CollectionUtils.isEmpty(userBasedContractIds)) {
			List<java.sql.Date> dates = entityManager
					.createNativeQuery("select r.effective_expiry_date from reminder r , contract c "
							+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) "
							+ " and r.reminder_id=c.reminder_id and r.active = 1 AND c.contract_id in (:contractIds) AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id))")
					.setParameter("contractIds", userBasedContractIds).setParameter("date", dateStr).getResultList();
			for (java.sql.Date d : dates) {
				dat.add(d);
			}
			as.setExpiryDate(dat);
			// Expired
			expiredCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , contract c "
							+ "where r.effective_expiry_date <= :date and r.reminder_id=c.reminder_id and r.active = 1 AND c.contract_id in (:contractIds) AND (c.is_verified = TRUE or c.is_deleted = true) AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) ")
					.setParameter("contractIds", userBasedContractIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiredAsset(expiredCount);
			// Expiring this month
			thisMonthCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , contract c "
							+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY)"
							+ "and r.reminder_id=c.reminder_id and r.active = 1 AND c.contract_id in (:contractIds) AND (c.is_verified = true or c.is_deleted = true) AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) ")
					.setParameter("contractIds", userBasedContractIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiringThisMonth(thisMonthCount);
			// Expiring next month
			nextmonthcount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , contract c "
							+ "where r.effective_expiry_date >= DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
							+ "and r.reminder_id=c.reminder_id and r.active = 1 AND c.contract_id in (:contractIds) AND (c.is_verified = TRUE or c.is_deleted = true) AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) ")
					.setParameter("contractIds", userBasedContractIds).setParameter("date", dateStr).getSingleResult();
			as.setExpiringNextMonth(nextmonthcount);
		}
	} catch (Exception e) {
		logger.error("Error in getting Contract Summary : " + e);
		entityManager.flush();
		entityManager.close();
	}
	l.add(as);
	return l;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Contracts getContractReviewData(String sortBy, String order, boolean isVerified, Integer limit,
			Integer page_no, String searchCriteria, int userId) {
		Contracts contracts = null;
		List<Contract> contractList = null;
		List<Integer> userBasedContractIds = getUserBasedUnVerifiedContractIds(userId);
		if (userBasedContractIds == null || userBasedContractIds.isEmpty())
			return contracts;

		int offset = 0;
		int maxResult = 1000;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}

		try {
			logger.info("inside the getContractReviewData() method");
			List<String> wconditions = new ArrayList<>();
			List<Integer> reminderIdsByDates = (List<Integer>) entityManager.createNativeQuery(
					" SELECT r.reminder_id FROM contract c INNER JOIN reminder r ON r.reminder_id = c.reminder_id WHERE "
							+ "(DATE_FORMAT(r.effective_startd_date, '%d/%m/%Y') LIKE :searchC OR DATE_FORMAT(r.effective_expiry_date, '%d/%m/%Y') LIKE :searchC) AND c.contract_id IN (:userBasedContractIds)")
					.setParameter("searchC", "%" + searchCriteria + "%")
					.setParameter("userBasedContractIds", userBasedContractIds).getResultList();

			if (searchCriteria != null && !searchCriteria.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				if (reminderIdsByDates.size() > 0) {
					builder.append(" OR r.reminder_id IN (:reminderIdsByDates) ");
				}
				wconditions.add(
						" (LOWER(c.contract_title) LIKE (:searchCriteria) OR LOWER(c.supplier) LIKE (:searchCriteria) OR LOWER(c.contract_reference_number) LIKE (:searchCriteria) OR LOWER(officerInCharge.user_name) LIKE (:searchCriteria) OR LOWER(createdUser.user_name) LIKE (:searchCriteria) OR LOWER(c.contract_status) LIKE (:searchCriteria) "
								+ builder.toString() + ") ");
			}

			StringBuilder sb = new StringBuilder();
			for (String s : wconditions) {
				sb.append(s);
				sb.append(" AND ");
			}
			String sortByString = "";
			if (sortBy.equalsIgnoreCase("office_in_charge_name")) {
				sortBy = "officerInCharge.user_name";
			} else if (sortBy.equalsIgnoreCase("user_group_name")) {
				sortBy = "userGroup.name";
			}
			if (sortBy != null)
				sortByString = " ORDER BY " + sortBy + " " + order;

			Query query = entityManager.createNativeQuery(" SELECT DISTINCT c.* from contract c "
					+ " join reminder r on r.reminder_id = c.reminder_id "
					+ " join contract_reviewer cr on cr.contract_id = c.contract_id "
					+ " LEFT JOIN user createdUser ON createdUser.user_id = r.created_by_id "
					+ " LEFT JOIN user officerInCharge ON officerInCharge.user_id = c.officer_in_charge_id "
					+ " LEFT JOIN groups userGroup ON r.user_group_id = userGroup.group_id "
					+ " where (cr.last_modified_by_id = :userId or cr.user_id = :userId) AND " + sb.toString()
					+ "  c.is_verified= :isVerified AND c.contract_id in (:userBasedContractIds) "
					+ " AND (c.version, c.parent_contract_id) IN (SELECT max(c1.version) as max_version, c1.parent_contract_id FROM contract c1 GROUP BY c1.parent_contract_id)"
					+ sortByString, Contract.class);
			Query countQuery = entityManager.createNativeQuery(" SELECT COUNT(DISTINCT c.contract_id) from contract c "
					+ " join reminder r on r.reminder_id = c.reminder_id "
					+ " join contract_reviewer cr on cr.contract_id = c.contract_id "
					+ " LEFT JOIN user createdUser ON createdUser.user_id = r.created_by_id "
					+ " LEFT JOIN user officerInCharge ON officerInCharge.user_id = c.officer_in_charge_id "
					+ " LEFT JOIN groups userGroup ON r.user_group_id = userGroup.group_id "
					+ " where (cr.last_modified_by_id = :userId or cr.user_id = :userId) AND " + sb.toString()
					+ "  c.is_verified= :isVerified AND c.contract_id in (:userBasedContractIds) "
					+ " AND (c.version, c.parent_contract_id) IN (SELECT max(c1.version) as max_version, c1.parent_contract_id FROM contract c1 GROUP BY c1.parent_contract_id)"
					+ sortByString);

			if (searchCriteria != null && !searchCriteria.isEmpty()) {
				query.setParameter("searchCriteria", "%" + searchCriteria + "%");
				countQuery.setParameter("searchCriteria", "%" + searchCriteria + "%");
				if (reminderIdsByDates.size() > 0) {
					query.setParameter("reminderIdsByDates", reminderIdsByDates);
					countQuery.setParameter("reminderIdsByDates", reminderIdsByDates);
				}
			}
			query.setParameter("userId", userId);
			countQuery.setParameter("userId", userId);
			query.setParameter("isVerified", isVerified);
			countQuery.setParameter("isVerified", isVerified);
			query.setParameter("userBasedContractIds", userBasedContractIds);
			countQuery.setParameter("userBasedContractIds", userBasedContractIds);

			if (maxResult != 0) {
				query.setFirstResult(offset).setMaxResults(maxResult);
			}

			contractList = (List<Contract>) query.getResultList();
			long count = ((BigInteger) countQuery.getSingleResult()).longValue();
			contracts = new Contracts();
			contracts.setContracts(contractList);
			contracts.setCount(count);

		} catch (HibernateException e) {
			logger.error("No contract found : " + e);
			entityManager.close();
		} catch(Exception e){
			logger.error("Exception in getContractReviewer : " + e);
			entityManager.close();
		}

		return contracts;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getUserBasedContractIds(int userId) {
		Query query = entityManager.createNativeQuery("SELECT DISTINCT "
				+ " c.contract_id FROM contract c INNER JOIN "
				+ " reminder r ON c.reminder_id = r.reminder_id INNER JOIN "
				+ " groups g ON g.module_type_id = (SELECT mt.module_type_id FROM module_type mt WHERE mt.ModuleType LIKE ('%Contract%')) "
				+ " AND g.group_id = r.user_group_id INNER JOIN "
				+ " group_user gu ON gu.group_id = g.group_id AND (c.is_verified = TRUE or c.is_deleted = TRUE)"
				+ " and g.active = 1 AND gu.user_id = :userId ").setParameter("userId", userId);
		List<Integer> userBasedContractIds = query.getResultList();
		return userBasedContractIds;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getUserBasedUnVerifiedContractIds(int userId) {
		List<Integer> userBasedUnVerifiedContractIds = (List<Integer>) entityManager.createNativeQuery("SELECT DISTINCT "
				+ " c.contract_id FROM contract c INNER JOIN "
				+ " reminder r ON c.reminder_id = r.reminder_id INNER JOIN "
				+ " groups g ON g.module_type_id = (SELECT mt.module_type_id FROM module_type mt WHERE mt.ModuleType LIKE ('%Contract%')) "
				+ " AND g.group_id = r.user_group_id INNER JOIN " + " group_user gu ON gu.group_id = g.group_id AND c.is_verified = FALSE"
				+ " and g.active = 1 AND gu.user_id = :userId ").setParameter("userId", userId).getResultList();
		return userBasedUnVerifiedContractIds;
	}

	@Override
	public void deleteContractReview(int contractId) {
		int query=0;
		try{
		query = entityManager.createNativeQuery("delete from contract_reviewer where contract_id= :contractId")
			.setParameter("contractId", contractId)
			.executeUpdate();
		entityManager.flush();
		entityManager.close();
		}catch(Exception e){
			logger.error("not able to delete contract review"+e);
			entityManager.flush();
			entityManager.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> contractAdvanceSearch(String search, String column) {
		List<Contract> contract = null;
		String searchString = "";
		if("contractReferenceNumber".equalsIgnoreCase(column))
			searchString = " LOWER(c.contractReferenceNumber) like '%"+search+"%'" ;
		if("contractTitle".equalsIgnoreCase(column))
			searchString = " LOWER(c.contractTitle) like '%"+search+"%'";
		if("supplier".equalsIgnoreCase(column))
			searchString =  " LOWER(c.supplier) like '%"+search+"%' ";
		try{
			//contract = entityManager.createQuery("select c from Contract c where "+searchString)
			contract = entityManager.createQuery("select c from Contract c where "+searchString + " AND (c.isVerified = TRUE or c.isDeleted = true) "
					+ "AND ((c.version, c.parentContractId.contractId) in "
					+ "(select max(c1.version) as max_version, c1.parentContractId.contractId from Contract c1 where (c1.isVerified = true or c1.isDeleted = true) "
					+ "group by c1.parentContractId.contractId))")
						.getResultList();
		}catch(HibernateException e){
			logger.error("exception in advance search contract logic "+e);
			entityManager.flush();
			entityManager.close();
		}
		return contract;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MyContractResponse> searchByOfficerInCharge(String name){
		List<MyContractResponse> listRes = new ArrayList<>();
		MyContractResponse myres = null;
		String searchName = name.replace("%20", " ");
		try{
			String search = " u.user_name like '%"+searchName+"%'";
			List<String> obj = entityManager.createNativeQuery("select u.user_name from contract c inner join user u  on c.officer_in_charge_id = u.user_id where "+search)
					.getResultList();
			for(String res: obj){
				//Object[] object = (Object[]) res;
				myres = new MyContractResponse();
				myres.setUserName(res);
				listRes.add(myres);
			}
		}catch(HibernateException e){
			logger.error("error fetching office in charge"+e);
			entityManager.flush();
			entityManager.close();
		}
		return listRes;
	}

}