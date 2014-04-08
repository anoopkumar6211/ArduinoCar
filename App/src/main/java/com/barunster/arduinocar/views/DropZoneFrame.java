package com.barunster.arduinocar.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by itzik on 3/12/14.
 */
public class DropZoneFrame extends FrameLayout {

    public static final String STATE_CAN_DROP = "state.can_drop";
    public static final String STATE_CANT_DROP = "state.cant_drop";

    private boolean isEmpty = true;
    private int rowNumber = -1;
    private int ColNumber = -1;

    /** When a button with a size that is more then one is added to controller layout the unneeded frames are being hidden.
     * The Parent id is the id of the view that contain the button. If the frame is not hidden it would not have a parent id.*/
    private int parentId = -1;

    private String state = STATE_CAN_DROP;

    public DropZoneFrame(Context context) {
        super(context);
        setNormalMode();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        parentId = -1; this.isEmpty = isEmpty;
    }

    public void setNotAvailableMode(){
        setBackgroundResource(android.R.color.holo_red_light);
        state = STATE_CANT_DROP;
    }

    public void setToAvailableMode(){
        setBackgroundResource(android.R.color.holo_green_light);
        state = STATE_CAN_DROP;
    }

    public void setNormalMode(){
        setBackgroundResource(android.R.color.transparent);
        state = STATE_CANT_DROP;
    }

    public void setToEditMode(){
        setBackgroundResource(android.R.color.holo_blue_light);
        state = STATE_CAN_DROP;
    }

    public boolean canDrop(){
        return state.equals(STATE_CAN_DROP) && isEmpty();
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColNumber() {
        return ColNumber;
    }

    public void setColNumber(int colNumber) {
        ColNumber = colNumber;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    public String getState() {
        return state;
    }
}
