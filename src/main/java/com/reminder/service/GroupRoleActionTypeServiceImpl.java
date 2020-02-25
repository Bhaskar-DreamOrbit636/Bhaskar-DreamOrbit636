package com.reminder.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.GroupRoleActionTypeDAO;
import com.reminder.model.GroupRoleActionType;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class GroupRoleActionTypeServiceImpl implements GroupRoleActionTypeService {

	@Autowired
	private GroupRoleActionTypeDAO roleActionDAO;
	@Override
	public void createGroup(GroupRoleActionType groupRoleActionType) {
		roleActionDAO.createGroup(groupRoleActionType);
	}
	
	@Override
	public void deleteGroupActions(GroupRoleActionType groupRoleActionType) {
		roleActionDAO.deleteGroupActions(groupRoleActionType);
	}

	@Override
	public void deleteGroupActions(Set<GroupRoleActionType> groupRolesActionType) {
		roleActionDAO.deleteGroupActions(groupRolesActionType);
	}
}
