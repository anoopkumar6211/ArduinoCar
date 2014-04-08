package com.barunster.arduinocar.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class DB   {
	
	final static String TAG = "Test";
	final static String ACTIVITY = "DB";

	// SQL Text
	private static final String TYPE_T = " TEXT";
	private static final String TYPE_I = " INTEGER";
	private static final String COMMA_SEP = ", ";
	
	// Creating Tables
	public static void onCreate(SQLiteDatabase db) {
        createControllersTable(db);
        createButtonsTable(db);
        createCommandsTable(db);
	}

    private static void createControllersTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "
                + Table.T_CUSTOM_CONTROLLERS + " ( " + Column.ID + " INTEGER PRIMARY KEY" + COMMA_SEP

                + Column.NAME + TYPE_T + COMMA_SEP

                + Column.ROWS + TYPE_I + COMMA_SEP

                + Column.COLUMNS + TYPE_I

                +" );");
    }

    private static void createButtonsTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "
                + Table.T_CUSTOM_BUTTONS + " ( " + Column.ID + " INTEGER PRIMARY KEY" + COMMA_SEP

                + Column.ID_CONTROLLER + TYPE_I + COMMA_SEP

                + Column.TYPE + TYPE_T + COMMA_SEP

                + Column.SIZE + TYPE_I + COMMA_SEP

                + Column.ORIENTATION + TYPE_I + COMMA_SEP

                + Column.POSITION + TYPE_I + COMMA_SEP

                + Column.CENTER_AFTER_DROP + TYPE_I + COMMA_SEP

                + Column.SHOW_MARKS + TYPE_I

                +" );");
    }

    private static void createCommandsTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "
                + Table.T_CUSTOM_COMMANDS + " ( " + Column.ID + " INTEGER PRIMARY KEY" + COMMA_SEP

                + Column.ID_BUTTON + TYPE_I + COMMA_SEP

                + Column.TYPE + TYPE_T + COMMA_SEP

                + Column.CHANNEL + TYPE_T + COMMA_SEP

                + Column.EXTRA_SPEED_DATA + TYPE_I

                +" );");
    }
	
	// Upgrading database
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		Log.d(TAG, ACTIVITY + " ON UPGRADE");

        db.execSQL("DROP TABLE IF EXISTS " + Table.T_CUSTOM_CONTROLLERS);
        db.execSQL("DROP TABLE IF EXISTS " + Table.T_CUSTOM_BUTTONS);
        db.execSQL("DROP TABLE IF EXISTS " + Table.T_CUSTOM_COMMANDS);

		onCreate(db);
	}

    public static final class Table {
        // Tables Names
        public static final String T_CUSTOM_CONTROLLERS = "_table_custom_controllers";
        public static final String T_CUSTOM_BUTTONS = "_table_buttons";
        public static final String T_CUSTOM_COMMANDS = "_table_commands";
    }
	// Columns names
    public static final class Column implements BaseColumns {


        //General
        public static final String ID = "_id";
        public static final String ID_CONTROLLER = "_id_controller";
        public static final String ID_BUTTON = "_id_controller";
        public static final String NAME = "_name";
        public static final String TYPE = "_type";
        public static final String SIZE = "_size";
        public static final String ORIENTATION = "_orientation";
        public static final String POSITION = "_position";
        public static final String CHANNEL = "_channel";
        public static final String ROWS = "_rows";
        public static final String COLUMNS = "_columns";
        public static final String EXTRA_SPEED_DATA = "_extra_speed_data";
        public static final String CENTER_AFTER_DROP = "_center_after_drop";
        public static final String SHOW_MARKS = "_show_marks";
    }
}
