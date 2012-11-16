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
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class TextbookServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			// AppAccount appAccount = new AppAccount();
			// appAccount.setName("Android 1.0");
			// appAccount.setKey("Ct9zBovhmb");
			// appAccount.save(pm);
			// Query query = pm.newQuery(Textbook.class);
			// List<Textbook> textbooks = (List<Textbook>) query.execute();
			// for(Textbook t: textbooks){
			// t.setCollegeId(16002);
			// t.save(pm);
			// }

			HttpServletRequestWrapper request = new HttpServletRequestWrapper(
					req);

			Long id = request.getLong("id", -1);
			JSONObject jsonObject = new JSONObject();

			

			if (0 < id) {
				Textbook textbook = pm.getObjectById(Textbook.class, id);

				jsonObject = textbook.toJSONObject();

				// phoneNumber
				String clientPhoneNumber = request.getString("clientPhoneNumber");
				if (clientPhoneNumber == null || clientPhoneNumber.length() == 0) {
					throw new Exception("clientPhoneNumber is missing");
				}
				
				FavoriteTextbook fav = FavoriteTextbook.getByPhoneNumberAndTextbookId(pm,
							clientPhoneNumber, textbook.getId());
				
				if (fav != null) {
					jsonObject.put("favorite", fav.toJSONObject());
				}
				
				jsonObject.put("seller", textbook.getSeller(pm).toJSONObject());

			} else {
				Query query = pm.newQuery(Textbook.class);

				String filter = "";
				String term = request.getString("term");
				if (term != null && term.trim().length() > 0) {
					filter += " this.tags.contains(tagsParam) &&";
					query.declareImports("import java.util.Set");
				}

				query.setOrdering("this.datePosted desc");

				int pageNumber = request.getPositiveInteger("pageNumber", 0);
				int numberOfRecordsPerPage = request.getPositiveInteger(
						"numberOfRecordsPerPage", 20);
				query.setRange(pageNumber * numberOfRecordsPerPage,
						(pageNumber + 1) * numberOfRecordsPerPage);

				// isSold
				filter += " this.isSold == " + request.getBoolean("isSold")
						+ " &&";

				// isBuyingRequestParam
				filter += " this.isBuyingRequest == "
						+ request.getBoolean("isBuyingRequest") + " &&";

				// phoneNumber
				String phoneNumber = request.getString("phoneNumber");

				// TODO: fix this issue. find the way to include collegeId, the
				// client needs to submit the collegeId when adding new record
				long collegeId = request.getLong("collegeId", 0);
				if (collegeId <= 0) {
					throw new Exception("collegeId is missing or invalid");

				}
				filter += " this.collegeId == " + collegeId + " &&";

				// term
				List<Textbook> textbooks = null;

				if (term != null && term.trim().length() > 0) {
					if (phoneNumber != null && phoneNumber.length() > 0) {
						filter += " this.phoneNumber == phoneNumberParam &&";
						query.declareParameters("String phoneNumberParam, String tagsParam");
						query.setFilter(filter.replaceAll("&&$", ""));
						textbooks = (List<Textbook>) query.execute(phoneNumber,
								term.toLowerCase());
					} else {
						query.declareParameters("String tagsParam");
						query.setFilter(filter.replaceAll("&&$", ""));
						textbooks = (List<Textbook>) query.execute(term
								.toLowerCase());
					}

				} else {
					if (phoneNumber != null && phoneNumber.length() > 0) {
						filter += " this.phoneNumber == phoneNumberParam &&";
						query.declareParameters("String phoneNumberParam");
						query.setFilter(filter.replaceAll("&&$", ""));
						textbooks = (List<Textbook>) query.execute(phoneNumber);
					} else {
						query.setFilter(filter.replaceAll("&&$", ""));
						textbooks = (List<Textbook>) query.execute();
					}
				}

				JSONArray jsonArray = new JSONArray();
				if (!textbooks.isEmpty()) {
					for (Textbook textbook : textbooks) {
						JSONObject o = textbook.toJSONObject();
//						FavoriteTextbook fav = FavoriteTextbook
//								.getByPhoneNumberAndTextbookId(pm,
//										clientPhoneNumber, textbook.getId());
//
//						if (fav != null) {
//							o.put("favorite", fav.toJSONObject());
//						}
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

			Long id = request.getLong("id");

			Textbook textbook = null;

			String oldImageBlobKey = null;
			if (0 < id) {
				textbook = pm.getObjectById(Textbook.class, id);
				oldImageBlobKey = textbook.getImageBlobKey();
			} else {
				textbook = new Textbook();
			}

			textbook.setAuthors(request.getString("authors"));
			textbook.setBuyingRequest(request.getBoolean("isBuyingRequest"));
			textbook.setDescription(request.getString("description"));
			textbook.setIsbn10(request.getString("isbn10"));
			textbook.setIsbn13(request.getString("isbn13"));
			textbook.setImageBlobKey(request.getString("imageBlobKey"));
			textbook.setPrice(request.getDouble("price", -1));
			textbook.setPublishers(request.getString("publishers"));
			textbook.setPhoneNumber(request.getString("phoneNumber"));
			textbook.setSold(request.getBoolean("isSold"));
			textbook.setTitle(request.getString("title"));
			textbook.setCollegeId(request.getLong("collegeId"));

			textbook.save(pm);

			if (null != oldImageBlobKey
					&& !textbook.getImageBlobKey().equals(oldImageBlobKey)) {
				// remove the image old image
				BlobstoreService blobstoreService = BlobstoreServiceFactory
						.getBlobstoreService();
				blobstoreService.delete(new BlobKey[] { new BlobKey(
						oldImageBlobKey) });
			}

			resp.getWriter().println(textbook + " has been saved");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			pm.close();
		}

	}

	// @Override
	// protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
	// throws ServletException, IOException {
	// if( !AppAccountServlet.checkKey(req, resp)){
	// return;
	// }
	// PersistenceManager pm = PMF.get().getPersistenceManager();
	// try {
	// HttpServletRequestWrapper request = new HttpServletRequestWrapper(
	// req);
	//
	//
	// Long id = request.getLong("id");
	// Textbook textbook = pm.getObjectById(Textbook.class, id);
	//
	// //remove the image
	// String blobKey = textbook.getImageBlobKey();
	// BlobstoreService blobstoreService = BlobstoreServiceFactory
	// .getBlobstoreService();
	// blobstoreService.delete(new BlobKey[]{new BlobKey(blobKey)});
	//
	// pm.deletePersistent(textbook);
	// resp.getWriter().println("textbook has been deleted");
	// } catch (Exception e) {
	// resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	// } finally {
	// pm.close();
	// }
	// }

}
