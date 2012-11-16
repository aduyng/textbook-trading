package com.aduyng.textbooktrading.android;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.db.FavoriteTextbookModel;
import com.aduyng.textbooktrading.android.db.TextbookModel;
import com.aduyng.textbooktrading.android.entity.Account;
import com.aduyng.textbooktrading.android.entity.FavoriteTextbook;
import com.aduyng.textbooktrading.android.entity.Textbook;

public class ViewASellingTextbookActivity extends Activity implements
		OnClickListener {
	public static final String TEXTBOOK_ID = "id";

	Button favoriteButton = null;
	Button callButton = null;
	Button textButton = null;
	ImageView pictureImageView = null;
	TextView titleTextView = null;
	TextView isbnTextView = null;
	TextView authorsTextView = null;
	TextView priceTextView = null;
	TextView publishersTextView = null;
	TextView descriptionTextView = null;
	TextView timestampTextView = null;

	Textbook textbook = null;
	Account seller = null;
	long textbookId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();

		if (null != bundle) {
			textbookId = bundle.getLong(TEXTBOOK_ID, 0);
			if (textbookId > 0) {
				new GettingTextbookAsyncTask().execute();
			}
		} else {
			finish();
		}

		setContentView(R.layout.view_a_selling_textbook);

		favoriteButton = (Button) findViewById(R.id.favoriteButton);
		callButton = (Button) findViewById(R.id.callButton);
		textButton = (Button) findViewById(R.id.textButton);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		isbnTextView = (TextView) findViewById(R.id.isbnTextView);
		authorsTextView = (TextView) findViewById(R.id.authorsTextView);
		publishersTextView = (TextView) findViewById(R.id.publishersTextView);
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		descriptionTextView = (TextView) findViewById(R.id.additionalInformationTextView);
		pictureImageView = (ImageView) findViewById(R.id.pictureImageView);
		timestampTextView = (TextView) findViewById(R.id.timestampTextView);

		favoriteButton.setOnClickListener(this);
		callButton.setOnClickListener(this);
		textButton.setOnClickListener(this);

	}

	private void dataToControlValues() {
		titleTextView.setText(textbook.getTitle());
		isbnTextView.setText("ISBN:" + textbook.getIsbn10());
		authorsTextView.setText("by " + textbook.getAuthors());
		publishersTextView.setText("published by: " + textbook.getPublishers());
		priceTextView.setText("$" + String.valueOf(textbook.getPrice()));
		String description = textbook.getDescription();
		if (description != null) {
			descriptionTextView.setText(description);
		} else {
			descriptionTextView.setVisibility(View.GONE);
		}
		// timestamp
		timestampTextView.setText(android.text.format.DateUtils
				.getRelativeDateTimeString(this, textbook.getDatePosted()
						.getTime(),
						android.text.format.DateUtils.MINUTE_IN_MILLIS,
						android.text.format.DateUtils.WEEK_IN_MILLIS, 0));
		// setting the image
		if (textbook.getPictureUrl() != null
				&& textbook.getPictureUrl().length() > 0) {
			new DownloadTextbookPictureAsyncTask().execute();
		}

		if (seller != null) {
			if (!seller.isCallable()) {
				callButton.setVisibility(View.GONE);
			}

			if (!seller.isTextable()) {
				textButton.setVisibility(View.GONE);
			}
		}

		FavoriteTextbook fav = textbook.getFavoriteTextbook();
		if (fav != null) {
			favoriteButton.setBackgroundResource(R.drawable.star);
			// Log.i(this.getClass().getSimpleName(), "is favorite");
		} else {
			favoriteButton.setBackgroundResource(R.drawable.star_disabled);
			// Log.i(this.getClass().getSimpleName(), "is NOT favorite");
		}
		favoriteButton.setVisibility(View.VISIBLE);

	}

	private class DownloadTextbookPictureAsyncTask extends
			AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... arg0) {

			try {
				HttpGet request = new HttpGet(textbook.getPictureUrl().trim());
				byte[] b = new RestClient(ViewASellingTextbookActivity.this)
						.execute(request, false);
				return BitmapFactory.decodeByteArray(b, 0, b.length);

			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(
								ViewASellingTextbookActivity.this)
								.setTitle(R.string.operation_failed)
								.setMessage(e.getMessage())
								.setPositiveButton(R.string.ok,
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

		// @Override
		// protected void onPreExecute() {
		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// pictureImageView.setImageResource(R.drawable.refresh);
		// }
		// });
		//
		// };

		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					pictureImageView.setImageBitmap(bitmap);
				}
			});
		}

	}

	private class GettingTextbookAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			try {
				textbook = new TextbookModel(ViewASellingTextbookActivity.this)
						.getById(textbookId,
								AppConfig.account.getPhoneNumber(), true);

				seller = textbook.getSeller();
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(
								ViewASellingTextbookActivity.this)
								.setTitle(R.string.operation_failed)
								.setMessage(e.getMessage())
								.setPositiveButton(R.string.ok,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												finish();

											}
										}).show();

					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			dataToControlValues();
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					ViewASellingTextbookActivity.this,
					getResources().getText(
							R.string.getting_textbook_information),
					getResources().getText(R.string.please_wait), true, false);

		};
	}

	private ProgressDialog progressDialog;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.saveButton:
		// if (controlValuesToData()) {
		// SaveTextbookAsyncTask task = new SaveTextbookAsyncTask();
		// task.execute();
		// }
		// break;

		case R.id.callButton:
			String url = "tel:" + textbook.getPhoneNumber();
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
			startActivity(intent);
			break;

		case R.id.textButton:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
					textbook.getPhoneNumber(), null)));
			break;

		case R.id.favoriteButton:
			new FavoriteTextbookAsyncTask().execute();
			break;

		}

	}

	private class FavoriteTextbookAsyncTask extends
			AsyncTask<Void, Void, Boolean> {
		FavoriteTextbook favoriteTextbook;

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				favoriteTextbook = textbook.getFavoriteTextbook();
				if (favoriteTextbook != null) {
					// delete
					boolean result = new FavoriteTextbookModel(
							ViewASellingTextbookActivity.this)
							.delete(favoriteTextbook);
					if (result) {
						favoriteTextbook = null;
					}
					return result;

				} else {
					// add
					favoriteTextbook = new FavoriteTextbook();
					favoriteTextbook.setPhoneNumber(AppConfig.account
							.getPhoneNumber());
					favoriteTextbook.setTextbookId(textbook.getId());
					return new FavoriteTextbookModel(
							ViewASellingTextbookActivity.this)
							.update(favoriteTextbook);
				}
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(
								ViewASellingTextbookActivity.this)
								.setTitle(R.string.operation_failed)
								.setMessage(e.getMessage())
								.setPositiveButton(R.string.ok,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										}).show();

					}
				});
			}

			return false;
			// android.text.format.DateUtils.getR
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result) {
				textbook.setFavoriteTextbook(favoriteTextbook);
				final boolean isFavorite = (favoriteTextbook != null);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (isFavorite) {
							favoriteButton
									.setBackgroundResource(R.drawable.star);
							// Log.i(this.getClass().getSimpleName(),
							// "is favorite");
						} else {
							favoriteButton
									.setBackgroundResource(R.drawable.star_disabled);
							// Log.i(this.getClass().getSimpleName(),
							// "is NOT favorite");
						}

					}
				});
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					ViewASellingTextbookActivity.this,
					getResources().getText(R.string.updating_favorite_items),
					getResources().getText(R.string.please_wait), true, false);
		}

	}
}
