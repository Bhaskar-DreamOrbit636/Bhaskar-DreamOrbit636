package com.reminder.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.Groups;
import com.reminder.response.model.DashboardModule;

@Repository
public class DashboardDAOImpl implements DashboardDAO {

	private Logger logger = Logger.getLogger(DashboardDAOImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public DashboardModule getAssetExpiringCount(int userId, List<Integer> userBasedAssetIds) {
		logger.info("inside the getAssetExpiringCount() method");
		if (userBasedAssetIds == null || userBasedAssetIds.isEmpty())
			return null;
		BigInteger expiringNextMonth = new BigInteger("-1");
		BigInteger expiringTotal = new BigInteger("-1");
		expiringNextMonth = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r , asset a "
						+ "WHERE r.effective_expiry_date >= CURDATE() AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
						+ "and r.reminder_id = a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds)")
				.setParameter("userBasedAssetIds", userBasedAssetIds).getSingleResult();
		expiringTotal = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r INNER JOIN asset a "
						+ "where r.effective_expiry_date >= CURDATE() and r.reminder_id = a.reminder_id and r.active = 1 and a.asset_id in (:userBasedAssetIds) ")
				.setParameter("userBasedAssetIds", userBasedAssetIds).getSingleResult();

		DashboardModule dashboardModule = new DashboardModule();
		dashboardModule.setExpiringNextMonth(expiringNextMonth);
		dashboardModule.setExpiringTotal(expiringTotal);
		return dashboardModule;
	}

	@Override
	public DashboardModule getStaffExpiringCount(int userId, List<Integer> userBasedStaffRecordIds) {
		logger.info("inside the getStaffExpiringCount() method");
		if (userBasedStaffRecordIds == null || userBasedStaffRecordIds.isEmpty())
			return null;
		BigInteger expiringNextMonth = new BigInteger("-1");
		BigInteger expiringTotal = new BigInteger("-1");
		expiringNextMonth = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r , staff_record a "
						+ "where r.effective_expiry_date >= CURDATE() AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
						+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
				.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).getSingleResult();
		expiringTotal = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r , staff_record a "
						+ "where r.effective_expiry_date >= CURDATE() "
						+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
				.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).getSingleResult();

		DashboardModule dashboardModule = new DashboardModule();
		dashboardModule.setExpiringNextMonth(expiringNextMonth);
		dashboardModule.setExpiringTotal(expiringTotal);
		return dashboardModule;
	}

	@Override
	public DashboardModule getContractExpiringCount(int userId, List<Integer> userBasedContractIds) {
		logger.info("inside the getContractExpiringCount() method");
		if (userBasedContractIds == null || userBasedContractIds.isEmpty())
			return null;
		BigInteger expiringNextMonth = new BigInteger("-1");
		BigInteger expiringTotal = new BigInteger("-1");
		expiringNextMonth = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r , contract c "
						+ "where r.effective_expiry_date >= CURDATE() AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
						+ "and r.reminder_id=c.reminder_id and r.active = 1 AND c.contract_id in (:userBasedContractIds) AND (c.is_verified = TRUE or c.is_deleted = true) AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) ")
				.setParameter("userBasedContractIds", userBasedContractIds).getSingleResult();
		expiringTotal = (BigInteger) entityManager
				.createNativeQuery("select count(r.reminder_id) from reminder r , contract a "
						+ "where r.effective_expiry_date >= CURDATE() AND (a.is_verified = TRUE OR a.is_deleted = TRUE)"
						+ " AND r.reminder_id=a.reminder_id and r.active = 1 and a.contract_id in (:userBasedContractIds) "
						+ " AND ((a.version, a.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) ")
				.setParameter("userBasedContractIds", userBasedContractIds).getSingleResult();

		DashboardModule dashboardModule = new DashboardModule();
		dashboardModule.setExpiringNextMonth(expiringNextMonth);
		dashboardModule.setExpiringTotal(expiringTotal);
		return dashboardModule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getAssetMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedAssetIds) {
		logger.info("inside the getAssetMonthlyCount() method");
		if (userBasedAssetIds == null || userBasedAssetIds.isEmpty())
			return null;
		List<Object> assetMonthlyCount = entityManager.createNativeQuery(" SELECT YEAR(r1.effective_expiry_date),"
				+ " MONTH(r1.effective_expiry_date), COUNT(r1.reminder_id), r1.user_group_id, g.name FROM asset a INNER JOIN"
				+ " reminder r1 ON a.reminder_id = r1.reminder_id inner join groups g on g.group_id = r1.user_group_id AND r1.effective_expiry_date >= DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY)"
				+ " AND r1.effective_expiry_date <= DATE_ADD(DATE_ADD(DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY), INTERVAL 6 MONTH), INTERVAL -1 DAY) "
				+ " AND r1.user_group_id in (:groupId)"
				+ " and r1.active = 1 AND a.asset_id IN (:userBasedAssetIds)"
				+ " GROUP BY YEAR(r1.effective_expiry_date) , MONTH(r1.effective_expiry_date),r1.user_group_id"
				+ " ORDER BY YEAR(r1.effective_expiry_date) , MONTH(r1.effective_expiry_date)")
				.setParameter("groupId", setOfGroupId).setParameter("userBasedAssetIds", userBasedAssetIds).getResultList();
		return assetMonthlyCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getContractMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedContractIds) {
		logger.info("inside the getContractMonthlyCount() method");
		if (userBasedContractIds == null || userBasedContractIds.isEmpty())
			return null;
		List<Object> contractMonthlyCount = entityManager
				.createNativeQuery("SELECT YEAR(r2.effective_expiry_date), MONTH(r2.effective_expiry_date),"
						+ " COUNT(r2.reminder_id), r2.user_group_id, g.name FROM contract c INNER JOIN"
						+ " reminder r2 ON c.reminder_id = r2.reminder_id inner join groups g on g.group_id = r2.user_group_id  AND r2.effective_expiry_date >= DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY)"
						+ " AND r2.effective_expiry_date <= DATE_ADD(DATE_ADD(DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY), INTERVAL 6 MONTH), INTERVAL -1 DAY) AND r2.user_group_id in (:groupId) and (c.is_verified = TRUE OR c.is_deleted = TRUE)"
						+ " AND ((c.version, c.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id)) "
						+ " and r2.active = 1 AND c.contract_id IN (:userBasedContractIds)"
						+ " GROUP BY YEAR(r2.effective_expiry_date) , MONTH(r2.effective_expiry_date),r2.user_group_id"
						+ " ORDER BY YEAR(r2.effective_expiry_date) , MONTH(r2.effective_expiry_date)")
				.setParameter("groupId", setOfGroupId).setParameter("userBasedContractIds", userBasedContractIds)
				.getResultList();
		return contractMonthlyCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getStaffMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedStaffRecordIds) {
		logger.info("inside the getStaffMonthlyCount() method");
		if (userBasedStaffRecordIds == null || userBasedStaffRecordIds.isEmpty())
			return null;
		List<Object> staffMonthlyCount = entityManager
				.createNativeQuery("SELECT YEAR(r3.effective_expiry_date), MONTH(r3.effective_expiry_date),"
						+ " COUNT(r3.reminder_id), r3.user_group_id, g.name FROM staff_record sr INNER JOIN"
						+ " reminder r3 ON sr.reminder_id = r3.reminder_id inner join groups g on g.group_id = r3.user_group_id AND r3.effective_expiry_date >= DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY)"
						+ " AND r3.effective_expiry_date <= DATE_ADD(DATE_ADD(DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL -1 MONTH)), INTERVAL 1 DAY), INTERVAL 6 MONTH), INTERVAL -1 DAY) AND r3.user_group_id in (:groupId)"
						+ " and r3.active = 1 AND sr.staff_record_id IN (:userBasedStaffRecordIds)"
						+ " GROUP BY YEAR(r3.effective_expiry_date) , MONTH(r3.effective_expiry_date),r3.user_group_id"
						+ " ORDER BY YEAR(r3.effective_expiry_date) , MONTH(r3.effective_expiry_date)")
				.setParameter("groupId", setOfGroupId).setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds)
				.getResultList();
		return staffMonthlyCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Groups> getGroupsByUserId(int userId) {
		logger.info("inside the getGroupsByUserId() method");
		List<Groups> groups = entityManager.createNativeQuery(
				"SELECT g.* FROM groups g INNER JOIN group_user gu ON g.group_id = gu.group_id WHERE gu.user_id = :userId ",
				Groups.class).setParameter("userId", userId).getResultList();
		return groups;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getExpiryDate(String tableName, String moduleName, List<Integer> userBasedIds) {
		logger.info("inside the getExpiryDate() method");
		if (userBasedIds == null || userBasedIds.isEmpty())
			return null;
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT r.effective_expiry_date FROM " + tableName + " a "
				+ "INNER JOIN reminder r ON a.reminder_id = r.reminder_id and r.active = 1 AND a." + tableName
				+ "_id in (:userBasedIds) " + "AND r.effective_expiry_date >= CURDATE()");
		if (tableName.equalsIgnoreCase("contract")) {
			builder.append(
					" AND (a.is_verified= TRUE OR a.is_deleted = TRUE) AND ((a.version, a.parent_contract_id) in (select max(c1.version) as max_version, c1.parent_contract_id from contract c1 where (c1.is_verified = true or c1.is_deleted = true) group by c1.parent_contract_id))");
		}
		List<Date> dates = (List<Date>) entityManager.createNativeQuery(builder.toString())
				.setParameter("userBasedIds", userBasedIds).getResultList();
		return dates;
	}

}
