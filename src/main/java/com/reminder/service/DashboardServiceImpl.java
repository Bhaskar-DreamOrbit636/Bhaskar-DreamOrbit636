package com.reminder.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.AssetDAO;
import com.reminder.dao.ContractDAO;
import com.reminder.dao.DashboardDAO;
import com.reminder.dao.StaffDAO;
import com.reminder.model.Groups;
import com.reminder.response.model.DashboardResponse;
import com.reminder.response.model.GroupRecordExpiring;
import com.reminder.response.model.Month;
import com.reminder.response.model.RecordExpiring;
import com.reminder.utils.SortByMonth;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class DashboardServiceImpl implements DashboardService {
	
	private Logger logger = Logger.getLogger(DashboardServiceImpl.class);

	@Autowired
	private DashboardDAO dashboardDAO;
	
	@Autowired
	private AssetDAO assetDAO;
	
	@Autowired
	private ContractDAO contractDAO;
	
	@Autowired
	private StaffDAO staffDAO;

	@Override
	public DashboardResponse getDashboardContent(int userId) {
		logger.info("getDashboardContent(userId=" + userId + ") - start - Inside getDashboardContent() method");
		List<Integer> userBasedAssetIds = assetDAO.getUserBasedAssetIds(userId);
		List<Integer> userBasedStaffRecordIds = staffDAO.getUserBasedStaffRecordIds(userId);
		List<Integer> userBasedContractIds = contractDAO.getUserBasedContractIds(userId);
		DashboardResponse dashboardResponse = new DashboardResponse();
		List<GroupRecordExpiring> groupRecordExpiringList = null;
		List<Groups> groups = dashboardDAO.getGroupsByUserId(userId);
		Set<Integer> setOfGroupId = new HashSet<>();
		if (groups != null) {
			groupRecordExpiringList = new ArrayList<>();
			for (Groups group : groups) {
				setOfGroupId.add(group.getGroupId());
			}
			if(!setOfGroupId.isEmpty()){
			Map<Integer,Map<Integer, Map<Integer, RecordExpiring>>> result = new HashMap<>();
				List<Object> assetMonthlyCountList = dashboardDAO.getAssetMonthlyCount(setOfGroupId, userBasedAssetIds);
				setRecordExpiring(assetMonthlyCountList, "Assets", result);
				List<Object> contractMonthlyCountList = dashboardDAO.getContractMonthlyCount(setOfGroupId, userBasedContractIds);
				setRecordExpiring(contractMonthlyCountList, "Contract", result);
				List<Object> staffMonthlyCountList = dashboardDAO.getStaffMonthlyCount(setOfGroupId, userBasedStaffRecordIds);
				setRecordExpiring(staffMonthlyCountList, "Staff", result);
				List<Month> monthList = convertMapToList(result);
				if (monthList != null && !monthList.isEmpty()) {
					GroupRecordExpiring groupRecordExpiring = new GroupRecordExpiring();
					//groupRecordExpiring.setGroupId(group.getGroupId());
					//groupRecordExpiring.setGroupName(group.getGroupName());
					groupRecordExpiring.setRecordExpiring(monthList);
					//groupRecordExpiring.setModuleName(group.getModuleType().getModuleType());
					groupRecordExpiringList.add(groupRecordExpiring);
				}
			//}
			}
		}
		dashboardResponse.setGroupRecordExpiringList(groupRecordExpiringList);
		List<Date> assetExpiryDates = dashboardDAO.getExpiryDate("asset", "Assets", userBasedAssetIds);
		List<Date> contractExpiryDates = dashboardDAO.getExpiryDate("contract", "Contract", userBasedContractIds);
		List<Date> staffExpiryDates = dashboardDAO.getExpiryDate("staff_record", "Staff", userBasedStaffRecordIds);
		TreeSet<Date> finalDates = new TreeSet<>();

		if (assetExpiryDates != null) {
			finalDates.addAll(assetExpiryDates);
		}
		if (contractExpiryDates != null) {
			finalDates.addAll(contractExpiryDates);
		}
		if (staffExpiryDates != null) {
			finalDates.addAll(staffExpiryDates);
		}
		dashboardResponse.setExpiryCalendar(finalDates);
		return dashboardResponse;
	}
	
	@Override
	public DashboardResponse getAssetExpiringCount(int userId) {
		List<Integer> userBasedAssetIds = assetDAO.getUserBasedAssetIds(userId);
		DashboardResponse dashboardResponse = new DashboardResponse();
		dashboardResponse.setAssetExpiring(dashboardDAO.getAssetExpiringCount(userId, userBasedAssetIds));
		return dashboardResponse;
	}
	
	@Override
	public DashboardResponse getContractExpiringCount(int userId) {
		List<Integer> userBasedContractIds = contractDAO.getUserBasedContractIds(userId);
		DashboardResponse dashboardResponse = new DashboardResponse();
		dashboardResponse.setContractExpiring(dashboardDAO.getContractExpiringCount(userId, userBasedContractIds));
		return dashboardResponse;
	}

	@Override
	public DashboardResponse getStaffExpiringCount(int userId) {
		List<Integer> userBasedStaffRecordIds = staffDAO.getUserBasedStaffRecordIds(userId);
		DashboardResponse dashboardResponse = new DashboardResponse();
		dashboardResponse.setStaffExpiring(dashboardDAO.getStaffExpiringCount(userId, userBasedStaffRecordIds));
		return dashboardResponse;
	}

	private Map<Integer,Map<Integer, Map<Integer, RecordExpiring>>> setRecordExpiring(List<Object> moduleCountList,
			String moduleName, Map<Integer,Map<Integer, Map<Integer, RecordExpiring>>> result) {
		if (moduleCountList != null) {
			for (Object moduleCountObject : moduleCountList) {
				Object[] moduleCountArray = (Object[]) moduleCountObject;
				Integer year = 0;
				Integer month = 0;
				if (moduleCountArray[0] instanceof BigInteger)
					year = ((BigInteger) moduleCountArray[0]).intValue();
				else if (moduleCountArray[0] instanceof Integer)
					year = ((Integer) moduleCountArray[0]).intValue();
				if (moduleCountArray[1] instanceof BigInteger)
					month = ((BigInteger) moduleCountArray[1]).intValue();
				else if (moduleCountArray[1] instanceof Integer)
					month = ((Integer) moduleCountArray[1]).intValue();
				BigInteger count = (BigInteger) moduleCountArray[2];
				int groupId = (Integer) moduleCountArray[3];
				Map<Integer, RecordExpiring> monthBasedMap;
				Map<Integer, Map<Integer, RecordExpiring>> yearBasedMap = null;
				if(result.containsKey(groupId)){
					yearBasedMap = result.get(groupId);
				if (yearBasedMap.containsKey(year)) {
					monthBasedMap = yearBasedMap.get(year);
					if (monthBasedMap.containsKey(month)) {
						RecordExpiring recordExpiring = monthBasedMap.get(month);
						setRecordExpiringValue(moduleName, count, recordExpiring);
					} else {
						RecordExpiring recordExpiring = new RecordExpiring();
						recordExpiring.setGroupId(groupId);
						recordExpiring.setGroupName((String)moduleCountArray[4]);
						monthBasedMap.put(month, setRecordExpiringValue(moduleName, count, recordExpiring));
					}
				} else {
					monthBasedMap = new HashMap<>();
					RecordExpiring recordExpiring = new RecordExpiring();
					recordExpiring.setGroupId(groupId);
					recordExpiring.setGroupName((String)moduleCountArray[4]);
					monthBasedMap.put(month, setRecordExpiringValue(moduleName, count, recordExpiring));
					yearBasedMap.put(year, monthBasedMap);
				}
				}else{
					yearBasedMap = new HashMap<>();
					monthBasedMap = new HashMap<>();
					RecordExpiring recordExpiring = new RecordExpiring();
					recordExpiring.setGroupId(groupId);
					recordExpiring.setGroupName((String)moduleCountArray[4]);
					monthBasedMap.put(month, setRecordExpiringValue(moduleName, count, recordExpiring));
					yearBasedMap.put(year, monthBasedMap);
					result.put(groupId,yearBasedMap);
				}
			}
		}
		return result;
	}

	private RecordExpiring setRecordExpiringValue(String moduleName, BigInteger count, RecordExpiring recordExpiring) {
		if (moduleName.equalsIgnoreCase("Assets")) {
			recordExpiring.setAssetExpiring(count);
		} else if (moduleName.equalsIgnoreCase("Contract")) {
			recordExpiring.setContractExpiring(count);
		} else if (moduleName.equalsIgnoreCase("Staff")) {
			recordExpiring.setStaffExpiring(count);
		}
		recordExpiring.setModuleName(moduleName);
		return recordExpiring;
	}

	private List<Month> convertMapToList(Map<Integer,Map<Integer, Map<Integer, RecordExpiring>>> result) {
		List<Month> monthList = null;
		for(Map.Entry<Integer,Map<Integer, Map<Integer, RecordExpiring>>> resultvalue : result.entrySet()){
		for (Map.Entry<Integer, Map<Integer, RecordExpiring>> yearValue : result.get(resultvalue.getKey()).entrySet()) {
			for (Map.Entry<Integer, RecordExpiring> monthValue : yearValue.getValue().entrySet()) {
				Month month = new Month();
				month.setGroupId(resultvalue.getKey().intValue());
				month.setYear(yearValue.getKey().intValue());
				month.setMonth(monthValue.getKey().intValue());
				month.setRecordExpiring(monthValue.getValue());
				if (monthList == null)
					monthList = new ArrayList<>();
				monthList.add(month);
			}
		}
	}
		/*if (monthList != null && !monthList.isEmpty()) {
			int startingMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			for (int i = 1; i <= 6; i++) {
				int month = startingMonth;
				if (startingMonth != 12)
					month = (startingMonth) % 12;
				if (!hasMonth(monthList, month)) {
					Month monthObj = new Month();
					//monthObj.setGroupId(groupId);
					monthObj.setMonth(month);
					monthObj.setYear(year);
					monthObj.setRecordExpiring(new RecordExpiring());
					monthList.add(monthObj);
				}
				if (startingMonth == 12)
					year++;
				startingMonth++;
			}
		}*/
		if (monthList != null)
			Collections.sort(monthList, new SortByMonth());
		return monthList;
	}

	private boolean hasMonth(List<Month> monthList, int month) {
		for (Month monthObj : monthList) {
			if (monthObj.getMonth() == month)
				return true;
		}
		return false;
	}

}
