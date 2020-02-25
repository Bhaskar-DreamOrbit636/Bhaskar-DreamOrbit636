package com.reminder.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RestController;

import com.reminder.model.GroupRoles;
import com.reminder.model.ResponseModel;
import com.reminder.model.User;
import com.reminder.response.model.ActionType;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.RoleService;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;

@RestController
public class GroupRoleController {
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserService userService;
	private Logger logger = Logger.getLogger(GroupRoleController.class);
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	CurrentDate curentDate = new CurrentDate();

/*	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groups/{groupId}/roles", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseModel createUser(@PathVariable("groupId") int groupId, @RequestBody GroupRolesRequest groupRole) {

		ResponseModel response = new ResponseModel();

		GroupRoles group_Roles = new GroupRoles();
		Set<GroupRoleActionType> groupRole_ActionTypes = new HashSet<>();

		try {
			if (groupRole != null) {
				logger.info("creating role with role name = " + groupRole.getGroupRoleName());
				//groupRole.setGroupId(groupId);
				group_Roles.setGroupRoleName(groupRole.getGroupRoleName());
				group_Roles.setCreatedAtTime(groupRole.getCreatedAtTime());
				group_Roles.setCreatedById(groupRole.getCreatedById());
				group_Roles.setLastModifiedAtTime(groupRole.getLastModifiedAtTime());
				group_Roles.setLastModifiedById(groupRole.getLastModifiedById());

				roleService.createGroupRole(groupId, group_Roles);
				for (ActionTypeRequest actionType : groupRole.getGroupRoleActionType()) {
					roleService.findActionType(actionType.getActionTypeId());
					//GroupRoleActionType groupRole_ActionType = new GroupRoleActionType(group_Roles, actionType);
					//groupRole_ActionTypes.add(groupRole_ActionType);
				}
				response.setSuccess("Record Inserted Successfully");
				response.setRecordNumber(group_Roles.getGroupRoleId());
				response.setFailure("No Exception");
			}
		} catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(group_Roles.getGroupRoleId());
			response.setFailure("Error while inserting record" + e.getMessage());
			e.printStackTrace();
		}
		return response;

	}*/

	
	
	
	
	 /*@CrossOrigin(maxAge = 4800, allowCredentials = "false")
		@RequestMapping(value = "/users/{userId}/roles", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseModel createUser(@PathVariable("userId") int userId, @RequestBody UserRequest userRole) {

			ResponseModel response = new ResponseModel();

			GrouproleUser groupRoleUser = new GrouproleUser();
			Set<GrouproleUser> groupRoleUserTypes = new HashSet<>();

			try {
				if (userRole != null) {
					logger.info("creating role with role name = " + userRole.getUserName());
					userRole.setUserId(userId);
					groupRoleUser.setGrouprole(userRole.getUserName());
					groupRoleUser.setEmailId(userRole.getEmailId());
					groupRoleUser.setCreatedById(userRole.getCreatedById());
					groupRoleUser.setCreatedAt(userRole.getCreatedAt());
					groupRoleUser.setLastModifiedAt(userRole.getLastModifiedAt());
					groupRoleUser.setLastModifiedById(userRole.getLastModifiedById());
					groupRoleUser.setMobileNumber(userRole.getMobileNumber());
					groupRoleUser.setRemark(userRole.getRemark());
					

					userService.createUserGroup(userId, groupRoleUser);
					for (ActionType actionType : userRole.getGrouprole_User()) {
						roleService.findActionType(actionType.getActionTypeId());
						GroupRole_ActionType groupRole_ActionType = new GroupRole_ActionType(groupRoleUser, actionType);
						groupRoleUserTypes.add(groupRole_ActionType);
					}
					response.setSuccess("Record Inserted Successfully");
					response.setRecordNumber(groupRoleUser.getId());
					response.setFailure("No Exception");
				}
			} catch (Exception e) {
				response.setSuccess("Not Success");
				response.setRecordNumber(groupRoleUser.getId());
				response.setFailure("Error while inserting record" + e.getMessage());
				e.printStackTrace();
			}
			return response;

		}*/
	

