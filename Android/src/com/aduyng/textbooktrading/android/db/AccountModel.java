package com.aduyng.textbooktrading.android.db;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.entity.Account;

public class AccountModel extends Model {

	public AccountModel(Context context) {
		super(context);
	}

	public Account getByPhoneNumber(String phoneNumber, boolean noCached)
			throws Exception {
		String uri = AppConfig.API_BASE_URL + "account?phoneNumber="
				+ URLEncoder.encode(phoneNumber);
		HttpGet request = new HttpGet(uri);
		RestClient client = new RestClient(context);

		JSONObject jsonObject = client.executeJSONObject(request, noCached);

		// convert to JSONObject

		return Account.fromJSONObject(jsonObject);

	}

	public boolean update(Account account) throws Exception {

		String uri = AppConfig.API_BASE_URL + "account?phoneNumber="
				+ URLEncoder.encode(account.getPhoneNumber());
		HttpPost request = new HttpPost(uri);

		// Add data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("collegeId", String
				.valueOf(account.getCollegeId())));
		nameValuePairs.add(new BasicNameValuePair("isCallable", String
				.valueOf(account.isCallable())));
		nameValuePairs.add(new BasicNameValuePair("isTextable", String
				.valueOf(account.isTextable())));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		RestClient client = new RestClient(context);

		boolean result = (null != client.execute(request, false));

		if (result) {
			client.removeCachedRequest(uri);
		}

		return result;

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
