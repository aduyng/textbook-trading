package com.aduyng.textbooktrading.gea.db;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;
@PersistenceCapable
public class Account {
	@PrimaryKey
	String phoneNumber;
	
	@Persistent
	long collegeId;
	
	@Persistent
	boolean isCallable;
	
	@Persistent
	boolean isTextable;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(long collegeId) {
		this.collegeId = collegeId;
	}

	public boolean isCallable() {
		return isCallable;
	}

	public void setCallable(boolean isCallable) {
		this.isCallable = isCallable;
	}

	public boolean isTextable() {
		return isTextable;
	}

	public void setTextable(boolean isTextable) {
		this.isTextable = isTextable;
	}

	@Override
	public String toString() {
		return Account.class.getSimpleName() + "[phoneNumber=" + phoneNumber
				+ ",collegeId=" + collegeId + ",isCallable=" + isCallable
				+ ",isTexable=" + isTextable + "]";
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("phoneNumber", phoneNumber);
		jsonObject.put("collegeId", collegeId);
		jsonObject.put("isCallable", isCallable);
		jsonObject.put("isTextable", isTextable);
		return jsonObject;
	}

	private void validate(PersistenceManager pm)
			throws InvalidFieldValueException {

		if (null == phoneNumber ||  phoneNumber.length() == 0) {
			throw new InvalidFieldValueException("phoneNumber is empty");
		}
		phoneNumber = phoneNumber.replace("[^\\d]", "").trim();

		if (phoneNumber.length() == 0) {
			throw new InvalidFieldValueException("phoneNumber is not valid");
		}

		if (collegeId != 0) {
			try {
				getCollege(pm);
			} catch (Exception e) {
				throw new InvalidFieldValueException("collegeId = " + collegeId
						+ " is not found");
			}
		}

	}

	public College getCollege(PersistenceManager pm) {
		return pm.getObjectById(College.class, collegeId);
	}

	public void save(PersistenceManager pm) throws InvalidFieldValueException {
		validate(pm);
		pm.makePersistent(this);
	}

}
