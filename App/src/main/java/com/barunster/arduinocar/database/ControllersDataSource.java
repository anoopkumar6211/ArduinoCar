package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/12/13.
 */
public class ControllersDataSource extends BaseDataSource<CustomController>{

    final String TAG = this.getClass().getSimpleName();

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.NAME, DB.Column.ROWS, DB.Column.COLUMNS, DB.Column.SIZE
    };

    public ControllersDataSource(Context context){
        super(context, DB.Table.T_CUSTOM_CONTROLLERS);
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

    @Override
    CustomController getObjFromCursor(Cursor cursor) {
        CustomController customController = new CustomController(
                cursor.getLong( cursor.getColumnIndex(DB.Column.ID) ),
                cursor.getString( cursor.getColumnIndex(allColumns[1]) ),
                cursor.getInt( cursor.getColumnIndex(allColumns[2]) ),
                cursor.getInt( cursor.getColumnIndex(allColumns[3]) ),
                cursor.getInt( cursor.getColumnIndex(allColumns[4]) )
        );

        return customController;
    }

    @Override
    ContentValues getValues(CustomController customController){
        values = new ContentValues();

        // Name and Type
        values.put(allColumns[1], customController.getName());
        values.put(allColumns[2], customController.getRows());
        values.put(allColumns[3], customController.getColumns());
        values.put(allColumns[4], customController.getBrickSize());

        return values;
    }
}
