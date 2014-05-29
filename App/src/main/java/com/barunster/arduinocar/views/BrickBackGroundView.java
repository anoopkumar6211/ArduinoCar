package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomUtils;

/**
 * Created by itzik on 4/12/2014.
 */
public class BrickBackGroundView extends View {

    private static final String TAG = BrickBackGroundView.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final float CIRCLE_STROKE_SIZE = 2.0f;

    public static final int MIN_BRICK_SIZE = 50;

    private Paint circlePaint, backgroundPaint, dropZoneValidDropPaint, dropZoneInvalidDropPaint;

    private Bitmap brickBitmap;

    private int rowsAmount = -1, columnsAmount =-1, rowsRemainder, columnsRemainder, brickSize = -1, radius;
    private boolean[][] bricksState;// Keep data if the brick_w200_h200 is available or not. false states that the break is empty, true is full.

    private boolean drawDropZoneShadow = false, canDropShadow = false;
    private int [] dropZoneShadowCoordinates = new int[2];
    private int [] dropZoneShadowDimensions = new int[2];

    private DropListener dropListener;

    private boolean fillScreenMode = true;

    public BrickBackGroundView(Context context, int multiplier) {
        super(context);

        this.brickSize = calcBrickSize(multiplier);

        if (DEBUG) Log.i(TAG, "Brick size: " + brickSize);
        init();
    }

    public BrickBackGroundView(Context context, int rowsAmount, int columnsAmount) {
        super(context);

        this.columnsAmount = columnsAmount;
        this.rowsAmount = rowsAmount;

        fillScreenMode = false;

        if (DEBUG) Log.i(TAG, "Rows Amount: " + rowsAmount + ", Columns Amount: " + columnsAmount);
        init();
    }

    private void init(){
        initPaints();

        setOnDragListener(new bricksDragListener());
    }

    private void initPaints(){
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(CIRCLE_STROKE_SIZE);
        circlePaint.setColor(getResources().getColor(android.R.color.black));
        circlePaint.setStyle(Paint.Style.STROKE);

        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(0.0f);
        backgroundPaint.setColor(Color.YELLOW);
        backgroundPaint.setStyle(Paint.Style.FILL);

        dropZoneValidDropPaint = new Paint();
        dropZoneValidDropPaint.setColor(getResources().getColor(R.color.drop_zone_ok_for_drop));
        dropZoneValidDropPaint.setStyle(Paint.Style.FILL);

        dropZoneInvalidDropPaint = new Paint();
        dropZoneInvalidDropPaint.setColor(getResources().getColor(R.color.drop_zone_cant_drop));
        dropZoneInvalidDropPaint.setStyle(Paint.Style.FILL);
    }

    int calcBrickSize(int multiplier){
        if(DEBUG) Log.v(TAG, "calcBrickSize, Height: " + getMeasuredHeight() + ", Dpi: " + getResources().getDisplayMetrics().densityDpi);

        brickSize = MIN_BRICK_SIZE * multiplier;
        brickSize = (int) (brickSize * CustomUtils.getDensityMultiple(getResources()));

        return brickSize;
    }

    void initBrickStates(){
        if (DEBUG) Log.v(TAG, "initBrickStates, rows: " + rowsAmount + ", columns: " + columnsAmount);
        if (rowsAmount != -1 && columnsAmount != -1)
            bricksState = new boolean[rowsAmount][columnsAmount];
        else if (DEBUG) Log.e(TAG, "cant init brick states, size of something is -1!");
    }

