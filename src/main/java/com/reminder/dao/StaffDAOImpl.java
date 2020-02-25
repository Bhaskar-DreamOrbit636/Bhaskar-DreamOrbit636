package com.reminder.dao;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.reminder.exception.DuplicateException;
import com.reminder.model.Department;
import com.reminder.model.Groups;
import com.reminder.model.RecordType;
import com.reminder.model.Reminder;
import com.reminder.model.Section;
import com.reminder.model.Staff;
import com.reminder.model.StaffRecord;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.StaffSearchCriteria;
import com.reminder.response.model.StaffResponse;
import com.reminder.utils.DateTimeUtil;
import com.reminder.utils.NRICSecurity;

@Repository
@PropertySource("classpath:/application.properties")
public class StaffDAOImpl implements StaffDAO {
	private Logger logger = Logger.getLogger(StaffDAOImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	GroupDAO groupDAO;
	
	@Autowired
	FileDAO fileDao;
	
	@Autowired
	DepartmentDAO deptDAO;
	
	@Autowired
	ReminderDAO reminderDAO;

	@Override
	public void createStaff(Staff staff) throws DuplicateException {
		String msg="";
		try {
			logger.info("inside the createStaff() method");
			/*
			 * Groups group = groupDAO.getGroupById(staff.getGroupId());
			 * staff.setUserGroupId(group);
			 */
			
			/**String nric = staff.getNRIC_FIN();
			if (nric != null) {
				String sr = getStaffByNRICheckDup(nric);
				if (sr.equalsIgnoreCase("NRICDuplicate")) {
					msg  = "NRIC/FIN already exists";
					throw new DuplicateException(msg);
				} else {
					String encrypt = new NRICSecurity().encrypt(nric);
					staff.setNRIC_FIN(encrypt);
				}**/
				String dob = staff.getDateOfBirth();
				if (dob != null) {
					String dobencrypt = new NRICSecurity().encrypt(dob);
					staff.setDateOfBirth(dobencrypt);
				}
				if (staff.getDepartments() != null && staff.getDepartments().getDepartmentId() == 0) {
					staff.setDepartments(null);
				}
				if (staff.getSections() != null && staff.getSections().getSectionId() == 0) {
					staff.setSections(null);
				}
				if (staffCodeCheckDup(staff.getStaffId()).equalsIgnoreCase("StaffCodeDuplicate")) {
					msg = "Staff Code already exists";
					throw new DuplicateException(msg);
				}
				Staff s = entityManager.merge(staff);
				logger.info(" Staff inserted "+s);
				
				entityManager.close();
			
		} catch (DuplicateException | HibernateException e) {
			logger.error("Staff not created due to error : " + e.getMessage());
			entityManager.flush();
			entityManager.close();
			throw new DuplicateException(msg);
		} catch(Exception e){
			logger.error("Staff not created due to error : " + e.getMessage());
		}
	}

	private String staffCodeCheckDup(String staffCode) {
		@SuppressWarnings("unchecked")
		List<Staff> staff = entityManager.createQuery("select n from Staff n ").getResultList();
		for (Staff st : staff) {
			String code = "";
			if (st != null) {
				code = st.getStaffId();
				if (code.equalsIgnoreCase(staffCode)) {
					return "StaffCodeDuplicate";
				}
			}
		}
		return "New";
	}

	@Override
	public StaffResponse getStaffById(int staffId) {
		Staff staff = null;
		Groups group = null;
		StaffResponse sr = new StaffResponse();
		try {
			logger.info("fetching Staff details of StaffId :" + staffId);
			staff = entityManager.find(Staff.class, staffId);
			//String nric = "";
			String dob = "";
			if (staff != null) {
				///nric = staff.getNRIC_FIN();
				dob = staff.getDateOfBirth();
				// group = staff.getUserGroupId();
			}
			/*
			 * if(group != null){ int groupId = group.getGroupId(); String
			 * groupName = group.getGroupName(); sr.setGroupId(groupId);
			 * sr.setGroupName(groupName); }
			 */
			/*if (nric != null) {
				// String output = getNric(nric);
				String output = new NRICSecurity().decrypt(nric);
				sr.setNric_fin(output);
			}*/
			if (dob != null) {
				// String output = getNric(dob);
				String output = new NRICSecurity().decrypt(dob);
				sr.setDOB(output);
			}
			sr.setStaff(staff);
		} catch (HibernateException e) {
			logger.error("No Staff fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return sr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getStaffByName(String staffName) {
		List<Staff> staff = null;
		List<StaffResponse> listSr = new ArrayList<>();
		StaffResponse sr = null;
		try {
			logger.info("fetching Staff details of StaffName :" + staffName);
			staff = entityManager
					.createQuery("select n from Staff n where lower(n.staffName) LIKE('%" + staffName + "%') and n.active=true")
					.getResultList();
			for (Staff st : staff) {
				sr = new StaffResponse();
				//String nric = "";
				if (st != null) {
					//nric = st.getNRIC_FIN();
					/*if (nric != null) {
						String output = new NRICSecurity().decrypt(nric);
						sr.setNric_fin(output);
					}*/
					String dob = st.getDateOfBirth();
					if (dob != null) {
						String output = new NRICSecurity().decrypt(dob);
						sr.setDOB(output);
					}
					sr.setStaff(st);
					listSr.add(sr);
				}
			}
		} catch (NoResultException e) {
			staff = null;
			entityManager.flush();
			entityManager.close();
			logger.error("No Staff fetched due to error : " + e);
		}
		return listSr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getStaffByPsId(String staffId) {
		List<Staff> staff = null;
		List<StaffResponse> listSr = new ArrayList<>();
		StaffResponse sr = null;
		try {

			logger.info("fetching Staff details of StaffName :" + staffId);
			staff = entityManager.createQuery("select n from Staff n where lower(n.staffId) LIKE('%" + staffId + "%') and n.active=true")
					.getResultList();
			for (Staff st : staff) {
				sr = new StaffResponse();
				//String nric = "";
				if (st != null) {
					/*nric = st.getNRIC_FIN();
					if (nric != null) {
						String output = new NRICSecurity().decrypt(nric);
						sr.setNric_fin(output);
					}*/
					String dob = st.getDateOfBirth();
					if (dob != null) {
						String output = new NRICSecurity().decrypt(dob);
						sr.setDOB(output);
					}
					sr.setStaff(st);
					listSr.add(sr);
				}
			}
		} catch (NoResultException e) {
			staff = null;
			logger.error("No Staff fetched due to error : " + e);
			entityManager.close();
		}
		return listSr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getAllStaffs(String sort_by, String order, String searchC, Integer limit,
			Integer page_no, int user_id) {

		StaffResponse sr = null;
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
		List<StaffResponse> listnric = null;
		StringBuffer sb = new StringBuffer();
		String ids="";
		List<Department> dept = deptDAO.getAllDepartments(user_id);
		if ((searchC) != null) {
			listnric = getStaffByNRIC(searchC, dept.get(0).getDepartmentId());
			if(!(listnric.isEmpty())){
			for(StaffResponse s: listnric){
				int sid = s.getStaff().getStaff_Id();
				sb.append(sid);
				sb.append(",");
			}
			 ids = sb.toString().substring(0, sb.toString().length()-1);
			 searchCriteria = " WHERE LOWER(d.departmentName) LIKE ('%" + dept.get(0).getDepartmentName() + "%') and (LOWER(s.staffId) LIKE('%" + searchC + "%')  OR LOWER(s.staffName) LIKE('%"
						+ searchC + "%') OR LOWER(d.departmentName) LIKE ('%" + searchC + "%')) " ;//+ " or s.staff_Id in ("+ids+")) ";
			}else{
			searchCriteria = " WHERE LOWER(d.departmentName) LIKE ('%" + dept.get(0).getDepartmentName() + "%') and (LOWER(s.staffId) LIKE('%" + searchC + "%')  OR LOWER(s.staffName) LIKE('%"
					+ searchC + "%') OR LOWER(d.departmentName) LIKE ('%" + searchC + "%')) ";
			}
		} else{
			searchCriteria = " WHERE LOWER(d.departmentName) LIKE ('%" + dept.get(0).getDepartmentName() + "%')";
			//searchCriteria=" ";
		}
		System.out.println("********searchCriteria*******" + searchCriteria);
		if ((sort_by) != null) {
			if (sort_by.equalsIgnoreCase("departmentName"))
				sort_by = "departments.departmentName";
			if(sort_by.equalsIgnoreCase("section"))
				sort_by = "sections.sectionName";
			sort = " ORDER BY s." + sort_by + " " + order ;
		} else
			sort = "";
		Long count = (Long) entityManager.createQuery("select count(s.staff_Id) from Staff s LEFT OUTER JOIN s.departments d LEFT OUTER JOIN s.sections sec" + searchCriteria)
				.getSingleResult();
		List<Staff> staffs = null;
		try {
			logger.info("inside the getAllStaffs() method");
			Query query = entityManager.createQuery("select s from Staff s LEFT OUTER JOIN s.departments d LEFT OUTER JOIN s.sections sec" + searchCriteria + sort);
			/*
			 * if ((searchC) != null) { query.setParameter("searchC", "%" +
			 * searchC + "%"); }
			 */
			staffs = query.setFirstResult(offset).setMaxResults(maxResult).getResultList();
		} catch (HibernateException e) {
			logger.error("No Staff found : " + e);
		}
		List<StaffResponse> liststaff = null;
		/*if (listnric != null)
			liststaff = new ArrayList<>(listnric);
		else*/
			liststaff = new ArrayList<>();
		for (Staff st : staffs) {
			sr = new StaffResponse();
			/*
			 * Groups group = st.getUserGroupId(); if(group != null){ int
			 * groupId = group.getGroupId(); String groupName =
			 * group.getGroupName(); sr.setGroupId(groupId);
			 * sr.setGroupName(groupName); }
			 */
			
			/*String nric = st.getNRIC_FIN();
			if (nric != null) {
				String output = getNric(nric);
				sr.setNric_fin(output);
			}*/
			String dob = st.getDateOfBirth();
			if (dob != null) {
				String output = decryptString(dob);
				sr.setDOB(output);
			}
			sr.setStaff(st);
			liststaff.add(sr);
			sr.setCount(count);
		}
		// sr.setListStaff(staffs);
		return liststaff;
	}
	
	@SuppressWarnings("unchecked")
	public List<StaffResponse> getStaffByNRIC(String nric, int deptId) {
		logger.info("fetching Staff details for Nric:" + nric);
		List<Staff> staff = null;
		List<StaffResponse> lsr = new ArrayList<>();
		StaffResponse sr = null;
		try {
			staff = entityManager.createQuery("select n from Staff n where n.departments.departmentId =:deptId ")
					.setParameter("deptId", deptId).getResultList();
			for (Staff st : staff) {
				//String encNric = "";
				if (st != null) {
					//String decrypt = new NRICSecurity().decrypt(st.getNRIC_FIN());
					//encNric = getNric(st.getNRIC_FIN());
					
						sr = new StaffResponse();
						//sr.setNric_fin(encNric);
						//sr.setDecriptedNric(decrypt);
						String dob = st.getDateOfBirth();
						if (dob != null) {
							String output = decryptString(dob);
							sr.setDOB(output);
						}
						sr.setStaff(st);
						lsr.add(sr);
					
				}
			}

		} catch (NoResultException e) {
			staff = null;
			logger.error("No Staff fetched  : " + e);
			entityManager.close();
		}
		return lsr;

	}
	
	@Override
	public void updateStaff(Staff staff) throws DuplicateException {
		logger.info("inside the updateStaff() method");
		try {
			Staff s;
			int id = staff.getStaff_Id();
			s = entityManager.find(Staff.class, id);
			//String nric = staff.getNRIC_FIN();
		/*	if (nric != null) {
				String encrypt = new NRICSecurity().encrypt(nric);
				staff.setNRIC_FIN(encrypt);
			} else {
				String encrypt = s.getNRIC_FIN();
				staff.setNRIC_FIN(encrypt);
			}*/
			String dob = staff.getDateOfBirth();
			if (dob != null) {
				String dobencrypt = new NRICSecurity().encrypt(dob);
				staff.setDateOfBirth(dobencrypt);
			} else {
				String dobencrypt = s.getDateOfBirth();
				staff.setDateOfBirth(dobencrypt);
			}
			Section section = staff.getSections();
			if (section == null) {
				Section sec = s.getSections();
				staff.setSections(sec);
			}
			staff.setCreatedById(s.getCreatedById());
			staff.setCreatedAt(s.getCreatedAt());
			/*
			 * Groups group = groupDAO.getGroupById(staff.getGroupId());
			 * staff.setUserGroupId(group);
			 */
			entityManager.merge(staff);
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Staff not updated due to error : " + e);
			entityManager.close();
		}
	}

	@Override
	public void deleteStaff(int staffId) {
		try {
			logger.info("inside the deleteStaff() method and deleting detail of StaffId : " + staffId);
			Staff c = entityManager.find(Staff.class, staffId);
			entityManager.remove(c);
			entityManager.close();
		} catch (ConstraintViolationException e) {
			logger.error("Staff details is not deleted, error : " + e);
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffRecord> getStaffRecordById(int staffId) {
		List<StaffRecord> listOfSR = new ArrayList<>();
		try {
			logger.info("fetching StaffFRecord details of StaffId :" + staffId);
			listOfSR = entityManager.createQuery("select sr from StaffRecord sr where sr.staffRecordId = :staffId")
					.setParameter("staffId", staffId).getResultList();

		} catch (HibernateException e) {
			logger.error("No Staff fetched due to error : " + e);
		}
		return listOfSR;
	}

	@Override
	public void createRecordType(RecordType record) {
		try {
			logger.info("inside the createRecordType() method");

			entityManager.persist(record);
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("Recordtype not created due to error : " + e);
			entityManager.close();
		}
	}

	@Override
	public RecordType getRecord(int recordTypeId) {
		RecordType recordtype = null;
		try {
			logger.info("fetching reocrd type details of recordTypeId :" + recordTypeId);
			recordtype = entityManager.find(RecordType.class, recordTypeId);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("No Staff fetched due to error : " + e);
			entityManager.close();
		}
		return recordtype;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StaffResponse getAllRecord(String sort_by, String order, String searchC, Integer limit, Integer page_no,
			boolean active) {

		StaffResponse sr = new StaffResponse();
		String searchCriteria = "";
		String sort = "";
		int offset = 0;
		int maxResult = 100;

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}
		if ((searchC) != null)
			searchCriteria = " WHERE LOWER(a.recordType) LIKE('%" + searchC + "%') OR a.firstReminderDay LIKE('%"
					+ searchC + "%') OR a.secondReminderDay LIKE('%" + searchC + "%') "
					+ "OR a.thirdReminderDay LIKE('%" + searchC + "%')";

		if (active == true || (searchC != null && searchC.equalsIgnoreCase("Yes"))) {
			searchCriteria = " where active=true";
		} else if (searchC != null && searchC.equalsIgnoreCase("No")) {
			searchCriteria = " where active=false";
		}

		if ((sort_by) != null)
			sort = " ORDER BY " + sort_by + " " + order;

		List<RecordType> recordType = null;
		Long count = (Long) entityManager.createQuery("select count(a.recordTypeId) from RecordType a" + searchCriteria)
				.getSingleResult();
		try {
			logger.info("inside the getAllRecord() method");
			recordType = entityManager.createQuery("select a from RecordType a" + searchCriteria + sort)
					.setFirstResult(offset).setMaxResults(maxResult).getResultList();

		} catch (HibernateException e) {
			logger.error("No Record Type found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		sr.setCount(count);
		sr.setRecordType(recordType);
		return sr;
	}

	@Override
	public void updateRecord(RecordType record) {
		try {
			logger.info("inside the updateRecord() method");
			entityManager.merge(record);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("RecordType not updated due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public void deleteRecord(int recordTypeId) {
		try {
			logger.info("inside the deleteRecord() method and deleting detail of recordTypeId : " + recordTypeId);
			RecordType c = entityManager.find(RecordType.class, recordTypeId);
			entityManager.remove(c);
			entityManager.flush();
			entityManager.close();
		} catch (ConstraintViolationException e) {
			logger.error("RecordType details is not deleted, error : " + e);
			entityManager.close();
		}

	}

	// ------------StaffReminder---------------

	@Override
	public StaffRecord createStaffReminder(StaffRecord entity) {
		StaffRecord sr = null;
		try {
			logger.info("inside the createAssetReminder() method");
			sr = entityManager.merge(entity);
		} catch (HibernateException e) {
			logger.error("Asset not created due to error : " + e);
			entityManager.close();
		}
		return sr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getStaffReminder(String sort_by, String order, String searchC, Integer limit,
			Integer page_no, int userId) {
		StaffResponse response = null;
		List<StaffResponse> staffRes = new ArrayList<>();
		String searchCriteria = "";
		String sort = "";
		int offset = 0;
		int maxResult = 1000;

		List<Integer> reminderIds = getReminderIdByUser(userId);

		if (CollectionUtils.isEmpty(reminderIds)) {
			return staffRes;
		}

		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}
		if ((searchC) != null)
			searchCriteria = " WHERE a.reminder.reminderId in (:ids) and LOWER(staffDescription) LIKE('%"
					+ searchC.toLowerCase() + "%')";
		else
			searchCriteria = " WHERE a.reminder.reminderId in (:ids)";

		System.out.println("********searchCriteria*******" + searchCriteria);
		if ((sort_by) != null)
			sort = " ORDER BY " + sort_by + " " + order;

		List<StaffRecord> staafRec = null;
		try {
			logger.info("inside the getAllRecord() method");
			staafRec = entityManager.createQuery("select a from StaffRecord a" + searchCriteria + sort)
					.setFirstResult(offset).setMaxResults(maxResult).setParameter("ids", reminderIds).getResultList();
			Long count = (Long) entityManager.createQuery("select count(a) from StaffRecord a").getSingleResult();

			for (StaffRecord sr : staafRec) {
				response = new StaffResponse();
				response.setStaffRec(sr);
				Groups group = sr.getReminder().getUserGroupId();
				response.setGroupId(group.getGroupId());
				response.setGroupName(group.getGroupName());
				response.setCount(count);
				staffRes.add(response);
			}
		} catch (HibernateException e) {
			logger.error("No Record Type found : " + e);
			entityManager.close();
		}
		return staffRes;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getReminderIdByUser(int userId) {
		List<Integer> reminderIds = (List<Integer>) entityManager
				.createNativeQuery(
						"select reminder_id from  reminder where user_group_id in (select group_id from groups  where group_id in "
								+ "(SELECT group_id FROM group_user where user_id=:userId) and module_type_id in (select Module_Type_Id from  module_type where ModuleType='Assets'))")
				.setParameter("userId", userId).getResultList();

		return reminderIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getStaffByNRIC(String nric) {
		logger.info("fetching Staff details for Nric:" + nric);
		List<Staff> staff = null;
		List<StaffResponse> lsr = new ArrayList<>();
		StaffResponse sr = null;
		try {
			staff = entityManager.createQuery("select n from Staff n ")
					.getResultList();
			for (Staff st : staff) {
				//String encNric = "";
				if (st != null) {
					//String decrypt = new NRICSecurity().decrypt(st.getNRIC_FIN());
					//encNric = getNric(st.getNRIC_FIN());
					
						sr = new StaffResponse();
						//sr.setNric_fin(encNric);
						//sr.setDecriptedNric(decrypt);
						String dob = st.getDateOfBirth();
						if (dob != null) {
							String output = decryptString(dob);
							sr.setDOB(output);
						}
						sr.setStaff(st);
						lsr.add(sr);
					
				}
			}

		} catch (NoResultException e) {
			staff = null;
			logger.error("No Staff fetched  : " + e);
			entityManager.close();
		}
		return lsr;

	}

	@Override
	public StaffRecord updateStaffReminder(StaffRecord entity) {
		StaffRecord sr = null;
		try {
			logger.info("inside the updateRecord() method");
			sr = entityManager.merge(entity);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("RecordType not updated due to error : " + e);
			entityManager.close();
		}
		return sr;
	}

	@Override
	public StaffResponse getStaffreminderById(int staffRecId) {
		StaffRecord staffRec = null;
		StaffResponse response = new StaffResponse();
		try {
			staffRec = entityManager.find(StaffRecord.class, staffRecId);
			logger.info("fetching Staff reminder by Id :" + staffRecId);
			//String nric = staffRec.getStaffs().getNRIC_FIN();
			/*if (nric != null) {
				// String nricDecrypt = getNric(nric);
				String nricDecrypt = new NRICSecurity().decrypt(nric);
				response.setNric_fin(nricDecrypt);
			}*/
			response.setGroupId(staffRec.getReminder().getUserGroupId().getGroupId());
			response.setGroupName(staffRec.getReminder().getUserGroupId().getGroupName());
			response.setStaffRec(staffRec);
			String refrence = staffRec.getReferenceNumber();
			if (refrence != null) {
				// String refDecrypt = getNric(refrence);
				String refDecrypt = new NRICSecurity().decrypt(refrence);
				response.setRefrence(refDecrypt);
			}
			String dob = staffRec.getStaffs().getDateOfBirth();
			if (dob != null) {
				// String output = getNric(dob);
				String output = new NRICSecurity().decrypt(dob);
				response.setDOB(output);
			}
		} catch (HibernateException e) {
			logger.error("No Staff Reminder to fetched due to error : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return response;
	}

	@Override
	public StaffRecord getStaffreminderByIdLocal(int staffRecId) {
		StaffRecord staffRec = null;
		try {
			logger.info("fetching Staff reminder by Id :" + staffRecId);
			staffRec = entityManager.find(StaffRecord.class, staffRecId);
			entityManager.flush();
			entityManager.close();
		} catch (HibernateException e) {
			logger.error("No Staff Reminder to fetched due to error : " + e);
			entityManager.close();
		}
		return staffRec;
	}

	@Override
	@Transactional
	public void deleteStaffReminder(int staffId, User user) {
		try {
			logger.info("Deleting Staff reminder by Id :" + staffId);
			String reminder_id = "select reminder_id from staff_record where staff_record_id=" + staffId;
			int rid =  (int) entityManager.createNativeQuery(reminder_id).getSingleResult();// executeUpdate();

			String deleteQuery = "DELETE FROM staff_record where staff_record_id=" + staffId;
			entityManager.createNativeQuery(deleteQuery).executeUpdate();
			
			@SuppressWarnings("unchecked")
			List<Integer> fileId = entityManager.createNativeQuery("select file_id from files where reminder_id = "+rid)
					   .getResultList();

			for(int fid: fileId){
				fileDao.deleteFile(fid);
			}
			/*String deleteFiles = "delete from files where reminder_id = " + rid;
			entityManager.createNativeQuery(deleteFiles)
			 .executeUpdate();*/
			Reminder rem = reminderDAO.getReminderById(rid);
			rem.setLastModifiedById(user.getUserId());
			reminderDAO.updateReminder(rem);
			entityManager.flush();
			
			String sql = "DELETE FROM reminder where reminder_id = " + rid;
			entityManager.createNativeQuery(sql).executeUpdate();

		} catch (HibernateException e) {
			logger.error("No Staff Reminder to delete due to error : " + e);
			entityManager.close();
		}catch (Exception e) {
			logger.error("No Staff Reminder to delete due to error : " + e);
			entityManager.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Summary> getExpiryCalendar(int userId, Date date) {
		List<Summary> l = new ArrayList<>();
		List<Integer> userBasedStaffRecordIds = getUserBasedStaffRecordIds(userId);
		if (userBasedStaffRecordIds == null || userBasedStaffRecordIds.isEmpty())
			return l;
		BigInteger expiredCount = new BigInteger("-1");
		BigInteger thisMonthCount = new BigInteger("-1");
		BigInteger nextmonthcount = new BigInteger("-1");

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(date);
		String currentDate = simpleDateFormat.format(new Date());
		
		Summary as = new Summary();
		List<java.sql.Date> dat = new ArrayList<>();

		List<java.sql.Date> dates = entityManager
				.createNativeQuery("select r.effective_expiry_date from reminder r , staff_record a "
						+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) "
						+ " and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
				.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).setParameter("date", dateStr).getResultList();
		for (java.sql.Date d : dates) {
			dat.add(d);
		}
		as.setExpiryDate(dat);
		try {
			// Expired
			expiredCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , staff_record a "
							+ "where r.effective_expiry_date <= :date and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
					.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiredAsset(expiredCount);
			// Expiring this month
			thisMonthCount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , staff_record a "
							+ "where r.effective_expiry_date >= :date AND r.effective_expiry_date < DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY)"
							+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
					.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).setParameter("date", currentDate).getSingleResult();
			as.setExpiringThisMonth(thisMonthCount);
			// Expiring next month
			nextmonthcount = (BigInteger) entityManager
					.createNativeQuery("select count(r.reminder_id) from reminder r , staff_record a "
							+ "where r.effective_expiry_date >= DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY) AND r.effective_expiry_date < DATE_ADD(DATE_ADD(LAST_DAY(:date), INTERVAL 1 DAY), INTERVAL 1 MONTH) "
							+ "and r.reminder_id=a.reminder_id and r.active = 1 and a.staff_record_id in (:userBasedStaffRecordIds)")
					.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds).setParameter("date", dateStr).getSingleResult();
			as.setExpiringNextMonth(nextmonthcount);
		} catch (Exception e) {
			logger.error("Errror in getting Staff Reminder Summary : " + e);
			entityManager.close();
		}
		l.add(as);
		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> searchStaff(String sort_by, String order, Integer limit, Integer page_no,
			StaffSearchCriteria staffSearchCriteria, int userId, String searchC) {
		List<Integer> userBasedStaffRecordIds = getUserBasedStaffRecordIds(userId);
		if (userBasedStaffRecordIds == null || userBasedStaffRecordIds.isEmpty())
			return null;

		List<Integer> reminderIds = (List<Integer>) entityManager
				.createNativeQuery(" SELECT r.reminder_id FROM reminder r WHERE "
						+ "DATE_FORMAT(r.effective_expiry_date,'%d/%m/%Y') LIKE :searchC ")
				.setParameter("searchC", searchC).getResultList();

		if (staffSearchCriteria.getReferenceNumber() != null) {
			List<Integer> searchCriteriaBasedIds = getReminderIds(staffSearchCriteria.getReferenceNumber(),
					"reference_number");
			if (searchCriteriaBasedIds.size() > 0) {
				reminderIds.addAll(searchCriteriaBasedIds);
			}
			if (searchC != null && searchC.length() > 0) {
				List<Integer> searchBasedIds = getReminderIds(searchC, "reference_number");
				if (searchBasedIds.size() > 0) {
					reminderIds.addAll(searchBasedIds);
				}
			}
		}

	/*	if (staffSearchCriteria.getNricFin() != null) {
			List<Integer> searchCriteriaBasedIds = getReminderIds(staffSearchCriteria.getNricFin(), "NRIC_FIN");
			if (searchCriteriaBasedIds.size() > 0) {
				reminderIds.addAll(searchCriteriaBasedIds);
			}
			if (searchC != null && searchC.length() > 0) {
				List<Integer> searchBasedIds = getReminderIds(searchC, "NRIC_FIN");
				if (searchBasedIds.size() > 0) {
					reminderIds.addAll(searchBasedIds);
				}
			}
		}*/

		if ((staffSearchCriteria.getReferenceNumber() != null )
				&& (reminderIds == null || reminderIds.isEmpty()))
			return null;

		StringBuilder searchCriteria = new StringBuilder();
		String sort = "";
		int offset = 0;
		int maxResult = 10;
		StaffResponse staffResponse = new StaffResponse();
		if (limit != null && page_no != null) {
			maxResult = limit;
			offset = limit * (page_no - 1);
		} else if (limit != null && page_no == null) {
			maxResult = limit;
			offset = 0;
		}
		List<String> conditionList = new ArrayList<>();
		
		if (staffSearchCriteria.getExpiryDateFrom() != null) {
			conditionList.add(" r.effectiveExpiryDate >= :expiryDateFrom ");
		}

		if (staffSearchCriteria.getExpiryDateTo() != null) {
			conditionList.add(" r.effectiveExpiryDate <= :expiryDateTo ");
		}

		if (staffSearchCriteria.getRecordToMonitor() != null) {
			conditionList.add(" LOWER(rt.recordType) LIKE :recordToMonitor ");
		}

		if (staffSearchCriteria.getStaffCode() != null) {
			conditionList.add(" LOWER(s.staffId) LIKE :staffId ");
		}

		if (staffSearchCriteria.getDepartmentId() != 0) {
			conditionList.add(" s.departments.departmentId = :departmentId ");
		}

		if (staffSearchCriteria.getOfoSfo() != null) {
			/*if (staffSearchCriteria.getOfoSfo().equalsIgnoreCase("OFO")) {
				conditionList.add(" s.ofo = true ");
			} else if (staffSearchCriteria.getOfoSfo().equalsIgnoreCase("SFO")) {
				conditionList.add(" s.ofo = false ");
			} else if (staffSearchCriteria.getOfoSfo().equalsIgnoreCase("All")) {
				conditionList.add(" (s.ofo = true OR s.ofo = false) ");
			}*/
			conditionList.add("s.ofo= :ofosfo");
		}

		if (staffSearchCriteria.getSection() != null) {
			conditionList.add(" LOWER(s.sections.sectionName) LIKE :section ");
		}
		
	/*	if (staffSearchCriteria.getExpiryDateFrom() != null) {
			conditionList.add(" r.effectiveExpiryDate >= :expiryDateFrom ");
		}

		if (staffSearchCriteria.getExpiryDateTo() != null) {
			conditionList.add(" r.effectiveExpiryDate <= :expiryDateTo ");
		}*/

		if (staffSearchCriteria.getUserGroupId() != 0) {
			conditionList.add(" r.userGroupId.groupId = :userGroupId ");
		}

		if (staffSearchCriteria.getStaffName() != null) {
			conditionList.add(" LOWER(s.staffName) LIKE :staffName ");
		}

		if (staffSearchCriteria.getActive() != null) {
			if (staffSearchCriteria.getActive().equalsIgnoreCase("Yes")) {
				conditionList.add(" r.active = true ");
			} else if (staffSearchCriteria.getActive().equalsIgnoreCase("No")) {
				conditionList.add(" r.active = false ");
			} else if (staffSearchCriteria.getActive().equalsIgnoreCase("All")) {
				conditionList.add(" (r.active = true OR r.active = false) ");
			}
		}

		if (staffSearchCriteria.getStatus() != null) {
			if (staffSearchCriteria.getStatus().equalsIgnoreCase("Expiring")) {
				conditionList.add(" (r.firstReminderDate < CURDATE() AND r.effectiveExpiryDate > CURDATE())");
			} else if (staffSearchCriteria.getStatus().equalsIgnoreCase("Expired")) {
				conditionList.add(" r.effectiveExpiryDate < CURDATE() ");
			} else if (staffSearchCriteria.getStatus().equalsIgnoreCase("Outside")) {
				conditionList.add(" r.firstReminderDate > CURDATE() ");
			}
		}

		if (staffSearchCriteria.getLocalCrew() != null) {
			if (staffSearchCriteria.getLocalCrew().equalsIgnoreCase("Yes")) {
				conditionList.add(" s.localCrew = true ");
			} else if (staffSearchCriteria.getLocalCrew().equalsIgnoreCase("No")) {
				conditionList.add(" s.localCrew = false ");
			} else if (staffSearchCriteria.getLocalCrew().equalsIgnoreCase("All")) {
				conditionList.add(" (s.localCrew = true OR s.localCrew = false) ");
			}
		}

		if (!StringUtils.isEmpty(searchC)) {
			if ("yes".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("Yes"))
				searchCriteria.append(" s.active = 1 ");
			else if ("no".contains(searchC.toLowerCase()) || searchC.equalsIgnoreCase("No"))
				searchCriteria.append(" s.active = 0 ");

			if (searchCriteria.length() > 0) {
				searchCriteria.append(" OR ");
			}
			conditionList.add(" (" + searchCriteria.toString()
					+ " LOWER(r.userGroupId.groupName) LIKE :searchC OR LOWER(s.staffId) LIKE :searchC OR LOWER(s.staffName) LIKE :searchC) ");
		}
		if (reminderIds.size() > 0) {
			conditionList.add(" r.reminderId IN (:reminderIds) ");
		}
		StringBuilder sb = new StringBuilder();
		if (conditionList != null) {
			for (int i = 0; i < conditionList.size(); i++) {
				sb.append(" AND ");
				sb.append(conditionList.get(i));
			}
		}

		if ((sort_by) != null && !sort_by.isEmpty()) {
			if (sort_by.equalsIgnoreCase("staff_name")) {
				sort_by = "s.staffName";
			} else if (sort_by.equalsIgnoreCase("active")) {
				sort_by = "r." + sort_by;
			} else if (sort_by.equalsIgnoreCase("reminder_sent")) {
				sort_by = " CASE when (r.firstReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + ","
						+ " CASE when (r.secondReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + ","
						+ " CASE when (r.thirdReminderSentAt IS NOT NULL OR r.effectiveExpiryDate < CURDATE()) then 1"
						+ " END " + order + "," + " r.effectiveExpiryDate";
			} else if (sort_by.equalsIgnoreCase("name")) {
				sort_by = "r.userGroupId.groupName";
			} else if (sort_by.equalsIgnoreCase("department_name")) {
				sort_by = " s.departments.departmentName ";
			} else if (sort_by.equalsIgnoreCase("section_name")) {
				sort_by = " s.sections.sectionName ";
			} else if (sort_by.equalsIgnoreCase("recordType")) {
				sort_by = " rt.recordType ";
			}
			sort = " ORDER BY rt.recordType " + order + ", " + sort_by + " " + order;
		}

		List<StaffRecord> staffRecords = null;
		List<StaffResponse> staffResponses = null;
		try {
			logger.info("inside the searchStaff() method");
			Query query = entityManager.createQuery(
					"SELECT sr from StaffRecord sr JOIN sr.staffs s JOIN sr.recordType rt JOIN sr.reminder r WHERE sr.staffRecordId in (:userBasedStaffRecordIds) "
							+ sb.toString() + sort);

			Query countQuery = entityManager.createQuery(
					"SELECT COUNT(sr.staffRecordId) from StaffRecord sr JOIN sr.staffs s JOIN sr.recordType rt JOIN sr.reminder r WHERE sr.staffRecordId in (:userBasedStaffRecordIds) "
							+ sb.toString() + sort);
			
			if(staffSearchCriteria.getExpiryDateTo() == null & staffSearchCriteria.getExpiryDateFrom() != null){
				logger.info("searchStaff where Expiry Date to is null");
				String param =  sb.toString().substring(46,sb.toString().length()-1);
				 query = entityManager.createQuery(
						"SELECT sr from StaffRecord sr JOIN sr.staffs s JOIN sr.recordType rt JOIN sr.reminder r WHERE sr.staffRecordId in (:userBasedStaffRecordIds) "
						+ " and (r.effectiveExpiryDate is null OR r.effectiveExpiryDate >= :expiryDateFrom) " + param + sort);

				 countQuery = entityManager.createQuery(
						"SELECT COUNT(sr.staffRecordId) from StaffRecord sr JOIN sr.staffs s JOIN sr.recordType rt JOIN sr.reminder r WHERE sr.staffRecordId in (:userBasedStaffRecordIds) "
						 + " and (r.effectiveExpiryDate is null OR r.effectiveExpiryDate >= :expiryDateFrom) " + param + sort);
			}

			if (!StringUtils.isEmpty(searchC)) {
				query.setParameter("searchC", "%" + searchC + "%");
				countQuery.setParameter("searchC", "%" + searchC + "%");
			}
			if (reminderIds.size() > 0) {
				query.setParameter("reminderIds", reminderIds);
				countQuery.setParameter("reminderIds", reminderIds);
			}
			query.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds);
			countQuery.setParameter("userBasedStaffRecordIds", userBasedStaffRecordIds);

			if (staffSearchCriteria.getRecordToMonitor() != null) {
				query.setParameter("recordToMonitor", "%" + staffSearchCriteria.getRecordToMonitor() + "%");
				countQuery.setParameter("recordToMonitor", "%" + staffSearchCriteria.getRecordToMonitor() + "%");
			}

			if (staffSearchCriteria.getDepartmentId() != 0) {
				query.setParameter("departmentId", +staffSearchCriteria.getDepartmentId());
				countQuery.setParameter("departmentId", +staffSearchCriteria.getDepartmentId());
			}

			if (staffSearchCriteria.getSection() != null) {
				query.setParameter("section", "%" + staffSearchCriteria.getSection() + "%");
				countQuery.setParameter("section", "%" + staffSearchCriteria.getSection() + "%");
			}

			if (staffSearchCriteria.getUserGroupId() != 0) {
				query.setParameter("userGroupId", staffSearchCriteria.getUserGroupId());
				countQuery.setParameter("userGroupId", staffSearchCriteria.getUserGroupId());
			}

			if (staffSearchCriteria.getExpiryDateFrom() != null) {
				query.setParameter("expiryDateFrom",
						DateTimeUtil.convertToSGTWithDate(staffSearchCriteria.getExpiryDateFrom()));
				countQuery.setParameter("expiryDateFrom",
						DateTimeUtil.convertToSGTWithDate(staffSearchCriteria.getExpiryDateFrom()));
			}

			if (staffSearchCriteria.getExpiryDateTo() != null) {
				query.setParameter("expiryDateTo", DateTimeUtil.convertToSGTWithDate(staffSearchCriteria.getExpiryDateTo()));
				countQuery.setParameter("expiryDateTo",
						DateTimeUtil.convertToSGTWithDate(staffSearchCriteria.getExpiryDateTo()));
			}

			if (staffSearchCriteria.getStaffCode() != null) {
				query.setParameter("staffId", "%" + staffSearchCriteria.getStaffCode() + "%");
				countQuery.setParameter("staffId", "%" + staffSearchCriteria.getStaffCode() + "%");
			}

			if (staffSearchCriteria.getStaffName() != null) {
				query.setParameter("staffName", "%" + staffSearchCriteria.getStaffName() + "%");
				countQuery.setParameter("staffName", "%" + staffSearchCriteria.getStaffName() + "%");
			}
			
			if (staffSearchCriteria.getOfoSfo() != null) {
				query.setParameter("ofosfo", "%" + staffSearchCriteria.getOfoSfo() + "%");
				countQuery.setParameter("ofosfo", "%" + staffSearchCriteria.getOfoSfo() + "%");
			}

			if (maxResult != 0) {
				query.setFirstResult(offset).setMaxResults(maxResult);
			}
			staffRecords = query.getResultList();
			Map <Integer,List<String>> map = new HashMap <>();
			Set<Integer> setOfGroupId = new HashSet<>();
			Long count = (Long) countQuery.getSingleResult();
			for (StaffRecord staffRecord : staffRecords) {
				staffResponse = new StaffResponse();

				//String nric = staffRecord.getStaffs().getNRIC_FIN();
				/*if (nric != null) {
					String nricDecrypt = getNric(nric);
					staffResponse.setNric_fin(nricDecrypt);
					staffResponse.setDecriptedNric(new NRICSecurity().decrypt(nric));
				}*/
				String refrence = staffRecord.getReferenceNumber();
				if (refrence != null) {
					String refDecrypt = decryptString(refrence);
					staffResponse.setRefrence(refDecrypt);
					staffResponse.setDecriptedRefrenceNumber(new NRICSecurity().decrypt(refrence));
				}
				if (staffRecord.getStaffs().getDateOfBirth() != null)
					staffResponse.setDecriptedDOB(new NRICSecurity().decrypt(staffRecord.getStaffs().getDateOfBirth()));
				staffResponse.setCount(count);
				staffResponse.setGroupId(staffRecord.getReminder().getUserGroupId().getGroupId());
				setOfGroupId.add(staffRecord.getReminder().getUserGroupId().getGroupId());
				staffResponse.setGroupName(staffRecord.getReminder().getUserGroupId().getGroupName());
				staffResponse.setStaffRec(staffRecord);
				/*List<String> actions = groupDAO.getGroupRolesAction(userId, staffResponse.getGroupId());
				staffResponse.setActions(actions);*/

				if (staffResponses == null) {
					staffResponses = new ArrayList<>();
				}
				staffResponses.add(staffResponse);
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
			for(StaffResponse ssr: staffResponses){
				ssr.setActions(map.get(ssr.getStaffRec().getReminder().getUserGroupId().getGroupId()));
			}
			}
		} catch (HibernateException e) {
			logger.error("No Staff found : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return staffResponses;
	}

	public String decryptString(String string) {
		String decrypt = new NRICSecurity().decrypt(string);
		String uiNric = "XXXXXXXXX";

		String orgNric = "";
		if (decrypt.length() <= 5) {
			orgNric = decrypt;
		} else {
			orgNric = decrypt.substring(decrypt.length() - 5);
		}
		uiNric = uiNric.substring(orgNric.length());
		String output = uiNric + orgNric;
		return output;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getReminderIds(String text, String fieldName) {
		StringBuilder fields = new StringBuilder();
		List<Integer> reminderIds = new ArrayList<>();
		if (fieldName.equalsIgnoreCase("reference_number")) {
			fields.append(", sr.reference_number");
		} else if (fieldName.equalsIgnoreCase("NRIC_FIN")) {
			fields.append(", s.NRIC_FIN");
		}
		List<Object> result = entityManager
				.createNativeQuery("select r.reminder_id" + fields.toString()
						+ " from staff_record sr INNER JOIN staff s ON sr.staff_id = s.staff_id INNER JOIN reminder r ON sr.reminder_id = r.reminder_id")
				.getResultList();
		for (Object resultArray : result) {
			Object[] resultArrays = (Object[]) resultArray;
			int reminderId = (int) resultArrays[0];
			String encryptedText = (String) resultArrays[1];
			String decrpytedText = new NRICSecurity().decrypt(encryptedText);
			if (text.equalsIgnoreCase(decrpytedText))
				reminderIds.add(reminderId);
		}
		return reminderIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getUserBasedStaffRecordIds(int userId) {
		Query query = entityManager
				.createNativeQuery("SELECT DISTINCT " + " sr.staff_record_id FROM staff_record sr INNER JOIN "
						+ " reminder r ON sr.reminder_id = r.reminder_id INNER JOIN "
						+ " groups g ON g.module_type_id = (SELECT mt.module_type_id FROM module_type mt WHERE mt.ModuleType LIKE ('%Staff%')) "
						+ " AND g.group_id = r.user_group_id INNER JOIN "
						+ " group_user gu ON gu.group_id = g.group_id " + " AND g.active = 1 and gu.user_id = :userId ")
				.setParameter("userId", userId);
		List<Integer> userBasedStaffRecordIds = query.getResultList();
		return userBasedStaffRecordIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffResponse> getStaffByRefrenceNumber(String ref) {
		logger.info("fetching  Refrence number for advance search");
		List<StaffRecord> staff = null;
		List<StaffResponse> lsr = new ArrayList<>();
		StaffResponse sr = null;
		try {
			staff = entityManager.createQuery("select n from StaffRecord n ").getResultList();
			for (StaffRecord st : staff) {
				String encRef = "";
				if (st != null) {
					encRef = new NRICSecurity().decrypt(st.getReferenceNumber());
					if (encRef.contains(ref)) {
						sr = new StaffResponse();
						sr.setRefrence(encRef);
						// sr.setStaffRec(st);
						lsr.add(sr);
					}
				}
			}

		} catch (NoResultException e) {
			staff = null;
			logger.error("No Staff fetched  : " + e);
			entityManager.flush();
			entityManager.close();
		}
		return lsr;
	}

}
