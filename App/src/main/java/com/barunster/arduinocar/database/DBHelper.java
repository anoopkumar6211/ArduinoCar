package com.barunster.arduinocar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	String TAG = DBHelper.class.getSimpleName();
	
	public static final String DATABASE_NAME = "Custom_Controllers_DB";
	
	private static int DATABASE_VERSION = 10;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		DB.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, " DB On Upgrade");
		DB.onUpgrade(db, oldVersion, newVersion);
	}
	
	public static int fromBooleanToInt(boolean bool){
		if (bool){return 1;}
		else{return 0;}
	}
	public static boolean fromIntToBoolean(int i){
		if (i == 1){ return true;}
		else
		{return false;}
	}
}
