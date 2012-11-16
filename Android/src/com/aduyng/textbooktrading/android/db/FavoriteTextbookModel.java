package com.aduyng.textbooktrading.android.db;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.entity.FavoriteTextbook;

public class FavoriteTextbookModel extends Model {

	public FavoriteTextbookModel(Context context) {
		super(context);
	}

	public FavoriteTextbook getById(long id, boolean noCached) throws Exception {
		String uri = AppConfig.API_BASE_URL + "favorite?id=" + id;
		HttpGet request = new HttpGet(uri);
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, noCached);

		// convert to JSONObject
		return FavoriteTextbook.fromJSONObject(jsonObject);
	}

	public List<FavoriteTextbook> fetch(String phoneNumber, Integer pageNumber, Integer numberOfRecordsPerPage, boolean noCached) throws Exception {
		String url = AppConfig.API_BASE_URL + "favorite?";
		if( null != phoneNumber){
			url += "phoneNumber="
				+ URLEncoder.encode(phoneNumber) + "&";
		}
		
		
		if( null != pageNumber ){
			url += "pageNumber=" + String.valueOf(pageNumber)+ "&";
		}
		
		if( null != numberOfRecordsPerPage ){
			url += "numberOfRecordsPerPage=" + String.valueOf(numberOfRecordsPerPage)+ "&";
		}
		
		HttpGet request = new HttpGet(url);
		
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, noCached);

		if (jsonObject != null) {
			JSONArray records = jsonObject.getJSONArray("records");
			ArrayList<FavoriteTextbook> textbooks = new ArrayList<FavoriteTextbook>();

			for (int i = 0; i < records.length(); i++) {
				textbooks.add(FavoriteTextbook.fromJSONObject(records.getJSONObject(i)));
			}

			return textbooks;
		}
		return null;

	}
	public boolean update(FavoriteTextbook textbook) throws Exception{
		String uri = AppConfig.API_BASE_URL + "favorite";

		HttpPost request = new HttpPost(uri);

		// Add data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("phoneNumber",textbook.getPhoneNumber()));
		nameValuePairs.add(new BasicNameValuePair("id", String
				.valueOf(textbook.getId())));
		
		nameValuePairs.add(new BasicNameValuePair("textbookId", String
				.valueOf(textbook.getTextbookId())));
		
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		return null != new RestClient(context).execute(request, true);
	

	}
	
	public boolean delete(FavoriteTextbook textbook)throws Exception{
		String uri = AppConfig.API_BASE_URL + "favorite?";
		if( textbook.getId() > 0 ){
			uri += "id=" + textbook.getId();
		}else{
			uri += "phoneNumber=" + textbook.getPhoneNumber() + "&textbookId=" + textbook.getTextbookId();
		}

		HttpDelete request = new HttpDelete(uri);

		return null != new RestClient(context).execute(request, true);
	}
}
