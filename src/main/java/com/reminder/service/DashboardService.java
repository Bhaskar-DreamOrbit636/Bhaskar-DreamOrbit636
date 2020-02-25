package com.reminder.service;

import com.reminder.response.model.DashboardResponse;

public interface DashboardService {
	
	public DashboardResponse getDashboardContent(int userId);

	public DashboardResponse getAssetExpiringCount(int createdUser);

	public DashboardResponse getContractExpiringCount(int createdUser);

	public DashboardResponse getStaffExpiringCount(int createdUser);
	
}
