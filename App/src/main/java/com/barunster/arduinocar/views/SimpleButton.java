package com.barunster.arduinocar.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.barunster.arduinocar.R;

/**
 * Created by itzik on 3/19/14.
 */
public class SimpleButton extends Button {

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
    }

    public int getCommandType() {
        return commandType;
    }
}
