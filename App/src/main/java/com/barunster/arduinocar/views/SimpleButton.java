package com.barunster.arduinocar.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;

/**
 * Created by itzik on 3/19/14.
 */
public class SimpleButton extends Button {

    private static final String TAG = SimpleButton.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final int STATE_ON = R.string.on;
    public static final int STATE_OFF = R.string.off;
    public static final int STATE_RIGHT = R.string.go_right;
    public static final int STATE_LEFT = R.string.go_left;

    private int state;
    private int type;
    private int commandType;

    public SimpleButton(Context context) {
        super(context);
    }

    public SimpleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setType(int type) {
        this.type = type;

    }

    public int getType() {
        return type;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;

        if (DEBUG)
            Log.d(TAG, "Command Type: " + (commandType != 0 ? getResources().getString(commandType) : "0") );

        switch (commandType)
        {
            case CustomCommand.TYPE_ON_OFF:
                this.setBackgroundResource(R.drawable.on_off_button);
                break;

            case CustomCommand.TYPE_DIRECTION_RIGHT:
                this.setBackgroundResource(R.drawable.right_button);
                break;

            case CustomCommand.TYPE_DIRECTION_LEFT:
                this.setBackgroundResource(R.drawable.left_button);
                break;

            case CustomCommand.TYPE_TOGGLE_DIRECTION:
                this.setBackgroundResource(R.drawable.toggle_direction_button);
                break;

            case CustomCommand.TYPE_SPEED_UP:
                this.setBackgroundResource(R.drawable.speed_up_button);
                break;

            case CustomCommand.TYPE_SPEED_DOWN:
                this.setBackgroundResource(R.drawable.speed_down_button);
                break;

            case CustomCommand.TYPE_ACC_CONTROL:
                this.setBackgroundResource(R.drawable.accelerometer_button);
                break;

            case 0:
                this.setBackgroundResource(R.drawable.stick_button);
                break;
        }
    }

    public int getCommandType() {
        return commandType;
    }
}
