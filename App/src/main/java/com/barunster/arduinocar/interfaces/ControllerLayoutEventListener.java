package com.barunster.arduinocar.interfaces;

import android.view.View;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

/**
 * Created by itzik on 4/16/2014.
 */
public interface ControllerLayoutEventListener {
    public void onButtonAdded(CustomButton customButton, View view);
    public void onDragEnded();
    public void onButtonRemoved(long buttonId);
    public void onButtonChanged(CustomButton customButton, View view);
}
