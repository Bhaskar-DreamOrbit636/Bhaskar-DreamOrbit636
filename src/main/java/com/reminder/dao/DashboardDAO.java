package com.reminder.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.reminder.model.Groups;
import com.reminder.response.model.DashboardModule;

public interface DashboardDAO {
	
	public DashboardModule getAssetExpiringCount(int userId, List<Integer> userBasedAssetIds);

	public DashboardModule getStaffExpiringCount(int userId, List<Integer> userBasedStaffRecordIds);

	public DashboardModule getContractExpiringCount(int userId, List<Integer> userBasedContractIds);

	public List<Object> getAssetMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedAssetIds);

	public List<Object> getContractMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedContractIds);

	public List<Object> getStaffMonthlyCount(Set<Integer> setOfGroupId, List<Integer> userBasedStaffRecordIds);

	public List<Groups> getGroupsByUserId(int userId);

	public List<Date> getExpiryDate(String tableName, String moduleName, List<Integer> userBasedIds);

}
