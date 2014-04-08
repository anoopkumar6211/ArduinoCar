package com.barunster.arduinocar.not_used;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by itzik on 3/10/14.
 */
public class ServoSemiCircle extends Drawable {

    private static final String TAG = ServoSemiCircle.class.getSimpleName();

    private Paint paint;
    private RectF rectF;
    private int color, screenBottom;
    private float radius;
    private Direction angle;
    private int [] from = new int[4], to = new int[4];

    public enum Direction
    {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public ServoSemiCircle() {
        this(Color.BLUE, Direction.LEFT);
    }

    public ServoSemiCircle(int color, Direction angle) {
        this.color = color;
        this.angle = angle;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(70f);
        paint.setStyle(Paint.Style.STROKE);
        rectF = new RectF();
    }

    public ServoSemiCircle(int color, Direction angle, int screenBottom) {
        this.color = color;
        this.angle = angle;
        this.screenBottom = screenBottom;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(50f);
        paint.setStyle(Paint.Style.STROKE);
        rectF = new RectF();
    }

    public int getColor() {
        return color;
    }

    /**
     * A 32bit color not a color resources.
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "onDraw, Bounds: " + " Right = " + getBounds().right + " Left = " + getBounds().left + " Top = " + getBounds().top + " Bottom = " + getBounds().bottom);
        canvas.save();

        final RectF oval = new RectF();
        oval.set(getBounds().left, getBounds().top, getBounds().right, screenBottom);

        Path myPath = new Path();
        myPath.arcTo(oval, 180,180, true);
        canvas.drawPath(myPath, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return PixelFormat.OPAQUE;
    }

}