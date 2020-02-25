package com.reminder.request.model;

public class ContractAggregateReminder {

	private String contractRefNo;
	private String contractTitle;
	private String toList;
	private String ccList;
	private String type;
	private String userName;
	private String expiryDate;
	private String groupName;
	private String oic;
	private int firstCount;
	private int secondCount;
	private int thirdCount;
	private int id;

	public ContractAggregateReminder(String contractRefNo, String contractTitle, String toList, String ccList,
			String type, String userName, String expiryDate, String groupName, String oic, int firstCount, int secondCount,int thirdCount, int id) {
		super();
		this.contractRefNo = contractRefNo;
		this.contractTitle = contractTitle;
		this.toList = toList;
		this.ccList = ccList;
		this.type = type;
		this.userName = userName;
		this.expiryDate = expiryDate;
		this.groupName = groupName;
		this.oic = oic;
		this.firstCount = firstCount;
		this.secondCount = secondCount;
		this.thirdCount = thirdCount;
		this.id = id;
	}

	public String getOic() {
		return oic;
	}

	public void setOic(String oic) {
		this.oic = oic;
	}

	public String getContractRefNo() {
		return contractRefNo;
	}

	public void setContractRefNo(String contractRefNo) {
		this.contractRefNo = contractRefNo;
	}

	public String getContractTitle() {
		return contractTitle;
	}

	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}

	public String getToList() {
		return toList;
	}

	public void setToList(String toList) {
		this.toList = toList;
	}

	public String getCcList() {
		return ccList;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getFirstCount() {
		return firstCount;
	}

	public void setFirstCount(int firstCount) {
		this.firstCount = firstCount;
	}

	public int getSecondCount() {
		return secondCount;
	}

	public void setSecondCount(int secondCount) {
		this.secondCount = secondCount;
	}

	public int getThirdCount() {
		return thirdCount;
	}

	public void setThirdCount(int thirdCount) {
		this.thirdCount = thirdCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
