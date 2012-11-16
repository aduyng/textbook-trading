package com.aduyng.textbooktrading.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "TextbookTrading";
	private static final int DATABASE_VERSION = 1;
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	} 
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		HttpCacheTable.onCreate(database);

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		HttpCacheTable.onUpgrade(database, oldVersion, newVersion);
	}

}
