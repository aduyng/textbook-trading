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

import com.aduyng.textbooktrading.gea.db.FavoriteTextbook;
import com.aduyng.textbooktrading.gea.db.PMF;
import com.aduyng.textbooktrading.gea.db.Textbook;
import com.google.appengine.api.datastore.PhoneNumber;

@SuppressWarnings("serial")
public class FavoriteTextbookServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id", -1);
			JSONObject jsonObject = new JSONObject();
			
			

			if (0 < id) {
				FavoriteTextbook favoriteTextbook = pm.getObjectById(
						FavoriteTextbook.class, id);

				jsonObject = favoriteTextbook.toJSONObject();

			} else {

				String phoneNumber = request.getString("phoneNumber");

				if (phoneNumber == null || phoneNumber.trim().length() == 0) {
					throw new Exception("phone number is missing");
				}
				Query query = pm.newQuery(FavoriteTextbook.class);

				int pageNumber = request.getPositiveInteger("pageNumber", 0);
				int numberOfRecordsPerPage = request.getPositiveInteger(
						"numberOfRecordsPerPage", 20);
				query.setRange(pageNumber * numberOfRecordsPerPage,
						(pageNumber + 1) * numberOfRecordsPerPage);

				query.setFilter("phoneNumber == phoneNumberParam");
				query.declareParameters("String phoneNumberParam");

				List<FavoriteTextbook> favoriteTextbooks = (List<FavoriteTextbook>) query
						.execute(phoneNumber);

				JSONArray jsonArray = new JSONArray();
				if (!favoriteTextbooks.isEmpty()) {
					for (FavoriteTextbook favoriteTextbook : favoriteTextbooks) {
						JSONObject o = favoriteTextbook.toJSONObject();
						o.put("textbook", favoriteTextbook.getTextbook(pm)
								.toJSONObject());
						jsonArray.add(o);
					}
				}
				jsonObject.put("records", jsonArray);
			}
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
		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id", -1);
			FavoriteTextbook favoriteTextbook = null;
			if (id > 0) {
				favoriteTextbook = pm.getObjectById(FavoriteTextbook.class, id);
			}else{
				favoriteTextbook = new FavoriteTextbook();
			}

			favoriteTextbook.setTextbookId(request.getLong("textbookId"));
			favoriteTextbook.setPhoneNumber(request.getString("phoneNumber"));
			favoriteTextbook.save(pm);

			resp.getWriter().println(favoriteTextbook + " has been saved");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id", -1);
			String phoneNumber = request.getString("phoneNumber");
			long textbookId = request.getLong("textbookId", -1);
			FavoriteTextbook favoriteTextbook = null;
			if( id > 0 ){
				favoriteTextbook = pm.getObjectById(
						FavoriteTextbook.class, id);
			}else if(phoneNumber != null && phoneNumber.length() > 0 && textbookId > 0 ){
				favoriteTextbook = FavoriteTextbook.getByPhoneNumberAndTextbookId(pm, phoneNumber, textbookId);
			}
			
					

			if( favoriteTextbook == null){
				throw new Exception("Favorite item is not found");
			}

			pm.deletePersistent(favoriteTextbook);
			resp.getWriter().println("Favorite Textbook has been deleted");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}
	}

}
