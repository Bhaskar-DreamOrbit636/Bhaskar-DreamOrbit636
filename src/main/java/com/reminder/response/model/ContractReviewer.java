package com.reminder.response.model;

public class ContractReviewer {
	private int contractReviewerId;
	private UserResponse userId;
	
	
	public int getContractReviewerId() {
		return contractReviewerId;
	}
	public void setContractReviewerId(int contractReviewerId) {
		this.contractReviewerId = contractReviewerId;
	}
	public UserResponse getUserId() {
		return userId;
	}
	public void setUserId(UserResponse userId) {
		this.userId = userId;
	}


}
