package com.reminder.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.dao.ActionTypeDAO;
import com.reminder.dao.GroupUserDAO;
import com.reminder.model.ActionType;
import com.reminder.model.GroupRoleActionType;
import com.reminder.model.GroupRoles;
import com.reminder.model.GrouproleUser;
import com.reminder.model.Groups;
import com.reminder.model.ModuleType;
import com.reminder.model.ResponseModel;
import com.reminder.model.User;
import com.reminder.request.model.ActionTypeRequest;
import com.reminder.request.model.AssignRoleRequest;
import com.reminder.request.model.GroupRequest;
import com.reminder.request.model.GroupRolesRequest;
import com.reminder.request.model.GroupSearchCriteria;
import com.reminder.request.model.GroupUpdateRequest;
import com.reminder.request.model.GroupUserRequest;
import com.reminder.request.model.MyGroupHome;
import com.reminder.response.model.AllGroupResponse;
import com.reminder.response.model.GroupResponse;
import com.reminder.response.model.MyGroupDetails;
import com.reminder.response.model.MyGroupDetailsResponse;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.response.model.MyGroupRoleDetails;
import com.reminder.response.model.ServiceResponse;
import com.reminder.response.model.UserDetailsResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.GroupRoleActionTypeService;
import com.reminder.service.GroupService;
import com.reminder.service.RoleService;
import com.reminder.service.UserService;
import com.reminder.utils.CsvUtils;
import com.reminder.utils.DateTimeUtil;

