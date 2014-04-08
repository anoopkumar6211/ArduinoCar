package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;

import java.util.ArrayList;

/**
 * Created by itzik on 10/12/13.
 */
public class CustomCommandsDataSource {

    final String TAG = this.getClass().getSimpleName();

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private Context context;

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.ID_BUTTON,
            DB.Column.TYPE, DB.Column.CHANNEL,
            DB.Column.EXTRA_SPEED_DATA
    };

    public CustomCommandsDataSource(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    /** Open DB */
    private void open() throws SQLException {
        // Log.i(TAG, ACTIVITY + " db.open");
        db = dbHelper.getWritableDatabase();
    }

    /** Close DB */
    private void close() {
        // Log.i(TAG, ACTIVITY + " db.close");
        dbHelper.close();
    }

    public long addCommand(CustomCommand customCommand){

        deleteCommandByButtonId(customCommand.getButtonId());

        open();

        long id;

//        Log.i(TAG, "Adding Command, Button ID: " + customCommand.getButtonId() + ", Command Type: " + customCommand.getType());

        // set value that will be inserted the row
        ContentValues values = new ContentValues();

        // Name and Type
        values.put(allColumns[1], customCommand.getButtonId());
        values.put(allColumns[2], customCommand.getType());
        values.put(allColumns[3], customCommand.getChannel());
        values.put(allColumns[4], customCommand.getExtraSpeedData());

        //insert to table
        id = db.insert(DB.Table.T_CUSTOM_COMMANDS, null, values);

        close();

        return id;
    }

    public CustomCommand getCommandByButtonId(long id){

        open();

//        Log.i(TAG, " Getting Command, ID: " + id);


        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_COMMANDS + " WHERE " + allColumns[1] + " = " + id ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        CustomCommand customCommand = null;

//        Log.d(TAG, "Cursur Count: " + cursor.getCount());

        if(cursor.moveToFirst())
        {
            do
            {
                if ( id == ( cursor.getInt(cursor.getColumnIndex(allColumns[1])) ) )
                {
                    customCommand = getCommandsFromCursor(cursor);

                    break;
                }
            }
            while (cursor.moveToNext());
        }

        close();

        return customCommand;
    }

    public ArrayList<CustomCommand> getAllCommands() {

        open();

//        Log.i(TAG, " getAllControllers ");

        ArrayList<CustomCommand> commands = new ArrayList<CustomCommand>();

        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_COMMANDS + " ORDER BY " + allColumns[0] ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do
            {
                commands.add(getCommandsFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        close();
        return commands;
    }

    private CustomCommand getCommandsFromCursor(Cursor cursor){

        CustomCommand customCommand = new CustomCommand(
                cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                cursor.getLong(cursor.getColumnIndex(allColumns[1])),
                cursor.getInt(cursor.getColumnIndex(allColumns[2])),
                cursor.getString(cursor.getColumnIndex(allColumns[3]))
        );

        customCommand.setExtraSpeedData( cursor.getInt(cursor.getColumnIndex(allColumns[4])) );

        return customCommand;
    }

    /** Delete event by given id.*/
    public boolean deleteCommandById(long id){

//        Log.d(TAG, "deleteCommandById, Id: " + id);

        open();

        boolean isDeleted = db.delete(DB.Table.T_CUSTOM_COMMANDS, allColumns[0] + " = " + id, null) > 0;

        close();

        return isDeleted;

    }

    public boolean deleteCommandByButtonId(long buttonId){


//        Log.d(TAG, "deleteCommandById, Button Id: " + buttonId);

        open();

        boolean isDeleted = db.delete(DB.Table.T_CUSTOM_COMMANDS, allColumns[1] + " = " + buttonId, null) > 0;

        close();

        return isDeleted;

    }

}
