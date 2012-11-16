package com.aduyng.textbooktrading.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteTextbook {
	long id;
	String phoneNumber;
	long textbookId; 
	Textbook textbook;


	public Textbook getTextbook() {
		return textbook;
	}

	public void setTextbook(Textbook textbook) {
		this.textbook = textbook;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getTextbookId() {
		return textbookId;
	}

	public void setTextbookId(long textbookId) {
		this.textbookId = textbookId;
	}


	@Override
	public String toString() {
		return "FavoriteTextbook [id=" + id + ", phoneNumber=" + phoneNumber
				+ ", textbookId=" + textbookId + ", textbook=" + textbook + "]";
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", id);
		jsonObject.put("phoneNumber", phoneNumber);
		jsonObject.put("textbookId", textbookId);
		
		
		return jsonObject;
	}
	

	
	
	
	public static FavoriteTextbook fromJSONObject(JSONObject jsonObject) throws JSONException {
		FavoriteTextbook favoriteTextbook = new FavoriteTextbook();
		favoriteTextbook.id = jsonObject.getLong("id");
		favoriteTextbook.textbookId = jsonObject.getLong("textbookId");
		favoriteTextbook.phoneNumber = jsonObject.getString("phoneNumber");
		if( jsonObject.has("textbook")){
			favoriteTextbook.textbook = Textbook.fromJSONObject(jsonObject.getJSONObject("textbook"));
		}
		
		return favoriteTextbook;
	}
}

