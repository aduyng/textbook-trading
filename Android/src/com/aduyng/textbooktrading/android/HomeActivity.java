package com.aduyng.textbooktrading.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aduyng.textbooktrading.android.common.AppConfig;
import com.aduyng.textbooktrading.android.db.AccountModel;
import com.aduyng.textbooktrading.android.entity.Account;

public class HomeActivity extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String phoneNumber = AppConfig.getPhoneNumber(this);

		if (phoneNumber == null || phoneNumber.length() == 0) {
			new AlertDialog.Builder(HomeActivity.this)
					.setTitle(
							R.string.your_device_does_not_meet_minimum_requirement)
					.setMessage(
							R.string.this_application_requires_a_valid_phone_number_to_run)
					.setPositiveButton(
							R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();

								}
							}).show();
		}

		setContentView(R.layout.home);

		if (AppConfig.account == null) {
			new DownloadAccountAsyncTask().execute();
		}

		searchForTextbooksToBuyButton = (Button) findViewById(R.id.searchForTextbooksToBuyButton);
		viewMyFavoriteItemsButton = (Button) findViewById(R.id.viewMyFavoriteItemsButton);
		sellATextbookButton = (Button) findViewById(R.id.sellATextbookButton);
		viewMySellingItemsButton = (Button) findViewById(R.id.viewMySellingItemsButton);
		updateMyAccountInformationButton = (Button) findViewById(R.id.updateMyAccountInformationButton);
		quitButton = (Button) findViewById(R.id.quitButton);

		searchForTextbooksToBuyButton.setOnClickListener(this);
		viewMyFavoriteItemsButton.setOnClickListener(this);
		sellATextbookButton.setOnClickListener(this);
		viewMySellingItemsButton.setOnClickListener(this);
		updateMyAccountInformationButton.setOnClickListener(this);
		quitButton.setOnClickListener(this);
	}

	ProgressDialog progressDialog;
	private Button searchForTextbooksToBuyButton;
	private Button viewMyFavoriteItemsButton;
	private Button sellATextbookButton;
	private Button viewMySellingItemsButton;
	private Button updateMyAccountInformationButton;
	private Button quitButton;

	private boolean isDisconnected = false;

//	@Override
//	protected void onPostResume() {
//		if (!isDisconnected && AppConfig.account != null && AppConfig.account.getCollegeId() == 0) {
//			onClick(updateMyAccountInformationButton);
//		}
//		super.onPostResume();
//	}

	private class DownloadAccountAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			AccountModel model = new AccountModel(HomeActivity.this);
			try {
				AppConfig.account = model.getByPhoneNumber(
						AppConfig.getPhoneNumber(HomeActivity.this), true);
			} catch (final Exception e) {
				// try to create one
				AppConfig.account = new Account();
				AppConfig.account.setPhoneNumber(AppConfig
						.getPhoneNumber(HomeActivity.this));
				AppConfig.account.setCallable(true);
				AppConfig.account.setTextable(false);
				try {
					model.update(AppConfig.account);
				} catch (Exception e1) {
					HomeActivity.this.isDisconnected = true;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(HomeActivity.this)
									.setMessage(R.string.account_initialization_failed)
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

					e1.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(HomeActivity.this,
					getResources().getText(R.string.initializing),
					getResources().getText(R.string.please_wait), true, false);

		};

		@Override
		protected void onPostExecute(Void account) {
			progressDialog.dismiss();

			if (!HomeActivity.this.isDisconnected) {
				if (AppConfig.account != null
						&& AppConfig.account.getCollegeId() == 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							onClick(updateMyAccountInformationButton);
						}
					});
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quitButton:
			finish();
			break;
		case R.id.updateMyAccountInformationButton:
			startActivity(new Intent(this, AccountActivity.class));
			break;
		case R.id.viewMySellingItemsButton:
			startActivity(new Intent(this, MySellingTextbookActivity.class));
			break;
		case R.id.sellATextbookButton:
			startActivity(new Intent(this, SellATextbookActivity.class));
			break;
		case R.id.viewMyFavoriteItemsButton:
			startActivity(new Intent(this, FavoriteTextbookActivity.class));
			break;
		case R.id.searchForTextbooksToBuyButton:
			startActivity(new Intent(this, SearchTextbooksToBuyActivity.class));
			break;

		}

	}

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// switch (position) {
	// case COMMAND_INDEX_BUY:
	// break;
	//
	// case COMMAND_INDEX_FAVORITE:
	// break;
	//
	// case COMMAND_INDEX_SELL:
	// startActivity(new Intent(this, SellATextbookActivity.class));
	// break;
	//
	// case COMMAND_INDEX_VIEW_SELLING_ITEMS:
	// startActivity(new Intent(this, MySellingTextbookActivity.class));
	// break;
	//
	// case COMMAND_INDEX_UPDATE_ACCOUNT:
	// startActivity(new Intent(this, AccountActivity.class));
	// break;
	//
	// case COMMAND_INDEX_QUIT:
	// finish();
	// break;
	//
	// }
	// Log.i("COMMAND", String.valueOf(position));
	// }

}
