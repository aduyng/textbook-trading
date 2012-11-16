package com.aduyng.textbooktrading.android.db;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.entity.Textbook;

public class TextbookModel extends Model {

	public TextbookModel(Context context) {
		super(context);
	}

	public Textbook getById(long id, String requesterPhoneNumber, boolean noCached) throws Exception {
		String uri = AppConfig.API_BASE_URL + "textbook?id=" + id + "&clientPhoneNumber=" + requesterPhoneNumber;
		HttpGet request = new HttpGet(uri);
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, noCached);

		// convert to JSONObject
		return Textbook.fromJSONObject(jsonObject);
	}

	public List<Textbook> fetch(String phoneNumber, String term, Boolean isSold, Boolean isBuyingRequest, Long collegeId, Integer pageNumber, Integer numberOfRecordsPerPage, boolean noCached) throws Exception {
		String url = AppConfig.API_BASE_URL + "textbook?";
		if( null != phoneNumber){
			url += "phoneNumber="
				+ URLEncoder.encode(phoneNumber) + "&";
		}
		if( null != term ){
			url += "term="
					+ URLEncoder.encode(term) + "&";
		}
		
		if( null != isSold ){
			url += "isSold=" + String.valueOf(isSold)+ "&";
		}

		if( null != isBuyingRequest ){
			url += "isBuyingRequest=" + String.valueOf(isBuyingRequest)+ "&";
		}

		if( null != collegeId ){
			url += "collegeId=" + String.valueOf(collegeId)+ "&";
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
			ArrayList<Textbook> textbooks = new ArrayList<Textbook>();

			for (int i = 0; i < records.length(); i++) {
				textbooks.add(Textbook.fromJSONObject(records.getJSONObject(i)));
			}

			return textbooks;
		}
		return null;

	}
	public boolean update(Textbook textbook) throws Exception{
		String uri = AppConfig.API_BASE_URL + "textbook";

		HttpPost request = new HttpPost(uri);

		// Add data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("phoneNumber",textbook.getPhoneNumber()));
		nameValuePairs.add(new BasicNameValuePair("title", textbook
				.getTitle()));
		nameValuePairs.add(new BasicNameValuePair("isbn10", textbook
				.getIsbn10()));
		nameValuePairs.add(new BasicNameValuePair("authors", textbook
				.getAuthors()));
		nameValuePairs.add(new BasicNameValuePair("imageBlobKey",
				textbook.getImageBlobKey()));
		nameValuePairs.add(new BasicNameValuePair("id", String
				.valueOf(textbook.getId())));
		
		nameValuePairs.add(new BasicNameValuePair("collegeId", String
				.valueOf(textbook.getCollegeId())));
		if (textbook.getPublishers() != null
				&& textbook.getPublishers().length() > 0) {
			nameValuePairs.add(new BasicNameValuePair("publishers",
					textbook.getPublishers()));
		}
		if (textbook.getDescription() != null
				&& textbook.getDescription().length() > 0) {
			nameValuePairs.add(new BasicNameValuePair("description",
					textbook.getDescription()));
		}
		nameValuePairs.add(new BasicNameValuePair("price", String
				.valueOf(textbook.getPrice())));
		nameValuePairs.add(new BasicNameValuePair("isSold", String
				.valueOf(textbook.isSold())));
		nameValuePairs.add(new BasicNameValuePair("isBuyingRequest",
				String.valueOf(false)));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		return null != new RestClient(context).execute(request, true);
	

	}
}
