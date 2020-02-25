package com.reminder.dao;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.model.Asset;
import com.reminder.model.AssetType;
import com.reminder.model.Reminder;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.AssetSearchCriteria;
import com.reminder.response.model.AssetResponse;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.utils.DateTimeUtil;

@Repository
public class AssetDAOImpl implements AssetDAO {
	
	private Logger logger = Logger.getLogger(AssetDAOImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
    private GroupDAO groupDAO;
	
	@Autowired
	FileDAO fileDao;
	
	@Autowired
	ReminderDAO reminderDAO;

	@Override
	public void createAsset(Asset asset) {
		try {
			// assettype, reminder
			logger.info("inside the createAsset() method");
			entityManager.merge(asset);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Asset not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public Asset createAssetReminder(Asset asset) {
		Asset as = null;
		try {
			logger.info("inside the createAssetReminder() method");
			as = entityManager.merge(asset);
		} catch (HibernateException e) {
			logger.error("Asset not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return as;
	}

	@Override
	public AssetResponse getAssetById(int assetId) {
		Asset asset = null;
		AssetResponse ar = new AssetResponse();
		// AssetRequest assetReq = new AssetRequest();
		try {
			logger.info("fetching Asset details of equipmentId :" + assetId);
			asset = entityManager.find(Asset.class, assetId);
			ar.setAsset(asset);
			Integer asset_id = (Integer) entityManager
					.createNativeQuery("select a.asset_type_id from asset a where a.asset_id=" + assetId)
					.getSingleResult();
			Integer parentAssetId = (Integer) entityManager
					.createNativeQuery(
							"select a.parent_asset_type_id from asset_type a where a.asset_type_id=" + asset_id)
					.getSingleResult();
			String parentAssetName = null;
			if (parentAssetId == null) {
				parentAssetName = (String) entityManager
						.createNativeQuery("select a.type from asset_type a where  a.asset_type_id=" + asset_id)
						.getSingleResult();
			} else {
				parentAssetName = (String) entityManager
						.createNativeQuery("select a.type from asset_type a where  a.asset_type_id=" + parentAssetId)
						.getSingleResult();
			}
			ar.setParentId(parentAssetId);
			ar.setParentname(parentAssetName);
			ar.setGroupId(asset.getReminder().getUserGroupId().getGroupId());
			ar.setGroupName(asset.getReminder().getUserGroupId().getGroupName());
		} catch (HibernateException e) {
			logger.error("No Asset fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return ar;
	}

	@Override
	public Asset getAssetByIdLocal(Integer assetId) {
		Asset asset = null;
		// AssetResponse ar = new AssetResponse();
		// AssetRequest assetReq = new AssetRequest();
		try {
			logger.info("fetching Asset details of equipmentId :" + assetId);
			asset = entityManager.find(Asset.class, assetId);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("No Asset fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return asset;
	}

	/*
	 * @Override public List<Asset> getAllAssets() { List<Asset> asset = null;
	 * try { logger.info("inside the getAllAssets() method"); equipments =
	 * entityManager.createQuery("select c from Asset c").getResultList(); }
	 * catch (HibernateException e) {
	 * logger.error("No equipment found and exception occurred : " + e); }
	 * return asset; }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<AssetResponse> getAllAssets(String sort_by, String order, String assetDescription, Integer limit,
			Integer page_no, int userId) {
		AssetResponse ar = null;
		List<AssetResponse> Assetres = new ArrayList<>();
		String searchCriteria = "";
		String sort = "";
		int offset = 0;
		Integer maxResult = 0;

		List<Integer> reminderIds = (List<Integer>) entityManager
				.createNativeQuery(
						"select reminder_id from  reminder where user_group_id in (select group_id from groups  where group_id in "
								+ "(SELECT group_id FROM group_user where user_id=:userId) and module_type_id in (select Module_Type_Id from  module_type where ModuleType='Assets'))")
				.setParameter("userId", userId).getResultList();

		if (CollectionUtils.isEmpty(reminderIds)) {
			return Assetres;
		}

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}

		if ((assetDescription) != null)
			searchCriteria = " WHERE a.reminder.reminderId in (:ids) and LOWER(assetDescription) LIKE('%"
					+ assetDescription.toLowerCase() + "%')";
		else
			searchCriteria = " WHERE a.reminder.reminderId in (:ids)";
		if ((sort_by) != null)
			sort = " ORDER BY " + sort_by + " " + order;
		else
			sort = "";
		List<Asset> assets = null;
		try {
			logger.info("inside the getAllEquipments() method");
			long count = (long) entityManager
					.createQuery("select count(a) from Asset a WHERE a.reminder.reminderId in (:ids)")
					.setParameter("ids", reminderIds).getSingleResult();

			if (maxResult == 0) {
				assets = entityManager.createQuery("select a from Asset a" + searchCriteria + sort)
						.setParameter("ids", reminderIds).getResultList();
			} else {
				assets = entityManager.createQuery("select a from Asset a" + searchCriteria + sort)
						.setFirstResult(offset).setMaxResults(maxResult).setParameter("ids", reminderIds)
						.getResultList();
			}
			for (Asset as : assets) {
				ar = new AssetResponse();
				ar.setAsset(as);
				ar.setGroupId(as.getReminder().getUserGroupId().getGroupId());

				/*
				 * List<Integer> reminderIds = (List<Integer>) entityManager.
				 * createNativeQuery("select reminder_id from  reminder where user_group_id in (select group_id from groups  where group_id in "
				 * +
				 * "(SELECT group_id FROM group_user where user_id=:userId) and module_type_id=2)"
				 * ). setParameter("userId", userId).getResultList();
				 */

				Integer asset_id = (Integer) entityManager
						.createNativeQuery("select a.asset_type_id from asset a where a.asset_id=" + as.getAssetId())
						.getSingleResult();
				Integer parentAssetId = (Integer) entityManager
						.createNativeQuery(
								"select a.parent_asset_type_id from asset_type a where a.asset_type_id=" + asset_id)
						.getSingleResult();
				String parentAssetName = (String) entityManager
						.createNativeQuery("select a.type from asset_type a where a.asset_type_id=" + asset_id)
						.getSingleResult();
				ar.setParentId(parentAssetId);
				ar.setParentname(parentAssetName);
				ar.setCount(count);
				Assetres.add(ar);
			}
		} catch (HibernateException e) {
			logger.error("No Asset found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return Assetres;
	}

	@Override
	public void updateAsset(Asset asset) {
		try {
			logger.info("inside the updateAsset() method");
			entityManager.merge(asset);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Asset not updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAsset(int assetId, User user) {
		try {
			logger.info("inside the deleteAsset() method and deleting detail of AssetId : " + assetId);
		
			String reminder_id = "select reminder_id from asset where asset_id=" + assetId;
			Integer rid = (Integer)entityManager.createNativeQuery(reminder_id).getSingleResult();// executeUpdate();

			String deleteQuery = "DELETE FROM asset where asset_id=" + assetId;
			entityManager.createNativeQuery(deleteQuery).executeUpdate();
			
			List<Integer> fileId = entityManager.createNativeQuery("select file_id from files where reminder_id = "+rid)
								   .getResultList();
			
			for(int fid: fileId){
				fileDao.deleteFile(fid);
			}
			
			/*String deleteFiles = "delete from files where reminder_id = " + rid;
			entityManager.createNativeQuery(deleteFiles).executeUpdate();*/
			Reminder rem = reminderDAO.getReminderById(rid);
			rem.setLastModifiedById(user.getUserId());
			reminderDAO.updateReminder(rem);
			entityManager.flush();
			
			String sql = "DELETE FROM reminder where reminder_id = " + rid;
			entityManager.createNativeQuery(sql).executeUpdate();

		} catch (HibernateException e) {
			logger.error("Asset details is not deleted, error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	// ------------------------------- AssetType
	// ---------------------------------------

	@Override
	public void createAssetType(AssetType assetType) {
		try {
			logger.info("inside the createAssetType() method");
			entityManager.merge(assetType);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("AssetType not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public AssetType getAssetTypeById(int assetTypeId) {
		AssetType assetType = null;
		try {
			logger.info("fetching Asset details of equipmentId :" + assetTypeId);
			assetType = entityManager.find(AssetType.class, assetTypeId);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("No AssetType fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		
		return assetType;
	}

	/*
	 * @Override public List<AssetType> getAllAssetTypes() { List<AssetType>
	 * assetType = null; try {
	 * logger.info("inside the getAllAssetTypes() method"); equipments =
	 * entityManager.createQuery("select c from AssetType c").getResultList(); }
	 * catch (HibernateException e) {
	 * logger.error("No equipment found and exception occurred : " + e); }
	 * return assetType; }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public AssetResponse getAllAssetTypes(String sort_by, String order, String assetTypeDescription, Integer limit,
			Integer page_no, String subType) {
		AssetResponse ar = new AssetResponse();
		String searchCriteria = "";
		String sort = "";
		int offset = 0;
		int maxResult = 1000;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}
		if ((assetTypeDescription) != null){
 
			searchCriteria =  " and LOWER(a.assetType) LIKE('%" + assetTypeDescription.toLowerCase() + "%') OR"
					+ " LOWER(a.active) LIKE('%" + assetTypeDescription.toLowerCase() + "%')"
							+ "OR LOWER(a.assetType.assetType) LIKE ('%" + assetTypeDescription.toLowerCase() + "%')";
		}
		else
			searchCriteria = "";
		System.out.println("********searchCriteria*******" + searchCriteria);
		if ((sort_by) != null){
			sort = " ORDER BY " + sort_by + " " + order;
		}
		else
			sort = "";
		
		Long count=0L;
		 count = (Long) entityManager.createQuery("select count(a.assetTypeId) from AssetType a where a.parentAssetType is null " + searchCriteria)
				.getSingleResult();
		ar.setCount(count);

		List<AssetType> assetTypes = null;
		List<AssetType> parent = new ArrayList<>();
		try {
			logger.info("inside the getAllAssetTypes() method");
			assetTypes = entityManager
					.createQuery("select a from AssetType a where a.parentAssetType is null " + searchCriteria + sort)
					.setFirstResult(offset).setMaxResults(maxResult).getResultList();
		
			if (!StringUtils.isEmpty(searchCriteria)) {
				
					mapToParentFromChild(assetTypes,parent);
				
				ar.setAssettype(parent);
				ar.setCount(Long.valueOf(parent.size()));
			}else{
				ar.setAssettype(assetTypes);
			}
		
			if(sort_by.equals("assetType")){
		        Collections.sort(parent, new SortbyAssetType());
			}
			
		} catch (HibernateException e) {
			logger.error("No Asset found : " + e);
			entityManager.flush();
			entityManager.close();
		}
 
		return ar;
	}

	private void mapToParentFromChild(List<AssetType> assetTypes,List<AssetType> parent) {
		Set<AssetType> subSet = new HashSet<>();
		Set<Integer> ids = new HashSet<>();
		Set<AssetType> temSet = new HashSet<>();
		//Map<Integer, Boolean> map = new HashMap<>();
		for(AssetType assetsub : assetTypes){
			AssetType assetparent = assetsub.getParentAssetType();
			if(assetparent != null){
			if(ids.contains(assetparent.getAssetTypeId())){
				subSet.add(assetsub);
				assetparent.setAssettypes(subSet);
				ids.add(assetparent.getAssetTypeId());
				temSet.add(assetsub.getParentAssetType());
			}else{
				subSet = new HashSet<>();
				ids.add(assetparent.getAssetTypeId());
				subSet.add(assetsub);
				assetparent.setAssettypes(subSet);
				temSet.add(assetsub.getParentAssetType());
			}
		}
	}
		for(AssetType at: temSet){
			parent.add(at);
		}
		
		
	}
	
	class SortbyAssetType implements Comparator<AssetType> {
		public int compare(AssetType a, AssetType b) {
			return a.getAssetType().compareTo(b.getAssetType());
		}
	}
	
	@Override
	public void updateAssetType(AssetType assetType) {
		try {
			logger.info("inside the updateAssetType() method");
			AssetType at = getAssetTypeById(assetType.getAssetTypeId());
			assetType.setCreatedById(at.getCreatedById());
			entityManager.merge(assetType);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("AssetType not updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void deleteAssetType(int assetTypeId) {
			try{
			logger.info("inside the deleteAssetType() method and deleting detail of AssetTypeId : " + assetTypeId);
			AssetType at = entityManager.find(AssetType.class, assetTypeId);
			
			Integer getReminderId = (Integer) entityManager.createNativeQuery("select a.reminder_id from asset a where a.asset_type_id=:asset_id")
					.setParameter("asset_id", assetTypeId)
					.getFirstResult();
			if(getReminderId == 0){
			   String deleteQuery = "DELETE FROM asset_type where asset_type_id=" + at.getAssetTypeId();
			  entityManager.createNativeQuery(deleteQuery).executeUpdate();
			 }
			else{	
			entityManager.remove(at);
			}
			entityManager.flush();
			entityManager.close();
			} catch (ConstraintViolationException e) {
				logger.error("Asset details is not deleted, error : " + e);
				entityManager.flush();
				entityManager.close();
			}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Summary> getExpiryCalendar(int userId, Date date) {
		List<Summary> l = new ArrayList<>();
		List<Integer> userBasedAssetIds = getUserBasedAssetIds(userId);
		if (userBasedAssetIds == null || userBasedAssetIds.isEmpty()) {
			return l;
		}
		BigInteger expiredCount = new BigInteger("-1");
		BigInteger thisMonthCount = new BigInteger("-1");
		BigInteger nextmonthcount = new BigInteger("-1");
		Summary as = new Summary();
		List<java.sql.Date> dat = new ArrayList<>();

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(date);
		String currentDate = simpleDateFormat.format(new Date());
	    
		List<java.sql.Date> dates = entityManager
				.createNativeQuery("select distinct(r.effective_expiry_date) from reminder r , asset a "
						+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) "
						+ " and r.reminder_id=a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds)")
				.setParameter("userBasedAssetIds", userBasedAssetIds).setParameter("date", dateStr).getResultList();
		for (java.sql.Date d : dates) {
			dat.add(d);
		}
		as.setExpiryDate(dat);
		try {
			// Expired
			expiredCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , asset a "
							+ "where r.effective_expiry_date <= :date and r.reminder_id=a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds)")
					.setParameter("userBasedAssetIds", userBasedAssetIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiredAsset(expiredCount);
			// Expiring this month
			thisMonthCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , asset a "
							+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY)"
							+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds)")
					.setParameter("userBasedAssetIds", userBasedAssetIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiringThisMonth(thisMonthCount);
			// Expiring next month
			nextmonthcount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , asset a "
							+ "where r.effective_expiry_date >= DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
							+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds)")
					.setParameter("userBasedAssetIds", userBasedAssetIds).setParameter("date", dateStr).getSingleResult();
			as.setExpiringNextMonth(nextmonthcount);
		} catch (Exception e) {
			logger.error("Errror in getting Asset Summary : " + e);
			entityManager.flush();
			entityManager.close();
		}
		l.add(as);
		return l;
	}

	@Override
	public MyGroupDropDown getGroupDetailByReminderId(int reminderId) {

		Object[] reminderIds = (Object[]) entityManager
				.createNativeQuery(
						"SELECT g.group_id,g.name FROM groups g where g.group_id in(SELECT user_group_id FROM reminder where reminder_id=:reminderId)")
				.setParameter("reminderId", reminderId).getSingleResult();

		MyGroupDropDown myGroupDropDown = new MyGroupDropDown();
		myGroupDropDown.setId((int) reminderIds[0]);
		myGroupDropDown.setName((String) reminderIds[1]);
		return myGroupDropDown;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AssetResponse> searchAsset(String sort_by, String order, Integer limit, Integer page_no,
			AssetSearchCriteria assetSearchCriteria, int userId, String searchC) {
		AssetResponse ar = new AssetResponse();
		List<AssetResponse> assetRes = new ArrayList<>();
		StringBuilder searchCriteria = new StringBuilder();
		String sort = "";
		if(sort_by !=null && sort_by.equals("location")){
			sort="";
		}
		int offset = 0;
		int maxResult = 0;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}


		List<Integer> userBasedAssetIds = getUserBasedAssetIds(userId);

		if (userBasedAssetIds == null || CollectionUtils.isEmpty(userBasedAssetIds)) {
			return assetRes;
		}
		
		List<Integer> reminderIdsByDates = null;
		if (searchC != null && !searchC.isEmpty()) {
		reminderIdsByDates = (List<Integer>) entityManager
				.createNativeQuery(" SELECT r.reminder_id FROM reminder r WHERE "
						+ "DATE_FORMAT(r.effective_expiry_date,'%d/%m/%Y') LIKE :searchC ")
				.setParameter("searchC", "%" + searchC + "%").getResultList();
		}

		List<Integer> parentId = new ArrayList<>();
		if (assetSearchCriteria.getAssetType() != null) {
			parentId = (List<Integer>) entityManager
					.createQuery("SELECT at.assetTypeId FROM AssetType at WHERE at.assetType LIKE :assetType")
					.setParameter("assetType", "%" + assetSearchCriteria.getAssetType() + "%").getResultList();
		}
		StringBuilder sb = new StringBuilder();
		List<String> conditionList = new ArrayList<>();


		if (assetSearchCriteria.getLocation() != null) {
			conditionList.add(" LOWER(a.location.locationName) LIKE ('%" + assetSearchCriteria.getLocation() + "%') ");
		}
		if (assetSearchCriteria.getAssetType() != null && parentId != null && !parentId.isEmpty()) {
			conditionList.add(" a.assetType.parentAssetType.assetTypeId in (:pa)");
		}
		if (assetSearchCriteria.getAssetType() != null && parentId != null && parentId.isEmpty()) {
			conditionList.add(" LOWER(a.assetType.parentAssetType.assetType) like ('%" + assetSearchCriteria.getAssetType() + "%')");
		}
		if (assetSearchCriteria.getAssetSubType() != null) {
			conditionList
					.add(" LOWER(a.assetType.assetType) LIKE ('%" + assetSearchCriteria.getAssetSubType() + "%') ");
		}
		if (assetSearchCriteria.getAssetId() != null) {
			conditionList.add(" a.id LIKE ('%" + assetSearchCriteria.getAssetId() + "%')");
		}
		if (assetSearchCriteria.getExpiryDateFrom() != null) {
			conditionList.add(" r.effectiveExpiryDate >= :expiryEffectiveStart ");
		}
		if (assetSearchCriteria.getExpiryDateTo() != null) {
			conditionList.add(" r.effectiveExpiryDate <= :expiryEffectiveTo ");
		}
		if (assetSearchCriteria.getUserGroupId() != 0) {
			conditionList.add(" r.userGroupId.groupId = " + assetSearchCriteria.getUserGroupId() + " ");
		}
		if (assetSearchCriteria.getActive() != null) {
			if (assetSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" r.active = true ");
			} else if (assetSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" r.active = false ");
			} else if (assetSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (r.active = true OR r.active = false) ");
			}
		}
		if (assetSearchCriteria.getAssetDescription() != null) {
			conditionList
					.add(" LOWER(a.assetDescription) LIKE ('%" + assetSearchCriteria.getAssetDescription() + "%') ");
		}
		if (assetSearchCriteria.getStatus() != null) {
			if (assetSearchCriteria.getStatus().equalsIgnoreCase("Expiring")) {
				conditionList.add(" (r.firstReminderDate < CURDATE() AND r.effectiveExpiryDate > CURDATE())");
			} else if (assetSearchCriteria.getStatus().equalsIgnoreCase("Expired")) {
				conditionList.add(" r.effectiveExpiryDate < CURDATE() ");
			} else if (assetSearchCriteria.getStatus().equalsIgnoreCase("Outside")) {
				conditionList.add(" r.firstReminderDate > CURDATE() ");
			}
		}
		for (int i = 0; i < conditionList.size(); i++) {
			sb.append(" AND ");
			sb.append(conditionList.get(i));
		}
		if (searchC != null && !searchC.isEmpty()) {
			if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
				searchCriteria.append(" a.active = 1 ");
			else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
				searchCriteria.append(" a.active = 0 ");

			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			sb.append(" AND (" + searchCriteria.toString()
					+ " LOWER(a.assetType.parentAssetType.assetType) LIKE :searchCriteria OR LOWER(a.assetType.assetType) LIKE :searchCriteria OR LOWER(a.id) LIKE :searchCriteria OR r.reminderId IN (:reminderIdsByDates)) ");
		}
		if (sort_by != null) {
			if (sort_by.equalsIgnoreCase("parent_asset_type")) {
				sort_by = "a.assetType.parentAssetType.assetType";
			} else if (sort_by.equalsIgnoreCase("active")) {
				sort_by = "r.active";
			} else if (sort_by.equalsIgnoreCase("reminder_sent")) {
				sort_by = " CASE when (r.firstReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + ","
						+ " CASE when (r.secondReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + ","
						+ " CASE when (r.thirdReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + "," + " r.effectiveExpiryDate";
			} else if (sort_by.equalsIgnoreCase("assetType")) {
				sort_by = "a.assetType.assetType";
			} else if (sort_by.equalsIgnoreCase("name")) {
				sort_by = "r.userGroupId.groupName";
			}else if (sort_by.equalsIgnoreCase("location")) {
				sort_by = "l.locationName ";
			}
			sort = " ORDER BY l.locationName " + order + ", " + sort_by + " " + order;
		}

		List<Asset> assets = null;
		try {
			logger.info("inside the searchAsset() method");
			Query query = null;
			query = entityManager.createQuery(
					"select a from Asset a JOIN a.reminder r JOIN a.location l where a.assetId in (:userBasedAssetIds) "
							+ sb.toString() + sort)
					.setParameter("userBasedAssetIds", userBasedAssetIds);
			Query countQuery = entityManager.createQuery(
					"select COUNT(a.assetId) from Asset a JOIN a.reminder r JOIN a.location l where a.assetId in (:userBasedAssetIds) "
							+ sb.toString())
					.setParameter("userBasedAssetIds", userBasedAssetIds);
			if (searchC != null && !searchC.isEmpty()) {
				query.setParameter("searchCriteria", "%" + searchC + "%").setParameter("reminderIdsByDates",
						reminderIdsByDates);
				countQuery.setParameter("searchCriteria", "%" + searchC + "%").setParameter("reminderIdsByDates",
						reminderIdsByDates);
			}
			if (assetSearchCriteria.getAssetType() != null && parentId != null
					&& !parentId.isEmpty()) {
				query.setParameter("pa", parentId);
				countQuery.setParameter("pa", parentId);
			}
			if (assetSearchCriteria.getExpiryDateFrom() != null) {
				query.setParameter("expiryEffectiveStart",
						DateTimeUtil.convertToSGTWithDate(assetSearchCriteria.getExpiryDateFrom()));
				countQuery.setParameter("expiryEffectiveStart",
						DateTimeUtil.convertToSGTWithDate(assetSearchCriteria.getExpiryDateFrom()));
			}
			if (assetSearchCriteria.getExpiryDateTo() != null) {
				query.setParameter("expiryEffectiveTo",
						DateTimeUtil.convertToSGTWithDate(assetSearchCriteria.getExpiryDateTo()));
				countQuery.setParameter("expiryEffectiveTo",
						DateTimeUtil.convertToSGTWithDate(assetSearchCriteria.getExpiryDateTo()));
			}
			if (maxResult != 0) {
				query = query.setFirstResult(offset).setMaxResults(maxResult);
			}
			assets = query.getResultList();
			Long count = new Long((Long) countQuery.getSingleResult());
			Map <Integer,List<String>> map = new HashMap <>();
			Set<Integer> setOfGroupId = new HashSet<>();
			for (Asset as : assets) {
				ar = new AssetResponse();
				ar.setAsset(as);
				if(as.getAssetType().getParentAssetType() != null){
				ar.setParentId(as.getAssetType().getParentAssetType().getAssetTypeId());
				ar.setParentname(as.getAssetType().getParentAssetType().getAssetType());
				}else{
					ar.setParentId(as.getAssetType().getAssetTypeId());
					ar.setParentname(as.getAssetType().getAssetType());
				}
				ar.setCount(count);
				ar.setGroupName(as.getReminder().getUserGroupId().getGroupName());
				ar.setGroupId(as.getReminder().getUserGroupId().getGroupId());
				setOfGroupId.add(as.getReminder().getUserGroupId().getGroupId());
				
			/*	if (ar.getGroupId() != 0) {
					
					if (map.get(ar.getGroupId()) == null) {
						
						List<String> roleActions = groupDAO.getGroupRolesAction(userId, ar.getGroupId());
						map.put(ar.getGroupId(), roleActions);
						ar.setActions(roleActions);
						
					} else {
						ar.setActions(map.get(ar.getGroupId()));
					}
				}*/
				assetRes.add(ar);
			}
			if(!setOfGroupId.isEmpty()){
			List<Object[]> roleActions = groupDAO.getGroupRolesAction(userId, setOfGroupId);
			
			List<String> actionName = null;
			for(Object[] obj: roleActions ){				
				if(!map.containsKey((Integer)obj[1])){
				actionName = new ArrayList<>();
				actionName.add((String)obj[0]);
				map.put((Integer)obj[1],actionName);
				}
				else{
					actionName = map.get((Integer)obj[1]);
					actionName.add((String)obj[0]);
					map.put((Integer)obj[1], actionName);
				}			
			}
			//map.forEach((k, v) -> System.out.println((k + ":" + v)));
			for(AssetResponse asr: assetRes){
				asr.setActions(map.get(asr.getAsset().getReminder().getUserGroupId().getGroupId()));
			}
			}
		} catch (HibernateException e) {
			logger.error("No Asset found : " + e);
		}
		return assetRes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getUserBasedAssetIds(int userId) {
		Query query = entityManager.createNativeQuery("SELECT DISTINCT " + " a.asset_id FROM asset a INNER JOIN "
				+ " reminder r ON a.reminder_id = r.reminder_id INNER JOIN "
				+ " groups g ON g.module_type_id = (SELECT mt.module_type_id FROM module_type mt WHERE mt.ModuleType LIKE ('%Assets%')) "
				+ " AND g.group_id = r.user_group_id INNER JOIN " + " group_user gu ON gu.group_id = g.group_id "
				+ " AND g.active = 1 and gu.user_id = :userId ").setParameter("userId", userId);
		return  query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> searchByAssetType(String assetType) {
		List<AssetType> assetT = null;
		String search = " and Lower(a.assetType) LIKE ('%" + assetType + "%') ";
		try{
			assetT= entityManager.createQuery("select a from AssetType a where a.assetType.parentAssetType is null"+search)
				.getResultList();
		}catch(HibernateException e){
			logger.error("error fetching assetTypes"+e);
			entityManager.flush();
			entityManager.close();
		}
		return assetT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> searchByAssetSubType(String assetSubType) {
		List<AssetType> assetT = null;
		String search = " and Lower(a.assetType) LIKE ('%" + assetSubType + "%') ";
		try{
			assetT= entityManager.createQuery("select a from AssetType a where a.assetType.parentAssetType is not null"+search)
				.getResultList();
		}catch(HibernateException e){
			logger.error("error fetching assetSubTypes"+e);
			entityManager.flush();
			entityManager.close();
		}
		return assetT;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> searchByAssetID(String assetID) {
		List<Asset> assetT = null;
		String search = " Lower(a.id) LIKE ('%" + assetID + "%') ";
		try{
			assetT= entityManager.createQuery("select a from Asset a where "+search)
				.getResultList();
		}catch(HibernateException e){
			logger.error("error fetching assetID"+e);
			entityManager.flush();
			entityManager.close();
		}
		return assetT;
	}
	
	
}