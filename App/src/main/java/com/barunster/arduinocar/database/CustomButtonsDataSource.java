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
public class CustomButtonsDataSource extends BaseDataSource<CustomButton> {

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
        super(context, DB.Table.T_CUSTOM_BUTTONS);
    }

    @Override
    CustomButton getObjFromCursor(Cursor cursor) {
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

    @Override
    ContentValues getValues(CustomButton customButton) {
        // set value that will be inserted the row
        values = new ContentValues();

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

        return values;
    }
}