	/*** Retrieve a single Role ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupRole/{groupRoleId}", produces = "application/json", method = RequestMethod.GET)
	public GroupRoles getUserById(@PathVariable("groupRoleId") int groupRoleId) {
		logger.info("getUserById(groupRoleId=" + groupRoleId + ") - start - fetching role data with groupRoleId = " + groupRoleId);
		GroupRoles groupRole = roleService.getGroupRoleById(groupRoleId);
		return groupRole;
	}

	/*** Retrieve all Roles ***/
	/*
	 * @RequestMapping(value="/roles",produces="application/json",
	 * method=RequestMethod.GET) public List<Role> getAllRoles() { List<Role>
	 * roleList = roleDAO.getAllRoles(); return roleList; }
	 */

	// ----- with response entity----------------------
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupRoles", method = RequestMethod.GET)
	public ResponseEntity<List<GroupRoles>> getAllGroupRoles() {
		List<GroupRoles> groupRoles = roleService.getAllGroupRoles();
		if (groupRoles.isEmpty()) {
			ResponseEntity<List<GroupRoles>> returnResponseEntity = new ResponseEntity<List<GroupRoles>>(HttpStatus.NO_CONTENT);
			logger.info("getAllGroupRoles() - end - No group_role found  - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<GroupRoles>> returnResponseEntity = new ResponseEntity<List<GroupRoles>>(groupRoles, HttpStatus.OK);
		logger.info("getAllGroupRoles() - end - fetching all group_roles  - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	// ---------------end----------------------------------------------------

	/*** Update a Role ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateGroupRole", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseModel updateRole(@RequestBody GroupRoles groupRole, HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		try {
			if (groupRole != null) {
				roleService.updateGroupRole(groupRole);
				response.setSuccess("Record Updated Successfully");
				response.setRecordNumber(groupRole.getGroupRoleId());
				response.setFailure("No Exception");
			}
		} catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRole.getGroupRoleId());
			response.setFailure("Error while updating record" + e.getMessage());
			logger.error("Error while updating record" + e.getMessage());
			e.printStackTrace();
		}
		logger.info("updateRole(groupRole=" + groupRole + ", request=" + request + ") - end - Group Role updated at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + response);
		return response;

	}

	/*** Delete a Role ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteGroupRole/{groupRoleId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseModel deleteUser(@PathVariable("groupRoleId") Integer groupRoleId, HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		try {
			if (groupRoleId != null) {
				roleService.deleteGroupRole(groupRoleId);
				response.setSuccess("Record Updated Successfully");
				response.setRecordNumber(groupRoleId);
				response.setFailure("No Exception");
			}
		} catch (Exception e) {
			response.setSuccess("Not Success");
			response.setRecordNumber(groupRoleId);
			response.setFailure("Error while deleting record" + e.getMessage());
			logger.error("Error while deleting record" + e.getMessage());
			e.printStackTrace();
		}
		logger.info("deleteUser(groupRoleId=" + groupRoleId + ", request=" + request + ") - end - Group Role deleted at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + response);
		return response;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupRoleActions", method = RequestMethod.GET)
	public ResponseEntity<List<ActionType>> getGroupRoleActions() {
		List<ActionType> groupRoles = roleService.getGroupRoleActions();
		if (CollectionUtils.isEmpty(groupRoles)) {
			ResponseEntity<List<ActionType>> returnResponseEntity = new ResponseEntity<List<ActionType>>(HttpStatus.NO_CONTENT);
			logger.info("getGroupRoleActions() - end - No group_role found  - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<ActionType>> returnResponseEntity = new ResponseEntity<List<ActionType>>(groupRoles, HttpStatus.OK);
		logger.info("getGroupRoleActions() - end - fetching all group_roles  - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
}