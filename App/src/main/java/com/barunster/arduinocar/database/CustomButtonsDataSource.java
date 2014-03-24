package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/12/13.
 */
public class CustomButtonsDataSource {

    final String TAG = this.getClass().getSimpleName();

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private Context context;

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.ID_CONTROLLER,
            DB.Column.TYPE, DB.Column.SIZE,
            DB.Column.ORIENTATION, DB.Column.POSITION
    };

    public CustomButtonsDataSource(Context context){
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

    public long addButton(CustomButton customButton){

        open();

        long id;

//        Log.i(TAG, "Adding Button, Controller ID: " + customButton.getControllerId() + ", Button ID: " + customButton.getId());

        // set value that will be inserted the row
        ContentValues values = new ContentValues();

        // Name and Type
        values.put(allColumns[1], customButton.getControllerId());
        values.put(allColumns[2], customButton.getType());
        values.put(allColumns[3], customButton.getSize());
        values.put(allColumns[4], customButton.getOrientation());
        values.put(allColumns[5], customButton.getPosition());

        //insert to table
        id = db.insert(DB.Table.T_CUSTOM_BUTTONS, null, values);

        close();

        return id;
    }

    public List<CustomButton> getButtonsById(long id){

        open();

//        Log.i(TAG, " Getting Button, ID: " + id);


        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_BUTTONS + " WHERE " + allColumns[1] + " = " + id ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<CustomButton> customButtons = new ArrayList<CustomButton>();

//        Log.d(TAG, "Cursur Count: " + cursor.getCount());

        if(cursor.moveToFirst())
        {
            do
            {
                if ( id == ( cursor.getInt(cursor.getColumnIndex(allColumns[1])) ) )
                {
                    customButtons.add(getButtonFromCursor(cursor));
                }
            }
            while (cursor.moveToNext());
        }

        close();

        return customButtons;
    }

    public ArrayList<CustomButton> getAllButtons() {

        open();

//        Log.i(TAG, " getAllButtons ");

        ArrayList<CustomButton> buttons = new ArrayList<CustomButton>();

        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_BUTTONS + " ORDER BY " + allColumns[0] ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do
            {
                buttons.add(getButtonFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        close();
        return buttons;
    }

    private CustomButton getButtonFromCursor(Cursor cursor){

        CustomButton customButton = new CustomButton(
                cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                cursor.getLong(cursor.getColumnIndex(allColumns[1])),
                cursor.getInt(cursor.getColumnIndex(allColumns[2])),
                cursor.getInt(cursor.getColumnIndex(allColumns[3])),
                cursor.getInt(cursor.getColumnIndex(allColumns[4])),
                cursor.getInt(cursor.getColumnIndex(allColumns[5]))
        );

        return customButton;
    }

    /** Delete button by given id.*/
    public boolean deleteButtonById(long id){

//        Log.d(TAG, "deleteButtonById, Id: " + id);

        open();

        boolean isDeleted = db.delete(DB.Table.T_CUSTOM_BUTTONS, allColumns[0] + " = " + id, null) > 0;

        close();

        return isDeleted;

    }

}
