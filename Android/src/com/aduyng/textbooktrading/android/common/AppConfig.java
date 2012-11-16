package com.aduyng.textbooktrading.android.common;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.aduyng.textbooktrading.android.entity.Account;

public class AppConfig {
	//production api server
	public static final String API_BASE_URL = "http://textbook-trading.appspot.com/";
	//local API server
//	public static final String API_BASE_URL = "http://10.0.2.2:8888/";

	public static String getPhoneNumber(Context context) {
		// getting the phone number
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

//	 public static String getPhoneNumber(Context context) {
//	 return "4587582875";
//	 }

	public static Account account = null;

	public static String APPLICATION_KEY = "Ct9zBovhmb";

}
