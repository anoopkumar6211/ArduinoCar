package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.barunster.arduinocar.R;

/**
 * Created by itzik on 4/14/2014.
 */
public class DropZoneShadowDrawable extends Drawable{

    private static final String TAG = DropZoneShadowDrawable.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int [] dimensions;
    private Paint shadowPaint;

    public DropZoneShadowDrawable(Context context, int [] dimensions) {
        super();
        this.dimensions = dimensions;

        shadowPaint = new Paint();
        shadowPaint.setColor(context.getResources().getColor(R.color.drop_zone_shadow));
        shadowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        if (DEBUG)
            Log.d(TAG, "draw");

        canvas.drawRect(this.getBounds().left, this.getBounds().top, this.getBounds().right, this.getBounds().bottom, shadowPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        if (DEBUG)
            Log.d(TAG, "setBounds");
        super.setBounds(left, top, right, bottom);
    }

    public int[] getDimensions() {
        return dimensions;
    }
}
