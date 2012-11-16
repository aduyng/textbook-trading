package com.aduyng.textbooktrading.android.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HttpCacheAdapter {
	private SQLiteDatabase db;
	private Context context;
	private DbHelper dbHelper;
	private static long CACHE_LIFETIME = 5 * 60 * 1000;

	public HttpCacheAdapter(Context context) {
		this.context = context;
	}

	public HttpCacheAdapter open() throws SQLException {
		dbHelper = new DbHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public byte[] get(String uri) {
		byte[] b = null;
		Cursor mCursor = db.query(true, HttpCacheTable.TABLE_NAME,
				new String[] { HttpCacheTable.COLUMN_DATA,
						HttpCacheTable.COLUMN_TIMESTAMP },
				HttpCacheTable.COLUMN_URI + "= ?", new String[] { uri }, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			if (mCursor.getCount() > 0) {
				Long timestamp = mCursor.getLong(mCursor
						.getColumnIndex(HttpCacheTable.COLUMN_TIMESTAMP));
				Long now = new Date().getTime();

				if (timestamp + CACHE_LIFETIME > now) {
					
					b = mCursor.getBlob(mCursor
							.getColumnIndex(HttpCacheTable.COLUMN_DATA));
				} else {
//					Log.i(this.getClass().getSimpleName(), "uri: " + uri + " has expired. Proceed removal.");
					// remove the cache object
					remove(uri);
				}
			}
			
			if( !mCursor.isClosed() ){
				mCursor.close();
			}

		}
		return b;

	}

	public long set(String uri, byte[] data) {
		long result = -1;
		ContentValues values = new ContentValues();
		
		values.put(HttpCacheTable.COLUMN_DATA, data);
		values.put(HttpCacheTable.COLUMN_TIMESTAMP, new Date().getTime());
		
		if( db.update(HttpCacheTable.TABLE_NAME, values, HttpCacheTable.COLUMN_URI + "= ?", new String[]{uri}) == 0 ){
			values.put(HttpCacheTable.COLUMN_URI, uri);
			result = db.insert(HttpCacheTable.TABLE_NAME, null, values);
		}
		
		if( result > 0 ){
//			Log.i(this.getClass().getSimpleName(), "uri: " + uri + " has been cached.");
		}
		return result;
	}
	
	public int remove(String uri){
		return db.delete(HttpCacheTable.TABLE_NAME,
				HttpCacheTable.COLUMN_URI + "= ?",
				new String[] { uri });
	}

	// public InputStream get(int method, String uri) {
	// InputStream is = null;
	//
	// Cursor mCursor = db.query(true, CacheTable.TABLE_NAME, new String[] {
	// CacheTable.COLUMN_DATA }, KEY_ROWID + "="
	// + rowId, null, null, null, null, null);
	// if (mCursor != null) {
	// mCursor.moveToFirst();
	// }
	// return mCursor;
	//
	// return is;
	// }
}
