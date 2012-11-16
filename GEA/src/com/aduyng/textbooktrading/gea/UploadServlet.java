package com.aduyng.textbooktrading.gea;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}
		resp.setContentType("text/plain");
		resp.getWriter().println(blobstoreService.createUploadUrl("/upload"));
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!AppAccountServlet.checkKey(req, resp)) {
			return;
		}
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("file");

		BlobKey blobKey = blobKeys.get(0);

		if (blobKey == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"There is no files uploaded");
		} else {
			resp.setContentType("text/plain");
			// ImagesService imagesService = ImagesServiceFactory
			// .getImagesService();
			resp.getWriter().println(blobKey.getKeyString());
		}
	}
}
