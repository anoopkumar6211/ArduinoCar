package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/12/13.
 */
public class ControllersDataSource {

    final String TAG = this.getClass().getSimpleName();

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private Context context;

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.NAME,
    };

    public ControllersDataSource(Context context){
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

    public long addController(CustomController customController){

        open();

        long id;

//        Log.i(TAG, "Adding Controller, Name: " + customController.getName());

        // set value that will be inserted the row
        ContentValues values = new ContentValues();

        // Name and Type
        values.put(allColumns[1], customController.getName());

        //insert to table
        id = db.insert(DB.Table.T_CUSTOM_CONTROLLERS, null, values);

        close();

        return id;
    }

    public CustomController getControllerById(long id){

        open();

//        Log.i(TAG, " Getting Controller, ID: " + id);


        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_CONTROLLERS + " WHERE " + allColumns[0] + " = " + id ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        CustomController customController = null;

//        Log.d(TAG, "Cursur Count: " + cursor.getCount());

        if(cursor.moveToFirst())
        {
            do
            {
                if ( id == ( cursor.getInt(cursor.getColumnIndex(DB.Column.ID)) ) )
                {
                    customController = getControllerFromCursor(cursor);

                    break;
                }
            }
            while (cursor.moveToNext());
        }

        close();

        return customController;
    }

    public ArrayList<CustomController> getAllControllers() {

        open();

//        Log.i(TAG, " getAllControllers ");

        ArrayList<CustomController> controllers = new ArrayList<CustomController>();

        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_CONTROLLERS + " ORDER BY " + allColumns[0] ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do
            {
                controllers.add(getControllerFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        close();
        return controllers;
    }

    private CustomController getControllerFromCursor(Cursor cursor){

        CustomController customController = new CustomController(
                cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                cursor.getString(cursor.getColumnIndex(allColumns[1]))
        );

        return customController;
    }

    /** Delete event by given id.*/
    public boolean deleteControllerById(long id){

//        Log.d(TAG, "deleteControllerById, Id: " + id);

        open();

        boolean isDeleted = db.delete(DB.Table.T_CUSTOM_CONTROLLERS, allColumns[0] + " = " + id, null) > 0;

        close();

        return isDeleted;

    }

    public static List<String> toStringList(List<CustomController> list){

        List<String> resultList = new ArrayList<String>();

        for (Object obj : list)
        {
            if (obj instanceof CustomController)
                resultList.add( ((CustomController)obj).getName());
        }

        return resultList;
    }
}
