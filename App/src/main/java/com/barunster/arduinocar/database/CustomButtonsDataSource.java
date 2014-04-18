package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.views.ControllerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/12/13.
 */
public class CustomButtonsDataSource {

    private static final String TAG = CustomButtonsDataSource.class.getSimpleName();
    private static final boolean DEBUG = false;

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private Context context;

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.ID_CONTROLLER,
            DB.Column.TYPE, DB.Column.SIZE,
            DB.Column.ROWS, DB.Column.COLUMNS,
            DB.Column.ORIENTATION, DB.Column.POSITION,
            DB.Column.START_POS_ROW, DB.Column.START_POS_COLUMN,
            DB.Column.CENTER_AFTER_DROP, DB.Column.SHOW_MARKS
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

        if (customButton.getId() != -1)
            if (deleteButtonById(customButton.getId()))
                if (DEBUG)
                    Log.i(TAG, "Old Button Deleted");

        open();

        long id;

        if (DEBUG)
            Log.i(TAG, "Adding Button, Controller ID: " + customButton.getControllerId() + ", Button ID: " + customButton.getId());

        // set value that will be inserted the row
        ContentValues values = new ContentValues();

        values.put(allColumns[1], customButton.getControllerId());
        values.put(allColumns[2], customButton.getType());
        values.put(allColumns[6], customButton.getOrientation());

        // If button does not have size use button dimension. This is the separation between FrameControllerLayout buttons to BrickControllerLayout
        if (customButton.getSize() == 0)
        {
            values.put(allColumns[4], customButton.getDimensions()[ControllerLayout.ROW]);
            values.put(allColumns[5], customButton.getDimensions()[ControllerLayout.COLUMN]);
            values.put(allColumns[8], customButton.getStartPosition()[ControllerLayout.ROW]);
            values.put(allColumns[9], customButton.getStartPosition()[ControllerLayout.COLUMN]);
        }
        else
        {
            values.put(allColumns[3], customButton.getSize());
            values.put(allColumns[7], customButton.getPosition());
        }

        values.put(allColumns[10], DBHelper.fromBooleanToInt(customButton.centerAfterDrop()));
        values.put(allColumns[11], DBHelper.fromBooleanToInt(customButton.showMarks()));

        //insert to table
        id = db.insert(DB.Table.T_CUSTOM_BUTTONS, null, values);

        close();

        return id;
    }

    public List<CustomButton> getButtonsByControllerId(long id){

        open();

        if (DEBUG)
            Log.i(TAG, " Getting Button, for controller ID: " + id);


        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_BUTTONS + " WHERE " + allColumns[1] + " = " + id ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<CustomButton> customButtons = new ArrayList<CustomButton>();

        if (DEBUG)
            Log.d(TAG, "Cursur Count: " + cursor.getCount());

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

    public CustomButton getButtonByButtonId(long id){

        open();

        if (DEBUG)
            Log.i(TAG, " Getting Button, ID: " + id);


        String selectQuery = "SELECT * FROM " + DB.Table.T_CUSTOM_BUTTONS + " WHERE " + allColumns[0] + " = " + id ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        CustomButton customButtons = null;

        if (DEBUG)
            Log.d(TAG, "Cursur Count: " + cursor.getCount());

        if(cursor.moveToFirst())
        {
            do
            {
                if ( id == ( cursor.getInt(cursor.getColumnIndex(allColumns[0])) ) )
                {
                    customButtons = getButtonFromCursor(cursor);
                }
            }
            while (cursor.moveToNext());
        }

        close();

        return customButtons;
    }

    public ArrayList<CustomButton> getAllButtons() {

        open();

        if (DEBUG)
            Log.i(TAG, " getAllButtons ");

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

        CustomButton customButton;

        // if button size is zero check for the button dimesions and start pos.
        if (cursor.getInt(cursor.getColumnIndex(allColumns[3])) == 0)
        {
            customButton = new CustomButton(
                    cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                    cursor.getLong(cursor.getColumnIndex(allColumns[1])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[2])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[4])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[5])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[6])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[8])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[9]))
            );
        }
        else
        {
            customButton = new CustomButton(
                    cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                    cursor.getLong(cursor.getColumnIndex(allColumns[1])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[2])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[3])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[6])),
                    cursor.getInt(cursor.getColumnIndex(allColumns[7]))
            );
        }

        customButton.setCenterAfterDrop(DBHelper.fromIntToBoolean(cursor.getInt(cursor.getColumnIndex(allColumns[10]))));
        customButton.setShowMarks(DBHelper.fromIntToBoolean(cursor.getInt(cursor.getColumnIndex(allColumns[11]))));

        return customButton;
    }

    /** Delete button by given id.*/
    public boolean deleteButtonById(long id){

        if (DEBUG)
            Log.d(TAG, "deleteButtonById, Id: " + id);

        open();

        boolean isDeleted = db.delete(DB.Table.T_CUSTOM_BUTTONS, allColumns[0] + " = " + id, null) > 0;

        close();

        return isDeleted;

    }

    public int updateButtonById(CustomButton customButton){

        open();

        // set value that will be inserted the row
        ContentValues values = new ContentValues();

        values.put(allColumns[1], customButton.getControllerId());
        values.put(allColumns[2], customButton.getType());
        values.put(allColumns[6], customButton.getOrientation());

        // If button does not have size use button dimension. This is the separation between FrameControllerLayout buttons to BrickControllerLayout
        if (customButton.getSize() == 0)
        {
            values.put(allColumns[4], customButton.getDimensions()[ControllerLayout.ROW]);
            values.put(allColumns[5], customButton.getDimensions()[ControllerLayout.COLUMN]);
            values.put(allColumns[8], customButton.getStartPosition()[ControllerLayout.ROW]);
            values.put(allColumns[9], customButton.getStartPosition()[ControllerLayout.COLUMN]);
        }
        else
        {
            values.put(allColumns[3], customButton.getSize());
            values.put(allColumns[7], customButton.getPosition());
        }

        values.put(allColumns[10], DBHelper.fromBooleanToInt(customButton.centerAfterDrop()));
        values.put(allColumns[11], DBHelper.fromBooleanToInt(customButton.showMarks()));

        int affectedRows = db.update(DB.Table.T_CUSTOM_BUTTONS, values, allColumns[0] + " = " + customButton.getId(), null);

        close();

        return affectedRows;
    }

}
