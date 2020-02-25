package com.reminder.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.Department;
import com.reminder.model.Section;
import com.reminder.response.model.DepartmentResponse;


@Repository
public class DepartmentDAOImpl implements DepartmentDAO
{
	private Logger logger = Logger.getLogger(DepartmentDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void createDepartment(Department department)
    {
    	try {
			logger.info("inside the createDepartment() method");
			entityManager.clear();
			entityManager.merge(department);
		} catch (HibernateException e) {
			logger.error("Department not created due to error : " + e);
		}
    }

    @Override
    public Department getDepartmentById(int departmentId)
    {
    	Department department = null;
    	try {
			logger.info("fetching Department details of roleId :"+departmentId);
			department = entityManager.find(Department.class,departmentId);
		} catch (HibernateException e) {
			logger.error("No Department fetched due to error : " + e);
		}
        return department;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Department> getAllDepartments(int userId)
    {
    	List<Department> departments = null;
    	try {
			logger.info("inside the getAllDepartments() method");
			departments = entityManager.createQuery("select d from Department d, User u where d.departmentId = u.departments and u.userId =:user_id")
					.setParameter("user_id", userId)
					.getResultList();
		} catch (HibernateException e) {
			logger.error("No Department found and exception ocurred : " + e);
		}
        return departments;
    }

    @Override
    public void updateDepartment(Department department)
    {
    	try {
			logger.info("inside the updateDepartment() method");
			entityManager.merge(department);
		} catch (HibernateException e) {
			logger.error("Department not updated due to Hibernate-exception : " + e);
		}
    }

    @Override
    public void deleteDepartment(int departmentId)
    {
    	try {
			logger.info("inside the deleteDepartment() method and deleting detail of Departmentid : " + departmentId);
			Department d = entityManager.find(Department.class, departmentId);
			entityManager.remove(d);
		} catch (HibernateException e) {
			logger.error("Department details is not deleted : " + e);
		}
    }

	@SuppressWarnings("unchecked")
	@Override
	public DepartmentResponse getSection(int deptId) {
		DepartmentResponse dept = new DepartmentResponse();
		List<Section>  deptRes = new ArrayList<Section>();
		List<Integer> sections = null;
		sections = entityManager.createNativeQuery("select a.section_id from section a where a.department_id =:departmentId")
				.setParameter("departmentId", deptId).getResultList();
		for(int sectionId:sections){
			Section section = entityManager.find(Section.class, sectionId);
			deptRes.add(section);
		}
		dept.setSection(deptRes);
		return dept;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Department> getAllDepartments() {
		List<Department> departments = null;
    	try {
			logger.info("inside the getAllDepartments() method");
			departments = entityManager.createQuery("select d from Department d ")
					.getResultList();
		} catch (HibernateException e) {
			logger.error("No Department found and exception ocurred : " + e);
		}
        return departments;
	}
    
   
   
}
