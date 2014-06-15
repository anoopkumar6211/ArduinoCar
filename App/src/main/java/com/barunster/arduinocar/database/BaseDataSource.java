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
 * Created by itzik on 5/29/2014.
 */
public abstract class BaseDataSource<E>  {

    private static final String TAG = BaseDataSource.class.getSimpleName();
    private static final boolean DEBUG = true;

    static SQLiteDatabase db;
    private static DBHelper dbHelper;
    private String tableName;
    private Context context;
    static boolean leaveOpen;

    ContentValues values;

    List<E> tmpData;

    E obj;

    public BaseDataSource(Context context, String tableName){
        this.context = context;

        if (dbHelper == null)
            dbHelper = new DBHelper(context);

        this.tableName = tableName;
    }

    /** Open DB */
    void open() throws SQLException {
        if (DEBUG) Log.v(TAG, "open, leaveOpen: " + String.valueOf(leaveOpen) + ", db is " + (db == null ? "null." : "not null."));
        if (!leaveOpen || db == null)
        {
            if (DEBUG) Log.i(TAG,"opening.");
            db = dbHelper.getWritableDatabase();
        }
    }

    /** Close DB */
    public void close() {
        if (DEBUG) Log.v(TAG, "close, leaveOpen: "  + String.valueOf(leaveOpen));

        if(!leaveOpen)
        {
            if (DEBUG) Log.i(TAG,"closing.");
            dbHelper.close();
            db = null;
        }
    }

    public boolean delete(String where, String selection){
        open();

        boolean isDeleted = db.delete(tableName, where + " = " + selection, null) > 0;

        close();

        return isDeleted;
    }

    public  void leaveOpen(boolean leaveOpen) {
        this.leaveOpen = leaveOpen;
    }

    public boolean update(E obj, String where, String selection){
        open();
        boolean isUpdated = db.update(tableName, getValues(obj), where +" = ?" , new String[] {selection}) > 0;
        close();
        return isUpdated;
    }

    public long add(E obj){
        open();

        long id;

        //insert to table
        id = insert(getValues(obj));

        close();

        return id;
    }

    public List<E> getList(String where, String selection){
        open();

//        Log.i(TAG, " getAllControllers ");

        tmpData = new ArrayList<E>();

        Cursor cursor = getCursor(where, selection);

        if(cursor.moveToFirst())
        {
            do
            {
                tmpData.add(getObjFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        close();

        return tmpData;
    }

    public E get(String where, String selection){
        open();

//        Log.i(TAG, " Getting Command, ID: " + id);

        Cursor cursor = getCursor(where, selection);

//        Log.d(TAG, "Cursur Count: " + cursor.getCount());

        if(cursor.moveToFirst())
        {
            do
            {
                if ( selection.equals(String.valueOf(cursor.getInt(cursor.getColumnIndex(where)))) )
                {
                    return obj = getObjFromCursor(cursor);
                }
            }
            while (cursor.moveToNext());
        }

        close();

        return null;
    }

    public  List<E> getAll(){
        open();

        tmpData = new ArrayList<E>();

        String selectQuery = "SELECT * FROM " + tableName + " ORDER BY " + DB.Column.ID ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do
            {
                tmpData.add(getObjFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        close();
        return tmpData;
    }

    abstract E getObjFromCursor(Cursor cursor);

    abstract ContentValues getValues(E obj);

    private long insert(ContentValues values){
        return db.insert(tableName, null, values);
    }

    private Cursor getCursor(String where, String selection){
        String selectQuery = "SELECT * FROM " + tableName + " WHERE " + where + " = " + selection ;
       return db.rawQuery(selectQuery, null);
    }
}
