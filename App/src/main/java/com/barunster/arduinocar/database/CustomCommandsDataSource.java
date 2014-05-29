package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/12/13.
 */
public class CustomCommandsDataSource extends BaseDataSource<CustomCommand> {

    final String TAG = this.getClass().getSimpleName();

    // Columns
    private final static String[] allColumns = {
            DB.Column.ID, DB.Column.ID_BUTTON,
            DB.Column.TYPE, DB.Column.CHANNEL,
            DB.Column.EXTRA_SPEED_DATA
    };

    public CustomCommandsDataSource(Context context){
        super(context, DB.Table.T_CUSTOM_COMMANDS);
    }

    @Override
    public long add(CustomCommand customCommand) {
        // Delete old command.
        delete(DB.Column.ID_BUTTON, String.valueOf(customCommand.getButtonId()));

        return super.add(customCommand);
    }

    @Override
    CustomCommand getObjFromCursor(Cursor cursor) {

        obj = new CustomCommand(
                cursor.getLong(cursor.getColumnIndex(DB.Column.ID)),
                cursor.getLong(cursor.getColumnIndex(allColumns[1])),
                cursor.getInt(cursor.getColumnIndex(allColumns[2])),
                cursor.getString(cursor.getColumnIndex(allColumns[3]))
        );

        obj.setExtraSpeedData( cursor.getInt(cursor.getColumnIndex(allColumns[4])) );

        return obj;
    }

    @Override
    ContentValues getValues(CustomCommand customCommand) {
        values = new ContentValues();

        // Name and Type
        values.put(allColumns[1], customCommand.getButtonId());
        values.put(allColumns[2], customCommand.getType());
        values.put(allColumns[3], customCommand.getChannel());
        values.put(allColumns[4], customCommand.getExtraSpeedData());

        return values;
    }
}
