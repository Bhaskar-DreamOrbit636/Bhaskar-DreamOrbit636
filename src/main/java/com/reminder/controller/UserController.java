package com.reminder.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.model.LdapUser;
import com.reminder.model.User;
import com.reminder.request.model.UserSearchCriteria;
import com.reminder.response.model.AllUserDetailResponse;
import com.reminder.response.model.MyGroupRoleDetails;
import com.reminder.response.model.UserPopUp;
import com.reminder.response.model.UserResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.AppUserDetailService;
import com.reminder.service.GroupService;
import com.reminder.service.UserService;
import com.reminder.utils.CsvUtils;

@RestController
public class UserController {
	@Autowired
	private UserService userService;

	/*
	 * @Autowired private RoleService roleService;
	 */

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private AppUserDetailService userDetailService;

	@Autowired
	private GroupService groupService;

	/*
	 * @Autowired private ActiveDirectoryLdapAuthenticationProvider a;
	 * 
	 * @Value("${spring.ldap.userDnPatterns}") private String rootDn;
	 * 
	 * @Value("${spring.ldap.domain}") private String domain;
	 */

	private Logger logger = Logger.getLogger(UserController.class);

	// *** Creating a new User ***//*

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/loadUserDetails", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<UserResponse> loadUserDetails(HttpServletRequest request) {

		String userName = getUserDetails(request);

		User user = userService.getUserByName(userName);
		List<MyGroupRoleDetails> moduleTypes = groupService.getMyGroupsRoleDetails(user.getUserId());

