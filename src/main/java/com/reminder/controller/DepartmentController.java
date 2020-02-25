package com.reminder.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.model.Department;
import com.reminder.model.User;
import com.reminder.response.model.DepartmentResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.DepartmentService;
import com.reminder.service.UserService;

@RestController
public class DepartmentController {
	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	private Logger logger = Logger.getLogger(DepartmentController.class);

	/*** Creating a new Department ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createDepartment", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public void createDepartment(@RequestBody Department department) {
		logger.info("createDepartment(department=" + department
				+ ") - start - creating group with api : /createDepartment");
		departmentService.createDepartment(department);
	}

	/*** Retrieve a single Department ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/department/{departmentId}", produces = "application/json", method = RequestMethod.GET)
	public Department getDepartmentById(@PathVariable("departmentId") int departmentId) {
		logger.info("getDepartmentById(departmentId=" + departmentId
				+ ") - start - fetching data for department with departmentId :" + departmentId);
		Department department = departmentService.getDepartmentById(departmentId);
		return department;
	}

	/*** Retrieve all Departments based on user ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/departments", produces = "application/json", method = RequestMethod.GET)
	public List<Department> getAllDepartments(HttpServletRequest request) {
		logger.info("getAllDepartments() - start - fetching data for all Departments based on login user");
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		int userId = createdUser.getUserId();
		return departmentService.getAllDepartments(userId);
	}

	/*** Retrieve all Departments ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/allDepartments", produces = "application/json", method = RequestMethod.GET)
	public List<Department> getAllDepartments() {
		logger.info("getAllDepartments() - start - fetching data for all Departments");
		return departmentService.getAllDepartments();
	}

	/*** Update a Department ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateDepartment", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public void updateDepartment(@RequestBody Department department) {
		logger.info("updateDepartment(department=" + department
				+ ") - start - updating a Department with api : /updateDepartment");
		departmentService.updateDepartment(department);
	}

	/*** Delete a Department ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteDepartment/{departmentId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteDepartment(@PathVariable("departmentId") int departmentId) {
		logger.info("deleteDepartment(departmentId=" + departmentId + ") - start - deleting Department with groupid = "
				+ departmentId);
		departmentService.deleteDepartment(departmentId);
	}

	/*** Retrieve all Section based om department ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/sections/{deptId}", produces = "application/json", method = RequestMethod.GET)
	public DepartmentResponse getSection(@PathVariable("deptId") int deptId) {
		logger.info("getSection(deptId=" + deptId + ") - start - fetching data for Section as per department");
		DepartmentResponse sectionList = departmentService.getSection(deptId);
		if (sectionList != null) {
			sectionList.setStatus("sucess");
		}

		return sectionList;
	}
}
