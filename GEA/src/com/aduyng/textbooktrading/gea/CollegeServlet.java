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

import com.aduyng.textbooktrading.gea.db.College;
import com.aduyng.textbooktrading.gea.db.PMF;

@SuppressWarnings("serial")
public class CollegeServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if( !AppAccountServlet.checkKey(req, resp)){
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id", 0);
			JSONObject jsonObject = new JSONObject();

			if (0 < id) {
				College college = pm.getObjectById(College.class, id);

				jsonObject = college.toJSONObject();

			} else {
				Query query = pm.newQuery(College.class);
				int pageNumber = request.getPositiveInteger("pageNumber", 0);
				int numberOfRecordsPerPage = request.getPositiveInteger(
						"numberOfRecordsPerPage", 20);

				query.setRange(pageNumber * numberOfRecordsPerPage,
						numberOfRecordsPerPage);

				List<College> colleges = (List<College>) query.execute();

				if (!colleges.isEmpty()) {
					JSONArray jsonArray = new JSONArray();
					for (College college : colleges) {
						JSONObject o = college.toJSONObject();
						jsonArray.add(o);
					}
					jsonObject.put("records", jsonArray);
				}
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
		if( !AppAccountServlet.checkKey(req, resp)){
			return;
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id");

			College college = null;
			if (0 < id) {
				college = pm.getObjectById(College.class, id);
			} else {
				college = new College();

			}

			college.setLogoBlobKey(request.getString("logoBlobKey"));
			college.setName(request.getString("name"));

			college.save(pm);
			resp.getWriter().println(college + " has been saved");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}

	}
}
