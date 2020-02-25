package com.reminder.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.dao.GroupDAO;
import com.reminder.exception.DuplicateException;
import com.reminder.exception.NoContentException;
import com.reminder.model.RecordType;
import com.reminder.model.ResponseModel;
import com.reminder.model.Staff;
import com.reminder.model.StaffRecord;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.Constants;
import com.reminder.request.model.StaffRequest;
import com.reminder.request.model.StaffSearchCriteria;
import com.reminder.response.model.StaffDesginationReport;
import com.reminder.response.model.StaffResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.ReportService;
import com.reminder.service.StaffService;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;

@RestController
public class StaffController {

	@Autowired
	private StaffService staffService;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private ReportService reportService;
	
	CurrentDate curentDate = new CurrentDate();

	private Logger logger = Logger.getLogger(StaffController.class);

	/*** Creating a new Staff Reminder ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createStaffReminder", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> createStaffReminder(@RequestBody StaffRequest[] staffReq, HttpServletRequest request) {
		try{
		List<ResponseModel> resp = new ArrayList<>();
		StaffRecord sr = null;
		User createdUser = getCreatedUser(request);
		for (StaffRequest r : staffReq) {
			Boolean result = isAuthorized(createdUser.getUserId(), r.getGroupId(), Constants.CREATE);
			if (!result) {
				return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
			}
		}
			logger.info("createStaffReminder(staffReq=" + staffReq + ", request=" + request + ") - Creating Reminder using createStaffReminder() by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
			for (StaffRequest r : staffReq) {
			sr = staffService.createStaffReminder(r, createdUser);
			ResponseModel response = new ResponseModel();
			if (sr == null) {
				response.setFailure("Fail to Upload");
			} else {
				int reminderId = sr.getReminder().getReminderId();
				User createdBy = userService.getUserById(sr.getReminder().getCreatedById());
				User lastModifiedBy = userService.getUserById(sr.getReminder().getLastModifiedById());
				response.setCreatedBy(createdBy.getUserName());
				response.setCreatedAt(sr.getReminder().getCreatedAt());
				response.setLastModifiedBy(lastModifiedBy.getUserName());
				response.setLastModifiedAt(sr.getReminder().getLastModifiedAt());
				response.setSuccess("Sucessfully created");
				response.setRecordNumber(r.getrecordNumber());
				response.setReminderId(reminderId);
			}
			resp.add(response);
		}
			ResponseEntity<?> returnResponseEntity = new ResponseEntity<List<ResponseModel>>(resp, HttpStatus.OK);
			logger.info("createStaffReminder(staffReq=" + staffReq + ", request=" + request + ") - end - Staff reminder created at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}catch(Exception e){
			ResponseEntity<?> returnResponseEntity = new ResponseEntity<String>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error Creating Staff Reminder: " + e);
			return returnResponseEntity;
		}

	}
	
	/*
	 * getting All staff reminder
	 */
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getstaffReminder", method = RequestMethod.GET)
	public ResponseEntity<List<StaffResponse>> getStaffReminder(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			HttpServletRequest request) {
		
		User createdUser = getCreatedUser(request);

		List<StaffResponse> staffs = staffService.getStaffReminder(sort_by, order, searchCriteria, limit, page_no,createdUser.getUserId());
		if (staffs.isEmpty()) {
			ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(HttpStatus.NO_CONTENT);
			logger.info("getStaffReminder(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", request=" + request + ") - end - No Staff created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(staffs, HttpStatus.OK);
		logger.info("getStaffReminder(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", request=" + request + ") - end - Fetching data for all Staffs - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
	
	private User getCreatedUser(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		return createdUser;
	}
	
	
	/**
	 * @param userId
	 * @param groupId
	 * @param permissionType
	 * @return
	 */
	public Boolean isAuthorized(int userId , int groupId,String permissionType) {
		List<String> list = groupDAO.getGroupRolesAction(userId, groupId);

		if (list.contains(permissionType)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	
	/*** Update a Staffs reminder ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateStaffReminder/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> updateStaffReminder(@RequestBody StaffRequest staffReq, @PathVariable("id") int id,
			HttpServletRequest request) {
		StaffRecord sr = null;

		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), staffReq.getGroupId(), Constants.UPDATE);
		if (!result) {
			return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		logger.info("updateStaffReminder(staffReq=" + staffReq + ", id=" + id + ", request=" + request + ") - Reminder updated by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		sr = staffService.updateStaffReminder(staffReq, id, createdUser);
		ResponseModel response = new ResponseModel();
		if (sr == null) {
			response.setFailure("Fail to Upload");
		} else {
			int reminderId = sr.getReminder().getReminderId();
			response.setSuccess("Sucessfully created");
			response.setRecordNumber(staffReq.getrecordNumber());
			response.setReminderId(reminderId);
		}
		ResponseEntity<?> returnResponseEntity = new ResponseEntity<>(response, HttpStatus.OK);
		logger.info("updateStaffReminder(staffReq=" + staffReq + ", id=" + id + ", request=" + request + ") - end - Staff reminder updated at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/*** Retrieve a single Staff reminder by id ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffReminderByid/{staffRecId}", produces = "application/json", method = RequestMethod.GET)
	public StaffResponse getStaffreminderById(@PathVariable("staffRecId") int staffRecId) {
		logger.info("getStaffreminderById(staffRecId=" + staffRecId + ") - start - Retrieving Staff using /Staff/{StaffId} api");
		StaffResponse staff = staffService.getStaffreminderById(staffRecId);
		StaffRecord as = staff.getStaffRec();
		User user1 = userService.getUserById(as.getReminder().getLastModifiedById());
		User user2 = userService.getUserById(as.getReminder().getCreatedById());
		staff.setLastModifiedBy(user1.getAdUserId());
		staff.setCreatedBy(user2.getAdUserId());
		return staff;
	}
	
	/*** Delete a Staff reminder ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteStaffReminder/{staffRecId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<?> deleteStaffReminder(@PathVariable("staffRecId") int staffId,
			@RequestParam(required = false) int groupId, HttpServletRequest request, HttpServletResponse response) {
		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), groupId, Constants.DELETE);
		if (!result) {
			return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		try{
		staffService.deleteStaffReminder(staffId, createdUser);
		}catch(Exception e){
			try {
				response.sendError(403, "Forbidden");
			} catch (IOException ex) {
				logger.error("Error in delete Staff reminder sending status code:"+ex);
				ex.printStackTrace();
			}
		}
		ResponseEntity<?> returnResponseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
		logger.info("deleteStaffReminder(staffId=" + staffId + ", groupId=" + groupId + ", request=" + request + ", response=" + response + ") - end -  Reminder deleted by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
	
	/** Api to get dates for Expire calendar */
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/StaffexpireCal", method = RequestMethod.GET, produces = "application/json")
	public List<Summary> getExpiryCalendar(HttpServletRequest request,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "date", required = false) Date date) {
		logger.info("getExpiryCalendar(request=" + request + ", date=" + date + ") - start - getting the dates expiring in current month");
		User loginUser = getCreatedUser(request);
		if (date == null) {
			date = new Date();
		}
		return staffService.getExpiryCalendar(loginUser.getUserId(),date);
	}
	
	
	// ----------------------followings are Staff api ----------------------

	/*** Creating a new staff ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createStaff", method = RequestMethod.POST, 
	produces = "application/json", consumes = "application/json")
	public void createStaff(@RequestBody Staff staff, HttpServletRequest request,
			HttpServletResponse response) {
		
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		staff.setCreatedById(createdUser.getUserId());
		//staff.setLastModifiedById(createdUser.getUserId());
		logger.info("createStaff(staff=" + staff + ", request=" + request + ", response=" + response + ") -  Staff created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		try{
		staffService.createStaff(staff);
		}catch(HibernateException | DuplicateException e ){
			logger.error("Error creating Staff, oter than Conflict:  " + e);
			throw new DuplicateException(e.toString());
		}
			
	}

	/*** Retrieve a single Staff by id ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffByid/{staffId}", produces = "application/json", method = RequestMethod.GET)
	public StaffResponse getStaffById(@PathVariable("staffId") int staffId) {
		logger.info("getStaffById(staffId=" + staffId + ") - start - Retrieving Staff using /Staff/{StaffId} api");
		StaffResponse staffRes = staffService.getStaffById(staffId);
		Staff staff = staffRes.getStaff();
		if(staff.getLastModifiedById() != 0 ){
		User lastuser = userService.getUserById(staff.getLastModifiedById());
		staffRes.setLastModifiedBy(lastuser.getAdUserId());
		}else
			staffRes.setLastModifiedBy(null);
		User createUser = userService.getUserById(staff.getCreatedById());
		staffRes.setCreatedBy(createUser.getAdUserId());
		return staffRes;
	}
	
	/*** Retrieve a single Staff by Ps Staff id ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/psStaffByid/{staffId}", produces = "application/json", method = RequestMethod.GET)
	public List<StaffResponse> getStaffByPsId(@PathVariable("staffId") String staffId, 
			 HttpServletResponse response) {
		logger.info("getStaffByPsId(staffId=" + staffId + ", response=" + response + ") - start - Retrieving Staff using /Staff/{StaffId} api");
		List<StaffResponse> staff = staffService.getStaffByPsId(staffId);
		if(staff==null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Erro in getStaffByPsId: "+e);
			}
		}
		return staff;
	}
	
	/*** Retrieve a single Staff by NRIC/FIN ***/
/*	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffByNRIC/{nric:.+}", produces = "application/json", method = RequestMethod.GET)
	public List<StaffResponse> getStaffByNRIC(@PathVariable("nric") String nric, 
			HttpServletResponse response) {
		logger.info("getStaffByNRIC(nric=" + nric + ", response=" + response + ") - start - Retrieving Staff using /Staff/{StaffId} api");
		List<StaffResponse> staff = staffService.getStaffByNRIC(nric);
		if(staff==null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Erro in staffByNRIC: "+e);
			}
		}
		return staff;
	} */

	/*** Retrieve a single Staff by name ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffByname/{staffName}", produces = "application/json", method = RequestMethod.GET)
	public List<StaffResponse> getStaffByName(@PathVariable("staffName") String staffName, 
			HttpServletResponse response) {
		logger.info("getStaffByName(staffName=" + staffName + ", response=" + response + ") - start - Retrieving Staff using /Staff/name/{staffName} api");
		List<StaffResponse> staff = staffService.getStaffByName(staffName);
		if(staff==null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Erro in getStaffByName: "+e);
			}
		}
		return staff;
	}

	// --- list of Staffs with pagination and search criteria -----------
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getstaffs", method = RequestMethod.GET)
	public ResponseEntity<List<StaffResponse>> getAllStaffs(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			HttpServletRequest request) {
		
		User createdUser = getCreatedUser(request);
		List<StaffResponse> staffs = staffService.getAllStaffs(sort_by, order, searchCriteria, limit, page_no, createdUser.getUserId());
		if (staffs==null) {
			ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(HttpStatus.NO_CONTENT);
			logger.info("getAllStaffs(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - No Staff created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(staffs, HttpStatus.OK);
		logger.info("getAllStaffs(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - end - Fetching data for all Staffs - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchStaff", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<List<StaffResponse>> searchStaff(
			@RequestParam(name = "sortBy", required = false) String sort_by,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "page_no", required = false) Integer page_no,
			@RequestParam(required = false) String searchCriteria,
			@RequestBody(required = false) StaffSearchCriteria staffSearchCriteria, HttpServletRequest request) {
		
		User createdUser = getCreatedUser(request);
		List<StaffResponse> staffResponses = staffService.searchStaff(sort_by, order, limit, page_no,
				staffSearchCriteria, createdUser.getUserId(), searchCriteria);
		if (staffResponses == null || staffResponses.isEmpty()) {
			ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(HttpStatus.NO_CONTENT);
			logger.info("searchStaff(sort_by=" + sort_by + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", searchCriteria=" + searchCriteria + ", staffSearchCriteria=" + staffSearchCriteria + ", request=" + request + ") - end - No Staff found - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<StaffResponse>> returnResponseEntity = new ResponseEntity<List<StaffResponse>>(staffResponses, HttpStatus.OK);
		logger.info("searchStaff(sort_by=" + sort_by + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", searchCriteria=" + searchCriteria + ", staffSearchCriteria=" + staffSearchCriteria + ", request=" + request + ") - end - Searching all Staffs - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/*** Update a Staffs 
	 * @throws DuplicateException ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateStaff", method = RequestMethod.PUT, 
	produces = "application/json", consumes = "application/json")
	public void updateStaff(@RequestBody Staff staff,  HttpServletRequest request, HttpServletResponse response) throws DuplicateException {
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		
		staff.setLastModifiedById(createdUser.getUserId());
		logger.info("updateStaff(staff=" + staff + ", request=" + request + ", response=" + response + ") -  Staff updated by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());

		staffService.updateStaff(staff);
		
	}

	/*** Delete a Staff ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteStaff/{staffId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteStaff(@PathVariable("staffId") int staffId, HttpServletRequest request, 
			HttpServletResponse response) {
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		logger.info("deleteStaff(staffId=" + staffId + ", request=" + request + ", response=" + response + ") -  staff deleted by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		try{
			staffService.deleteStaff(staffId);
			}catch(Exception ex){
				try {
					response.sendError(403, "Forbidden");
				} catch (IOException e) {
					logger.error("Error in delete staff status code: "+e);
					e.printStackTrace();
				}
			}
	}

	/*** Retrieve a StaffRecord ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffRec/{staffId}", produces = "application/json", method = RequestMethod.GET)
	public List<StaffRecord> getStaffrecById(@PathVariable("staffId") int staffId) {
		List<StaffRecord> listOfSR = new ArrayList<>();
		logger.info("getStaffrecById(staffId=" + staffId + ") - Retrieving Staff using /StaffRecord/{StaffId} api");
		listOfSR = staffService.getSaffRecordById(staffId);
		return listOfSR;
	}

	// ----------------------followings are Record_Type api ----------------------

	/*** Creating a new Record to Monitor ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createRecordType", method = RequestMethod.POST, 
	produces = "application/json", consumes = "application/json")
	public void createRecordToMonitor(@RequestBody RecordType record, HttpServletRequest request) {
		
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		 record.setCreatedBy(createdUser.getUserId());
		 record.setLastModifiedBy(createdUser.getUserId());
		logger.info("createRecordToMonitor(record=" + record + ", request=" + request + ") -  Record Type created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		staffService.createRecordType(record);
	}

	/*** Retrieve a Record to Monitor ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getRecord/{recordTypeId}", method = RequestMethod.GET, 
	produces = "application/json", consumes = "application/json")
	public RecordType getRecord(@PathVariable("recordTypeId") int recordTypeId) {
		logger.info("getRecord(recordTypeId=" + recordTypeId + ") - start - Getting a Record Type using getRecord()");
		return staffService.getRecord(recordTypeId);
	}

	/*** Retrieve all Record to Monitor ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getRecords", method = RequestMethod.GET, 
	produces = "application/json", consumes = "application/json")
	public ResponseEntity<StaffResponse> getAllRecord(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			@RequestParam(required = false) boolean active) {
		
		StaffResponse recordTypes = staffService.getAllRecord(sort_by, order, searchCriteria, limit, page_no,active);
		logger.info("getAllRecord(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", active=" + active + ") - Getting a record types using getAllRecord()");
		if(recordTypes==null){
			ResponseEntity<StaffResponse> returnResponseEntity = new ResponseEntity<StaffResponse>(HttpStatus.NO_CONTENT);
			logger.info("getAllRecord(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", active=" + active + ") - end - No Record Type created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return  new ResponseEntity<StaffResponse>(recordTypes, HttpStatus.OK);
	}

	/*** Update a RecordType ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateRecord", method = RequestMethod.PUT, 
	produces = "application/json", consumes = "application/json")
	public void updateRecord(@RequestBody RecordType record, HttpServletRequest request) {
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		record.setLastModifiedBy(createdUser.getUserId());
		logger.info("updateRecord(record=" + record + ", request=" + request + ") -  Record Type updated by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		staffService.updateRecord(record);
	}

	/*** Delete a RecordType ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteRecord/{recordTypeId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteRecord(@PathVariable("recordTypeId") int recordTypeId, HttpServletRequest request,
			HttpServletResponse response) {
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			 String username =
			 jwtTokenUtil.getUsernameFromToken(header.substring(7));
			 createdUser = userService.getUserByName(username);
		}
		logger.info("deleteRecord(recordTypeId=" + recordTypeId + ", request=" + request + ", response=" + response + ") -  Record Type deleted by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		try{
			staffService.deleteRecord(recordTypeId);
			}catch(Exception ex){
				try {
					response.sendError(403, "Forbidden");
				} catch (IOException e) {
					logger.error("Error in delete Record To Monitor status code: "+e);
					e.printStackTrace();
				}
			}
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/staffByRefrenceNumber/{ref:.+}", produces = "application/json", method = RequestMethod.GET)
	public List<StaffResponse> getStaffByRefrenceNumber(@PathVariable("ref") String ref, 
			HttpServletResponse response) {
		logger.info("getStaffByRefrenceNumber(ref=" + ref + ", response=" + response + ") - start - Retrieving Refrence number for advance search");
		List<StaffResponse> staff = staffService.getStaffByRefrenceNumber(ref);
		if(staff==null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Erro in staffByNRIC: "+e);
			}
		}
		return staff;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value="/desginationReport", produces="application/json", method=RequestMethod.GET)
	public List<StaffDesginationReport> getDesginationReport(){
		logger.info("getDesginationReport - start - getting desgination report");
		List<StaffDesginationReport> desReport = null;
		try{
			desReport = reportService.getDesginationReport();
		}catch(Exception e){
			
		}
		return desReport;
	}

}
