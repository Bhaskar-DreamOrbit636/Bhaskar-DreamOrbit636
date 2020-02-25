package com.reminder.service;
import java.util.List;

import com.reminder.model.Department;
import com.reminder.response.model.DepartmentResponse;


public interface DepartmentService {
	public void createDepartment(Department department);

	public Department getDepartmentById(int departmentId);

	public List<Department> getAllDepartments(int userId);

	public void updateDepartment(Department department);

	public void deleteDepartment(int departmentId);

	public DepartmentResponse getSection(int deptId);

	public List<Department> getAllDepartments();
	    
	   
}
