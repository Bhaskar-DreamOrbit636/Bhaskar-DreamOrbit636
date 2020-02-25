package com.reminder.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.model.Department;
import com.reminder.model.GrouproleUser;
import com.reminder.model.User;
import com.reminder.request.model.UserSearchCriteria;
import com.reminder.response.model.UserPopUp;
import com.reminder.response.model.UserResponse;
import com.reminder.utils.DateTimeUtil;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
@PropertySource("classpath:/application.properties")
public class UserDAOImpl implements UserDAO {
	private Logger logger = Logger.getLogger(UserDAOImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/*
	 * @Value("${appsupport}") private String supprtMail;
	 */

	// private String supprtMail = System.getProperty("appsupport");

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void createUser(User user, String userName) {
		try {
			logger.info("inside the createUser() method");

			User createdUser = null;
			try {
				createdUser = getUserByName(userName);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("error in getting username: " + userName + ", " + e);
			}

			if (createdUser != null) {
				user.setCreatedById(createdUser.getUserId());
				//user.setLastModifiedById(createdUser.getUserId());
			}
			user.setDepartments(findDepartment(user.getDepartments().getDepartmentId()));
			entityManager.persist(user);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("User not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	public Department findDepartment(int deptId) {
		Department department = null;
		try {
			logger.info("fetching user details of userid :" + deptId);
			department = entityManager.find(Department.class, deptId);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("No user found with this id : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return department;
	}

	@Override
	public User getUserById(int userId) {
		User user = null;
		try {
			logger.info("fetching user details of userid :" + userId);
			user = entityManager.find(User.class, userId);
		} catch (HibernateException e) {
			logger.error("No user found with this id : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return user;
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public List<User> getAllUsers() {
	 * 
	 * List<User> users = null; try {
	 * logger.info("inside the getAllUsers() method"); users =
	 * entityManager.createQuery("select u from User u").getResultList(); }
	 * catch (HibernateException e) { logger.error("No user found : " + e); }
	 * return users; }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllActiveUsersGroup(String sort_by, String order, String searchC, Integer limit,
			Integer page_no) {
		String searchCriteria = "";
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
		if (!StringUtils.isEmpty(searchC)) {
			searchCriteria = " WHERE (LOWER(u.departments.departmentName) LIKE :searchC OR LOWER(u.adUserId) LIKE :searchC OR LOWER(u.userName) LIKE :searchC OR LOWER(u.emailId) LIKE :searchC "
					+ "	OR LOWER(u.mobileNumber) LIKE :searchC OR LOWER(u.remark) LIKE :searchC) and u.active=1";
		} else
			searchCriteria = " WHERE u.active = 1";

		if (sort_by != null)
			sort = " ORDER BY " + sort_by + " " + order;

		List<User> users = null;
		try {
			logger.info("inside the getAllUsers() method");
			Query query = entityManager.createQuery("select u from User u" + searchCriteria + sort);
			if (!StringUtils.isEmpty(searchC))
				query.setParameter("searchC", "%" + searchC + "%");
			users = query.setFirstResult(offset).setMaxResults(maxResult).getResultList();

		} catch (HibernateException e) {
			logger.error("No user found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return users;
	}

	@Override
	public long getAllActiveUsersCount(String sort_by, String searchText) {
		String searchCriteria = "";
		boolean execute = true;
		if (!StringUtils.isEmpty(searchText))
			searchCriteria = " WHERE (LOWER(u.departments.departmentName) LIKE :searchText OR LOWER(u.adUserId) LIKE :searchText OR LOWER(u.userName) LIKE :searchText OR LOWER(u.emailId) LIKE :searchText OR LOWER(u.mobileNumber) LIKE :searchText OR LOWER(u.remark) LIKE :searchText )"
					+ "  and u.active=1  ";
		else {
			searchCriteria = " WHERE u.active=1";
			execute = false;
		}
		Query q = entityManager.createQuery("select count(u) from User u" + searchCriteria);
		if (execute) {
			q.setParameter("searchText", "%" + searchText + "%");
		}
		return (long) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsers(String sort_by, String order, String searchC, Integer limit, Integer page_no,
			UserSearchCriteria userSearchCriteria) {
		StringBuilder searchCriteria = new StringBuilder();
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

		List<String> conditionList = new ArrayList<>();
		if (userSearchCriteria.getUserId() != null) {
			conditionList.add(" LOWER(u.adUserId) LIKE :userId ");
		}
		if (userSearchCriteria.getUserName() != null) {
			conditionList.add(" LOWER(u.userName) LIKE :userName ");
		}
		if (userSearchCriteria.getDepartment() != null) {
			conditionList.add(" LOWER(u.departments.departmentName) LIKE :departmentName ");
		}
		if (userSearchCriteria.getEmailId() != null) {
			conditionList.add(" LOWER(u.emailId) LIKE :emailId ");
		}
		if (userSearchCriteria.getMobileNumber() != null) {
			conditionList.add(" LOWER(u.mobileNumber) LIKE :mobileNumber ");
		}

		if (userSearchCriteria.getGroupAdmin() != null) {
			if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.groupAdmin = true ");
			} else if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("No")) {
				conditionList.add(" u.groupAdmin = false ");
			} else if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("All")) {
				conditionList.add(" (u.groupAdmin = true OR u.groupAdmin = false) ");
			}
		}
		if (userSearchCriteria.getUserAdmin() != null) {
			if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.userAdmin = true ");
			} else if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("No")) {
				conditionList.add(" u.userAdmin = false ");
			} else if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("All")) {
				conditionList.add(" (u.userAdmin = true OR u.userAdmin = false) ");
			}
		}
		if (userSearchCriteria.getActive() != null) {
			if (userSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.active = true ");
			} else if (userSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" u.active = false ");
			} else if (userSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (u.active = true OR u.active = false) ");
			}
		}
		if (!StringUtils.isEmpty(searchC)) {
			if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
				searchCriteria.append(" u.groupAdmin = 1 OR u.userAdmin = 1 ");
			else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
				searchCriteria.append(" u.groupAdmin = 0 OR u.userAdmin = 0 ");
			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			conditionList.add(" (" + searchCriteria.toString()
					+ "LOWER(u.departments.departmentName) LIKE :searchC OR LOWER(u.adUserId) LIKE :searchC OR LOWER(u.userName) LIKE :searchC OR LOWER(u.emailId) LIKE :searchC OR LOWER(u.mobileNumber) LIKE :searchC OR LOWER(u.remark) LIKE :searchC) ");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < conditionList.size(); i++) {
			if (i == 0)
				sb.append(" WHERE ");
			sb.append(conditionList.get(i));
			if (i != (conditionList.size() - 1))
				sb.append(" AND ");
		}
		StringBuilder joinString = new StringBuilder();
		if (sort_by != null) {
			if (sort_by.equalsIgnoreCase("department_name")) {
				joinString.append(" JOIN u.departments d ");
				sort_by = " u.departments.departmentName ";
			}
			sort = " ORDER BY " + sort_by + " " + order;
		}

		List<User> users = null;
		try {
			logger.info("inside the getAllUsers() method");
			Query query = entityManager
					.createQuery("select u from User u" + joinString.toString() + sb.toString() + sort);
			if (searchC != null)
				query.setParameter("searchC", "%" + searchC + "%");
			if (userSearchCriteria.getUserId() != null) {
				query.setParameter("userId", "%" + userSearchCriteria.getUserId() + "%");
			}
			if (userSearchCriteria.getUserName() != null) {
				query.setParameter("userName", "%" + userSearchCriteria.getUserName() + "%");
			}
			if (userSearchCriteria.getDepartment() != null) {
				query.setParameter("departmentName", "%" + userSearchCriteria.getDepartment() + "%");
			}
			if (userSearchCriteria.getEmailId() != null) {
				query.setParameter("emailId", "%" + userSearchCriteria.getEmailId() + "%");
			}
			if (userSearchCriteria.getMobileNumber() != null) {
				query.setParameter("mobileNumber", "%" + userSearchCriteria.getMobileNumber() + "%");
			}
			users = query.setFirstResult(offset).setMaxResults(maxResult).getResultList();

		} catch (HibernateException e) {
			logger.error("No user found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return users;
	}

	@Override
	public long getAllUsersCount(String sort_by, String searchText, UserSearchCriteria userSearchCriteria) {
		StringBuilder searchCriteria = new StringBuilder();
		long usersCount = 0;

		List<String> conditionList = new ArrayList<>();
		if (userSearchCriteria.getUserId() != null) {
			conditionList.add(" LOWER(u.adUserId) LIKE :userId ");
		}
		if (userSearchCriteria.getUserName() != null) {
			conditionList.add(" LOWER(u.userName) LIKE :userName ");
		}
		if (userSearchCriteria.getDepartment() != null) {
			conditionList.add(" LOWER(u.departments.departmentName) LIKE :departmentName ");
		}
		if (userSearchCriteria.getEmailId() != null) {
			conditionList.add(" LOWER(u.emailId) LIKE :emailId ");
		}
		if (userSearchCriteria.getMobileNumber() != null) {
			conditionList.add(" LOWER(u.mobileNumber) LIKE :mobileNumber ");
		}

		if (userSearchCriteria.getGroupAdmin() != null) {
			if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.groupAdmin = true ");
			} else if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("No")) {
				conditionList.add(" u.groupAdmin = false ");
			} else if (userSearchCriteria.getGroupAdmin().equalsIgnoreCase("All")) {
				conditionList.add(" (u.groupAdmin = true OR u.groupAdmin = false) ");
			}
		}
		if (userSearchCriteria.getUserAdmin() != null) {
			if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.userAdmin = true ");
			} else if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("No")) {
				conditionList.add(" u.userAdmin = false ");
			} else if (userSearchCriteria.getUserAdmin().equalsIgnoreCase("All")) {
				conditionList.add(" (u.userAdmin = true OR u.userAdmin = false) ");
			}
		}
		if (userSearchCriteria.getActive() != null) {
			if (userSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" u.active = true ");
			} else if (userSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" u.active = false ");
			} else if (userSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (u.active = true OR u.active = false) ");
			}
		}
		if (!StringUtils.isEmpty(searchText)) {
			if ("yes".contains(searchText.toLowerCase()) || searchText.equalsIgnoreCase("Yes"))
				searchCriteria.append(" u.groupAdmin = 1 OR u.userAdmin = 1 ");
			else if ("no".contains(searchText.toLowerCase()) || searchText.equalsIgnoreCase("No"))
				searchCriteria.append(" u.groupAdmin = 0 OR u.userAdmin = 0 ");
			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			conditionList.add(" (" + searchCriteria.toString()
					+ "LOWER(u.departments.departmentName) LIKE :searchC OR LOWER(u.adUserId) LIKE :searchC OR LOWER(u.userName) LIKE :searchC OR LOWER(u.emailId) LIKE :searchC OR LOWER(u.mobileNumber) LIKE :searchC OR LOWER(u.remark) LIKE :searchC) ");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < conditionList.size(); i++) {
			if (i == 0)
				sb.append(" WHERE ");
			sb.append(conditionList.get(i));
			if (i != (conditionList.size() - 1))
				sb.append(" AND ");
		}

		try {
			logger.info("inside the getAllUsersCount() method");
			Query query = entityManager.createQuery("select COUNT(u) from User u" + sb.toString());
			if (searchText != null)
				query.setParameter("searchC", "%" + searchText + "%");
			if (userSearchCriteria.getUserId() != null) {
				query.setParameter("userId", "%" + userSearchCriteria.getUserId() + "%");
			}
			if (userSearchCriteria.getUserName() != null) {
				query.setParameter("userName", "%" + userSearchCriteria.getUserName() + "%");
			}
			if (userSearchCriteria.getDepartment() != null) {
				query.setParameter("departmentName", "%" + userSearchCriteria.getDepartment() + "%");
			}
			if (userSearchCriteria.getEmailId() != null) {
				query.setParameter("emailId", "%" + userSearchCriteria.getEmailId() + "%");
			}
			if (userSearchCriteria.getMobileNumber() != null) {
				query.setParameter("mobileNumber", "%" + userSearchCriteria.getMobileNumber() + "%");
			}
			usersCount = (long) query.getSingleResult();

		} catch (HibernateException e) {
			logger.error("No user found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return usersCount;
	}

	@Override
	public void updateUser(User user, String userName) {
		try {
			logger.info("inside the updateUser() method");
			User createdUser = null;
			try {
				createdUser = getUserByName(userName);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("error in updating username: " + userName + ", " + e);
			}

			User savedUser = getUserById(user.getUserId());
			user.setCreatedAt(savedUser.getCreatedAt());
			user.setCreatedById(savedUser.getCreatedById());

			if (createdUser != null) {
				user.setLastModifiedById(createdUser.getUserId());
			}
			user.setDepartments(findDepartment(user.getDepartments().getDepartmentId()));

			entityManager.merge(user);
			entityManager.flush();
			entityManager.close();
		} catch (Exception e) {
			logger.error("User not updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void deleteUser(int id) {
		try {
			logger.info("inside the deleteUser() method and deleting detail of user id : " + id);
			User s = entityManager.find(User.class, id);
			entityManager.remove(s);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("User is not deleted : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void createUserGroup(GrouproleUser groupRoleUser) {
		try {
			logger.info("inside the createUser() method");
			entityManager.persist(groupRoleUser);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("User not created due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public User getUserByName(String userName) {
		User user = null;
		List<User> users = new ArrayList<>();
		try{
			users = entityManager.createQuery("select u from User u where u.adUserId=:userName")
				.setParameter("userName", userName).getResultList();
		}catch(HibernateException e){
			logger.error("User not found : " + e);
			entityManager.flush();
			entityManager.close();
		}

		if (!(CollectionUtils.isEmpty(users))) {
			user = users.get(0);
		} else {
			throw new UsernameNotFoundException("user not found");
		}
		return user;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int getUserIdByName(String userName) {
		int user = 0;
		List<Integer> users = entityManager
				.createNativeQuery("select u.user_id from user u where u.ad_user_id=:userName")
				.setParameter("userName", userName).getResultList();

		if (!(CollectionUtils.isEmpty(users))) {
			user = users.get(0);
		} else {
			throw new UsernameNotFoundException("user not found");
		}
		return user;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean getUserByUserId(String userId) {
		boolean user = false;
		List<User> users = entityManager.createQuery("select u from User u where u.adUserId=:userId")
				.setParameter("userId", userId).getResultList();

		if (!(CollectionUtils.isEmpty(users))) {
			user = true;
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> loadEmails(String emailId) {
		List<String> users = null;
		String searchCriteria = "";

		if (!StringUtils.isEmpty(emailId))
			searchCriteria = " where u.email_id LIKE('%" + emailId.toLowerCase() + "%') and u.active='1'";
		else {
			searchCriteria = " where u.active='1'";
		}
		try {

			users = entityManager.createNativeQuery("select u.email_id from user u" + searchCriteria).getResultList();
		} catch (Exception e) {
			logger.error("error in loading emails : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return users;
	}

	@Override
	public boolean getUserByUserName(String emailId) {
		boolean user = false;
		long userscount = (long) entityManager.createQuery("select count(u) from User u where u.emailId=:emailId")
				.setParameter("emailId", emailId).getSingleResult();

		if (userscount != 0)
			user = true;
		else
			user = false;
		return user;

	}

	@Override
	public void updateLoginDate(int userId) {
		try {
			User user = getUserById(userId);
			user.setLastSuccessfullLoginDate(user.getCurrentLoginDate());
			user.setCurrentLoginDate(DateTimeUtil.now());
			entityManager.merge(user);
			entityManager.flush();
			entityManager.close();
		} catch (Exception e) {
			logger.error("Error in updating login data " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void updateUnSuccessLoginDate(int userId) {
		try {
			User user = getUserById(userId);
			if (user != null) {
				user.setLastUnSuccessfullLoginDate(DateTimeUtil.now());
				entityManager.merge(user);
				entityManager.flush();
				entityManager.close();
			}
		} catch (Exception e) {
			logger.error("error updating in success login data: " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public String getUserName(Integer createdById) {
		return (String) entityManager.createNativeQuery("select u.user_name from user u where u.user_id=:createdById")
				.setParameter("createdById", createdById).getSingleResult();
	}

	@Override
	public boolean getActiveStatusOfUser(int userId) {
		return (boolean) entityManager.createNativeQuery("SELECT u.active FROM user u WHERE u.user_id = :userId")
				.setParameter("userId", userId).getSingleResult();
	}

	@Override
	public UserPopUp isUserEligibleForInactivation(int userId) {
		// String message = "";
		UserPopUp userPopUp = null;
		BigInteger isUserEligibleForInactivation = BigInteger.ONE;

		isUserEligibleForInactivation = (BigInteger) entityManager
				.createNativeQuery(
						"SELECT (SELECT COUNT(u.user_id) FROM user u WHERE (u.group_admin OR u.user_admin) AND user_id = :userId) = 0")
				.setParameter("userId", userId).getSingleResult();

		if (isUserEligibleForInactivation.equals(BigInteger.ZERO)) {
			/*
			 * message
			 * ="This user cannot be inactivated because he/she is the Overall Group Administrator/User Administrator "
			 * + + groupNames + " Contact IT Support "+ supprtMail
			 * +" to inactivate this user";
			 */
			userPopUp = new UserPopUp("Overall Group Administrator/User Administrator", "");
			return userPopUp;
		}

		isUserEligibleForInactivation = (BigInteger) entityManager
				.createNativeQuery("SELECT (SELECT COUNT(gu.group_user_id) FROM group_user gu "
						+ "INNER JOIN group_role gr ON gr.group_role_id = gu.group_role_id "
						+ "INNER JOIN groups g ON gu.group_id = g.group_id "
						+ "WHERE gr.role ='Group Admin' AND g.active = TRUE AND gu.user_id = :userId) = 0")
				.setParameter("userId", userId).getSingleResult();

		if (isUserEligibleForInactivation.equals(BigInteger.ZERO)) {
			String groupNames = getGroupAndRoleName(userId, true, "");
			/*
			 * message
			 * ="This user cannot be inactivated because he/she is the Group Administrator of "
			 * + groupNames + " Contact IT Support "+ supprtMail
			 * +" to inactivate this user";
			 */
			userPopUp = new UserPopUp("Group Administrator", groupNames);
			return userPopUp;
		}
		isUserEligibleForInactivation = (BigInteger) entityManager
				.createNativeQuery(
						"SELECT (SELECT COUNT(c.contract_id) from contract c WHERE c.officer_in_charge_id = :userId) = 0")
				.setParameter("userId", userId).getSingleResult();

		if (isUserEligibleForInactivation.equals(BigInteger.ZERO)) {
			/*
			 * message
			 * ="This user cannot be inactivated because he/she is the Officer-In-Charge of "
			 * + groupNames + " Contact IT Support "+ supprtMail
			 * +" to inactivate this user";
			 */
			userPopUp = new UserPopUp("Officer-In-Charge", getGroupAndRoleName(userId, false, ""));
			return userPopUp;
		}
		isUserEligibleForInactivation = (BigInteger) entityManager
				.createNativeQuery("SELECT (SELECT COUNT(gu.group_user_id) FROM group_user gu "
						+ "INNER JOIN group_role gr ON gr.group_role_id = gu.group_role_id "
						+ "INNER JOIN groups g ON gu.group_id = g.group_id "
						+ "WHERE gr.role !='Group Admin' AND g.active = TRUE AND gu.user_id = :userId) = 0")
				.setParameter("userId", userId).getSingleResult();

		if (isUserEligibleForInactivation.equals(BigInteger.ZERO)) {
			/*
			 * message =" This user is User in " + groupNames +
			 * " Are you sure to inactivate this user?";
			 */
			String groupNames = getGroupAndRoleName(userId, true, "");
			userPopUp = new UserPopUp("Other Role", groupNames);
			return userPopUp;
		}
		return userPopUp;
	}

	/**
	 * @param userId
	 * @param roleNameRequired
	 * @param role
	 * @return
	 */
	private String getGroupAndRoleName(int userId, boolean roleNameRequired, String role) {
		List<Object[]> objectList = null;
		if (!StringUtils.isEmpty(role))
			objectList = roleNameList(userId);
		else
			objectList = roleNameWithoutGroupAdminList(userId);
		String groupNames = "";
		int count = 0;
		for (Object[] objArray : objectList) {
			if (count != 0) {
				groupNames += ", ";
			}
			// if(count ==0){
			groupNames += "  [";
			// }
			groupNames += (String) objArray[1] + "]";
			if (roleNameRequired) {
				groupNames += " in [" + (String) objArray[0] + "]";
			}

			count++;
		}
		return groupNames;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> roleNameList(int userId) {
		List<Object[]> objectList = entityManager.createNativeQuery("SELECT g.name,gr.role FROM  group_user gu "
				// +" join group_user gu on gu.user_id =u.user_id"
				+ "  join groups g on g.group_id =gu.group_id"
				+ "  join group_role gr on gr.group_role_id = gu.group_role_id"
				// +" join group_role_has_action_type gra on gra.group_role_id =
				// gr.group_role_id"
				// +" join action_type a on a.action_type_id =
				// gra.action_type_id"
				+ "  where gr.role ='Group Admin'  and gu.user_id =:userId" + "  group by g.name")
				.setParameter("userId", userId).getResultList();
		return objectList;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> roleNameWithoutGroupAdminList(int userId) {
		List<Object[]> objectList = entityManager.createNativeQuery("SELECT g.name,gr.role FROM  group_user gu "
				// +" join group_user gu on gu.user_id =u.user_id"
				+ "  join groups g on g.group_id =gu.group_id"
				+ "  join group_role gr on gr.group_role_id = gu.group_role_id"
				// +" join group_role_has_action_type gra on gra.group_role_id =
				// gr.group_role_id"
				// +" join action_type a on a.action_type_id =
				// gra.action_type_id"
				+ "  where gu.user_id =:userId" + "  group by g.name").setParameter("userId", userId).getResultList();
		return objectList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void unSuccessfullLoginDate(String userName) {
		try {
			List<User> users = entityManager.createQuery("select u from User u where u.adUserId=:userId")
					.setParameter("userId", userName).getResultList();

			if (!(CollectionUtils.isEmpty(users))) {
				User user = users.get(0);
				user.setLastUnSuccessfullLoginDate(DateTimeUtil.now());
				entityManager.merge(user);
			}
			entityManager.flush();
			entityManager.close();
		} catch (Exception e) {
			logger.error("Error in unSuccessfull Login Date  " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserResponse> userAdvanceSearch(String search, String column) {
		List<UserResponse> userRes = null;
		String searchColumn = "";

		try {
			if ("adUserId".equalsIgnoreCase(column))
				searchColumn = " LOWER(u.adUserId) like '%" + search + "%'";
			if ("emailId".equalsIgnoreCase(column))
				searchColumn = " LOWER(u.emailId) like '%" + search + "%'";
			if ("mobileNumber".equalsIgnoreCase(column))
				searchColumn = " LOWER(u.mobileNumber) like '%" + search + "%'";
			if ("userName".equalsIgnoreCase(column))
				searchColumn = " LOWER(u.userName) like '%" + search + "%'";

			userRes = entityManager.createQuery("select u from User u where " + searchColumn).getResultList();
		} catch (HibernateException e) {
			logger.error("Error fetching advance search data " + e);
			entityManager.flush();
			entityManager.close();
		}
		return userRes;
	}
}