    void initSizes(){
        if (DEBUG) Log.v(TAG, "initSizes");

        int tmpHeight = getMeasuredHeight() / rowsAmount;
        int tmpWidth = getMeasuredWidth() / columnsAmount;
        Log.d(TAG, "tmpWidth: " + tmpWidth + ", tmpHeight: " + tmpHeight);

        // Rounding the size of the brick to the size of the closest and smaller bitmap available
        // Currently only resizing when screen density is medium or low.
        if (getResources().getDisplayMetrics().densityDpi < DisplayMetrics.DENSITY_HIGH)
            brickSize = getRoundedBrickSize(Math.min(tmpHeight, tmpWidth));
        else brickSize = Math.min(tmpHeight, tmpWidth);

        brickBitmap = getBitmapForBrickSize(brickSize);

        rowsRemainder = getMeasuredHeight() - (brickSize*rowsAmount);
        columnsRemainder = getMeasuredWidth() - (brickSize*columnsAmount);

        radius = brickSize/4;

        if (DEBUG) {
            Log.d(TAG, "Brick Size: " + brickSize);
            Log.d(TAG, "Height: " + getMeasuredHeight() + ", Width: " + getMeasuredWidth());
            Log.d(TAG, "Rows Amount: " + rowsAmount + ", Columns Amount: " + columnsAmount);
            Log.d(TAG, "Rows Remainder: " + rowsRemainder + ", Columns Remainder: " + columnsRemainder);
        }
    }