		UserResponse userResponse = new UserResponse();
		User createUser = null;
		User lastModifed = null;
		if (user.getCreatedById() != null)
			createUser = userService.getUserById(user.getCreatedById());
		if (user.getLastModifiedById() != null)
			lastModifed = userService.getUserById(user.getLastModifiedById());
		mapToUserResponse(userResponse, user, createUser, lastModifed);
		userResponse.setUserGroupResponse(moduleTypes);
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/loadUserDetailsByToken", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<UserDetails> loadUserDetailsByToken(HttpServletRequest request) {
		String userName = "";
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			userName = jwtTokenUtil.getUsernameFromToken(header.substring(7));
		}
		UserDetails userDetails = userDetailService.loadUserByUsername(userName);
		return new ResponseEntity<UserDetails>(userDetails, HttpStatus.OK);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/loadEmails", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<String>> loadEmails(@RequestParam(required = false, defaultValue = "") String emailId) {
		List<String> emailList = userService.loadEmails(emailId);
		return new ResponseEntity<List<String>>(emailList, HttpStatus.OK);
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ResponseEntity<String> accesssDenied(Principal user) {

		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userName != null) {
			String str = "Hi " + userName + ", you do not have permission to access this page!";
		}
		String str1 = "you do not have permission to access this page";

		return new ResponseEntity<String>(str1, HttpStatus.OK);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> createUser(@RequestBody User user, HttpServletRequest request) {
		String duplicateUser = "";
		logger.info("createUser(user=" + user + ", request=" + request + ") - Creating User using createUser");
		boolean existngUser = userService.getUserByUserId(user.getAdUserId());
		boolean existngEmailId = userService.getUserByUserName(user.getEmailId());

		if (existngUser) {
			duplicateUser = "Duplicate userId ";
		}
		if (existngEmailId) {
			duplicateUser = "Duplicate EmailId";
		} /*
			 * else if(existngUserName){ duplicateUser = "Duplicate userName"; }
			 */

		if (!existngUser && !existngEmailId) {
			userService.createUser(user, getUserDetails(request));
		} else {
			return new ResponseEntity<String>(duplicateUser, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	private String getUserDetails(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		String username = "";
		if (!StringUtils.isEmpty(header) && header.startsWith("Bearer ")) {
			username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
		}
		return username;
	}

	/*** Retrieve a single User ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/user/{userId}", produces = "application/json", method = RequestMethod.GET)
	public UserResponse getUserById(@PathVariable("userId") int userId) {
		logger.info("getUserById(userId=" + userId + ") - start - Fetching data for single user");
		UserResponse userResponse = new UserResponse();
		User createUser = null;
		User lastModifed = null;
		User user = userService.getUserById(userId);
		if (user.getCreatedById() != null)
			createUser = userService.getUserById(user.getCreatedById());
		if (user.getLastModifiedById() != null)
			lastModifed = userService.getUserById(user.getLastModifiedById());

		mapToUserResponse(userResponse, user, createUser, lastModifed);
		return userResponse;
	}

	private void mapToUserResponse(UserResponse userResponse, User user, User createUser, User lastModifed) {
		userResponse.setActive(user.getActive());
		userResponse.setAdUserId(user.getAdUserId());
		userResponse.setCreatedAt(user.getCreatedAt());
		if (createUser != null)
			userResponse.setCreatedById(createUser.getAdUserId());
		userResponse.setEmailId(user.getEmailId());
		userResponse.setGroupAdmin(user.getGroupAdmin());
		userResponse.setLastModifiedAt(user.getLastModifiedAt());
		if (lastModifed != null)
			userResponse.setLastModifiedById(lastModifed.getAdUserId());
		userResponse.setMobileNumber(user.getMobileNumber());
		userResponse.setRemark(user.getRemark());
		userResponse.setUserAdmin(user.getUserAdmin());
		userResponse.setUserId(user.getUserId());
		userResponse.setUserName(user.getUserName());
		userResponse.setLastSuccesFullLoggedIn(user.getLastSuccessfullLoginDate());
		userResponse.setLastUnSuccesFullLoggedIn(user.getLastUnSuccessfullLoginDate());
		if (user.getDepartments() != null) {
			userResponse.setDepartmentId(user.getDepartments().getDepartmentId());
			userResponse.setDepartmentName(user.getDepartments().getDepartmentName());
		}
	}

	/*** Search users from LDAP server ***/

	@RequestMapping(value = "/searchUser", method = RequestMethod.GET)
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	public List<LdapUser> getLdapUser(@RequestParam("userName") String userName) {
		List<LdapUser> users = userService.getUserByUsername(userName);
		return users;
	}
	/*
	 * private DirContextOperations getUserByUsername(@RequestParam("userName")
	 * String userName) throws NamingException { DirContext context = new
	 * InitialDirContext(); SearchControls searchControls = new
	 * SearchControls();
	 * searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	 * 
	 * String searchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
	 * 
	 * String bindPrincipal = createBindPrincipal(userName); String searchRoot =
	 * rootDn ;
	 * 
	 * 
	 * try { return
	 * SpringSecurityLdapTemplate.searchForSingleEntryInternal(context,
	 * searchControls, searchRoot, searchFilter, new Object[] { bindPrincipal,
	 * userName}); } catch (javax.naming.NamingException e) {
	 * e.printStackTrace(); } return null; }
	 * 
	 * String createBindPrincipal(String username) { if (domain == null ||
	 * username.toLowerCase().endsWith(domain)) { return username; }
	 * 
	 * return username + "@" + domain; }
	 */

	@RequestMapping(value = "/unSuccessfullLogin", method = RequestMethod.GET)
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	public ResponseEntity<Object> unSuccessfullLoginDate(@RequestParam("userName") String userName) {
		userService.unSuccessfullLoginDate(userName);
		return new ResponseEntity<Object>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	public ResponseEntity<AllUserDetailResponse> getAllUsers(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			@RequestBody(required = false) UserSearchCriteria userSearchCriteria, HttpSession session) {

		List<User> users = userService.getAllUsers(sort_by, order, searchCriteria, limit, page_no, userSearchCriteria);
		long count = userService.getAllUsersCount(sort_by, searchCriteria, userSearchCriteria);
		if ((CollectionUtils.isEmpty(users))) {
			ResponseEntity<AllUserDetailResponse> returnResponseEntity = new ResponseEntity<AllUserDetailResponse>(
					HttpStatus.NO_CONTENT);
			logger.info("getAllUsers(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria
					+ ", limit=" + limit + ", page_no=" + page_no + ", userSearchCriteria=" + userSearchCriteria
					+ ", session=" + session + ") - end - No user created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		List<UserResponse> userList = new ArrayList<>();

		for (User user : users) {
			UserResponse userResponse = new UserResponse();

			/*
			 * User createUser = null; User lastModifed = null; if
			 * (user.getCreatedById() != null) createUser =
			 * userService.getUserById(user.getCreatedById()); if
			 * (user.getLastModifiedById() != null) lastModifed =
			 * userService.getUserById(user.getLastModifiedById());
			 */
			mapToUserResponse(userResponse, user, null, null);
			userList.add(userResponse);
		}
		AllUserDetailResponse userDetailResponse = new AllUserDetailResponse(userList, count);

		ResponseEntity<AllUserDetailResponse> returnResponseEntity = new ResponseEntity<AllUserDetailResponse>(
				userDetailResponse, HttpStatus.OK);
		logger.info("getAllUsers(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria
				+ ", limit=" + limit + ", page_no=" + page_no + ", userSearchCriteria=" + userSearchCriteria
				+ ", session=" + session + ") - end - Fetching data for all user - return value="
				+ returnResponseEntity);
		return returnResponseEntity;
	}

	@RequestMapping(value = "/usersactive", method = RequestMethod.GET)
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	public ResponseEntity<AllUserDetailResponse> getAllActiveUsers(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			HttpSession session) {

		List<User> users = userService.getAllActiveUsersGroup(sort_by, order, searchCriteria, limit, page_no);
		long count = userService.getAllActiveUsersCount(sort_by, searchCriteria);

		if ((CollectionUtils.isEmpty(users))) {
			ResponseEntity<AllUserDetailResponse> returnResponseEntity = new ResponseEntity<AllUserDetailResponse>(
					HttpStatus.NO_CONTENT);
			logger.info("getAllActiveUsers(sort_by=" + sort_by + ", order=" + order + ", searchCriteria="
					+ searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", session=" + session
					+ ") - end - No user created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		List<UserResponse> userList = new ArrayList<>();

		for (User user : users) {
			UserResponse userResponse = new UserResponse();

			User createUser = null;
			User lastModifed = null;
			if (user.getCreatedById() != null)
				createUser = userService.getUserById(user.getCreatedById());
			if (user.getLastModifiedById() != null)
				lastModifed = userService.getUserById(user.getLastModifiedById());

			mapToUserResponse(userResponse, user, createUser, lastModifed);
			userList.add(userResponse);
		}
		AllUserDetailResponse userDetailResponse = new AllUserDetailResponse(userList, count);

		ResponseEntity<AllUserDetailResponse> returnResponseEntity = new ResponseEntity<AllUserDetailResponse>(
				userDetailResponse, HttpStatus.OK);
		logger.info("getAllActiveUsers(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria
				+ ", limit=" + limit + ", page_no=" + page_no + ", session=" + session
				+ ") - end - Fetching data for all user - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/***
	 * Update a User
	 * 
	 * @return
	 ***/
	/***
	 * Update a User
	 * 
	 * @return
	 ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateUser", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> updateUser(@RequestBody User user, HttpServletRequest request,
			@RequestParam boolean validated) {
		logger.info("updateUser(user=" + user + ", request=" + request + ", validated=" + validated
				+ ") - start - Updating user");
		String duplicateEmail = "";

		boolean existngUserName = userService.getUserByUserName(user.getEmailId());

		User createUser = userService.getUserById(user.getUserId());

		if (createUser.getEmailId() != null && user.getEmailId() != null
				&& createUser.getEmailId().equalsIgnoreCase(user.getEmailId())) {
			existngUserName = false;
		}

		if (existngUserName) {
			duplicateEmail = "EmailId already exists!";
		}

		if (!existngUserName) {
			UserPopUp message = userService.updateUser(user, getUserDetails(request), validated);
			if (message != null) {
				return new ResponseEntity<Object>(message, HttpStatus.NOT_ACCEPTABLE);
			}
		} else {
			return new ResponseEntity<Object>(new UserPopUp("duplicateEmail", duplicateEmail),
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<Object>(new UserPopUp("Success", "updated successfully"), HttpStatus.OK);
	}

	/*** Delete a User ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteUser/{userId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable("userId") int userId) {
		logger.info("deleteUser(userId=" + userId + ") - start - Deleting user with particular id");
		userService.deleteUser(userId);
	}

	/***
	 * Userss to CSV
	 * 
	 * @throws IOException
	 ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/writeUsersToCSV", method = RequestMethod.GET, produces = "application/json")
	public void getDataForCsv(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no)
			throws IOException {
		String csvFile = "D:/Reminder365_AllCsvs/user.csv";
		FileWriter writer = new FileWriter(csvFile);
		try {
			List<User> users = userService.getAllUsers(sort_by, order, searchCriteria, limit, page_no,
					new UserSearchCriteria());
			CsvUtils.writeLine(writer,
					Arrays.asList("UserId", "UserName", "EmailId", "MobileNumber", "Remark", "CreatedBy", "CreatedDate",
							"LastModifiedBy", "LastModifiedDate", "GroupId", "ReminderId", "RoleId"));

			for (User user : users) {

				List<String> list = new ArrayList<>();

				list.add(String.valueOf(user.getUserId()));
				list.add(String.valueOf(user.getUserName()));
				list.add(String.valueOf(user.getEmailId()));
				list.add(String.valueOf(user.getMobileNumber()));
				list.add(String.valueOf(user.getRemark()));

				System.out.println("******************" + list.toString());

				CsvUtils.writeLine(writer, list);
				logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria="
						+ searchCriteria + ", limit=" + limit + ", page_no=" + page_no
						+ ") - CSV file created at location : " + csvFile);
			}
		} catch (IOException e) {
			logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria
					+ ", limit=" + limit + ", page_no=" + page_no + ") - CSV file couldn't be created for error : "
					+ e.getMessage());
		}
		writer.flush();
		writer.close();
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/userAdvanceSearch/{search}", produces = "application/JSON", method = RequestMethod.GET)
	public ResponseEntity<List<UserResponse>> userAdvanceSearch(@PathVariable("search") String search,
			@RequestParam(required = false) String column) {
		List<UserResponse> userRes = userService.userAdvanceSearch(search, column);
		if (userRes == null)
			return new ResponseEntity<List<UserResponse>>(userRes, HttpStatus.NO_CONTENT);
		return new ResponseEntity<List<UserResponse>>(userRes, HttpStatus.OK);

	}

	/*
	 * @CrossOrigin(maxAge=4800, allowCredentials="false")
	 * 
	 * @RequestMapping(value="/logout", method=RequestMethod.GET) public String
	 * logout(HttpServletRequest req, HttpServletResponse res){
	 * logger.info("loging out the user--"); try{ Authentication auth =
	 * SecurityContextHolder.getContext().getAuthentication(); if(auth != null)
	 * new SecurityContextLogoutHandler().logout(req, res, auth);
	 * logger.info("----user log out sucessfully----"); }catch(Exception e){
	 * logger.error("Error while logout: "+e.getMessage()); } return
	 * "redirect:/login?logout"; }
	 */

}
