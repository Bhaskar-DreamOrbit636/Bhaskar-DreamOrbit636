package com.reminder.model;

import java.io.Serializable;

public class LdapUser implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 9081527761576640803L;

	private String uid;
	private String cn;
	private String sn;
	private String email;
	//private String givenName;
	private String displayNamePrintable;
	private String telephoneNumber;
	private String department;
	
	/**
	 * @return the uid
	 */
	public synchronized final String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public synchronized final void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the cn
	 */
	public synchronized final String getCn() {
		return cn;
	}

	/**
	 * @param cn
	 *            the cn to set
	 */
	public synchronized final void setCn(String cn) {
		this.cn = cn;
	}

	/**
	 * @return the sn
	 */
	public synchronized final String getSn() {
		return sn;
	}

	/**
	 * @param sn
	 *            the sn to set
	 */
	public synchronized final void setSn(String sn) {
		this.sn = sn;
	}

	public synchronized String getDisplayNamePrintable() {
		return displayNamePrintable;
	}

	public synchronized void setDisplayNamePrintable(String displayNamePrintable) {
		this.displayNamePrintable = displayNamePrintable;
	}

	public synchronized String getTelephoneNumber() {
		return telephoneNumber;
	}

	public synchronized void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public synchronized String getDepartment() {
		return department;
	}

	public synchronized void setDepartment(String department) {
		this.department = department;
	}

/*	public synchronized String getGivenName() {
		return givenName;
	}

	public synchronized void setGivenName(String givenName) {
		this.givenName = givenName;
	}*/

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LdapUser [");
		if (uid != null) {
			builder.append("uid=");
			builder.append(uid);
			builder.append(", ");
		}
		if (cn != null) {
			builder.append("cn=");
			builder.append(cn);
			builder.append(", ");
		}
		if (sn != null) {
			builder.append("sn=");
			builder.append(sn);
			builder.append(", ");
		}
		if (email != null) {
			builder.append("email=");
			builder.append(email);
			builder.append(", ");
		}
	/*	if (givenName != null) {
			builder.append("givenName=");
			builder.append(givenName);
			builder.append(", ");
		}*/
		if (displayNamePrintable != null) {
			builder.append("displayNamePrintable=");
			builder.append(displayNamePrintable);
			builder.append(", ");
		}
		if (telephoneNumber != null) {
			builder.append("telephoneNumber=");
			builder.append(telephoneNumber);
			builder.append(", ");
		}
		if (department != null) {
			builder.append("department=");
			builder.append(department);
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}