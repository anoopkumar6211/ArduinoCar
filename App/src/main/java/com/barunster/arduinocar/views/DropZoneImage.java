package com.barunster.arduinocar.views;

import android.content.Context;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;

/**
 * Created by itzik on 3/12/14.
 */
public class DropZoneImage extends ImageView{

    private static final String TAG = DropZoneImage.class.getSimpleName();

    private int orientation, size, type;
    private CustomButton taggedButton;

    private int[] dimensions = new int[2];// Holds the button dimension, first entry is number of rows the button will cover and the second is how much columns.

    public DropZoneImage(Context context) {
        super(context);
    }

    public DropZoneImage(Context context, int orientation, int size) {
        super(context);
        this.orientation = orientation;
        this.size = size;
    }

    public DropZoneImage(Context context, int[] dimensions) {
        super(context);
        this.dimensions = dimensions;
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

        if (type == CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL)
            orientation = LinearLayout.HORIZONTAL;
        else if (type == CustomButton.BUTTON_TYPE_SLIDE_VERTICAL)
            orientation = LinearLayout.VERTICAL;
    }

    public int getType() {
        return type;
    }

    public CustomButton getTaggedButton() {
        return taggedButton;
    }

    /** Set the custom button tag to the view, also setting the orientation to the tagged button orientation. */
    public void setTaggedButton(CustomButton taggedButton) {
        this.taggedButton = taggedButton;

        if (taggedButton != null)
            orientation = taggedButton.getOrientation();
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    /* Static Methods*/
    public static DropZoneImage crateDropZoneImageForButton(Context context, CustomButton customButton){
        if (customButton == null)
        {
            Log.e(TAG, " Cant create drop zone image, Button is null");
            return null;
        }

        DropZoneImage buttonImage = new DropZoneImage(context);
        buttonImage.setImageDrawable(context.getResources().getDrawable(R.drawable.stick_button));
        buttonImage.setLayoutParams(new GridView.LayoutParams(100, 100));
        buttonImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        buttonImage.setPadding(8, 8, 8, 8);

        buttonImage.setSize(customButton.getSize());
        buttonImage.setOrientation(customButton.getOrientation());
        buttonImage.setType(customButton.getType());
        buttonImage.setDimensions(customButton.getDimensions());

        buttonImage.setTaggedButton(customButton);

        return buttonImage;
    }
}
