package com.reminder.dao;

import java.util.List;

import com.reminder.model.Department;
import com.reminder.response.model.DepartmentResponse;


public interface DepartmentDAO
{
    public void createDepartment(Department department);
    
    public Department getDepartmentById(int id);
    
    public List<Department> getAllDepartments(int userId);
    
    public void updateDepartment(Department department);
    
    public void deleteDepartment(int departmentId);

	public DepartmentResponse getSection(int deptId);

	public List<Department> getAllDepartments();
    
   
}
