package com.aduyng.textbooktrading.android.db;

import android.database.sqlite.SQLiteDatabase;

public class HttpCacheTable {
	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS HttpCache(uri text not null, data BLOB NOT NULL, timestamp INTEGER NOT NULL, PRIMARY KEY(uri))";
	public static final String COLUMN_URI = "uri";
	public static final String COLUMN_DATA = "data";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String TABLE_NAME = "HttpCache";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		onCreate(database);
	}
}
