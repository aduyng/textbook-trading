package com.aduyng.textbooktrading.android.common;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageUploadHelper extends RestClient {
	public ImageUploadHelper(Context context) {
		super(context);
	}

	private static final String TEXTBOOK_PICTURE_TO_UPLOAD_NAME = "textbook-picture-to-upload.png";

	public String upload(Bitmap bitmap) throws Exception {
		File cacheDir = context.getCacheDir();
		String cacheDirPath = cacheDir.getAbsolutePath();
		String absFilePath = cacheDirPath + "/"
				+ TEXTBOOK_PICTURE_TO_UPLOAD_NAME;

		// compress the picture first
		File f = new File(absFilePath);
		FileOutputStream fOut = new FileOutputStream(f);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();

		HttpGet getRequest = new HttpGet(AppConfig.API_BASE_URL + "upload");
		String postUrl = executeString(getRequest, true);
		if (null == postUrl) {
			throw new Exception("Unable to get the url for uploading image");
		}
		HttpPost postRequest = new HttpPost(postUrl.trim());
		FileBody file = new FileBody(f, "image/png");
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		reqEntity.addPart("file", file);

		postRequest.setEntity(reqEntity);
		String uploadedUrl = executeString(postRequest, true);

		// delete the file
		f.delete();

		if (null != uploadedUrl) {
			return uploadedUrl.trim();
		}
		return null;
	}
}
