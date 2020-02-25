package com.reminder.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.reminder.model.GroupRoleActionType;

@Repository
public class GroupRoleActionTypeDAOImpl implements GroupRoleActionTypeDAO {
	
	private Logger logger = Logger.getLogger(GroupRoleActionTypeDAOImpl.class);
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public void createGroup(GroupRoleActionType groupRoleType) {
		try {
			logger.info("inside the createGroup() method");
			logger.error(groupRoleType.getGroupRole());
			entityManager.merge(groupRoleType);
		} catch (HibernateException e) {
			logger.error("Group not created due to error : " , e);
		}
	}
	
	@Override
	public void deleteGroupActions(GroupRoleActionType groupRoleType) {
		try {
			logger.info("inside the createGroup() method");
			logger.error(groupRoleType.getGroupRole());
			entityManager.createNativeQuery("delete from group_role_has_action_type where group_role_action_type_id = ?").setParameter(1, groupRoleType).executeUpdate();
		} catch (HibernateException e) {
			logger.error("GroupActions not deleted due to error : " , e);
		}
	}

	@Override
	public void deleteGroupActions(Set<GroupRoleActionType> groupRolesActionType) {
		List<Integer> ids = groupRolesActionType.stream().map(GroupRoleActionType::getGroupRoleActionTypeId).collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(ids))
		entityManager.createNativeQuery("delete from group_role_has_action_type where group_role_action_type_id in (:ids)").setParameter("ids", ids).executeUpdate();
	}

}
