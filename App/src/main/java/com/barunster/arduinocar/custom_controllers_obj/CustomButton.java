package com.barunster.arduinocar.custom_controllers_obj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomButton {

    /* Button Tags*/
    public static final String BUTTON_TYPE = "button_type";
    public static final String BUTTON_ID = "button_id";

    public static final int BUTTON_TYPE_SIMPLE = 9000;
    public static final int BUTTON_TYPE_SLIDE_HORIZONTAL = 9001;
    public static final int BUTTON_TYPE_SLIDE_VERTICAL = 9002;

    public static final List<Integer> buttonTags = new ArrayList<Integer>( Arrays.asList(new Integer[]{BUTTON_TYPE_SIMPLE, BUTTON_TYPE_SLIDE_HORIZONTAL, BUTTON_TYPE_SLIDE_VERTICAL}));

    private long id = -1 , controllerId;
    private int type, size, orientation = -1, position;
    private CustomCommand customCommand;
    private boolean centerAfterDrop = false, showMarks = false;

    public CustomButton(long id, long controllerId, int type, int size, int orientation, int position) {
        this.id = id;
        this.controllerId = controllerId;
        this.type = type;
        this.size = size;
        this.orientation = orientation;
        this.position = position;
    }

    public CustomButton(long controllerId, int type, int size, int orientation, int position) {
        this.controllerId = controllerId;
        this.type = type;
        this.size = size;
        this.orientation = orientation;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public long getControllerId() {
        return controllerId;
    }

    public int getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setCustomCommand(CustomCommand customCommand) {
        this.customCommand = customCommand;
    }

    public CustomCommand getCustomCommand() {
        return customCommand;
    }

    public boolean centerAfterDrop() {
        return centerAfterDrop;
    }

    public boolean showMarks() {
        return showMarks;
    }

    public void setShowMarks(boolean showMarks) {
        this.showMarks = showMarks;
    }

    public void setCenterAfterDrop(boolean centerAfterDrop) {
        this.centerAfterDrop = centerAfterDrop;
    }

    public void setId(long id) {
        this.id = id;
    }
}