    private void calcRowsAndColumnsAmount(){
        if (rowsAmount == -1 && columnsAmount == -1)
        {
            rowsAmount = getMeasuredHeight() / brickSize;
            columnsAmount = getMeasuredWidth() / brickSize;

            initSizes();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG) Log.v(TAG, "OnMeasure");

        // Do calculations only if not initialized already.
        if ( (rowsAmount == -1 && columnsAmount == -1) || brickSize == -1) {
            if (fillScreenMode)
                calcRowsAndColumnsAmount();
            else
                initSizes();

            initBrickStates();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (DEBUG)
            Log.d(TAG, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (DEBUG)
            Log.d(TAG, "onDraw");

        // Drawing the background
        canvas.drawRect(
                columnsRemainder/2, // Left
                rowsRemainder/2, // Top
                getMeasuredWidth() - columnsRemainder/2, // Right
                getMeasuredHeight() - rowsRemainder/2, // Bottom
                backgroundPaint ); // Paint



        // Drawing the circles
        for (int i = 0 ; i < rowsAmount ; i++)
            for (int j = 0 ; j < columnsAmount ; j++)
            {
//                canvas.drawCircle( (j * brickSize) + brickSize/2 + columnsRemainder/2, // X
//                        (i * brickSize) + brickSize/2 + rowsRemainder/2, // Y
//                        radius, // Radius
//                        circlePaint);// Paint

                canvas.drawBitmap(brickBitmap, (j * brickSize)  + columnsRemainder/2 , (i * brickSize)  + rowsRemainder/2, new Paint(Paint.ANTI_ALIAS_FLAG));
            }

        if (drawDropZoneShadow) {
            if (DEBUG)
                Log.d(TAG, "onDraw, drawDropZoneShadow");

            canvas.drawRect(
                    columnsRemainder/2 + (dropZoneShadowCoordinates[ControllerLayout.COLUMN] * brickSize), // Left Calc - The out of bound size + brick_w200_h200 size multiple by the starting column position.(ie. col 2 * 100 brickSize)
                    rowsRemainder/2 + (dropZoneShadowCoordinates[ControllerLayout.ROW] * brickSize), // Top
                    (dropZoneShadowCoordinates[ControllerLayout.COLUMN] * brickSize) + (dropZoneShadowDimensions[ControllerLayout.COLUMN] * brickSize) + columnsRemainder/2, // Right
                    (dropZoneShadowCoordinates[ControllerLayout.ROW] * brickSize) + (dropZoneShadowDimensions[ControllerLayout.ROW] * brickSize) + rowsRemainder/2, // Bottom
                    canDropShadow ? dropZoneValidDropPaint : dropZoneInvalidDropPaint ); // Paint
        }
    }

    /** Gets the row number of a brick_w200_h200 by given touch position on the screen.*/
    private int getRowNumberForY(float y){

        // Check for getting out of bounds
        if (y < rowsRemainder/2 || y > rowsAmount * brickSize + rowsRemainder/2 )
            return -1;

        y -= rowsRemainder/2;

        if (y <= brickSize)
            return 0;
        else
            return ((int) Math.ceil(y / brickSize)) - 1;
    }
    /** Gets the column number of a brick_w200_h200 by given touch position on the screen.*/
    private int getColumnForX(float x){

        // Check for getting out of bounds
        if (x < columnsRemainder/2 || x > columnsAmount * brickSize +  columnsRemainder/2)
            return -1;

        x -= columnsRemainder/2;

        if (x <= brickSize)
            return 0;
        else return ((int) Math.ceil(x / brickSize)) - 1;
    }
    /** @return an array that contain the brick_w200_h200 row and column position by the given position of the user touch coordinates*/
    private int [] getBrickNumberForCoordinates(float x , float y){
        if (DEBUG)
            Log.d(TAG, "getBrickNumberForCoordinates, X: " + x + ", Y; " + y);

        int columnNumber = getColumnForX(x);
        int rowNumber = getRowNumberForY(y);

        if (DEBUG)
            Log.d(TAG, "Row Number: " + rowNumber + ", Column Number: " + columnNumber);


        return new int[]{rowNumber,columnNumber};
    }
    /** Check if a row by given parameters is available for button drop.*/
    private boolean checkRowAvailableBricks(int row, int start, int end){

        if (DEBUG)
            Log.d(TAG, "checkRowAvailableBricks, Row: " + row + ", Start: " + start + ", End: " + end);

        boolean rowOk = true;

        if (end > columnsAmount)
            rowOk = false;
        else
            for (int i = start ; i < end ; i++)
            {
                if (DEBUG)
                    Log.d(TAG, "Checked Brick; " + i);

                if (bricksState[row][i])
                {
                    rowOk = false;
                    break;
                }
            }

        return rowOk;
    }
    /** Check if there's a place for dropping the button to the layout.*/
    private boolean isAvailableForDrop(int[] startBrickPos, int[] dimensions){

        if (DEBUG)
            Log.d(TAG, "isAvailableForDrop, Dimensions: Rows " + dimensions[ControllerLayout.ROW] + ", Columns "  + dimensions[ControllerLayout.COLUMN]);

        boolean canDrop = true;

        // Check the first row(The row the user hover above).
        if (checkRowAvailableBricks(startBrickPos[ControllerLayout.ROW], startBrickPos[ControllerLayout.COLUMN], startBrickPos[ControllerLayout.COLUMN] + dimensions[ControllerLayout.COLUMN]))
            for (int i = 1 ; i < dimensions[ControllerLayout.ROW] && canDrop; i++)
            {
                if (startBrickPos[ControllerLayout.ROW] + i == rowsAmount) {
                    canDrop = false;
                    break;
                }

                canDrop = checkRowAvailableBricks(startBrickPos[ControllerLayout.ROW] + i, startBrickPos[ControllerLayout.COLUMN], startBrickPos[ControllerLayout.COLUMN] + dimensions[ControllerLayout.COLUMN]);
            }
        else canDrop = false;

        return canDrop;
    }
    /** Set the data for highlighting relevant bricks and call "invalidate" so the view will redraw himself with the highlight.*/
    private void highlightLayoutForButtonDrop(int[] startBrickPos, int[] dimensions, boolean canDrop){
        dropZoneShadowCoordinates = startBrickPos;
        dropZoneShadowDimensions = dimensions;

        drawDropZoneShadow = true;
        canDropShadow = canDrop;

        this.invalidate();
    }
    /** Mark brick_w200_h200 as full by changing their position in "brickState" to true.*/
    void markBricksAsFull(int[] startBrickPos, int[] dimensions){
        markBricks(startBrickPos, dimensions, true);
    }
    /** Mark brick_w200_h200 as empty by changing their position in "brickState" to true.*/
    void markBricksAsEmpty(int[] startBrickPos, int[] dimensions){
        markBricks(startBrickPos, dimensions, false);
    }
    /** Mark brick_w200_h200  by changing their position in "brickState" to the state given.*/
    private void markBricks(final int[] startBrickPos, final int[] dimensions, final boolean state){
        if (dimensions == null || startBrickPos == null)
        {
            if (DEBUG) Log.d(TAG, "dimensions/startBrickPos is null");
            return;
        }

        if (bricksState == null)
            initBrickStates();

        if (DEBUG) Log.v(TAG, "markBricks, length: " + bricksState.length);

        // Prevent to much work on main thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Running on the rows
                for (int i = startBrickPos[ControllerLayout.ROW] ; i < startBrickPos[ControllerLayout.ROW] + dimensions[ControllerLayout.ROW] ; i++)
                    // Running on the columns.
                    for (int j = startBrickPos[ControllerLayout.COLUMN] ; j < startBrickPos[ControllerLayout.COLUMN] + dimensions[ControllerLayout.COLUMN] ; j++)
                    {
                        if (DEBUG)
                            Log.d(TAG, "Marking Brick["+i+"]["+j+"] = " + String.valueOf(state));

                        // Mark brick.
                        /* After the screen is locked or go to sleep in some devices the system will destroy and recreate the app due to orientation change,
                          This behavior can cause some problems that do not affect the UI and can be ignored just need to be caught.*/
                        try {
                            bricksState[i][j] = state;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(TAG, "requested brick is out of bounds, row: " + i + ", column: " + j);
                        }
                    }
            }
        }).start();

    }

    class bricksDragListener implements OnDragListener {

        private int lastBrick[] = new int[]{-1,-1}, currentBrick[] = new int[]{0,0};
        private DropZoneImage dropZoneImage;

        @Override
        public boolean onDrag(View v, DragEvent event) {

//            Log.d(TAG, "Drag, Action: " + event.getAction() + ", X: " + event.getX() + ", Y: " + event.getY());

            dropZoneImage = (DropZoneImage) event.getLocalState();
            currentBrick = getBrickNumberForCoordinates( event.getX() - ( brickSize * (dropZoneImage.getDimensions()[ControllerLayout.COLUMN]/2) ),event.getY() - ( brickSize * (dropZoneImage.getDimensions()[ControllerLayout.ROW]/2) ) );

            switch (event.getAction())
            {
                case DragEvent.ACTION_DRAG_LOCATION:
                    // Drag is hovering above a new and valid brick_w200_h200.
                    if  ( currentBrick[ControllerLayout.ROW] != -1 && currentBrick[ControllerLayout.COLUMN] != -1 )
                    {
                        // Check if there was a change from the last pos so the app wont redraw the same highlight.
                        if (drawDropZoneShadow && lastBrick[ControllerLayout.ROW] == currentBrick[ControllerLayout.ROW] && lastBrick[ControllerLayout.COLUMN] == currentBrick[ControllerLayout.COLUMN] )
                            return true;

                            if (DEBUG)
                            Log.d(TAG, "Brick, Row: " + currentBrick[ControllerLayout.ROW] + ", Column: " + currentBrick[ControllerLayout.COLUMN]);

                        // Get the most left brick_w200_h200 that the shadows is upon
                        if ( isAvailableForDrop(currentBrick, dropZoneImage.getDimensions()) )
                        {
                            if (DEBUG)
                                Log.d(TAG, "Available for drop");

                            highlightLayoutForButtonDrop(currentBrick, dropZoneImage.getDimensions() , true);
                        }
                        else
                        {
                            highlightLayoutForButtonDrop(currentBrick, dropZoneImage.getDimensions() , false);
                        }
                    }
                    // If the current position isn't Valid and a frame is drawn redraw the view without the highlight.
                    else if (drawDropZoneShadow)
                    {
                        if (DEBUG)
                            Log.d(TAG, "Hiding old highlight");

                        drawDropZoneShadow = false;
                        canDropShadow = false;

                        BrickBackGroundView.this.invalidate();
                    }
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    if (drawDropZoneShadow)
                    {
                        if (DEBUG)
                            Log.d(TAG, "Hiding old highlight");

                        drawDropZoneShadow = false;
                        canDropShadow = false;

                        BrickBackGroundView.this.invalidate();
                    }
                    break;

                case DragEvent.ACTION_DROP:

                    if (canDropShadow)
                    {
                        markBricksAsFull(currentBrick, dropZoneImage.getDimensions());

                        // If the button is moving from another position and it's not new.
                        if (dropZoneImage.getTaggedButton() != null)
                            dispatchButtonChangedPosition(dropZoneImage, currentBrick);
                        else
                            dispatchButtonAdded(dropZoneImage, currentBrick);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Cant drop here", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case DragEvent.ACTION_DRAG_ENDED:

                    if (DEBUG)
                        Log.d(TAG, "Drag Ended.");

                    drawDropZoneShadow = false;
                    canDropShadow = false;

                    // Button is moved here from another position.
                    // After button is dropped this tagged button is null,
                    // so this is only if button is moving and not dropped or dropped in invalid place.
                    if (dropZoneImage.getTaggedButton() != null)
                    {
                        dispatchButtonChangedPosition(dropZoneImage, dropZoneImage.getTaggedButton().getStartPosition());
                    }

                    BrickBackGroundView.this.invalidate();


                    break;
            }

            lastBrick = currentBrick;

            return true;
        }
    }

    /* Interfaces*/
    public interface DropListener{
        public void onButtonAdded(CustomButton customButton);
        public void onButtonDeleted(CustomButton customButton);
        public void onButtonChangedPosition(CustomButton customButton);
    }

    void setDropListener(DropListener dropListener) {
        this.dropListener = dropListener;
    }

    private void dispatchButtonAdded(DropZoneImage dropZoneImage, int[] startingPos){

        if (dropListener != null)
        {
            dropListener.onButtonAdded(CustomButton.fromDropZoneImage(dropZoneImage, startingPos));
        }
        else
        if (DEBUG)
            Log.e(TAG, "No button Added Listener");
    }

    private void dispatchButtonChangedPosition(DropZoneImage dropZoneImage, int[] startingPos){

        if (dropListener != null)
        {
            // If the button is moving from another position and is not new.
            if (dropZoneImage.getTaggedButton() != null)
            {
                dropZoneImage.getTaggedButton().setStartPosition(startingPos);
                dropListener.onButtonChangedPosition(dropZoneImage.getTaggedButton());
                // Setting to null so any other method using this check will know this been handled.
                dropZoneImage.setTaggedButton(null);
            }
        }
        else
        if (DEBUG)
            Log.e(TAG, "No button Added Listener");
    }

    /* Getters & Setters*/
    int getRowsRemainder() {
        return rowsRemainder;
    }

    int getColumnsRemainder() {
        return columnsRemainder;
    }

    int getRowsAmount() {
        return rowsAmount;
    }

    int getColumnsAmount() {
        return columnsAmount;
    }

    int getBrickSize() {
        return brickSize;
    }

    /* Building Site*/
    private int[] getStartPositionForButtonDropForRow(int[] brickPos, int[] dimensions){

        if (DEBUG)
            Log.d(TAG, "getStartPositionForButtonDropForRow");

        boolean found = false, upOk = true, downOk = false; //  Indicate if a place for the button is found
        int upSteps = 0, downSteps = 0, columnRunner;
        // Get all relevant and available bricks that can contain this button in the row.
        int [] fromTo = getAllAvailableBricksForButtonInRow(brickPos[ControllerLayout.COLUMN], brickPos[ControllerLayout.ROW], dimensions[ControllerLayout.WIDTH]);

        // Run from the starting optional brick_w200_h200 to the last. (not exactly the last, but instead the last available brick_w200_h200 minus the size of the button minus one.
        for (columnRunner  = fromTo[ControllerLayout.FROM]; (columnRunner < (fromTo[ControllerLayout.TO] - dimensions[ControllerLayout.WIDTH] - 1)) && !found ; columnRunner++)
        {
            for (int j = 1 ; (j < dimensions[ControllerLayout.HEIGHT]) && (upOk || downOk) ; j++)
            {
                /* "downOk" - make sure that the checking process did not end for that side due to out of bounds or taken spot.
                *  "(brickPos[ROW] - 1 >= 0) - Check if that the check positions is still inside the grid dimensions.
                * "checkRowAvailableBricks" - Check the row for given positions.*/
                if (downOk && (brickPos[ControllerLayout.ROW] - j >= 0) && checkRowAvailableBricks(brickPos[ControllerLayout.ROW] - j, columnRunner, columnRunner + (dimensions[ControllerLayout.WIDTH] - 1)))
                {
                    downSteps++;
                }
                else downOk = false;

                /* Check if enough is found*/
                if (downSteps + upSteps + 1 == dimensions[ControllerLayout.HEIGHT])
                {
                    found = true;
                    break;
                }

                /* "upOk" - make sure that the checking process did not end for that side due to out of bounds or taken spot.
                *  "(brickPos[ROW] + 1 >= bricksState[brickPos[ROW]].length)" - Check if that the check positions is still inside the grid dimensions.
                * "checkRowAvailableBricks" - Check the row for given positions.*/
                if (upOk && (brickPos[ControllerLayout.ROW] + j >= bricksState[brickPos[ControllerLayout.ROW]].length)
                        && checkRowAvailableBricks(j + brickPos[ControllerLayout.ROW], columnRunner, columnRunner + (dimensions[ControllerLayout.WIDTH] - 1)))
                {
                    upSteps++;
                }
                else upOk = false;

                /* Check if enough is found*/
                if (downSteps + upSteps + 1 == dimensions[ControllerLayout.HEIGHT])
                {
                    found = true;
                    break;
                }
            }
        }

        dropZoneShadowCoordinates[ControllerLayout.ROW] = Math.abs(brickPos[ControllerLayout.ROW] - downSteps);
        dropZoneShadowCoordinates[ControllerLayout.COLUMN]= Math.abs(brickPos[ControllerLayout.COLUMN] - columnRunner);


        return dropZoneShadowCoordinates;
    }

    private int[] getAllAvailableBricksForButtonInRow(int startBrick, int row, int size){

        if (DEBUG)
            Log.d(TAG, "");

        int startPos = startBrick;
        int leftSteps = 0, rightSteps = 0;
        boolean leftOk = true, rightOk = true;

        // Run on the columns size
        for (int i = 1; (i < size) && (rightOk || leftOk) ; i++)
        {
            /* "leftOk" - make sure that the checking process did not end for that side due to out of bounds or taken spot.
            *  "bricksState[row][startBrick  - i]" - check that the brick_w200_h200 is available.
            * "startBrick - i >= 0" - Check if that the check positions is still inside the row dimensions.*/
            if (leftOk && bricksState[row][startBrick  - i] && startBrick - i >= 0 )
            {
                startPos = startBrick  - i;
                leftSteps++;
            }
            else leftOk = false;

            /* "rightOk" - make sure that the checking process did not end for that side due to out of bounds or taken spot.
            *  "bricksState[row][startBrick  + i]" - check that the brick_w200_h200 is available.
            * "startBrick  + i < bricksState[row].length" - Check if that the check positions is still inside the row dimensions.*/
            if (rightOk && bricksState[row][startBrick  + i] && startBrick  + i < bricksState[row].length)
            {
                rightSteps++;
            }
            else rightOk = false;
        }

        return new int[]{startBrick - leftSteps, startBrick + rightSteps};
    }

    private int getRoundedBrickSize(int brickSize){

        if (brickSize < 100)
        {
            brickSize = 50;
        }
        else if (brickSize < 150)
        {
            brickSize = 100;
        }
        else if (brickSize < 200)
        {
             brickSize = 150;
        }
        else if (brickSize < 300)
        {
            brickSize = 200;
        }
        else if (brickSize < 400)
        {
            brickSize = 300;
        }
        else brickSize = 500;

        return brickSize;
    }

    private Bitmap getBitmapForBrickSize(int brickSize){
        if (brickSize < 50)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w50_h50), brickSize, brickSize);
        }
        else if (brickSize <= 100)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w100_h100), brickSize, brickSize);
        }
        else if (brickSize <= 150)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w150_h150), brickSize, brickSize);
        }
        else if (brickSize <= 200)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w200_h200), brickSize, brickSize);
        }
        else if (brickSize <= 300)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w300_h300), brickSize, brickSize);
        }
        else if (brickSize <= 400)
        {
            return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w400_h400), brickSize, brickSize);
        }

        return getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brick_w500_h500), brickSize, brickSize);
    }

    private Bitmap getResizeBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizeBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizeBitmap;
    }

}
