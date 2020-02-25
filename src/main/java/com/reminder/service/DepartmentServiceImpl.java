package com.reminder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.DepartmentDAO;
import com.reminder.model.Department;
import com.reminder.response.model.DepartmentResponse;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class DepartmentServiceImpl implements DepartmentService
{

	@Autowired
    private DepartmentDAO departmentDAO;
    
    @Override
    public void createDepartment(Department department)
    {
    	departmentDAO.createDepartment(department);
    }

    @Override
    public Department getDepartmentById(int departmentId)
    {
        return departmentDAO.getDepartmentById(departmentId);
    }

    @Override
    public List<Department> getAllDepartments(int userId)
    {
        return departmentDAO.getAllDepartments(userId);
    }

    @Override
    public void updateDepartment(Department department)
    {
    	departmentDAO.updateDepartment(department);
    }

    @Override
    public void deleteDepartment(int departmentId)
    {
    	departmentDAO.deleteDepartment(departmentId);
    }

	@Override
	public DepartmentResponse getSection(int deptId) {
		return departmentDAO.getSection(deptId);
	}

	@Override
	public List<Department> getAllDepartments() {
		 return departmentDAO.getAllDepartments();
	}
    
   
}
