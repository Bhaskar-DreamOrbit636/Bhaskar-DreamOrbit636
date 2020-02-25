package com.reminder.response.model;

import java.math.BigInteger;

public class DashboardModule {
	
	private BigInteger expiringNextMonth;
	
	private BigInteger expiringTotal;
	
	public BigInteger getExpiringNextMonth() {
		return expiringNextMonth;
	}

	public void setExpiringNextMonth(BigInteger expiringNextMonth) {
		this.expiringNextMonth = expiringNextMonth;
	}

	public BigInteger getExpiringTotal() {
		return expiringTotal;
	}

	public void setExpiringTotal(BigInteger expiringTotal) {
		this.expiringTotal = expiringTotal;
	}

}
