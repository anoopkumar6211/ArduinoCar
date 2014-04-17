package com.barunster.arduinocar.interfaces;

import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import braunster.btconnection.BTConnection;

/**
 * Created by itzik on 4/13/2014.
 */
public interface ControllerInterface {
    public void enterEditMode();
    public void exitEditMode();
    public void setController(CustomController customController);
    public void setOutputConnection(BTConnection connection);
}
