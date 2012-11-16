package com.aduyng.textbooktrading.android;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.db.AccountModel;
import com.aduyng.textbooktrading.android.db.CollegeModel;
import com.aduyng.textbooktrading.android.entity.College;

public class AccountActivity extends Activity implements OnClickListener {
	private static final int SELECT_COLLEGE_REQUEST_CODE = 1000;

	Button saveButton = null;
	Button cancelButton = null;
	Button changeCollegeButton = null;
	ImageView collegeLogoImageView = null;
	TextView collegeNameTextView = null;
	CheckBox isCallableCheckBox = null;
	CheckBox isTextableCheckBox = null;

	College college = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		saveButton = (Button) findViewById(R.id.saveButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		changeCollegeButton = (Button) findViewById(R.id.changeCollegeButton);
		collegeLogoImageView = (ImageView) findViewById(R.id.collegeLogoImageView);
		collegeNameTextView = (TextView) findViewById(R.id.collegeNameTextView);
		isCallableCheckBox = (CheckBox) findViewById(R.id.callableCheckBox);
		isTextableCheckBox = (CheckBox) findViewById(R.id.textableCheckBox);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		changeCollegeButton.setOnClickListener(this);

		isCallableCheckBox.setChecked(AppConfig.account.isCallable());
		isTextableCheckBox.setChecked(AppConfig.account.isTextable());

		// start getting college information
		if (AppConfig.account.getCollegeId() > 0) {
			new DownloadCollegeAsyncTask().execute();
		}

	}

	private boolean controlsToAccount() {
		AppConfig.account.setCallable(this.isCallableCheckBox.isChecked());
		AppConfig.account.setTextable(this.isTextableCheckBox.isChecked());

		if (!AppConfig.account.isCallable() && !AppConfig.account.isTextable()) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.input_error)
					.setMessage(R.string.you_must_have_one_contact_method)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).show();
			return false;
		}

		if (college == null) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.input_error)
					.setMessage(
							R.string.you_must_indicate_your_school_or_college)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).show();
			return false;
		}

		AppConfig.account.setCollegeId(college.getId());

		return true;
	}

	private class SaveAccountAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {

			try {

				return new AccountModel(AccountActivity.this)
						.update(AppConfig.account);

			} catch (final Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(AccountActivity.this)
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

			return false;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(AccountActivity.this,
					getResources().getText(R.string.saving_your_account),
					getResources().getText(R.string.please_wait), true, false);

		};

		@Override
		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();

			if (success) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								AccountActivity.this.getApplicationContext(),
								R.string.your_account_has_been_saved,
								Toast.LENGTH_LONG);

						AccountActivity.this.setResult(RESULT_OK);
						AccountActivity.this.finish();
					}
				});
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(AccountActivity.this)
								.setTitle(R.string.operation_failed)
								.setMessage(R.string.unknown_error_occur)
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
		}

	}

	private class DownloadCollegeAsyncTask extends
			AsyncTask<Void, Void, College> {

		@Override
		protected College doInBackground(Void... arg0) {

			try {
				return new CollegeModel(AccountActivity.this).getById(
						AppConfig.account.getCollegeId(), false);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(AccountActivity.this)
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

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(AccountActivity.this,
					getResources()
							.getText(R.string.getting_college_information),
					getResources().getText(R.string.please_wait), true, false);

			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// collegeNameTextView.setText(getResources().getString(
			// R.string.getting_college_information));
			// }
			// });

		};

		@Override
		protected void onPostExecute(College college) {
			progressDialog.dismiss();

			AccountActivity.this.college = college;
			// AppConfig.account.setCollegeId(college.getId());

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (AccountActivity.this.college != null) {
						collegeNameTextView
								.setText(AccountActivity.this.college.getName());

						// start image download thread
						new DownloadCollegeImageAsyncTask().execute();
					}

				}
			});
		}

	}

	private class DownloadCollegeImageAsyncTask extends
			AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... arg0) {

			try {

				HttpGet request = new HttpGet(college.getLogoUrl());
				byte[] b = new RestClient(AccountActivity.this).execute(
						request, false);
				return BitmapFactory.decodeByteArray(b, 0, b.length);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		// @Override
		// protected void onPreExecute() {
		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// collegeLogoImageView.setImageResource(R.drawable.refresh);
		// }
		// });
		//
		// };

		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					collegeLogoImageView.setImageBitmap(bitmap);
				}
			});
		}

	}

	ProgressDialog progressDialog;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.saveButton:
			if (controlsToAccount()) {
				new SaveAccountAsyncTask().execute();
			}
			break;
		case R.id.cancelButton:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.changeCollegeButton:
			Intent i = new Intent(this, SelectCollegeActivity.class);
			startActivityForResult(i, SELECT_COLLEGE_REQUEST_CODE);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_COLLEGE_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				long collegeId = data.getLongExtra(
						SelectCollegeActivity.COLLEGE_ID, 0);
				AppConfig.account.setCollegeId(collegeId);
				if (collegeId > 0) {
					new DownloadCollegeAsyncTask().execute();
				}
			}
			break;

		}
	}

	// @Override
	// public void onCheckedChanged(CompoundButton buttonView, boolean
	// isChecked) {
	// switch (buttonView.getId()) {
	// case R.id.callableCheckBox:
	// account.setCallable(isChecked);
	// break;
	// case R.id.textableCheckBox:
	// account.setTextable(isChecked);
	// break;
	// }
	//
	// }
}
