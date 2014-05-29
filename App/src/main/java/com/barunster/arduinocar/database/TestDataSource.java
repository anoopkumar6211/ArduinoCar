package com.barunster.arduinocar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;

/**
 * Created by itzik on 5/29/2014.
 */
public class TestDataSource extends BaseDataSource<CustomButton>{

    public TestDataSource(Context context){
        super(context, DB.Table.T_CUSTOM_BUTTONS);
    }
    @Override
    CustomButton getObjFromCursor(Cursor cursor) {
        return null;
    }

    @Override
    ContentValues getValues(CustomButton obj) {
        return null;
    }
}
