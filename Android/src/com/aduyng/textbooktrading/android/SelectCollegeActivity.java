package com.aduyng.textbooktrading.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aduyng.textbooktrading.android.db.CollegeModel;
import com.aduyng.textbooktrading.android.entity.College;

public class SelectCollegeActivity extends ListActivity {
	public static final String COLLEGE_ID = "collegeId";
	CollegesAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		adapter = new CollegesAdapter(this);
		setListAdapter(adapter);

		new DownloadCollegesAsyncTask().execute();
	}

	private class DownloadCollegesAsyncTask extends
			AsyncTask<Void, Void, List<College>> {

		@Override
		protected List<College> doInBackground(Void... arg0) {

			try {
				return new CollegeModel(SelectCollegeActivity.this).fetch();
			} catch (Exception e) {
				new AlertDialog.Builder(SelectCollegeActivity.this)
						.setTitle(
								R.string.operation_failed)
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

			return null;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SelectCollegeActivity.this,
					getResources()
							.getText(R.string.getting_college_information),
					getResources().getText(R.string.please_wait), true, false);

		};

		@Override
		protected void onPostExecute(List<College> colleges) {
			progressDialog.dismiss();
			
			if(null != colleges){
				adapter.appendData(colleges);
			}
			
		}

	}

	ProgressDialog progressDialog;

	private class CollegesAdapter extends BaseAdapter {
		private List<College> records;
		LayoutInflater inflater;

		public void appendData(List<College> colleges) {
			records.addAll(colleges);
			this.notifyDataSetChanged();
		}

		public CollegesAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			records = new ArrayList<College>();

		}

		@Override
		public int getCount() {
			return records.size();
		}

		@Override
		public College getItem(int position) {
			return records.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				rowView = inflater.inflate(android.R.layout.simple_list_item_1,
						parent, false);
			} else {
				rowView = convertView;
			}
			final College college = this.getItem(position);
			TextView textView = (TextView) rowView
					.findViewById(android.R.id.text1);
			textView.setText(college.getName());

			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent data = new Intent();
					data.putExtra(COLLEGE_ID, college.getId());
					SelectCollegeActivity.this.setResult(RESULT_OK, data);
					SelectCollegeActivity.this.finish();
				}
			});

			return rowView;
		}

	}

}