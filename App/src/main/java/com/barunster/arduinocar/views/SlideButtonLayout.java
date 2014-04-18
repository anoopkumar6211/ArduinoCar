package com.barunster.arduinocar.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by itzik on 3/10/14.
 */
public class SlideButtonLayout extends LinearLayout implements View.OnTouchListener, View.OnClickListener{

    private static final String TAG = SlideButtonLayout.class.getSimpleName();

    private static final int NUMBER_OF_SERVO_POSITIONS = 9;

    private static final boolean DEBUG = false;

    public static final int SLIDE_HORIZONTALLY = 0;
    public static final int SLIDE_VERTICALLY = 1;

    /* Views*/
    private SlideButton btnSlide;
    private TextView txtPos, txtFloatingCurPos;
    private LinearLayout linearPositions;

    /* Interface */
    private SlideButtonListener slideButtonListener;

    /* Paint*/
    private Paint framePaint; // Use for drawing a frame.

    private float  buttonSlidingLength, startingPos, point;
    private int stickSize = -1, speedPoints = -1, slideDirection , curPos, numberOfMarks;
    private int marginLeft = 0, marginRight = 0, marginBottom = 0, marginTop = 0, layoutSizeInCells = 0;
    private boolean centerWhenSlideStop = false, showPointMarks = false;
    private OnLongClickListener onLongClickListener;
    private OnClickListener onClickListener;
    private int [] position = new int[2];

    // The button type. Types are in the ArduinoLegoFragment
    private int type;

    // For formatting a float to have only one number after the decimal point
    private DecimalFormat df = new DecimalFormat("0.0");

