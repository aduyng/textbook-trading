package com.aduyng.textbooktrading.android;

import java.util.List;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.ImageUploadHelper;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.db.IsbnDbLookupModel;
import com.aduyng.textbooktrading.android.db.TextbookModel;
import com.aduyng.textbooktrading.android.entity.Book;
import com.aduyng.textbooktrading.android.entity.Textbook;
import com.google.zxing.integration.android.IntentIntegrator;

public class SellATextbookActivity extends Activity implements OnClickListener {
	private static final int SEARCH_BOOK_REQUEST_CODE = 1001;
	private static final int CAMERA_PIC_REQUEST_CODE = 1337;
	private static final int GALLERY_PIC_REQUEST_CODE = 1338;

	public static final String TEXTBOOK_ID = "id";

	Button saveButton = null;
	Button deleteButton = null;
	Button cancelButton = null;
	Button searchButton = null;
	Button scanISBNButton = null;
	ImageView pictureImageView = null;
	EditText titleEditText = null;
	EditText isbnEditText = null;
	EditText authorsEditText = null;
	EditText priceEditText = null;
	EditText publishersEditText = null;
	EditText descriptionEditText = null;

	Button takeAPictureButton = null;
	Button selectFromGalleryButton = null;

	Textbook textbook = null;
	boolean isPictureSelected = false;
	Bitmap selectedPicture = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sell_a_textbook);
		// tryUpload();

		saveButton = (Button) findViewById(R.id.saveButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		searchButton = (Button) findViewById(R.id.seachButton);
		scanISBNButton = (Button) findViewById(R.id.scanISBNButton);
		titleEditText = (EditText) findViewById(R.id.titleEditText);
		isbnEditText = (EditText) findViewById(R.id.isbnEditText);
		authorsEditText = (EditText) findViewById(R.id.authorsEditText);
		publishersEditText = (EditText) findViewById(R.id.publishersEditText);
		priceEditText = (EditText) findViewById(R.id.priceEditText);
		descriptionEditText = (EditText) findViewById(R.id.additionalInformationEditText);
		pictureImageView = (ImageView) findViewById(R.id.pictureImageView);
		takeAPictureButton = (Button) findViewById(R.id.takeAPictureButton);
		selectFromGalleryButton = (Button) findViewById(R.id.selectFromGalleryButton);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		scanISBNButton.setOnClickListener(this);
		selectFromGalleryButton.setOnClickListener(this);
		takeAPictureButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();
		long textbookId = 0;
		if (null != bundle) {
			textbookId = bundle.getLong(TEXTBOOK_ID, 0);
		} else {
			textbookId = 0;
		}

		if (textbookId == 0) {
			textbook = new Textbook();
			
			deleteButton.setVisibility(View.GONE);

		} else {

			new GettingTextbookAsyncTask().execute(new Long[] { textbookId });
		}

	}

	private void dataToControlValues() {
		titleEditText.setText(textbook.getTitle());
		isbnEditText.setText(textbook.getIsbn10());
		authorsEditText.setText(textbook.getAuthors());
		publishersEditText.setText(textbook.getPublishers());
		priceEditText.setText(String.valueOf(textbook.getPrice()));
		descriptionEditText.setText(textbook.getDescription());

		// setting the image
		if (textbook.getPictureUrl() != null
				&& textbook.getPictureUrl().length() > 0) {
			new DownloadTextbookPictureAsyncTask().execute();
		}
		isPictureSelected = false;

	}

	private class DownloadTextbookPictureAsyncTask extends
			AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... arg0) {

			try {
				HttpGet request = new HttpGet(textbook.getPictureUrl().trim());
				byte[] b = new RestClient(SellATextbookActivity.this).execute(
						request, false);
				return BitmapFactory.decodeByteArray(b, 0, b.length);

			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellATextbookActivity.this)
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

	private boolean controlValuesToData() {
		textbook.setPhoneNumber(AppConfig.account.getPhoneNumber());
		textbook.setCollegeId(AppConfig.account.getCollegeId());

		String val = titleEditText.getText().toString().trim();
		if (null == val || val.length() == 0) {
			Toast.makeText(SellATextbookActivity.this,
					R.string.title_is_missing, Toast.LENGTH_LONG).show();
			titleEditText.requestFocus();
			return false;
		}
		textbook.setTitle(val);

		val = isbnEditText.getText().toString().trim();
		if (null == val || val.length() == 0) {
			Toast.makeText(SellATextbookActivity.this,
					R.string.isbn_is_missing, Toast.LENGTH_LONG).show();
			isbnEditText.requestFocus();
			return false;
		}
		textbook.setIsbn10(val);

		val = authorsEditText.getText().toString().trim();
		if (null == val || val.length() == 0) {
			Toast.makeText(SellATextbookActivity.this,
					R.string.authors_is_missing, Toast.LENGTH_LONG).show();
			authorsEditText.requestFocus();
			return false;
		}
		textbook.setAuthors(val);

		textbook.setPublishers(publishersEditText.getText().toString());

		try {
			textbook.setPrice(Double.parseDouble(priceEditText.getText()
					.toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(SellATextbookActivity.this,
					R.string.price_is_missing, Toast.LENGTH_LONG).show();
			priceEditText.requestFocus();
			return false;
		}
		textbook.setDescription(descriptionEditText.getText().toString().trim());

		if (!isPictureSelected
				&& (textbook.getPictureUrl() == null || textbook
						.getPictureUrl().length() == 0)) {
			Toast.makeText(SellATextbookActivity.this,
					R.string.picture_is_missing, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;

	}

	private class SaveTextbookAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// perform upload
				if (isPictureSelected) {
//					pictureImageView.buildDrawingCache();
//					Bitmap bitmap = pictureImageView.getDrawingCache();

					String imageBlobKey = new ImageUploadHelper(
							SellATextbookActivity.this).upload(selectedPicture);

					textbook.setImageBlobKey(imageBlobKey);
				}

				return new TextbookModel(SellATextbookActivity.this)
						.update(textbook);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellATextbookActivity.this)
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
				Toast.makeText(SellATextbookActivity.this,
						R.string.your_textbook_has_been_listed_successfully,
						Toast.LENGTH_LONG).show();
				setResult(RESULT_OK);

				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SellATextbookActivity.this,
					getResources().getText(R.string.saving_your_textbook),
					getResources().getText(R.string.please_wait), true, false);
		}

	}

	private class DeleteTextbookAsyncTask extends
			AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {

//				String uri = AppConfig.API_BASE_URL + "textbook?id="
//						+ URLEncoder.encode(String.valueOf(textbook.getId()));
				textbook.setSold(true);
				return new TextbookModel(SellATextbookActivity.this).update(textbook);
//				HttpDelete request = new HttpDelete(uri);
//				byte[] b = new RestClient(SellATextbookActivity.this).execute(
//						request, true);
//				return null != b;
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellATextbookActivity.this)
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

			return false;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result) {
				Toast.makeText(SellATextbookActivity.this,
						R.string.your_textbook_has_been_deleted,
						Toast.LENGTH_LONG).show();
				setResult(RESULT_OK);

				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SellATextbookActivity.this,
					getResources().getText(R.string.deleting_your_textbook),
					getResources().getText(R.string.please_wait), true, false);
		}

	}

	private class GettingTextbookAsyncTask extends
			AsyncTask<Long, Void, Textbook> {
		@Override
		protected Textbook doInBackground(Long... textbookIds) {

			try {
				return new TextbookModel(SellATextbookActivity.this).getById(
						textbookIds[0], AppConfig.account.getPhoneNumber(), false);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellATextbookActivity.this)
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
												finish();

											}
										}).show();

					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Textbook result) {
			progressDialog.dismiss();
			textbook = result;
			dataToControlValues();

		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					SellATextbookActivity.this,
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
		case R.id.saveButton:
			if (controlValuesToData()) {
				SaveTextbookAsyncTask task = new SaveTextbookAsyncTask();
				task.execute();
			}
			break;
		case R.id.cancelButton:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case R.id.deleteButton:
			new AlertDialog.Builder(this)
					.setTitle(R.string.delete_your_textbook)
					.setMessage(
							getResources()
									.getString(
											R.string.are_you_sure_you_want_delete_this_textbook))
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											new DeleteTextbookAsyncTask()
													.execute();
										}
									});
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).show();

			break;

		case R.id.seachButton:
			Intent intent = new Intent(this, BookSearchActivity.class);
			startActivityForResult(intent, SEARCH_BOOK_REQUEST_CODE);
			break;

		case R.id.scanISBNButton:
			onScanISBNButtonPressed();
			break;

		case R.id.takeAPictureButton:
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST_CODE);
			break;

		case R.id.selectFromGalleryButton:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, GALLERY_PIC_REQUEST_CODE);

		}
	}

	// private void onSearchButtonPressed() {
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// alert.setTitle(R.string.search_for_textbooks);
	// alert.setMessage(R.string.enter_keyword_or_isbn_to_search);
	//
	// // Set an EditText view to get user input
	// final EditText input = new EditText(this);
	// alert.setView(input);
	// // input.setLayoutParams(new Lay)
	//
	// alert.setPositiveButton(R.string.search,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// String value = input.getText().toString();
	// if (value.length() > 0) {
	// Intent i2 = new Intent(SellATextbookActivity.this,
	// BookSearchActivity.class);
	// i2.putExtra(BookSearchActivity.QUERY, value);
	// startActivityForResult(i2, SEARCH_BOOK_REQUEST_CODE);
	// }
	// }
	// });
	//
	// alert.setNegativeButton(R.string.cancel,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// // Canceled.
	// }
	// });
	//
	// alert.show();
	// // see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
	// }

	private void onScanISBNButtonPressed() {
		// start other activity to capture the ISBN first
		// TODO: need to enable the following lines when release
		IntentIntegrator integrator = new IntentIntegrator(
				SellATextbookActivity.this);
		integrator.initiateScan();

		// TODO: need to disable the following line when release
		// onBarcodeScanned("0130976059");
	}

	private void onBarcodeScanned(String barcode) {
		new TextbookISBNLookupAsyncTask().execute(new String[] { barcode });
	}

	private void onTextbookSelected(Book book) {
		String authors = book.getAuthors();
		String isbn = book.getIsbn();
		String title = book.getTitle();
		String publisher = book.getPublisher();

		// set everything to control
		if (null != authors) {
			authorsEditText.setText(authors);
		}

		if (null != isbn) {
			isbnEditText.setText(isbn);
		}

		if (null != title) {
			titleEditText.setText(title);
		}

		if (null != publisher) {
			publishersEditText.setText(publisher);
		}
	}

	private class TextbookISBNLookupAsyncTask extends
			AsyncTask<String, Void, Book> {

		@Override
		protected Book doInBackground(String... queries) {
			try {
				List<Book> books = new IsbnDbLookupModel(
						SellATextbookActivity.this).search(queries[0], 0);
				if (books != null && books.get(0) != null) {
					return books.get(0);
				}
			} catch (Exception e) {
				progressDialog.dismiss();
//				Log.d(this.getClass().getSimpleName(),
//						"ISBN Lookup of " + queries[0]
//								+ " threw exception with message "
//								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Book book) {
			progressDialog.dismiss();

			if (null != book) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						SellATextbookActivity.this.onTextbookSelected(book);

						Toast.makeText(SellATextbookActivity.this,
								R.string.textbook_found, Toast.LENGTH_SHORT)
								.show();

					}
				});

			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SellATextbookActivity.this)

								.setTitle(R.string.searching_for_textbooks)
								.setMessage(
										R.string.no_textbooks_found_with_scanned_barcode)
								.setPositiveButton(R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												return;

											}
										}).show();
					}
				});
			}

		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			progressDialog = ProgressDialog
					.show(SellATextbookActivity.this,
							getResources().getString(
									R.string.searching_for_textbooks),
							getResources().getString(R.string.please_wait),
							true, false);
		}
	}

	// @Override
	// protected void onResume() {
	// if (textbook != null && textbook.getId() == 0
	// && pictureImageView != null) {
	// Resources resources = getResources();
	// if (resources != null) {
	// pictureImageView.setImageDrawable(resources
	// .getDrawable(R.drawable.no_image));
	// }
	// }
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				String barcode = data.getStringExtra("SCAN_RESULT");
				// String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Intent i2 = new Intent(this, BookSearchActivity.class);
				// i2.putExtra(BookSearchActivity.QUERY, contents);
				// startActivityForResult(i2, SEARCH_BOOK_REQUEST_CODE);
				onBarcodeScanned(barcode);
			}
			break;
		case SEARCH_BOOK_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				String authors = data
						.getStringExtra(BookSearchActivity.AUTHORS);
				String isbn = data.getStringExtra(BookSearchActivity.ISBN);
				String title = data.getStringExtra(BookSearchActivity.TITLE);
				String edition = data
						.getStringExtra(BookSearchActivity.PUBLISHERS);

				// set everything to control
				if (null != authors) {
					authorsEditText.setText(authors);
				}

				if (null != isbn) {
					isbnEditText.setText(isbn);
				}

				if (null != title) {
					titleEditText.setText(title);
				}

				if (null != edition) {
					publishersEditText.setText(edition);
				}

			}
			break;
		case CAMERA_PIC_REQUEST_CODE:

			if (resultCode == RESULT_OK) {

				selectedPicture = Bitmap.createScaledBitmap((Bitmap) data
						.getExtras().get("data"), 128, 128, false);

				pictureImageView.setImageBitmap(selectedPicture);
				isPictureSelected = true;
			}
			break;

		case GALLERY_PIC_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				
				try{
					
				


				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2; // for 1/2 the image to be loaded
				selectedPicture = Bitmap.createScaledBitmap(
						BitmapFactory.decodeFile(filePath, options), 128, 128,
						false);

				pictureImageView.setImageBitmap(selectedPicture);
				isPictureSelected = true;
				}catch(Exception e){
					new AlertDialog.Builder(SellATextbookActivity.this)

					.setTitle(R.string.input_error)
					.setMessage(
							R.string.unable_to_load_the_selected_picture)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									return;

								}
							}).show();
				}
			}
			break;

		}
	}
}
