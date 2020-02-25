package com.reminder.response.model;

import java.util.List;

import com.reminder.model.Contract;

public class Contracts {
	
	private List<Contract> contracts;
	
	private long count;

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
