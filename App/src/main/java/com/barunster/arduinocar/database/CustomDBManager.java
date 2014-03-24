package com.barunster.arduinocar.database;

import android.content.Context;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomDBManager {

    private CustomCommandsDataSource customCommandsDataSource;
    private ControllersDataSource controllersDataSource;
    private CustomButtonsDataSource customButtonsDataSource;

    public CustomDBManager(Context context){
        customButtonsDataSource = new CustomButtonsDataSource(context);
        controllersDataSource = new ControllersDataSource(context);
        customCommandsDataSource = new CustomCommandsDataSource(context);
    }

    public CustomCommandsDataSource getCustomCommandsDataSource() {
        return customCommandsDataSource;
    }

    public CustomController getControllerById(long id ){

        CustomController customController = controllersDataSource.getControllerById(id);

        for (CustomButton btn : customButtonsDataSource.getButtonsById(id))
            if (customCommandsDataSource.getCommandByButtonId(btn.getId()) != null)
                btn.setCustomCommand(customCommandsDataSource.getCommandByButtonId(btn.getId()));

        if (customController != null)
        {
            customController.setButtons(customButtonsDataSource.getButtonsById(id));
        }

        return customController;
    }

    public ControllersDataSource getControllersDataSource() {
        return controllersDataSource;
    }

    public CustomButtonsDataSource getCustomButtonsDataSource() {
        return customButtonsDataSource;
    }
}
