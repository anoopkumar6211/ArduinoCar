package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by itzik on 4/12/2014.
 */
public class BrickBackGroundView extends View {

    private static final String TAG = BrickBackGroundView.class.getSimpleName();

    private static final int MIN_BRICK_SIZE = 100;

    private int rowsAmount, columnsAmount;
    
    public BrickBackGroundView(Context context) {
        super(context);
    }

    private int calcBrickSize(){

        return MIN_BRICK_SIZE;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
