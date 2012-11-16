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
import android.widget.ImageView;
import android.widget.TextView;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.db.FavoriteTextbookModel;
import com.aduyng.textbooktrading.android.entity.FavoriteTextbook;
import com.aduyng.textbooktrading.android.entity.Textbook;

public class FavoriteTextbookActivity extends ListActivity implements
		OnScrollListener {

	public static final int EDIT_REQUEST_CODE = 2000;
	private static final int NUMBER_OF_RECORDS_PER_PAGE = 10;

	private HashMap<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		refresh();
		getListView().setOnScrollListener(FavoriteTextbookActivity.this);

	}

	FavoriteTextbooksAdapter adapter;
	GettingMySellingFavoriteTextbooksAsyncTask asyncTask;
	boolean shouldLoadFromCache = false;

	private void loadMore() {
		asyncTask = new GettingMySellingFavoriteTextbooksAsyncTask();
		asyncTask.execute();
	}

	private class FavoriteTextbooksAdapter extends BaseAdapter {
		private List<FavoriteTextbook> favoriteTextbooks = new ArrayList<FavoriteTextbook>();
		LayoutInflater inflater = (LayoutInflater) FavoriteTextbookActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public void appendData(List<FavoriteTextbook> favoriteTextbooks) {
			this.favoriteTextbooks.addAll(favoriteTextbooks);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return favoriteTextbooks.size();
		}

		@Override
		public FavoriteTextbook getItem(int position) {
			return favoriteTextbooks.get(position);
		}

		@Override
		public long getItemId(int position) {
			FavoriteTextbook item = getItem(position);
			if (null != item)
				return item.getId();
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				rowView = inflater
						.inflate(R.layout.textbook_row, parent, false);
			}

			FavoriteTextbook favoriteTextbook = getItem(position);
			final Textbook record = favoriteTextbook.getTextbook();

			((TextView) rowView.findViewById(R.id.titleTextView))
					.setText(record.getTitle());

			((TextView) rowView.findViewById(R.id.priceTextView)).setText("$"
					+ record.getPrice());

			((TextView) rowView.findViewById(R.id.timestampTextView))
					.setText(android.text.format.DateUtils
							.getRelativeDateTimeString(
									FavoriteTextbookActivity.this,
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

				new DownloadFavoriteTextbookImageAsyncTask()
						.execute(new RowViewDownloadImage[] { new RowViewDownloadImage(
								(ImageView) rowView
										.findViewById(R.id.pictureImageView),
								record.getPictureSmallUrl()) });
			}
			// attaching event to each row
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent data = new Intent(FavoriteTextbookActivity.this,
							ViewASellingTextbookActivity.class);
					data.putExtra(ViewASellingTextbookActivity.TEXTBOOK_ID,
							record.getId());
					FavoriteTextbookActivity.this.startActivityForResult(data,
							FavoriteTextbookActivity.EDIT_REQUEST_CODE);

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

	private class DownloadFavoriteTextbookImageAsyncTask extends
			AsyncTask<RowViewDownloadImage, Void, Bitmap> {
		private RowViewDownloadImage target;

		@Override
		protected Bitmap doInBackground(RowViewDownloadImage... params) {

			try {
				target = params[0];
				HttpGet request = new HttpGet(target.getImageUrl());
				byte[] b = new RestClient(FavoriteTextbookActivity.this)
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

	private class GettingMySellingFavoriteTextbooksAsyncTask extends
			AsyncTask<Void, Void, List<FavoriteTextbook>> {
		@Override
		protected List<FavoriteTextbook> doInBackground(Void... params) {
			// isGettingData = true;
			try {
				return new FavoriteTextbookModel(FavoriteTextbookActivity.this)
						.fetch(AppConfig.account.getPhoneNumber(), pageNumber,
								NUMBER_OF_RECORDS_PER_PAGE,
								!shouldLoadFromCache);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(FavoriteTextbookActivity.this)
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
		protected void onPostExecute(List<FavoriteTextbook> favoriteTextbooks) {
			progressDialog.dismiss();

			hasMoreRecords = (null != favoriteTextbooks)
					&& (favoriteTextbooks.size() == NUMBER_OF_RECORDS_PER_PAGE);

			if (null != favoriteTextbooks && favoriteTextbooks.size() > 0) {
				adapter.appendData(favoriteTextbooks);
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					FavoriteTextbookActivity.this,
					getResources().getText(
							R.string.getting_your_favorite_textbooks),
					getResources().getText(R.string.please_wait), true, false);
		}

	}

	ProgressDialog progressDialog;
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.my_selling_favoriteTextbooks, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.sellAFavoriteTextbookMenuItem:
	// Intent intent = new Intent(this, SellAFavoriteTextbookActivity.class);
	// startActivityForResult(intent, EDIT_REQUEST_CODE);
	// return true;
	//
	// case R.id.refreshMenuItem:
	// refresh();
	// return true;
	//
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

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
				refresh();
			}
		}
	}

	private void refresh() {
		adapter = new FavoriteTextbooksAdapter();
		setListAdapter(adapter);
		pageNumber = 0;
		shouldLoadFromCache = false;
		loadMore();

	}

}