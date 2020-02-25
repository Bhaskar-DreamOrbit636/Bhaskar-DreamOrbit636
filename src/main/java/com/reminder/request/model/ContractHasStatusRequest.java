package com.reminder.request.model;

public class ContractHasStatusRequest {

	private int contractHasStatusId;
	private String comments;
	//private int contractReviewerId;
	private int contractStatusId;

	public int getContractHasStatusId() {
		return contractHasStatusId;
	}

	public void setContractHasStatusId(int contractHasStatusId) {
		this.contractHasStatusId = contractHasStatusId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

/*	public int getContractReviewerId() {
		return contractReviewerId;
	}

	public void setContractReviewerId(int contractReviewerId) {
		this.contractReviewerId = contractReviewerId;
	}*/

	public int getContractStatusId() {
		return contractStatusId;
	}

	public void setContractStatusId(int contractStatusId) {
		this.contractStatusId = contractStatusId;
	}

}
