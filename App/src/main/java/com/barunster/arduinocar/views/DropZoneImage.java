package com.barunster.arduinocar.views;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by itzik on 3/12/14.
 */
public class DropZoneImage extends ImageView{

    private int orientation, size, type;
    private boolean onDrag = false;

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

    public void setOnDrag(boolean onDrag) {
        this.onDrag = onDrag;
    }

    public boolean isOnDrag() {
        return onDrag;
    }
}
