package com.mattprecious.notisync.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotificationDatabaseAdapter {

	public static final String KEY_ROWID = "_id",
			
							KEY_PACKAGE_NAME = "packageName",
							KEY_TAG = "tag",
							KEY_APP_NAME = "appName",
							KEY_MESSAGE_TITLE = "messageTitle",
							KEY_MESSAGE = "message",
							KEY_TIME = "time";
	
	public static final int KEY_ROWID_INDEX = 0,
							KEY_PACKAGE_NAME_INDEX = 1,
							KEY_TAG_INDEX = 2,
							KEY_APP_NAME_INDEX = 3,
							KEY_MESSAGE_TITLE_INDEX = 4,
							KEY_MESSAGE_INDEX = 5,
							KEY_TIME_INDEX = 6;
	
	private static final String TAG = NotificationDatabaseAdapter.class.getSimpleName();
	
	private static final String DATABASE_NAME = "notificationData",
								TABLE_NOTIFICATIONS = "notifications";
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String CREATE_TABLE_NOTIFICATIONS = 
			"create table " + TABLE_NOTIFICATIONS + " (" + KEY_ROWID + " integer primary key, "
					+ KEY_PACKAGE_NAME + " text not null, " 
					+ KEY_TAG + " text not null, " 
					+ KEY_APP_NAME + " text not null, "
					+ KEY_MESSAGE_TITLE + " text not null, "
					+ KEY_MESSAGE + " text not null, "
					+ KEY_TIME + " text not null);";
	
private final Context context;
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		@SuppressWarnings("unused")
		private final Context myContext;

		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.myContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(CREATE_TABLE_NOTIFICATIONS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(TAG, "Upgrading database from version : " + oldVersion + " to " 
					+ newVersion + ", which will destrol all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
			onCreate(db);
		}		
	}
	
	public NotificationDatabaseAdapter(Context context){
		this.context = context;
	}
	
	public NotificationDatabaseAdapter openWrite() throws SQLException {
		mDbHelper = new DatabaseHelper(context);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public NotificationDatabaseAdapter openRead() throws SQLException {
		mDbHelper = new DatabaseHelper(context);
		mDb = mDbHelper.getReadableDatabase();
		return this;
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public long insertNotification(String packageName, String tag, String appName,
									String messageTitle, String message, String time){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PACKAGE_NAME, packageName);
		initialValues.put(KEY_TAG, tag);
		initialValues.put(KEY_APP_NAME, appName);
		initialValues.put(KEY_MESSAGE_TITLE, messageTitle);
		initialValues.put(KEY_MESSAGE, message);
		initialValues.put(KEY_TIME, time);
		return mDb.insert(TABLE_NOTIFICATIONS, null, initialValues);
	}
	
	public Cursor fetchNotifications() throws SQLException{
		Cursor mCursor = 
				mDb.query(true, TABLE_NOTIFICATIONS, new String[] 
						{KEY_ROWID, KEY_PACKAGE_NAME, KEY_TAG,
						KEY_APP_NAME, KEY_MESSAGE_TITLE,
						KEY_MESSAGE, KEY_TIME}, 
						null, null, null, null, KEY_TIME + " DESC", null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchNotifications(String packageName) throws SQLException{
		Cursor mCursor = 
				mDb.query(true, TABLE_NOTIFICATIONS, new String[] 
						{KEY_ROWID, KEY_PACKAGE_NAME, KEY_TAG,
						KEY_APP_NAME, KEY_MESSAGE_TITLE,
						KEY_MESSAGE, KEY_TIME}, 
						KEY_PACKAGE_NAME + "=" + packageName, null, null, null, KEY_TIME + " DESC", null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public void deleteAllNotifications(){
		mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
		mDb.execSQL(CREATE_TABLE_NOTIFICATIONS);
	}
	
	public boolean deleteNotifications(String packageName){
		return mDb.delete(TABLE_NOTIFICATIONS, KEY_PACKAGE_NAME + "=" + packageName, null) > 0;
	}
}