@RestController
public class GroupController {
	@Autowired
	private GroupService groupService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ActionTypeDAO actionDAO;
	@Autowired
	private UserService userService;
	@Autowired
	private GroupUserDAO groupUserDao;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;	
	@Autowired
	private GroupRoleActionTypeService groupRoleActionTypeService;
	
	
	private Logger logger = Logger.getLogger(GroupController.class);

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/createGroup", method=RequestMethod.POST, 
            produces="application/json", consumes="application/json")
    public void createGroup(@RequestBody Groups group)
    {
		logger.info("createGroup(group=" + group + ") - start - creating group with api : /createGroup");
    	groupService.createGroup(group);
    }

	/*** Creating a new Group ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groups", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> createGroup(@RequestBody GroupRequest groupRequest,
									  HttpServletRequest request) {
	
		Groups groupCreate = new Groups();
		Set<GroupRoles> groupRoles = new HashSet<>();
		Groups newGroup = null;
		User createdUser = getLoggedInUserDetails(request);
			
		try {
			if (groupRequest != null) {
				logger.info("createGroup(groupRequest=" + groupRequest + ", request=" + request + ") - creating group with api : /groups");
				groupCreate.setGroupName(groupRequest.getGroupName());
				groupCreate.setDescription(groupRequest.getDescription());
				groupCreate.setCreatedAt(groupRequest.getCreatedAt());
				groupCreate.setModuleType(groupUserDao.findModuleType(groupRequest.getModuleType()));
				groupCreate.setCreatedBy(createdUser);
				groupCreate.setLastModifiedBy(createdUser);
				groupCreate.setActive(groupRequest.isActive());
				for (GroupRolesRequest roleRequest : groupRequest.getRoles()) {
					GroupRoles role = new GroupRoles();
					role.setGroupRoleName(roleRequest.getGroupRoleName());
					role.setGroup(groupCreate);
					Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
					for (ActionTypeRequest actions : roleRequest.getGroupRoleActionType()) {
						ActionType newAction = actionDAO.find(actions.getActionTypeId());
						GroupRoleActionType roleAction = new GroupRoleActionType();
						roleAction.setGroupRole(role);
						roleAction.setActionType(newAction);
						groupRoleActions.add(roleAction);
						Set<GrouproleUser> groupUsers = new HashSet<>();
						for (GroupUserRequest user : roleRequest.getGroupUsers()) {
							GrouproleUser group_user = new GrouproleUser();
							group_user.setGroup(groupCreate);
							group_user.setUser(userService.getUserById(user.getUserId()));
							group_user.setRoleUser(role);
							group_user.setGroup(groupCreate);
							groupUsers.add(group_user);
						}
						role.setRoleUsers(groupUsers);
						groupCreate.setGroupUser(groupUsers);
					}
					
					role.setGroupRolesActionType(groupRoleActions);
					groupRoles.add(role);
				}
				groupCreate.setGroupRoles(groupRoles);
			    newGroup = groupService.createGroup(groupCreate);
			 
			}
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		if(newGroup != null)
		return new ResponseEntity<Object>(newGroup.getGroupId(), HttpStatus.OK);
		return new ResponseEntity<Object>("Group is null", HttpStatus.NO_CONTENT);

	}

	/*** Retrieve a single Group ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groups/{groupId}", produces = "application/json", method = RequestMethod.GET)
	public GroupResponse getGroupById(@PathVariable("groupId") Integer groupId, HttpServletRequest request) {

		GroupResponse group = null;
		try {
			String header = request.getHeader("Authorization");
			User createdUser = null;
			if (header.startsWith("Bearer ")) {
				String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
				createdUser = userService.getUserByName(username);
			}
			if (groupId != null) {
				logger.info("getGroupById(groupId=" + groupId + ", request=" + request + ") - Fetching data for group with groupId :" + groupId);
				if(createdUser != null)
				group = groupService.getGroupById(createdUser.getUserId(),groupId);
			}
		} catch (Exception e) {
			logger.error("Error in fetching data for group with groupId :" + groupId);
		}
		return group;
	}

	/*** Retrieve all Groups ***/
	/*
	 * @RequestMapping(value="/groups",produces="application/json",
	 * method=RequestMethod.GET) public List<Group1> getAllGroups() {
	 * logger.info("fetching data for all groups"); List<Group1> groupList =
	 * groupService.getAllGroups(); return groupList; }
	 */

	/*** Retrieve all Groups using search criteria ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchGroup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<AllGroupResponse> getAllGroups(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			@RequestBody(required = false) GroupSearchCriteria groupSearchCriteria) {

		List<GroupResponse> groups = groupService.getAllGroups(sort_by, order, searchCriteria, limit, page_no, groupSearchCriteria);
		long count = groupService.getAllGroupsCount(searchCriteria, groupSearchCriteria);

		AllGroupResponse AllGroupResponse = new AllGroupResponse(groups, count);

		if (CollectionUtils.isEmpty(groups)) {
			ResponseEntity<AllGroupResponse> returnResponseEntity = new ResponseEntity<AllGroupResponse>(HttpStatus.NO_CONTENT);
			logger.info("getAllGroups(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", groupSearchCriteria=" + groupSearchCriteria + ") - end - No group created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<AllGroupResponse> returnResponseEntity = new ResponseEntity<AllGroupResponse>(AllGroupResponse, HttpStatus.OK);
		logger.info("getAllGroups(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", groupSearchCriteria=" + groupSearchCriteria + ") - end - Fetching data for all group - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/*** Update a Group ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateGroup/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseEntity<ResponseModel> updateGroup(@RequestBody GroupUpdateRequest groupUpdate, @PathVariable Integer id,
										HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		boolean result = false ;
		if (!groupUpdate.isActive())
			result = groupService.checkforActiveReminder(id);

		if (result) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.NOT_ACCEPTABLE);
		}
		Groups group = groupService.getGroupByID(id);
		User createdUser =  getLoggedInUserDetails(request);
		group.setLastModifiedBy(createdUser);
		group.setLastModifiedAt(DateTimeUtil.now());
		group.setActive(groupUpdate.isActive());
		group.setGroupName(groupUpdate.getGroupName());
		group.setDescription(groupUpdate.getDescription());
		group.setModuleType(groupUserDao.findModuleType(groupUpdate.getModuleType()));
		try {
			if (group != null) {
				logger.info("updateGroup(groupUpdate=" + groupUpdate + ", id=" + id + ", request=" + request + ") - updating a group with api : /updateGroup");
				groupService.updateGroup(group);
				response.setSuccess("Record Updated Successfully");
				response.setRecordNumber(group.getGroupId());
				response.setFailure("No Exception");
			}
		} catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(group.getGroupId());
			response.setFailure("Error while Updating record" + e.getMessage());
			return new ResponseEntity<ResponseModel>(response,HttpStatus.UNPROCESSABLE_ENTITY);

		}
		return new ResponseEntity<ResponseModel>(response,HttpStatus.OK);

	}

	private User getLoggedInUserDetails(HttpServletRequest request) {
		User createdUser = null;
		String header = request.getHeader("Authorization");
		if(header.startsWith("Bearer ")){
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		return createdUser;
	}
	
	/*** Update a Group Roles ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "groups/{groupId}/groupRoles/{id}/users", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
	public List<GrouproleUser> getGroupRoleUser(@PathVariable int groupId, @PathVariable int id){
		List<GrouproleUser> groupRoleUsers = groupService.getAllUserByGroupRole(groupId, id);
		return groupRoleUsers;
	}
	

	
	/*** Update a Group Roles ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "groups/{groupId}/groupRoles/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseModel updateGroupRole(@RequestBody GroupRolesRequest groupRoleRequest, @PathVariable Integer id, @PathVariable Integer groupId,
			HttpServletRequest request) 
	{
		ResponseModel response = new ResponseModel();
		GroupRoles groupRoles = roleService.getGroupRoleById(id);
		groupRoles.setGroupRoleName(groupRoleRequest.getGroupRoleName());
		User createdUser =  getLoggedInUserDetails(request );
		Set<ActionTypeRequest> actionTypeRequest = groupRoleRequest.getGroupRoleActionType();
		Set<GrouproleUser> groupRoleUserSet=new HashSet<>();
		GrouproleUser groupRoleUser = null;
		Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
		try {
			logger.info("updateGroupRole(groupRoleRequest=" + groupRoleRequest + ", id=" + id + ", groupId=" + groupId + ", request=" + request + ") - updating a group role with api : /groupRoles");
			roleService.updateGroupRole(groupRoles);
			
			GroupRoles newGroupRole = roleService.findGroupRole(groupRoles.getGroupRoleId());
		/*	for(GroupRoleActionType groupAction: newGroupRole.getGroupRolesActionType()) {
				groupRoleActionTypeService.deleteGroupActions(groupAction);
			}*/
			Groups group = groupService.getGroupByID(groupId);
			newGroupRole.setGroup(group);
			groupRoleActionTypeService.deleteGroupActions(newGroupRole.getGroupRolesActionType());
			//roleService.flushGroupRole();
			//newGroupRole.getGroupRolesActionType().clear();
			for (ActionTypeRequest actions : actionTypeRequest) {
				ActionType newAction = actionDAO.find(actions.getActionTypeId());
				GroupRoleActionType roleAction = new GroupRoleActionType();
				roleAction.setActionType(newAction);
				roleAction.setGroupRole(newGroupRole);
				roleAction.setCreatedAt(DateTimeUtil.now());
				roleAction.setCreatedBy(createdUser);
				roleAction.setLastModifiedAt(DateTimeUtil.now());
				roleAction.setLastModifiedBy(createdUser);
				groupRoleActions.add(roleAction);
			}
			newGroupRole.setGroupRolesActionType(groupRoleActions);
			if (newGroupRole.getGroupRoleName().equals("Group Admin")) {
				roleService.deleteRoleUser(newGroupRole);

				// group.getGroupUser().clear();
				for (GroupUserRequest userRequest : groupRoleRequest.getGroupUsers()) {
					groupRoleUser = new GrouproleUser();
					groupRoleUser.setUser(userService.getUserById(userRequest.getUserId()));
					groupRoleUser.setCreatedAt(DateTimeUtil.now());
					groupRoleUser.setCreatedBy(createdUser);
					groupRoleUser.setLastModifiedAt(DateTimeUtil.now());
					groupRoleUser.setLastModifiedBy(createdUser);
					groupRoleUser.setGroup(group);
					groupRoleUser.setRoleUser(newGroupRole);
					groupRoleUserSet.add(groupRoleUser);
				}
				newGroupRole.setRoleUsers(groupRoleUserSet);
			}
			roleService.updateGroupRole(newGroupRole);
			
			response.setSuccess("Record Updated Successfully");
			response.setRecordNumber(newGroupRole.getGroupRoleId());
			response.setFailure("No Exception");
		}

		catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("Error while Updating record" + e.getMessage());
		}
		return response;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groups/{groupId}/groupRoles", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
	public List<GroupRoles> getGroupRole(@PathVariable int groupId, HttpServletRequest request) {
		List<GroupRoles> groupRole = roleService.getRolesByGroup(groupId);
		return groupRole;
	}
	

	/*** Update a Group Roles ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groups/{groupid}/groupRoles", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseModel createGroupRole(@RequestBody GroupRolesRequest groupRoleRequest, @PathVariable int groupid,
			HttpServletRequest request) {
		ResponseModel response = new ResponseModel();

		GroupRoles groupRoles = new GroupRoles();
		User createdUser = null;
		String header = request.getHeader("Authorization");
		
		Groups group=groupService.getGroupByID(groupid);
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}

		
		Set<ActionTypeRequest> actionTypeRequest = groupRoleRequest.getGroupRoleActionType();

		Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
		for (ActionTypeRequest actions : actionTypeRequest) {
			ActionType newAction = actionDAO.find(actions.getActionTypeId());
			GroupRoleActionType roleAction = new GroupRoleActionType();
			roleAction.setGroupRole(groupRoles);
			roleAction.setActionType(newAction);
			groupRoleActions.add(roleAction);

		}
		Set<GrouproleUser> groupRoleUserSet=new HashSet<>();
		GrouproleUser groupRoleUser=null;
		for(GroupUserRequest userRequest:groupRoleRequest.getGroupUsers()) {
			groupRoleUser=new GrouproleUser();
			User user=userService.getUserById(userRequest.getUserId());
			groupRoleUser.setUser(user);
			groupRoleUser.setCreatedAt(DateTimeUtil.now());
			groupRoleUser.setCreatedBy(createdUser);
			groupRoleUser.setLastModifiedAt(DateTimeUtil.now());
			groupRoleUser.setLastModifiedBy(createdUser);
			
			groupRoleUserSet.add(groupRoleUser);
			
		}
		groupRoles.setGroup(group);
		groupRoles.setGroupRolesActionType(groupRoleActions);
		groupRoles.setGroupRoleName(groupRoleRequest.getGroupRoleName());
		groupRoles.setCreatedBy(createdUser);
		groupRoles.setRoleUsers(groupRoleUserSet);

		try {
			logger.info("createGroupRole(groupRoleRequest=" + groupRoleRequest + ", groupid=" + groupid + ", request=" + request + ") - Creating a group role with api : /groupRoles");
			roleService.createGroupRole(groupid, groupRoles);
			
			response.setSuccess("Record Updated Successfully");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("No Exception");
		}

		catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("Error while Updating record" + e.getMessage());
		}
		return response;
	}
	
	
	/***Delete GroupRole ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/delete/groupRoles/{id}", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public ResponseModel deleteGroupRole(@PathVariable Integer id,
			HttpServletRequest request) {
		ResponseModel response = new ResponseModel();

		GroupRoles groupRoles = roleService.getGroupRoleById(id);


		/*Set<GroupRoleActionType> actionTypeSet = groupRoles.getGroupRolesActionType();

		Set<ActionTypeRequest> actionTypeRequest = groupRoleRequest.getGroupRoleActionType();

		Set<GroupRoleActionType> groupRoleActions = new HashSet<>();
		for (ActionTypeRequest actions : actionTypeRequest) {
			ActionType newAction = actionDAO.find(actions.getActionTypeId());
			GroupRoleActionType roleAction = new GroupRoleActionType();
			roleAction.setGroupRole(groupRoles);
			roleAction.setActionType(newAction);
			groupRoleActions.add(roleAction);

		}

		groupRoles.setGroupRolesActionType(groupRoleActions);*/

		try {
			logger.info("deleteGroupRole(id=" + id + ", request=" + request + ") - deleting a group role with api : /delete/groupRoles");
			roleService.deleteGroupRole(id);
			response.setSuccess("Record deleted Successfully");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("No Exception");
		}

		catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRoles.getGroupRoleId());
			response.setFailure("Error while deleting record" + e.getMessage());
		}
		return response;
	}

	/***
	 * Group to CSV
	 * 
	 * @throws IOException
	 ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/writeGroupToCSV", method = RequestMethod.GET, produces = "application/json")
	public void getDataForCsv(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no)
			throws IOException {
		String csvFile = "D:/Reminder365_AllCsvs/group.csv";
		FileWriter writer = new FileWriter(csvFile);
		try {
			List<GroupResponse> groups = groupService.getAllGroups(sort_by, order, searchCriteria, limit, page_no, new GroupSearchCriteria());
			CsvUtils.writeLine(writer, Arrays.asList("GroupId", "GroupName", "Remarks", "CreatedDate",
					"LastModifiedDate", "ModuleId", "CreatedById", "LastModifiedById", "GroupRole"));

			for (GroupResponse group : groups) {

				List<String> list = new ArrayList<>();
				list.add(String.valueOf(group.getGroupId()));
				list.add(String.valueOf(group.getGroupName()));
				/*
				 * list.add(String.valueOf(group.getRemarks()));
				 * list.add(String.valueOf(group.getCreatedDate()));
				 * list.add(String.valueOf(group.getLastModifiedDate()));
				 */
				// list.add(String.valueOf(group.getModuleId()));
				/*list.add(String.valueOf(group.getCreatedById()));
				list.add(String.valueOf(group.getLastModifiedById()));*/
				list.add(String.valueOf(group.getGroupId()));

				CsvUtils.writeLine(writer, list);
				logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - CSV file created at location : " + csvFile);
			}
		} catch (IOException e) {
			logger.info("getDataForCsv(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ") - CSV file couldn't be created for error : " + e.getMessage());
		}
		writer.flush();
		writer.close();
	}


	@RequestMapping(value = "/groups/modules", produces = "application/json", method = RequestMethod.GET)
	public List<ModuleType> getAllModule() {
		List<ModuleType> moduleTypes = null;
		
		try {	
				moduleTypes = groupService.getAllModule();
		} catch (Exception e) {
			logger.error("error in getAllModule {} " + e.getMessage());
		}
		return moduleTypes;
	}
	
	@RequestMapping(value = "/groups/modules/{id}", produces = "application/json", method = RequestMethod.GET)
	public List<MyGroupDropDown> getAllUserGroupsByModule(@PathVariable("id") Integer moduleId,HttpServletRequest request,@RequestParam boolean isAdmin) {

		List<MyGroupDropDown> moduleTypes = null;
		try {	
				if(moduleId!=null){
					String header = request.getHeader("Authorization");
					if(header.startsWith("Bearer ")){
						String username =jwtTokenUtil.getUsernameFromToken(header.substring(7));
						User user = userService.getUserByName(username);
						moduleTypes = groupService.getAllGroupByModuleId(moduleId,user.getUserId(),isAdmin);
					}
				}
		} catch (Exception e) {
			logger.error("getAllUserGroupsByModule(moduleId=" + moduleId + ", request=" + request + ", isAdmin=" + isAdmin + ") - error in getAllModuleByGroups {} ", e);
		}
		return moduleTypes;
	}
	
	
	@RequestMapping(value = "/groups/roles/{groupId}/{moduleId}", produces = "application/json", method = RequestMethod.GET)
	public 	List<MyGroupDropDown>  getAllModuleByRoles(@PathVariable("groupId") Integer groupId,@PathVariable("moduleId") Integer moduleId, HttpServletRequest request) {
		 
		List<MyGroupDropDown>  moduleTypes = null;
		try {	
				if(moduleId!=null){
					moduleTypes = groupService.getAllModuleByRoles(groupId,moduleId);
				} 
		} catch (Exception e) {
			logger.error("getAllModuleByRoles(groupId=" + groupId + ", moduleId=" + moduleId + ", request=" + request + ") - error in getAllModuleByRoles {} ", e);
		}
		return moduleTypes;
	}
	
	@RequestMapping(value = "/groups/mygroups/{pageNo}/{pageSize}", produces = "application/json", method = RequestMethod.GET)
	public List<MyGroupDetails> getMyGroupDetailsByUserId(  @PathVariable("pageNo") Integer pageNo ,
															@PathVariable("pageSize") Integer pageSize ,
															HttpServletRequest request) {
		List<MyGroupDetails> moduleTypes = null;
		try {	
					String header = request.getHeader("Authorization");
					if(header.startsWith("Bearer ")){
						String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
						User user=userService.getUserByName(username);
					moduleTypes = groupService.getMyGroupDetailsByUserId(user.getUserId(),pageNo,pageSize);
				} 
		} catch (Exception e) {
			logger.error("getMyGroupDetailsByUserId(pageNo=" + pageNo + ", pageSize=" + pageSize + ", request=" + request + ") - error in getMyGroupDetailsByUserId {} ", e);
		}
		return moduleTypes;
	}
	
	
	/**
	 * Assign role screen 
	 * @param myGroupHome
	 * @param searchCriteria
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/groups/mygroups", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<MyGroupDetailsResponse> getMyGroupDetailsByUserId(@RequestBody MyGroupHome myGroupHome,
			@RequestParam(required = false) String searchCriteria, HttpServletRequest request) {
		List<MyGroupDetails> moduleTypes = null;
		MyGroupDetailsResponse myGroupDetailsResponse = null;
		try {
			String header = request.getHeader("Authorization");
			if (header != null && header.startsWith("Bearer ")) {
				String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
				User user = userService.getUserByName(username);
				myGroupHome.setAdminUser(user.getUserId());
				moduleTypes = groupService.getMyGroupDetailsByUserId(myGroupHome, searchCriteria);
				BigInteger count = groupService.getMyGroupDetailsCountByUserId(myGroupHome, searchCriteria);

				myGroupDetailsResponse = new MyGroupDetailsResponse(moduleTypes, count, moduleTypes.get(0).getUserRolePerGroup() );

				if ((CollectionUtils.isEmpty(moduleTypes))) {
					return new ResponseEntity<MyGroupDetailsResponse>(HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<MyGroupDetailsResponse>(myGroupDetailsResponse, HttpStatus.OK);
			}
		} catch (Exception e) {
			ResponseEntity<MyGroupDetailsResponse> returnResponseEntity = new ResponseEntity<MyGroupDetailsResponse>(HttpStatus.NO_CONTENT);
			logger.error("getMyGroupDetailsByUserId(myGroupHome=" + myGroupHome + ", searchCriteria=" + searchCriteria + ", request=" + request + ") - end - error in getMyGroupDetailsByUserId {}  - return value=" + returnResponseEntity, e);
			return returnResponseEntity;
		}
		return new ResponseEntity<MyGroupDetailsResponse>(myGroupDetailsResponse, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/groups/assignUsers", consumes = "application/json", method = RequestMethod.POST)
	public ResponseModel saveUserGroupUsersRole(@RequestBody AssignRoleRequest assignRoleRequest, 
									     HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		try {	
			String header = request.getHeader("Authorization");
				if(header.startsWith("Bearer ")){
					String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
					User user=userService.getUserByName(username);
					assignRoleRequest.setCreatedUserId(user.getUserId());
				if(assignRoleRequest.getUserIds()!=null){
					groupService.saveUserGroupUsersRole(assignRoleRequest);
					response.setSuccess("Record Deleted Successfully");
					response.setFailure("No Exception");
				}
			}
		} catch (Exception e) {
			response.setSuccess("Not Success");
			response.setFailure("Error while deleting an record :: " + e.getMessage());
		}
		return response;
	}
	
	@RequestMapping(value = "/deleteGroupUser/{id}",  method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteGroupUser(@PathVariable int id, 
								   HttpServletRequest request, HttpServletResponse res) {
		try {	
			if(id!=0 ){
				groupService.deleteGroupUser(id);
			}
		} catch (Exception e) {
			//res.sendError(406,"Single Group Admin");
            return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/groupUsers", consumes = "application/json", method = RequestMethod.POST)
	// @CrossOrigin(maxAge = 4800, allowCredentials = "false")
	public ResponseEntity<UserDetailsResponse> getAllUsers(@RequestParam(required = false) String searchCriteria,
			@RequestBody MyGroupHome myGroupHome) {

		List<User> users = groupService.getAllUserDetailsNotInGroup(myGroupHome, searchCriteria);
		long rowCount = groupService.getAllUsersCountNotInGroup(myGroupHome, searchCriteria);

		UserDetailsResponse userResponse = new UserDetailsResponse(users, rowCount);

		if (users != null && users.isEmpty()) {
			return new ResponseEntity<UserDetailsResponse>(HttpStatus.NO_CONTENT);
		}
		ResponseEntity<UserDetailsResponse> returnResponseEntity = new ResponseEntity<UserDetailsResponse>(userResponse, HttpStatus.OK);
		logger.info("getAllUsers(searchCriteria=" + searchCriteria + ", myGroupHome=" + myGroupHome + ") - end - Fetching data for all user - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
	
	
	@RequestMapping(value = "/groupNameExists/{moduleId}/{groupName}",method = RequestMethod.GET)
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    public ServiceResponse groupNameExists(@PathVariable int moduleId, @PathVariable String groupName ) {
		
		 Boolean value = null ;
		 ServiceResponse serviceResponse = new ServiceResponse();
		try {
          value = groupService.groupNameExists(moduleId,groupName);
          serviceResponse.setData(value);
          serviceResponse.setStatus("Success");
		} catch (Exception e) {
			 serviceResponse.setStatus("Failure");
			 serviceResponse.setData(value);
		}
        return serviceResponse;
    }
	
	/*** Delete a Group ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteGroup/{groupId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteGroup(@PathVariable("groupId") Integer groupId) {
			if (groupId != null) {
			logger.info("deleteGroup(groupId=" + groupId + ") - deleting group with groupid = " + groupId);
				try {
					boolean result = groupService.checkforActiveReminder(groupId);
					if (result) {
						return new ResponseEntity<String>( HttpStatus.NOT_ACCEPTABLE);
					}
					groupService.deleteGroup(groupId);
				} catch (Exception e) {
					return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<String>("Success", HttpStatus.OK);
			}
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupsDetails", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<MyGroupRoleDetails>>  getMyGroupsRoleDetails(HttpServletRequest request) {

		List<MyGroupRoleDetails> moduleTypes = null;
		try {	
					String header = request.getHeader("Authorization");
					if(header.startsWith("Bearer ")){
						String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
						User user = userService.getUserByName(username);
						moduleTypes = groupService.getMyGroupsRoleDetails(user.getUserId());
						return new ResponseEntity<List<MyGroupRoleDetails>>(moduleTypes, HttpStatus.OK);
					}
		} catch (Exception e) {
			logger.error("getMyGroupsRoleDetails(request=" + request + ") - error in getMyGroupsRoleDetails {} ", e);
		}
		return new ResponseEntity<List<MyGroupRoleDetails>>(HttpStatus.NO_CONTENT);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupByName/{groupName}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<MyGroupRoleDetails>>  groupByName(@PathVariable("groupName") String groupName) {

		List<MyGroupRoleDetails> moduleTypes = null;
		try {	
			moduleTypes = groupService.groupByName(groupName);
			return new ResponseEntity<List<MyGroupRoleDetails>>(moduleTypes, HttpStatus.OK);
		
		} catch (Exception e) {
			logger.error("groupByName(groupName=" + groupName + ") - error in groupByName{} ", e);
		}
		return new ResponseEntity<List<MyGroupRoleDetails>>(HttpStatus.NO_CONTENT);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/moduleNameByUser", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<ModuleType>> getModuleNameByUser(HttpServletRequest request){
		List<ModuleType> moduleName = null;
		try{
			String header = request.getHeader("Authorization");
			if(header.startsWith("Bearer ")){
				String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
				User user = userService.getUserByName(username);
				moduleName = groupService.getModuleNameByUser(user.getUserId());
			return new ResponseEntity<List<ModuleType>>(moduleName, HttpStatus.OK);
			}
		}catch(Exception e){
			logger.error("Error fetching module name for user: "+e);
			return new ResponseEntity<List<ModuleType>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ModuleType>>(HttpStatus.NO_CONTENT);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/isAdmin", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isAdmin(HttpServletRequest request, @RequestParam(required = true) int moduleId){
		boolean isAdmin = false;
		try{
			String header = request.getHeader("Authorization");
			if(header.startsWith("Bearer ")){
				String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
				User user = userService.getUserByName(username);
				isAdmin = groupService.isAdmin(user.getUserId(),moduleId);
			return new ResponseEntity<Boolean>(isAdmin, HttpStatus.OK);
			}
		}catch(Exception e){
			logger.error("Error fetching module name for user: "+e);
			return new ResponseEntity<Boolean>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Boolean>(HttpStatus.NO_CONTENT);
	}
}
