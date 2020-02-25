package com.reminder.response.model;

import java.util.List;

public class MyContractResponse {
	private List<ContractResponse> contractResponse;
	private long count;
	private String userName;
	
	public MyContractResponse(){
		
	}
	
	public MyContractResponse(List<ContractResponse> contractResponse, int count) {
		super();
		this.contractResponse = contractResponse;
		this.count = count;
	}
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<ContractResponse> getContract() {
		return contractResponse;
	}
	public void setContact(List<ContractResponse> contractResponse) {
		this.contractResponse = contractResponse;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
}
