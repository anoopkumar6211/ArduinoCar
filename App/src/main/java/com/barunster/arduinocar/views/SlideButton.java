package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.text.DecimalFormat;

/**
 * Created by itzik on 3/11/14.
 */
class SlideButton extends Button {

    private static final String TAG = SlideButton.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int orientation = LinearLayout.VERTICAL, speed;
    private float curX = 0, curY  = 0, point, buttonSlidingLength;
    private OnTouchListener onTouchListener;
    private int direction = 0;
    private int width, height, size = -1;

    // For formatting a float to have only one number after the decimal point
    private DecimalFormat df = new DecimalFormat("0.0");

    public SlideButton(Context context) {
        super(context);

        if (DEBUG)
            Log.d(TAG, "SlideButton Created.");

        setBackgroundResource(R.drawable.stick_button);
    }

    public SlideButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.stick_button);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG)
            Log.d(TAG, " Measurements, Width: " + getMeasuredWidth() + ", Height: " + getMeasuredHeight());

        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (orientation == LinearLayout.HORIZONTAL)
            setY(curY);
        else
            setX(curX);

        if (DEBUG)
            Log.d(TAG, "onDraw, X: " + curX + " Y: " + curY);

        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (DEBUG)
            Log.d(TAG, "OnLayout");

        buttonSlidingLength = orientation == LinearLayout.HORIZONTAL ? ((LinearLayout)getParent()).getHeight() : ((LinearLayout)getParent()).getWidth();

        if (DEBUG)
        {
            Log.d(TAG, "buttonSlidingLength = " + buttonSlidingLength + " X " + curX +  " Y: " + curY);
            Log.d(TAG, "Button Width = " + getMeasuredWidth() + " Button Height = " + getMeasuredHeight());
        }


        setSpeedPoints(ArduinoLegoFragment.DEFAULT_SPEED_POINTS);

        // Centering the button
        setToCenter();

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        setPosition( orientation == LinearLayout.HORIZONTAL ? event.getRawY() : event.getRawX());

        if(onTouchListener != null)
            onTouchListener.onTouch(this, event);
        else if (DEBUG)
            Log.e(TAG, "No Touch listner for slide button");

        return super.onTouchEvent(event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
//        super.setOnTouchListener(get);
        this.onTouchListener = onTouchListener;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (DEBUG)
            Log.d(TAG, "LayoutParams, Width: " + params.width + ", Height: " + params.height);
    }

    /** Setting the button to a wanted position.*/
    private void setPosition(float pos){

        if (orientation == LinearLayout.HORIZONTAL)
        {
            curY = getFinalPos(pos);
        }
        else
        {
            curX =  getFinalPos(pos);
        }

        this.invalidate();
    }
    /** Setting the button to a wanted position.*/
    public void setPosition(float pos , boolean useMargins){

        if (orientation == LinearLayout.HORIZONTAL)
        {
            curY = useMargins ? getFinalPos(pos) : getFinalPositionWithNoMargin(pos);
        }
        else
        {
            curX =  useMargins ? getFinalPos(pos) : getFinalPositionWithNoMargin(pos);
        }

        this.invalidate();
    }
    /** Getting the final position of where the button need to be. Decreasing half of the button size. Also calculation speed and direction of the slide.*/
    public float getFinalPositionWithNoMargin(float pos){

        speed = getSpeed(pos, point );
        direction = getDirectionForPosition(pos);

        // Making sure the button don't go outside of the view
        float against = size != -1 ? size : this.getWidth();

//        Log.d(TAG, "Against: " + against);

        // if out from left set it to the zero position
        if (pos - (against/2) < 0)
            pos = (against/2);
            //if out from the right set it to most right position
        else if (pos + (against/2) > buttonSlidingLength)
            pos = buttonSlidingLength - (against/2);

//        Log.d(TAG, "Final SetPosition: " + (pos - (against/2)) );

        return pos - (against/2);
    }
    /** Getting the final position of where the button need to be. Decreasing margins and half of the button size. Also calculation speed and direction of the slide.*/
    private float getFinalPos(float pos){
//        Log.d(TAG, "SetPosition Before Margin: " + pos);

        if (orientation == LinearLayout.HORIZONTAL)
        {
            pos -= ( ((SlideButtonLayout)getParent()).getMarginTop() - ((SlideButtonLayout)getParent()).getMarginBottom());
        }
        else
        {
            pos -= (((SlideButtonLayout)getParent()).getMarginLeft() - ((SlideButtonLayout)getParent()).getMarginRight());
        }

//        Log.d(TAG, "SetPosition After Margin: " + pos);

        speed = getSpeed(pos, point );
        direction = getDirectionForPosition(pos);

        // Making sure the button don't go outside of the view
        float against = size != -1 ? size : this.getWidth();

//        Log.d(TAG, "Against: " + against);

        // if out from left set it to the zero position
        if (pos - (against/2) < 0)
            pos = (against/2);

        //if out from the right set it to most right position
        else if (pos + (against/2) > buttonSlidingLength)
            pos = buttonSlidingLength - (against/2);

//        Log.d(TAG, "Final SetPosition: " + (pos - (against/2)) );

        return pos - (against/2);
    }
    /** Doing a slide animation to a wanted position.*/
    public void animateToPosition(final float pos){
        TranslateAnimation anim = new TranslateAnimation( getX(), (orientation == LinearLayout.HORIZONTAL ? getX() : getFinalPos(pos)),
                                                                    getY(), (orientation == LinearLayout.HORIZONTAL ? getFinalPos(pos) : getY()) );
        anim.setDuration(1000);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setPosition(pos);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.startAnimation(anim);
    }
    /** Setting the button to its center position.*/
    public void setToCenter(){
//        Log.d(TAG, "Setting button to center, Center is: " + getCenter());
        setPosition(getCenter());
    }

    public void setOrientation(int orientation){
        this.orientation = orientation;
    }

    private int getSpeed(float pos, float point){

        int speed;

//        Log.d(TAG, "getSpeed, buttonSlidingLength = " + buttonSlidingLength + ", Pos = " + pos + ", Point = " + point);

        if ( pos > buttonSlidingLength/2 )
        {
            pos =  pos - ( (int) buttonSlidingLength / 2 )  ;
        }
        else
        {
            pos = ( (int) buttonSlidingLength / 2 ) - pos ;
        }

        speed = Math.round(pos / point);

        if (speed > 255)
            speed = 255;

        return  speed;

    }

    private int getDirectionForPosition(float position){
        int direction;

        if ( position < buttonSlidingLength/2 )
        {
            direction =  getResources().getInteger(R.integer.left);
        }
        else
        {
            direction =  getResources().getInteger(R.integer.right);
        }

        return direction;
    }

    private void scaleButton() {
        if (orientation == LinearLayout.HORIZONTAL)
        {
            setLayoutParams(new LinearLayout.LayoutParams(getWidth(), getWidth()));
        }
        else
        {
            setLayoutParams(new LinearLayout.LayoutParams(getHeight(), getHeight()));
        }

        this.invalidate();
    }


    /* Getters And Setters*/
    public void setSpeedPoints(int speedPoints){
        point = Float.parseFloat(df.format((buttonSlidingLength / 2) / speedPoints));
//        Log.d(TAG, "Setting SpeedPoints, " + " ButtonSlidingLength = " + buttonSlidingLength + " speedPoints = " + speedPoints +  ", Point = " + point);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getCurPosition(){
        return orientation == LinearLayout.HORIZONTAL ? curY : curX;
    }

    public float getCenter(){
        return buttonSlidingLength / 2 + (orientation == LinearLayout.HORIZONTAL ? ((SlideButtonLayout)getParent()).getMarginTop() : ((SlideButtonLayout)getParent()).getMarginLeft());
    }

    public int getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }
}
