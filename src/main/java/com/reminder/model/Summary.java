package com.reminder.model;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

public class Summary {
	
    private List<Date> expiryDate;
	
	private BigInteger expiredAsset;
	
	private BigInteger expiringThisMonth;

	private BigInteger expiringNextMonth;
	
	private BigInteger toBeVerifiedCount;


	public List<Date> getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(List<Date> expiryDate) {
		this.expiryDate = expiryDate;
	}

	public BigInteger getExpiredAsset() {
		return expiredAsset;
	}

	public void setExpiredAsset(BigInteger expiredAsset) {
		this.expiredAsset = expiredAsset;
	}

	public BigInteger getExpiringThisMonth() {
		return expiringThisMonth;
	}

	public void setExpiringThisMonth(BigInteger expiringThisMonth) {
		this.expiringThisMonth = expiringThisMonth;
	}

	public BigInteger getExpiringNextMonth() {
		return expiringNextMonth;
	}

	public void setExpiringNextMonth(BigInteger expiringNextMonth) {
		this.expiringNextMonth = expiringNextMonth;
	}

	public BigInteger getToBeVerifiedCount() {
		return toBeVerifiedCount;
	}

	public void setToBeVerifiedCount(BigInteger toBeVerifiedCount) {
		this.toBeVerifiedCount = toBeVerifiedCount;
	}

}
