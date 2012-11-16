package com.aduyng.textbooktrading.android.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class College {
	private long id; 
	private String name; 
	private String logoUrl; 
	private String logoSmallUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getLogoSmallUrl() {
		return logoSmallUrl;
	}
	public void setLogoSmallUrl(String logoSmallUrl) {
		this.logoSmallUrl = logoSmallUrl;
	} 
	
	public static College fromJSONObject(JSONObject jsonObject) throws JSONException{
		College college = new College();
		college.setId(jsonObject.getLong("id"));
		college.setName(jsonObject.getString("name"));
		college.setLogoUrl(jsonObject.getString("logoUrl"));
		college.setLogoSmallUrl(jsonObject.getString("logoSmallUrl"));
		
		return college;
	}
	
	
	
	
	
	
}
