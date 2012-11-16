package com.aduyng.textbooktrading.android.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.entity.College;

public class CollegeModel extends Model {

	public CollegeModel(Context context) {
		super(context);
	}

	public College getById(long id, boolean noCached) throws Exception {
		String uri = AppConfig.API_BASE_URL + "college?id=" + id;
		HttpGet request = new HttpGet(uri);
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, noCached);

		// convert to JSONObject

		return College.fromJSONObject(jsonObject);

	}

	public List<College> fetch() throws Exception {
		String uri = AppConfig.API_BASE_URL + "college";
		HttpGet request = new HttpGet(uri);
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, false);

		if (jsonObject != null) {
			JSONArray records = jsonObject.getJSONArray("records");
			ArrayList<College> colleges = new ArrayList<College>();

			for (int i = 0; i < records.length(); i++) {
				colleges.add(College.fromJSONObject(records.getJSONObject(i)));
			}

			return colleges;
		}
		return null;

	}
	// public boolean update(Account account) {
	// HttpPost httpPost = new HttpPost(AppConfig.API_BASE_URL
	// + "account/save/phoneNumber/" + account.getPhoneNumber());
	//
	// // Add your data
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	// nameValuePairs.add(new BasicNameValuePair("isCallable", String
	// .valueOf(account.isCallable())));
	// nameValuePairs.add(new BasicNameValuePair("isTextable", String
	// .valueOf(account.isTextable())));
	// if (account.getCollegeId() != 0) {
	// nameValuePairs.add(new BasicNameValuePair("collegeId", String
	// .valueOf(account.getCollegeId())));
	// }
	// // try {
	// // httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// //
	// // restClient.execute(httpPost);
	// // return true;
	// //
	// // } catch (UnsupportedEncodingException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	//
	// return false;
	//
	// }
}