    public SlideButtonLayout(Context context, int slideDirection, int buttonSize, boolean centerWhenSlideStop, boolean showPointMarks) {
        super(context);

        if (DEBUG)
            Log.d(TAG, "SlideButtonLayout Created" + (showPointMarks ? ", Showing point marks." : "")  + " Button size: " + buttonSize );

        init();

        if (slideDirection == SLIDE_HORIZONTALLY)
            setOrientation(VERTICAL);
        else if (slideDirection == SLIDE_VERTICALLY)
            setOrientation(HORIZONTAL);
        else throw new IllegalArgumentException("Slide button orientation must be 'SLIDE_HORIZONTALLY' or 'SLIDE_VERTICALLY'.");

        if (getOrientation() == HORIZONTAL)
        {
            if (DEBUG)
                Log.d(TAG, "Orientation is Horizontal");
            setLayoutParams(new LayoutParams(buttonSize, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        else
        {
            if (DEBUG)
                Log.d(TAG, "Orientation is Vertical");
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonSize));
        }

        this.showPointMarks = showPointMarks;
        this.centerWhenSlideStop = centerWhenSlideStop;
        this.stickSize = buttonSize;
        numberOfMarks = NUMBER_OF_SERVO_POSITIONS;

        initFloatingCurPos();
        initPositionsMarks();
    }

    public SlideButtonLayout(Context context, int orientation, int buttonSize, boolean centerWhenSlideStop, boolean showPointMarks, int numberOfMarks) {
        super(context);
//        Log.d(TAG, "SlideButtonLayout Created" + (showPointMarks ? ", Showint point marks." : "")  );

        init();

        setOrientation(orientation);

        if (orientation == HORIZONTAL)
        {
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        else
        {
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        this.showPointMarks = showPointMarks;
        this.centerWhenSlideStop = centerWhenSlideStop;
        this.stickSize = buttonSize;
        this.numberOfMarks = numberOfMarks;

        initFloatingCurPos();
        initPositionsMarks();
    }


    public SlideButtonLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        init();

        if (!isInEditMode())
        {
//            Log.d(TAG, "reading attributes");
            numberOfMarks = attributeSet.getAttributeIntValue(R.styleable.SlideButtonLayout_markAmount, NUMBER_OF_SERVO_POSITIONS);
            // TODO fix problem with define mark amount from xml
        }

        centerWhenSlideStop = attributeSet.getAttributeBooleanValue(R.styleable.SlideButtonLayout_centerAfterSlideStop, false);
        showPointMarks = attributeSet.getAttributeBooleanValue(R.styleable.SlideButtonLayout_showMarks, false);


        initFloatingCurPos();
        initPositionsMarks();

        initButton();
    }

    private void init(){
        // Setting a random color for the background
        TypedArray ta = getContext().getResources().obtainTypedArray(R.array.colors);
        setBackgroundColor(ta.getColor(randInt(0,6), 0));
        ta.recycle();


//        initPaint();

//        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG)
            Log.d(TAG, "onMeasure");
//        Log.d(TAG, " Measurements, Width: " + widthMeasureSpec + ", Height: " + heightMeasureSpec);

        // Measuring the size of the markPosition so the button will adjust to it.
        if (stickSize != -1 && btnSlide == null)
        {
            position = new int[2];
            ( (View)this.getParent() ).getLocationInWindow(position);

            if (DEBUG)
                Log.d(TAG, "pos[x] = " + position[0] + " pos [y] = " + position[1]); // TODO fix pos problem

            if (getOrientation() == HORIZONTAL)
            {
                setMargins(getMarginLeft(), getMarginTop() + position[1], getMarginRight(), getMarginBottom()); // Adding the distance from the top.
            }

            if(showPointMarks)
            {
//                Log.d(TAG, "Position Marks Measurements, Width: " + linearPositions.getMeasuredWidth() + ", Height: " + linearPositions.getMeasuredHeight());
                stickSize -= getOrientation() == HORIZONTAL ? linearPositions.getMeasuredWidth() : linearPositions.getMeasuredHeight();
//                Log.d(TAG, "StickSize: " + stickSize);
            }

            initButton();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (DEBUG)
            Log.d(TAG, "OnLayout");

        buttonSlidingLength = getOrientation() == LinearLayout.HORIZONTAL ? getMeasuredHeight() : getMeasuredWidth();

        setSpeedPoints(ArduinoLegoFragment.DEFAULT_SPEED_POINTS);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setMargins(int l, int t, int r,int b){
        if (DEBUG)
            Log.d(TAG, "Layout Margins, Left = " + l + ", Top = " + t + ", Right = " + r + ", Bottom = " + b);

        this.marginLeft = l;
        this.marginRight = r;
        this.marginBottom = b;
        this.marginTop = t;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (getOrientation() == HORIZONTAL)
        {
            curPos = (int) motionEvent.getRawY();
        }
        else
        {
            curPos = (int) motionEvent.getRawX();
        }

//        Log.d(TAG, "%2: " + ((curPos - (stickSize / 2)) % 2));
//
//        Log.d(TAG, "X: " + (int) motionEvent.getRawX() + " Y: " + (int) motionEvent.getRawY() );

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (slideButtonListener != null)
                    return slideButtonListener.onSlideStarted(this);
                else Log.e(TAG, " No Slide Button Listener");

                txtFloatingCurPos.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

                break;

            case MotionEvent.ACTION_MOVE:

                if (getOrientation() == HORIZONTAL)
                {
                    if (((((int) motionEvent.getRawY()) - (stickSize / 2)) % 4) == 0)
                        return true;
                }
                else
                {
                    if (((((int) motionEvent.getRawX()) - (stickSize / 2)) % 4) == 0)
                        return true;
                }

                if (speedPoints != btnSlide.getSpeed() || slideDirection != btnSlide.getDirection()) {
                    speedPoints = btnSlide.getSpeed();
                    slideDirection = btnSlide.getDirection();

                    if (slideButtonListener != null)
                        return slideButtonListener.onSliding(this, slideDirection, speedPoints);
                    else Log.e(TAG, " No Slide Button Listener");
                }

                break;

            case MotionEvent.ACTION_UP:

                speedPoints = centerWhenSlideStop ? 0 : speedPoints;

                if (centerWhenSlideStop)
                    btnSlide.setToCenter();

                if (slideButtonListener != null)
                     return slideButtonListener.onSlideStop(this, curPos);
                else Log.e(TAG, " No Slide Button Listener");

                txtFloatingCurPos.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));

                break;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (DEBUG)
            Log.d(TAG, "onClick");

        if (v instanceof TextView)
        {
            float curPos;
            if ( ((TextView) v).getText().toString().equals("1") )
            {
                curPos = 0;
            }
            else if ( ((TextView) v).getText().toString().equals("9") )
            {
                curPos = buttonSlidingLength;
            }
            else
            {
                curPos = getOrientation()==HORIZONTAL ? v.getY() + v.getHeight() / 2 : v.getX() + v.getWidth() / 2;
            }

            if (DEBUG)
                Log.d(TAG, "Click CurPos: " +  curPos);

            btnSlide.setPosition(curPos, false);

            if (slideButtonListener != null)
                slideButtonListener.onMarkedPositionPressed(this, getDirectionForPosition(curPos), Integer.parseInt(((TextView) v).getText().toString()), getSpeed((int) curPos));

            else Log.e(TAG, " No Slide Button Listener");
        }
    }

    /* Private Methods*/
    private void initButton(){

        if (DEBUG)
            Log.d(TAG, "Init Button");

        btnSlide = new SlideButton(getContext());

        btnSlide.setOrientation(getOrientation());

        btnSlide.setOnTouchListener(this);

        if (onClickListener != null)
            btnSlide.setOnClickListener(onClickListener);

        if (onLongClickListener != null)
            btnSlide.setOnLongClickListener(onLongClickListener);

        LinearLayout.LayoutParams params = new LayoutParams(stickSize != -1 ? stickSize : LinearLayout.LayoutParams.WRAP_CONTENT ,
                stickSize != -1 ? stickSize: LinearLayout.LayoutParams.WRAP_CONTENT);

//        params.gravity = getOrientation() == HORIZONTAL ? Gravity.RIGHT : Gravity.BOTTOM;

        if (DEBUG)
            Log.d(TAG, " Linear Height: " + getMeasuredHeight() +  " Linear Width: " + getMeasuredWidth()
                + " Pos Height: " + (linearPositions != null ? linearPositions.getMeasuredHeight() : 0) + " Pos Width: " + (linearPositions != null ? linearPositions.getMeasuredWidth() : 0) );

        btnSlide.setLayoutParams(params);

        btnSlide.setSize(stickSize);

        /*The slide layout and the button share the same if,
         *Its for the on click handling and finding the button in the controller layout by this id.*/
        btnSlide.setId(getId());

        addView(btnSlide);

        btnSlide.onMeasure(MeasureSpec.makeMeasureSpec(stickSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(stickSize, MeasureSpec.EXACTLY));
    }

    private void initPositionsMarks(){
        if (showPointMarks)
        {
//            Log.d(TAG, "init position marks");
            linearPositions = new LinearLayout(getContext());
            linearPositions.setOrientation(getOrientation() == HORIZONTAL ? VERTICAL : HORIZONTAL);
//            linearPositions.setPadding(0,5,0,10);

            initMarks();

            LinearLayout.LayoutParams params = getOrientation() == HORIZONTAL ? new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT) :
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.gravity = getOrientation() == HORIZONTAL ? Gravity.LEFT :Gravity.TOP;
            linearPositions.setLayoutParams(params);

            addView(linearPositions);
        }
    }

    /* Called by initPositionsMarks*/
    private void initMarks(){
        if (showPointMarks && linearPositions != null && linearPositions.getChildCount() == 0) //  Making sure not to over populate the view
        {
            if (DEBUG)
                Log.d(TAG, "Init marks, " + numberOfMarks);

            LinearLayout.LayoutParams params = getOrientation() == HORIZONTAL ? new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) :
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            params.weight = 1;

            for (int i = 1; i < numberOfMarks + 1 ; i++)
            {
//                Log.d(TAG, "Button " + i);
                txtPos = new TextView(getContext());
                txtPos.setText(String.valueOf(i));
                txtPos.setLayoutParams(params);
                txtPos.setGravity(Gravity.CENTER);
                txtPos.setTextColor(Color.WHITE);
                txtPos.setTextSize(20f);
                txtPos.setOnClickListener(this);

                linearPositions.addView(txtPos);
            }

            this.invalidate();
        }
    }

    private void initFloatingCurPos(){
        txtFloatingCurPos = new TextView(getContext());
        txtFloatingCurPos.setVisibility(GONE);
        txtFloatingCurPos.setText("0");
        txtFloatingCurPos.setTextSize(20f);

        addView(txtFloatingCurPos);
    }

    private int getSpeed(int pos){

        int speed;

        if (DEBUG)
            Log.d(TAG, "getSpeed, buttonSlidingLength = " + buttonSlidingLength + ", Pos = " + pos + ", Point = " + point);

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

    private String getDirectionForPosition(float position){
        String direction;

        if ( position < buttonSlidingLength/2 )
        {
            direction =  getResources().getString(R.string.tag_engine_direction_forwards);
        }
        else
        {
            direction = getResources().getString(R.string.tag_engine_direction_backwards);
        }

        return direction;
    }

    public void setSpeedPoints(int speedPoints){
        point = Float.parseFloat(df.format((buttonSlidingLength / 2) / speedPoints));

        if (DEBUG)
           Log.d(TAG, "Setting SpeedPoints, " + " ButtonSlidingLength = " + buttonSlidingLength + " speedPoints = " + speedPoints +  ", Point = " + point);
    }

    /* Interfaces */
    public interface SlideButtonListener{
        /**The returned value will be passed as the return value of the OnTouchListener handling the slide.*/
        public boolean onSlideStop(SlideButtonLayout slideButtonLayout, int pos);
        /**The returned value will be passed as the return value of the OnTouchListener handling the slide.*/
        public boolean onSlideStarted(SlideButtonLayout slideButtonLayout);
        /**The returned value will be passed as the return value of the OnTouchListener handling the slide.*/
        public boolean onSliding(SlideButtonLayout slideButtonLayout,int direction, int speed);

        public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int position);
    }

    public void setSlideButtonListener(SlideButtonListener slideButtonListener) {
        this.slideButtonListener = slideButtonListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        onClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        onLongClickListener = l;
    }

    /* Getters And Setters */

    @Override
    public void setId(int id) {
        super.setId(id);
         /*The slide layout and the button share the same if,
         *Its for the on click handling and finding the button in the controller layout by this id.*/
        if (btnSlide != null)
            btnSlide.setId(id);
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public boolean isCenterWhenSlideStop() {
        return centerWhenSlideStop;
    }

    public void setCenterWhenSlideStop(boolean centerWhenSlideStop) {
        this.centerWhenSlideStop = centerWhenSlideStop;
    }

    public void setShowPointMarks(boolean showPointMarks) {
        this.showPointMarks = showPointMarks;
    }

    public boolean isShowPointMarks() {
        return showPointMarks;
    }

    public SlideButton getBtnSlide() {
        return btnSlide;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getLayoutSizeInCells() {
        return layoutSizeInCells;
    }

    public void setLayoutSizeInCells(int layoutSizeInCells) {
        this.layoutSizeInCells = layoutSizeInCells;
    }

    /* Worker*/
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
