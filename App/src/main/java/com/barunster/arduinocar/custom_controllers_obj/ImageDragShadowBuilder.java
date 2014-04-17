package com.barunster.arduinocar.custom_controllers_obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.barunster.arduinocar.views.ControllerLayout;
import com.barunster.arduinocar.views.DropZoneShadowDrawable;

public class ImageDragShadowBuilder extends View.DragShadowBuilder {
    private static final String TAG = ImageDragShadowBuilder.class.getSimpleName();
    private static final boolean DEBUG = true;

    private Drawable shadow;

    private ImageDragShadowBuilder() {
        super();
    }

    public static View.DragShadowBuilder fromResource(Context context, int drawableId) {
        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = context.getResources().getDrawable(drawableId);
        if (builder.shadow == null) {
            throw new NullPointerException("Drawable from id is null");
        }

        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder fromBitmap(Context context, Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = new BitmapDrawable(context.getResources(), bmp);
        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder drawBricksShadow(Context context, int[] dimensions){
        if (dimensions == null) {
            throw new IllegalArgumentException("Dimensions cannot be null");
        }

        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = new DropZoneShadowDrawable(context, dimensions);
        builder.shadow.setBounds(0, 0, dimensions[ControllerLayout.WIDTH], dimensions[ControllerLayout.HEIGHT]);

        return builder;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        if (DEBUG)
            Log.d(TAG, "inDrawShadow");

        shadow.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        if (DEBUG)
            Log.d(TAG, "onProvideShadowMetrics, Width: " + shadow.getMinimumWidth() + ", Height: " + shadow.getMinimumHeight());

        if (shadow instanceof DropZoneShadowDrawable)
        {
            if (DEBUG)
                Log.d(TAG, "Shadow is custom");

            shadowSize.x = shadow.getBounds().right;
            shadowSize.y = shadow.getBounds().bottom;
        }
        else
        {
            shadowSize.x = shadow.getMinimumWidth();
            shadowSize.y = shadow.getMinimumHeight();
        }

        shadowTouchPoint.x = (int)(shadowSize.x / 2);
        shadowTouchPoint.y = (int)(shadowSize.y / 2);
    }
}
