package com.barunster.arduinocar.custom_controllers_obj;

import com.barunster.arduinocar.R;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomCommand {

    /* Button Commands Types*/
    public static final int TYPE_ON_OFF = R.string.on_off;
    public static final int TYPE_DIRECTION_LEFT = R.string.go_left;
    public static final int TYPE_DIRECTION_RIGHT = R.string.go_right;
    public static final int TYPE_TOGGLE_DIRECTION = R.string.toggle_direction;
    public static final int TYPE_SPEED_UP = R.string.speed_up;
    public static final int TYPE_SPEED_DOWN = R.string.speed_down;
    public static final int TYPE_SPEED_CONTROL = R.string.speed_control;
    public static final int TYPE_ACC_CONTROL = R.string.acc_control;

    public static final int[] slideButtonLayoutCommandTypes = {TYPE_ON_OFF, TYPE_SPEED_CONTROL, TYPE_TOGGLE_DIRECTION};
    public static final int[] regularButtonCommandTypes = {TYPE_ON_OFF, TYPE_TOGGLE_DIRECTION, TYPE_DIRECTION_LEFT, TYPE_DIRECTION_RIGHT, TYPE_SPEED_UP, TYPE_SPEED_DOWN, TYPE_ACC_CONTROL };

    private long id , buttonId;
    private int type, extraSpeedData = 20;
    private String channel;

    public CustomCommand(long id, long buttonId, int type, String channel) {
        this.id = id;
        this.buttonId = buttonId;
        this.type = type;
        this.channel = channel;
    }

    public CustomCommand(long buttonId, int type, String channel) {
        this.buttonId = buttonId;
        this.type = type;
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public long getButtonId() {
        return buttonId;
    }

    public int getType() {
        return type;
    }

    public String getChannel() {
        return channel;
    }

    public void setExtraSpeedData(int extraSpeedData) {
        this.extraSpeedData = extraSpeedData;
    }

    public int getExtraSpeedData() {
        return extraSpeedData;
    }
}
