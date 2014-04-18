package com.barunster.arduinocar.views.edit_command;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.RadioGroupGridAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;

/**
 * Created by itzik on 4/18/2014.
 */
public class CommandsSelectionGrid extends GridView {

    private RadioGroupGridAdapter radioGroupGridAdapter;

    public CommandsSelectionGrid(Context context) {
        super(context);
    }

    public CommandsSelectionGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initForButton(CustomButton customButton){
        /* Select the proper command list by the button type.*/
        int list[] = new int[0];
        switch (customButton.getType())
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                list = CustomCommand.regularButtonCommandTypes;
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;
        }


        /*Creates the radio group grid for command type selection.*/
        radioGroupGridAdapter = new RadioGroupGridAdapter(getContext(), list, (customButton.getCustomCommand() != null)? customButton.getCustomCommand().getType() : -1);
        this.setAdapter(radioGroupGridAdapter);
    }

    public void setRadioCheckChangedListener(RadioGroupGridAdapter.RadioCheckedListener radioCheckChangedListener){
        radioGroupGridAdapter.setRadioCheckedListener(radioCheckChangedListener);
    }

    public RadioGroupGridAdapter getRadioGroupGridAdapter() {
        return radioGroupGridAdapter;
    }
}
