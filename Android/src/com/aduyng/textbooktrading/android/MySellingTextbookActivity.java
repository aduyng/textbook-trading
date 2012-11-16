package com.aduyng.textbooktrading.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.db.TextbookModel;
import com.aduyng.textbooktrading.android.entity.Textbook;

public class MySellingTextbookActivity extends ListActivity implements
		OnScrollListener, OnClickListener {

	public static final int EDIT_REQUEST_CODE = 1000;
	private static final int NUMBER_OF_RECORDS_PER_PAGE = 10;

	private HashMap<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(
				R.layout.search_listview_header, lv, false);
		lv.addHeaderView(header, null, false);

		searchEditText = (EditText) findViewById(R.id.searchEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);

		clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(this);

		refresh();
		getListView().setOnScrollListener(MySellingTextbookActivity.this);

	}

	EditText searchEditText;
	Button searchButton;
	Button clearButton;

	TextbooksAdapter adapter;
	GettingMySellingTextbooksAsyncTask asyncTask;
	boolean shouldLoadFromCache = false;

	private void loadMore() {
		asyncTask = new GettingMySellingTextbooksAsyncTask();
		asyncTask.execute();
	}

	private class TextbooksAdapter extends BaseAdapter {
		private List<Textbook> textbooks = new ArrayList<Textbook>();
		LayoutInflater inflater = (LayoutInflater) MySellingTextbookActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public void appendData(List<Textbook> textbooks) {
			this.textbooks.addAll(textbooks);
			this.notifyDataSetChanged();
		}

		// public void clear() {
		// this.textbooks = new ArrayList<Textbook>();
		// this.notifyDataSetChanged();
		// }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return textbooks.size();
		}

		@Override
		public Textbook getItem(int position) {
			return textbooks.get(position);
		}

		@Override
		public long getItemId(int position) {
			Textbook item = getItem(position);
			if (null != item)
				return item.getId();
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				rowView = inflater.inflate(R.layout.textbook_row,
						parent, false);
			}

			final Textbook record = getItem(position);

			((TextView) rowView.findViewById(R.id.titleTextView))
					.setText(record.getTitle());

			((TextView) rowView.findViewById(R.id.priceTextView)).setText("$"
					+ record.getPrice());

			((TextView) rowView.findViewById(R.id.timestampTextView))
					.setText(android.text.format.DateUtils
							.getRelativeDateTimeString(
									MySellingTextbookActivity.this,
									record.getDatePosted().getTime(),
									android.text.format.DateUtils.MINUTE_IN_MILLIS,
									android.text.format.DateUtils.WEEK_IN_MILLIS,
									0));

			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.pictureImageView);

			if (cachedImages.containsKey(record.getPictureSmallUrl())) {
				imageView.setImageBitmap(cachedImages.get(record
						.getPictureSmallUrl()));
			} else {

				new DownloadTextbookImageAsyncTask()
						.execute(new RowViewDownloadImage[] { new RowViewDownloadImage(
								(ImageView) rowView
										.findViewById(R.id.pictureImageView),
								record.getPictureSmallUrl()) });
			}
			// attaching event to each row
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent data = new Intent(MySellingTextbookActivity.this,
							SellATextbookActivity.class);
					data.putExtra(SellATextbookActivity.TEXTBOOK_ID,
							record.getId());
					MySellingTextbookActivity.this.startActivityForResult(data,
							MySellingTextbookActivity.EDIT_REQUEST_CODE);

				}
			});

			return rowView;
		}

	}

	private class RowViewDownloadImage {
		ImageView imageView;
		String imageUrl;

		public ImageView getImageView() {
			return imageView;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public RowViewDownloadImage(ImageView imageView, String imageUrl) {
			super();
			this.imageView = imageView;
			this.imageUrl = imageUrl;
		}

	}

	private class DownloadTextbookImageAsyncTask extends
			AsyncTask<RowViewDownloadImage, Void, Bitmap> {
		private RowViewDownloadImage target;

		@Override
		protected Bitmap doInBackground(RowViewDownloadImage... params) {

			try {
				target = params[0];
				HttpGet request = new HttpGet(target.getImageUrl());
				byte[] b = new RestClient(MySellingTextbookActivity.this)
						.execute(request, false);
				// convert to JSONObject
				return BitmapFactory.decodeByteArray(b, 0, b.length);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// collegeLogoImageView.setImageResource(R.drawable.refresh);
			// }
			// });

		};

		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					cachedImages.put(target.getImageUrl(), bitmap);
					target.getImageView().setImageBitmap(bitmap);
				}
			});
		}

	}

	private class GettingMySellingTextbooksAsyncTask extends
			AsyncTask<Void, Void, List<Textbook>> {
		@Override
		protected List<Textbook> doInBackground(Void... params) {
			// isGettingData = true;
			try {
				return new TextbookModel(MySellingTextbookActivity.this)
						.fetch(AppConfig.account.getPhoneNumber(),
								searchEditText.getText().toString(), false,
								false, AppConfig.account.getCollegeId(), pageNumber,
								NUMBER_OF_RECORDS_PER_PAGE,
								!shouldLoadFromCache);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(MySellingTextbookActivity.this)
								.setTitle(
										R.string.operation_failed)
								.setMessage(e.getMessage())
								.setPositiveButton(
										R.string.ok,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												return;

											}
										}).show();
					}
				});
			}
			return null;

		}

		@Override
		protected void onPostExecute(List<Textbook> textbooks) {
			progressDialog.dismiss();

			hasMoreRecords = (null != textbooks)
					&& (textbooks.size() == NUMBER_OF_RECORDS_PER_PAGE);

			if (null != textbooks && textbooks.size() > 0) {
				adapter.appendData(textbooks);
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					MySellingTextbookActivity.this,
					getResources().getText(
							R.string.getting_your_selling_textbooks),
					getResources().getText(R.string.please_wait), true, false);
		}

	}

	ProgressDialog progressDialog;
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.my_selling_textbooks, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//		case R.id.sellATextbookMenuItem:
//			Intent intent = new Intent(this, SellATextbookActivity.class);
//			startActivityForResult(intent, EDIT_REQUEST_CODE);
//			return true;
//
//		case R.id.refreshMenuItem:
//			refresh();
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	private boolean hasMoreRecords = true;
	private int pageNumber = 0;

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount,
			int totalCount) {
		if (hasMoreRecords
				&& (asyncTask == null || asyncTask.getStatus() != Status.RUNNING)) {
			boolean loadMore = firstVisible + visibleCount >= totalCount;
			if (loadMore) {
				pageNumber++;
				loadMore();
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case EDIT_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				// adapter = new TextbooksAdapter();
				// setListAdapter(adapter);
				refresh();

			}
		}
	}

	private void refresh() {
		adapter = new TextbooksAdapter();
		setListAdapter(adapter);
		pageNumber = 0;
		shouldLoadFromCache = false;
		loadMore();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchButton:
			adapter = new TextbooksAdapter();
			setListAdapter(adapter);
			pageNumber = 0;
			loadMore();
			break;
		case R.id.clearButton:
			searchEditText.setText("");
			adapter = new TextbooksAdapter();
			setListAdapter(adapter);
			pageNumber = 0;
			loadMore();
			break;
		default:
			break;
		}

	}
}