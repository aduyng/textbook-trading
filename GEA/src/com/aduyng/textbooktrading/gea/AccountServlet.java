package com.aduyng.textbooktrading.gea;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.aduyng.textbooktrading.gea.db.Account;
import com.aduyng.textbooktrading.gea.db.College;
import com.aduyng.textbooktrading.gea.db.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class AccountServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		if( !AppAccountServlet.checkKey(req, resp)){
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);
			String phoneNumber = request.getString("phoneNumber");

			JSONObject jsonObject = new JSONObject();

			if (null == phoneNumber) {
				throw new Exception("phoneNumber is missing");
			}
			Key k = KeyFactory.createKey(Account.class.getSimpleName(),
					phoneNumber);
			Account account = pm.getObjectById(Account.class, k);

			jsonObject = account.toJSONObject();

			resp.getWriter().print(jsonObject.toString());
			resp.setContentType("application/json");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if( !AppAccountServlet.checkKey(req, resp)){
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			String phoneNumber = request.getString("phoneNumber");

			if (null == phoneNumber) {
				throw new Exception("phoneNumber is missing");
			}
			Key k = KeyFactory.createKey(Account.class.getSimpleName(),
					phoneNumber);
			Account account = null;
			try{
				account = pm.getObjectById(Account.class, k);
			}catch(Exception e){
				account = new Account();
				account.setPhoneNumber(phoneNumber);
			}
			account.setCallable(request.getBoolean("isCallable"));
			account.setTextable(request.getBoolean("isTextable"));
			account.setCollegeId(request.getPositiveInteger("collegeId"));

			account.save(pm);
			resp.getWriter().println(account + " has been saved");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}

	}
}
