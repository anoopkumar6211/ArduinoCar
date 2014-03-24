package com.barunster.arduinocar.custom_controllers_obj;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomButton {

    private long id , controllerId;
    private int type, size, orientation, position;
    private CustomCommand customCommand;

    public CustomButton(long id, long controllerId, int type, int size, int orientation, int position) {
        this.id = id;
        this.controllerId = controllerId;
        this.type = type;
        this.size = size;
        this.orientation = orientation;
        this.position = position;
    }

    public CustomButton(long controllerId, int type, int size, int orientation, int position) {
        this.id = id;
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

    public void setCustomCommand(CustomCommand customCommand) {
        this.customCommand = customCommand;
    }

    public CustomCommand getCustomCommand() {
        return customCommand;
    }
}
