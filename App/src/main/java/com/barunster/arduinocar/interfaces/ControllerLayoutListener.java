package com.barunster.arduinocar.interfaces;

import android.view.View;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

/**
 * Created by itzik on 4/13/2014.
 */
/* Notify about button changes and drag ending.*/
public interface ControllerLayoutListener{
    public void onButtonAdded(CustomButton customButton, View view);
    public void onDragEnded();
    public void onButtonRemoved(long buttonId);
    public void onButtonChanged(CustomButton customButton, View view);
}

