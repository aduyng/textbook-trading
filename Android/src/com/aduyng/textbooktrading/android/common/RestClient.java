package com.aduyng.textbooktrading.android.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.aduyng.textbooktrading.android.R;
import com.aduyng.textbooktrading.android.db.HttpCacheAdapter;

public class RestClient {
	Context context;

	public RestClient(Context context) {
		this.context = context;
	}

	public byte[] execute(HttpUriRequest request, boolean noCached)
			throws Exception {
		// append the key to the request
		request.addHeader("textbook-trading-key", AppConfig.APPLICATION_KEY);

		if (!noCached && request instanceof HttpGet) {
			// check the cache
			HttpCacheAdapter adapter = new HttpCacheAdapter(context);
			adapter.open();
			String uri = request.getURI().toString();
			byte[] b = adapter.get(uri);

			adapter.close();

			if (null != b) {
//				Log.i(this.getClass().getSimpleName(), "Load response of "
//						+ uri + " from cache");
				return b;
			}
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			throw new Exception(
					context.getResources()
							.getString(
									R.string.unknown_error_occur));
		} catch (IOException e) {
			throw new Exception(
					context.getResources()
							.getString(
									R.string.malformed_response));
		}

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(statusLine.getReasonPhrase());
		}

		InputStream is = response.getEntity().getContent();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		byte[] b = buffer.toByteArray();

		if (request instanceof HttpGet) {
			HttpCacheAdapter adapter = new HttpCacheAdapter(context);
			adapter.open();
			adapter.set(request.getURI().toString(), b);
			adapter.close();
		}

		return b;

	}

	public String executeString(HttpUriRequest request, boolean noCached)
			throws Exception {
		byte[] b = execute(request, noCached);
		if (null != b) {
			return new String(b);
		}
		return null;
	}

	public JSONObject executeJSONObject(HttpUriRequest request, boolean noCached)
			throws Exception {
		String s = executeString(request, noCached);
		if (null != s) {
			try {
				return new JSONObject(s);
			} catch (JSONException e) {
				new AlertDialog.Builder(context)
						.setTitle(
								R.string.malformed_response)
						.setMessage(e.getMessage())
						.setPositiveButton(
								R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;

									}
								}).show();
			}
		}
		return null;
	}

	public int removeCachedRequest(String uri) {
		HttpCacheAdapter adapter = new HttpCacheAdapter(context);
		adapter.open();
		int result = adapter.remove(uri);
		adapter.close();

//		Log.i(this.getClass().getSimpleName(), "cached version of " + uri
//				+ " has been removed");

		return result;
	}
}
