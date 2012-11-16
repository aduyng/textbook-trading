package com.aduyng.textbooktrading.android.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class Account {
	private String phoneNumber;
	private long collegeId;
	private boolean isTextable;
	private boolean isCallable;

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

	public boolean isTextable() {
		return isTextable;
	}

	public void setTextable(boolean isTextable) {
		this.isTextable = isTextable;
	}

	public boolean isCallable() {
		return isCallable;
	}

	public void setCallable(boolean isCallable) {
		this.isCallable = isCallable;
	}



	public static Account fromJSONObject(JSONObject jsonObject) throws JSONException{
		Account account = new Account();
		account.setPhoneNumber(jsonObject.getString("phoneNumber"));
		account.setCallable(jsonObject.getBoolean("isCallable"));
		account.setTextable(jsonObject.getBoolean("isTextable"));
		account.setCollegeId(jsonObject.getLong("collegeId"));
		
		return account;
	}

}
