package com.barunster.arduinocar.views;

import android.content.Context;
import android.widget.ImageView;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;

/**
 * Created by itzik on 3/12/14.
 */
public class DropZoneImage extends ImageView{

    private int orientation, size, type;
    private CustomButton taggedButton;

    public DropZoneImage(Context context) {
        super(context);
    }

    public DropZoneImage(Context context, int orientation, int size) {
        super(context);
        this.orientation = orientation;
        this.size = size;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getSize() {
        return size;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public CustomButton getTaggedButton() {
        return taggedButton;
    }

    public void setTaggedButton(CustomButton taggedButton) {
        this.taggedButton = taggedButton;
    }


}
