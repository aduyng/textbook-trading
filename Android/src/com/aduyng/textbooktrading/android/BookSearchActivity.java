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
import android.widget.ListView;
import android.widget.TextView;

import com.aduyng.textbooktrading.android.db.IsbnDbLookupModel;
import com.aduyng.textbooktrading.android.entity.Book;

public class BookSearchActivity extends ListActivity implements
		OnScrollListener, OnClickListener {
	public static final String ISBN = "isbn";
	public static final String TITLE = "title";
	public static final String AUTHORS = "authors";
	public static final String PUBLISHERS = "publisher";
	private static final int NUMBER_OF_RECORDS_PER_PAGE = 10;

	public static final String QUERY = "query";
	// Button searchButton = null;
	// EditText queryEditText = null;

	private ProgressDialog progressDialog;
	BookAdapter adapter = null;

	private int pageNumber = 1;
	private boolean hasMoreRecords = false;
	private SearchAsyncTask asyncTask = null;

	private EditText searchEditText;
	private Button searchButton;
	private Button clearButton;
	private String query;

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

		adapter = new BookAdapter(this);
		setListAdapter(adapter);

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String query = bundle.getString(QUERY);
			if (null != query && query.length() > 0) {
				searchEditText.setText(query);
				this.query= query;
				loadMore();
			}
		}

		getListView().setOnScrollListener(this);

	}

	private void loadMore() {
		if (query == null || query.trim().length() == 0) {
			new AlertDialog.Builder(BookSearchActivity.this)
					.setTitle(R.string.input_error)
					.setMessage(R.string.your_search_term_is_empty)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									return;
								}
							}).show();
			return;

		}
		asyncTask = new SearchAsyncTask();
		asyncTask.execute();
	}

	private class SearchAsyncTask extends AsyncTask<Void, Void, List<Book>> {

		@Override
		protected List<Book> doInBackground(Void... queries) {
			try {
				return new IsbnDbLookupModel(BookSearchActivity.this).search(
						query.trim(), pageNumber);
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Book> books) {
			progressDialog.dismiss();
			if (null != books && books.size() > 0) {
				adapter.appendData(books);
				hasMoreRecords = (books.size() == NUMBER_OF_RECORDS_PER_PAGE);
			} else {
				hasMoreRecords = false;

				if (pageNumber == 0) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(BookSearchActivity.this)
									.setTitle(R.string.searching_for_textbooks)
									.setMessage(
											R.string.there_is_no_textbooks_found)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													BookSearchActivity.this
															.finish();
												}
											}).show();

						}
					});
				}
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog
					.show(BookSearchActivity.this,
							getResources().getString(
									R.string.searching_for_textbooks),
							getResources().getString(R.string.please_wait),
							true, false);
		}

	}

	private class BookAdapter extends BaseAdapter {
		private List<Book> records = new ArrayList<Book>();
		LayoutInflater inflater = null;

		public BookAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public void appendData(List<Book> books) {
			records.addAll(books);
			notifyDataSetChanged();
		}

		public void clear() {
			records.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return records.size();
		}

		@Override
		public Book getItem(int position) {
			return records.get(position);
		}

		@Override
		public long getItemId(int position) {
			return (long) position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				rowView = inflater.inflate(R.layout.search_book_item, parent,
						false);
			}

			TextView titleTextView = (TextView) rowView
					.findViewById(R.id.titleTextView);
			TextView infoTextView = (TextView) rowView
					.findViewById(R.id.infoTextView);
			final Book record = getItem(position);

			titleTextView.setText(record.getTitle());

			StringBuilder sb = new StringBuilder();
			final String authors = record.getAuthors();

			if (null != authors && authors.length() > 0) {
				sb.append("by: " + authors);

			}

			final String isbn = record.getIsbn();
			if (null != isbn && isbn.length() > 0) {
				sb.append("| ISBN: " + isbn);
			}

			final String publishers = record.getPublisher();
			if (null != publishers && publishers.length() > 0) {
				sb.append("| published by: " + publishers);
			}

			titleTextView.setText(record.getTitle());
			infoTextView.setText(sb.toString());

			// attaching event to each row
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent data = new Intent();
					data.putExtra(ISBN, record.getIsbn());
					data.putExtra(TITLE, record.getTitle());
					data.putExtra(AUTHORS, record.getAuthors());
					data.putExtra(PUBLISHERS, record.getPublisher());

					BookSearchActivity.this.setResult(RESULT_OK, data);
					BookSearchActivity.this.finish();
				}
			});

			return rowView;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (hasMoreRecords
				&& (asyncTask == null || asyncTask.getStatus() != Status.RUNNING)) {
			boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
			if (loadMore) {
				pageNumber++;
				loadMore();
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchButton:
			query = searchEditText.getText().toString();
			pageNumber = 1;
			loadMore();
			break;
		case R.id.clearButton:
			searchEditText.setText("");
			query = "";
			adapter.clear();

			break;

		}

	}

	// private class BookArrayAdapter extends ArrayAdapter<Book> {
	// private final Context context;
	// private final Book[] values;
	//
	// public BookArrayAdapter(Context context, Book[] values) {
	// super(context, R.layout.search_book_item, values);
	// this.context = context;
	// this.values = values;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// LayoutInflater inflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//
	// View rowView = inflater.inflate(R.layout.search_book_item, parent,
	// false);
	// TextView titleTextView = (TextView) rowView
	// .findViewById(R.id.titleTextView);
	// TextView infoTextView = (TextView) rowView
	// .findViewById(R.id.infoTextView);
	// final Book record = values[position];
	//
	// titleTextView.setText(record.getTitle());
	//
	// StringBuilder sb = new StringBuilder();
	// final String authors = record.getAuthors();
	//
	// if (null != authors && authors.length() > 0) {
	// sb.append("by: " + authors);
	//
	// }
	//
	// final String isbn = record.getIsbn();
	// if (null != isbn && isbn.length() > 0) {
	// sb.append(", ISBN: " + isbn);
	// }
	//
	// final String publishers = record.getPublisher();
	// if (null != publishers && publishers.length() > 0) {
	// sb.append(", published by: " + publishers);
	// }
	//
	// titleTextView.setText(record.getTitle());
	// infoTextView.setText(sb.toString());
	//
	// // attaching event to each row
	// rowView.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// Intent data = new Intent();
	// data.putExtra(ISBN, record.getIsbn());
	// data.putExtra(TITLE, record.getTitle());
	// data.putExtra(AUTHORS, record.getAuthors());
	// data.putExtra(PUBLISHERS, record.getPublisher());
	//
	// BookSearchActivity.this.setResult(RESULT_OK, data);
	// BookSearchActivity.this.finish();
	// }
	// });
	//
	// return rowView;
	// }
	// }

}