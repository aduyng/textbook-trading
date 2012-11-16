package com.aduyng.textbooktrading.gea;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aduyng.textbooktrading.gea.db.AppAccount;
import com.aduyng.textbooktrading.gea.db.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class AppAccountServlet extends HttpServlet {
	public static boolean checkKey(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String key = req.getHeader("textbook-trading-key");
		if( null == key ){
			key = req.getParameter("key");
		}
		
		if (null == key || key.length() == 0) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"key is missing. Your request must include textbook-trading-key as a header field or \"key\" parameter in the query string");
			return false;
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();
		boolean result = false;
		try {

			Key k = KeyFactory.createKey(AppAccount.class.getSimpleName(), key);
			AppAccount account = pm.getObjectById(AppAccount.class, k);

			result = (null != account) ;

		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Key is not found. Please check your key again!");
			result = false;
		} finally {
			pm.close();
		}
		
		return result;
	}
}
