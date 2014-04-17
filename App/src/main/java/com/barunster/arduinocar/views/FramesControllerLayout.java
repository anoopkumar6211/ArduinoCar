package com.barunster.arduinocar.views;

import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.custom_controllers_obj.ImageDragShadowBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 3/27/14.
 */
public class FramesControllerLayout extends RelativeLayout implements View.OnLongClickListener{

    // TODO fix problem with frames and buttons has same id that causes cast errors

    private static final String TAG = FramesControllerLayout.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int rowsAmount, columnsAmount, cellSize, columnSpace, rowSpace;
    private boolean editing = false, showingAvailableFrames = false;
    private CustomController customController;
    private OnClickListener onClickListener;
    private SlideButtonLayout.SlideButtonListener slideButtonListener;

    private ControllerLayoutListener controllerLayoutListener;

    public FramesControllerLayout(Context context, int rows, int columns) {
        super(context);

        init();

        this.rowsAmount = rows;
        this.columnsAmount = columns;

    }

    public FramesControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

        this.rowsAmount = 4;
        this.columnsAmount = 4;
    }

    private void init(){
//        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG)
            Log.i(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (DEBUG)
            Log.i(TAG, "onLayout,  Frames: " + this.getChildCount());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (DEBUG)
            Log.i(TAG, "onSizeChanged, Width: " + w + ", Height: " + h + ", Old Width: " + oldw + ", OldHeight: " + oldh);

        if (oldh != 0 || oldw != 0)
        {
            post(new Runnable() {
                @Override
                public void run() {
                    setCustomController(customController);
                }
            });

            // Scale animation from size change. dosen't work.
            /*ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(500);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    setVisibility(VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            startAnimation(animation);*/
        }
    }

    private void initFramesForController(CustomController controller){

        customController = controller;

        rowsAmount = customController.getRows();
        columnsAmount = customController.getColumns();

        if (DEBUG)
            Log.d(TAG, "initFramesForController, Name: " + controller.getName());

        initFrames();

        DropZoneFrame frame;

        for (CustomButton btn : customController.getButtons())
        {
//            if (DEBUG)
//                Log.d(TAG, "Btn ID: " + btn.getId() + ", Type: " + btn.getType() + ", Size: " + btn.getSize() );

            frame = (DropZoneFrame) this.getChildAt(btn.getPosition());

            RelativeLayout.LayoutParams frameParams;

            if (btn.getOrientation() == LinearLayout.HORIZONTAL)
            {
//                if (DEBUG)
//                    Log.d(TAG, "Button orientation is horizontal");

                frameParams = new RelativeLayout.LayoutParams(
                        (cellSize * btn.getSize()) + ( columnSpace * (btn.getSize() - 1) ), cellSize);

                // Hiding not relevant frames
                markFramesForButton(btn, false, 1);
            }
            else if (btn.getOrientation() == LinearLayout.VERTICAL)
            {
//                if (DEBUG)
//                    Log.d(TAG, "Button orientation is vertical");

                frameParams = new RelativeLayout.LayoutParams(
                        cellSize, (cellSize * btn.getSize()) + ( rowSpace * (btn.getSize() - 1) ));

                // Hiding not relevant frames
                markFramesForButton(btn, false, columnsAmount);
            }
            else
            {
                if (DEBUG)
                    Log.d(TAG, "Button does not have orientation");
                frameParams = (LayoutParams) frame.getLayoutParams();
            }

            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin   ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin  ;
            frame.setLayoutParams(frameParams);

            // Setting the frame to full
            frame.setEmpty(false);

            frame.setTag(btn.getType());

            createButtonForFrame(frame, btn.getType(), btn).setId((int) btn.getId());
        }

    }

    private void initFrames(){
        DropZoneFrame frame;
        RelativeLayout.LayoutParams frameParams;
        cellSize = rowsAmount >= columnsAmount ? (int)getMeasuredHeight() / (rowsAmount + 1) : (int)getMeasuredHeight() / (columnsAmount + 1)  ; // TODO find better solution then calc by height both scenarios
        columnSpace = (int) ( getMeasuredWidth() - (cellSize * columnsAmount) ) / (columnsAmount+1);
        rowSpace = (int) ( getMeasuredHeight() - (cellSize * rowsAmount) ) / (rowsAmount + 1);

        if (DEBUG) {
            Log.i(TAG, "Rows: " + rowsAmount + ", Columns: " + columnsAmount);
            Log.i(TAG, "Measured height = " + getMeasuredHeight() + ", Measured width: " + getMeasuredWidth());
            Log.i(TAG, "Row space = " + rowSpace + ", Column space: " + columnSpace + ", Cell Size: " + cellSize);
        }


        // Adding the grid frames.
        for (int i = 0; i < rowsAmount  ; i++)
        {
            for (int j = 0 ; j < columnsAmount; j++)
            {
                frame = new DropZoneFrame(getContext());
                frame.setId(i * columnsAmount + j);
                frame.setOnDragListener(new GridDropZoneListener());
                frame.setColNumber(j);
                frame.setRowNumber(i);

                frameParams = new RelativeLayout.LayoutParams(cellSize, cellSize);
                frameParams.leftMargin = (j + 1) * columnSpace+ (cellSize*j);
                frameParams.topMargin = (i + 1) * rowSpace + (cellSize*i); //  Calc of spacing from the top. Space * the row number + the amount of cells above size

                if (DEBUG)
                    Log.i(TAG, "frame id: " + frame.getId() + " Left Margin: " + frameParams.leftMargin + " Top Margin: " + frameParams.topMargin);

                frame.setLayoutParams(frameParams);

                this.addView(frame);
            }
        }

        // Adding additional two frames for dumping unwanted buttons.

        /*#1*/
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setId(1000);
        frameLayout.setOnDragListener(new DeleteButtonZoneListener());
        frameParams = new RelativeLayout.LayoutParams(columnSpace, getMeasuredHeight() - (rowSpace * 2));
        frameParams.topMargin = rowSpace;
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        frameLayout.setLayoutParams(frameParams);
        this.addView(frameLayout);

        /*#2*/
        frameLayout = new FrameLayout(getContext());
        frameLayout.setId(1001);
        frameLayout.setOnDragListener(new DeleteButtonZoneListener());
        frameParams = new RelativeLayout.LayoutParams(columnSpace, getMeasuredHeight() - (rowSpace * 2));
        frameParams.topMargin = rowSpace;
        frameParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        frameLayout.setLayoutParams(frameParams);
        this.addView(frameLayout);

    }

    /* Creating a button inside a frame */
    private View createButtonForFrame(DropZoneFrame frame,final int type, CustomButton customButton){

        Log.d(TAG, "creating button for frame in row = " + frame.getRowNumber() + ", column = " + frame.getColNumber() + ", frame id: " + frame.getId());

        SlideButtonLayout slideButtonLayout = null;
        SimpleButton button = null;

        // Removing all old views that can cause some problems with interface.
        frame.removeAllViews();

        // Making sure no background will interferes.
        frame.setNormalMode();

        switch (type)
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                button = new SimpleButton(getContext());

                // Assign image
                if (customButton.getCustomCommand() != null) {
                    if (DEBUG)
                        Log.d(TAG, "Button has command, Type: " + getResources().getString(customButton.getCustomCommand().getType()));

                    button.setCommandType(customButton.getCustomCommand().getType());
                }
                else
                    button.setCommandType(0);

                // type
                button.setType(type);

                button.setOnClickListener(onClickListener);
                button.setOnLongClickListener(this);

                // Adding to the frame.
                frame.addView(button);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:

                slideButtonLayout =
                        new SlideButtonLayout(getContext(), LinearLayout.HORIZONTAL, cellSize, customButton.centerAfterDrop(), customButton.showMarks());

                slideButtonLayout.setType(type);

                // Adding the distance from the top.
                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins(0, ((RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin, 0, 0);

                slideButtonLayout.setSlideButtonListener(slideButtonListener);
                slideButtonLayout.setLayoutSizeInCells(customButton.getSize());
                slideButtonLayout.setOnLongClickListener(this);

                frame.addView(slideButtonLayout);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:

                slideButtonLayout =
                        new SlideButtonLayout(getContext(), LinearLayout.VERTICAL, cellSize, customButton.centerAfterDrop(), customButton.showMarks());

                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins( ((RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin, 0 ,0 , 0);

                slideButtonLayout.setType(type);

                slideButtonLayout.setSlideButtonListener(slideButtonListener);
                slideButtonLayout.setOnLongClickListener(this);
                slideButtonLayout.setLayoutSizeInCells(customButton.getSize());

                frame.addView(slideButtonLayout);

                break;
        }

        return (button != null  ? button : slideButtonLayout);
    }

    public void showAvailableFrames(){

        if (DEBUG)
            Log.d(TAG, "showAvailableFrames" + (showingAvailableFrames ? ", Already Showing." : "."));

        for (int i = 0 ; i < this.getChildCount() ; i++)
        {
            if (this.getChildAt(i) instanceof DropZoneFrame)
            {
                final DropZoneFrame drop = (DropZoneFrame) this.getChildAt(i);

                if(drop.isEmpty() && drop.getVisibility() == View.VISIBLE)
                {
                    drop.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                    drop.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            drop.setToEditMode();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        }

        showingAvailableFrames = true;
    }

    public void hideAvailableFrames(){

        for (int i = 0 ; i < this.getChildCount() ; i++)
        {
            if (this.getChildAt(i) instanceof DropZoneFrame) {
                final DropZoneFrame drop = (DropZoneFrame) this.getChildAt(i);
                if (drop.isEmpty() && drop.getVisibility() == View.VISIBLE) {
                    drop.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
                    drop.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            drop.setNormalMode();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }
        }

        showingAvailableFrames = false;
    }

    /* Change empty frames background so it would be visible or hidden*/
    public void enterEditMode(){
        if(DEBUG)
            Log.i(TAG, "enterEditMode");

        if (showingAvailableFrames)
            hideAvailableFrames();

        editing = true;

        Log.d(TAG, "Children: " + this.getChildCount());
    }

    public void exitEditMode(){
        if(DEBUG)
            Log.i(TAG, "exitEditMode");



        editing = false;
    }

    @Override
    public boolean onLongClick(View v) {

        if (DEBUG)
            Log.d(TAG, "onLongClick");

        if (editing)
        {
            // Setting the frame back to normal
            RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(cellSize, cellSize);

            // The frame to manipulate
            // making sure casting will be ok.
            if (v instanceof SlideButton)
            {
                v = (View) v.getParent();
            }

            DropZoneFrame frame = ((DropZoneFrame)v.getParent());
            frame.setEmpty(true);
            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin   ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin  ;
            frame.setLayoutParams(frameParams);

            ClipData data = ClipData.newPlainText("", "");

            // The image to drag
            DropZoneImage dropZoneImage = new DropZoneImage(getContext());
            dropZoneImage.setTaggedButton(customController.getCustomButtonById(v.getId()));

            if (DEBUG)
                if (dropZoneImage.getTaggedButton() == null)
                    Log.e(TAG, "Tagged button is null, button id: " + v.getId());
                else Log.d(TAG, "Tagged button poistion = " + dropZoneImage.getTaggedButton().getPosition());

            // The amount of cells the button take on the layout.
            int buttonSize = 1;

            if (v instanceof SimpleButton)
            {
                dropZoneImage.setType(((SimpleButton)v).getType());
                if (DEBUG)
                    Log.d(TAG, "Button is simple button");
            }
            else if (v instanceof SlideButtonLayout)
            {
                buttonSize = ((SlideButtonLayout)v).getLayoutSizeInCells();

                if (DEBUG)
                    Log.d(TAG, "Button is slide button, size: " + buttonSize);

                dropZoneImage.setType(((SlideButtonLayout) v).getType());

                // Showing the frame that are now available when the button is on the move.
                markFramesForButton(dropZoneImage.getTaggedButton(), true);
            }

            // Setting the size, Simple button size is one. sliding layout slide can be from 2 and up.
            dropZoneImage.setSize(buttonSize);

            int viewResourceId = R.drawable.stick_button;

            v.startDrag(data,  // the data to be dragged
                    ImageDragShadowBuilder.fromResource(getContext(), viewResourceId),  // the drag shadow builder
                    dropZoneImage,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );

            // Removing the view from the frame.
            frame.removeView(v);

            showAvailableFrames();
        }

        return editing;
    }

    /*--- Finding, Hiding, Showing Frames ---*/
    private void markFramesForButton(CustomButton customButton, boolean show, int skip){
        markFramesForButton(customButton.getPosition() + skip,
                customButton.getSize() - (customButton.getOrientation() == LinearLayout.HORIZONTAL ? skip : skip/columnsAmount)
                , customButton.getOrientation(), show);
    }

    private void markFramesForButton(CustomButton customButton, boolean show){
        markFramesForButton(customButton.getPosition(), customButton.getSize(), customButton.getOrientation(), show);
    }

    private void markFramesForButton(int fromId, int size, int orientation, boolean show){
        if (orientation == LinearLayout.HORIZONTAL)
        {
            for (int i = fromId ; i < fromId + size ; i++)
            {
                Log.d(TAG," Showing in pos = " + i);
                if (show)
                {
                    this.getChildAt(i).setVisibility(View.VISIBLE);
                    ((DropZoneFrame)this.findViewById(i)).setEmpty(true);

                    if (DEBUG)
                        Log.d(TAG," Showing in pos = " + i);
                }
                else
                {
                    this.getChildAt(i).setVisibility(View.GONE);
                    ((DropZoneFrame)this.findViewById(i)).setEmpty(false);

                    if (DEBUG)
                        Log.d(TAG," Hiding in pos = " + i);
                }


            }
        }
        else
        {
            for (int i = fromId; i < fromId + (size * columnsAmount) ; i += columnsAmount )
            {
                if (show)
                {
                    if (DEBUG)
                        Log.d(TAG," Showing in pos = " + i);

                    this.getChildAt(i).setVisibility(View.VISIBLE);
                    ((DropZoneFrame)this.findViewById(i)).setEmpty(true);
                }
                else
                {
                    if (DEBUG)
                        Log.d(TAG," Hiding in pos = " + i);

                    this.getChildAt(i).setVisibility(View.GONE);
                    ((DropZoneFrame)this.findViewById(i)).setEmpty(false);
                }

            }
        }
    }

    private List<DropZoneFrame> getRelevantFramesForButton(CustomButton customButton){
//        if (highlightedZones.size() > 0)

        List<DropZoneFrame> list = new ArrayList<DropZoneFrame>();

        if (customButton.getOrientation() == LinearLayout.HORIZONTAL)
        {
            for (int i = customButton.getPosition() ; i < customButton.getPosition() + customButton.getSize() ; i++)
            {
                list.add((DropZoneFrame) findViewById(i));

                if (DEBUG)
                    Log.d(TAG, "Ended, Highlighted Zone id: " + findViewById(i).getId());
            }
        }
        else
        {
            for (int i = customButton.getPosition() ; i < customButton.getPosition() + (customButton.getSize()*columnsAmount) ; i += columnsAmount )
            {
                list.add((DropZoneFrame) findViewById(i));

                if (DEBUG)
                    Log.d(TAG, "Ended, Highlighted Zone id: " + findViewById(i).getId());
            }
        }

        return list;
    }

    /* Drag Listener*/
    class GridDropZoneListener implements View.OnDragListener {
//        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
//        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        private DropZoneFrame frame;
        private DropZoneImage dropZoneImage;
        private List<DropZoneFrame> highlightedZones = new ArrayList<DropZoneFrame>();

        @Override
        public boolean onDrag(View v, DragEvent event) {

            dropZoneImage = (DropZoneImage) event.getLocalState();

            if (dropZoneImage != null)
            {
                frame = ((DropZoneFrame)v);


                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        highlightedZones = dragEnteredLogic(frame);
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        dragExitedLogic(frame);
                        highlightedZones = new ArrayList<DropZoneFrame>();
                        break;

                    case DragEvent.ACTION_DROP:
                        dragDropLogic(frame);
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        dragEndedLogic(frame);
                        break;

                    default:
                        break;
                }
            }
            else
                Log.e(TAG, "dropZoneImage is null");

            return true;
        }

        private List<DropZoneFrame> dragEnteredLogic(DropZoneFrame frame){
            if (DEBUG)
                Log.d(TAG, "Grid Frame, Drag Entered");

            List<DropZoneFrame> tmp = new ArrayList<DropZoneFrame>();

            // If the frame is empty
            if (frame.isEmpty())
            {
                // If the size is more then one calculate if the position is available for drop.
                if (dropZoneImage.getSize() > 1)
                {
                    return calcMovementOptions(frame);
                }
                // size is one show available for drop
                else
                {
                    frame.setToAvailableMode();
                    tmp.add(frame);
                }
            }
            // The frame is occupied show un available and hide the existing content for now
            else
            {
                frame.getChildAt(0).setAlpha(0.0f);
                frame.setNotAvailableMode();
                tmp.add(frame);
            }

            return tmp;
        }

        /* Drag Action Logic*/
        private void dragExitedLogic(DropZoneFrame frame){
            if (DEBUG)
                Log.d(TAG, "Grid Frame, Drag Exited");

            // Exit an empty frame set back do edit mode
            if (frame.isEmpty())
            {
                for (DropZoneFrame d : highlightedZones)
                    d.setToEditMode();
            }
            // Frame is full set back to normal state(transparent) and set the view alpha back to one so it would be visible
            else
            {
                frame.setNormalMode();
                frame.getChildAt(0).setAlpha(1.0f);
            }
        }

        private void dragDropLogic(DropZoneFrame frame){
            if (DEBUG)
                Log.d(TAG, "Grid Frame, Dropped");

            // If the spot is available for drop.
            if (dropZoneImage != null && frame.canDrop())
            {
                // Check if the image button has a button tagged to it indicating that the drag was moving an button to a new position.
                boolean moveAction = dropZoneImage.getTaggedButton() != null;

                // Setting the image from the dragged view to the container picked.
                dropButtonToFrame(frame);

                // If it was a move action hide all available frames.
                if (moveAction)
                {
                    hideAvailableFrames();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Can't drop here.", Toast.LENGTH_LONG).show();

                for (DropZoneFrame d : highlightedZones)
                    if(!d.isEmpty()) {
                        d.setNormalMode();
                        frame.getChildAt(0).setAlpha(1.0f);
                    }
                    else
                    {
                        d.setToEditMode();
                    }

                // Check if the image button has a button tagged to it indicating that the drag was moving an button to a new position.
                if (dropZoneImage.getTaggedButton() != null)
                {
                    highlightedZones = getRelevantFramesForButton(dropZoneImage.getTaggedButton());

                    dropButtonToFrame((DropZoneFrame) findViewById(dropZoneImage.getTaggedButton().getPosition()));

                    hideAvailableFrames();
                }
            }
        }

        private void dragEndedLogic(DropZoneFrame frame){
//                        if (DEBUG)
//                            Log.d(TAG, "Grid Frame, Drag Ended");

            // Only notify the fragment that the drag was done if the layout is not in edit button mode.
            if (!editing) {
                if (controllerLayoutListener != null)
                    controllerLayoutListener.onDragEnded();
            }

            // Check if the image button has a button tagged to it indicating that the drag was moving an button to a new position.
            if (dropZoneImage.getTaggedButton() != null)
            {
                DropZoneFrame relatedFrame = (DropZoneFrame) findViewById(dropZoneImage.getTaggedButton().getPosition());

                // If the size is more then one calculate if the position is available for drop.
                if (dropZoneImage.getSize() > 1)
                {
                    highlightedZones = getRelevantFramesForButton(dropZoneImage.getTaggedButton());
                }

                if (DEBUG)
                    Log.d(TAG, "frame position: " + dropZoneImage.getTaggedButton().getPosition());

                dropButtonToFrame(relatedFrame);

                hideAvailableFrames();
            }
//                        else
//                            if (DEBUG)
//                                Log.d(TAG, "DropZone Image doesnt have a tag");
        }
        /*--------------------*/

        private void dropButtonToFrame(DropZoneFrame frame){
            RelativeLayout.LayoutParams frameParams;
            DropZoneFrame tmpFrame;

            // If the dropZoneImage size is more then one get the left/top frame.
            // The Frame inflation is always starting from the most left/top frame that is highlighted for available for drop.
            if (dropZoneImage.getSize() > 1) {
                if (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL) {
                    // Getting the most left frame.

                    tmpFrame = getMostLeftFrame();

                    if (tmpFrame != null)
                        frame = tmpFrame;

                    frameParams = new RelativeLayout.LayoutParams(
                            (cellSize * dropZoneImage.getSize()) + (columnSpace * (dropZoneImage.getSize() - 1)), cellSize);
                } else {
                    // Getting the to[ frame.
                    tmpFrame = getTopFrame();

                    if (tmpFrame != null)
                        frame = tmpFrame;

                    frameParams = new RelativeLayout.LayoutParams(
                            cellSize, (cellSize * dropZoneImage.getSize()) + (rowSpace * (dropZoneImage.getSize() - 1)));
                }

                // Hiding all frames that are overridden by the inflated new frame.
                for (DropZoneFrame d : highlightedZones)
                {
                    if (DEBUG)
                        Log.d(TAG, "Highlighted Zone id: " + d.getId());
                    if (!d.equals(frame))
                    {
                        d.setParentId(frame.getId());
                        d.setVisibility(View.GONE);
                        d.setEmpty(false);
                    }
                }
            }
            else
                frameParams = (LayoutParams) frame.getLayoutParams();



            // Adding the margin to the SlideLayout, The final margin is the actual frame margin + column/row number + cellSize r.g row 3 * 144
            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin;
            frame.setLayoutParams(frameParams);

            // Setting the frame to full
            frame.setEmpty(false);

            frame.setTag(dropZoneImage.getType());

            // Check if the image button has a button tagged to it indicating that the drag was moving an existing button to a new position.
            View view;
            if (dropZoneImage.getTaggedButton() != null)
            {
                dropZoneImage.getTaggedButton().setPosition(frame.getId());

                view = createButtonForFrame(frame, dropZoneImage.getType(), dropZoneImage.getTaggedButton());
                view.setId((int) dropZoneImage.getTaggedButton().getId());

                if (controllerLayoutListener != null)
                    controllerLayoutListener.onButtonChanged(dropZoneImage.getTaggedButton(), view);

                // For safety with the drag ended event that check to see if the drop wat nowhere and the button is recreated in the original place.
                dropZoneImage.setTaggedButton(null);
            }
            // new button is added the button is added to the current controller.
            else
            {
                // Creating the custom button object
                CustomButton customButton = new CustomButton(
                        customController.getId(), dropZoneImage.getType(), dropZoneImage.getSize(), dropZoneImage.getOrientation(), frame.getId());

                view = createButtonForFrame(frame, dropZoneImage.getType(), customButton);

                if (controllerLayoutListener != null)
                    controllerLayoutListener.onButtonAdded(customButton, view);

                // Adding the button id that inserted to the view after the button is added to the database in the controller fragment.
                customButton.setId(view.getId());

                // Add the button to the controller
                customController.getButtons().add(customButton);
            }
        }

        private List<DropZoneFrame> calcMovementOptions(DropZoneFrame frame){
            boolean canGoLeft = true;
            boolean canGoRight = true;
            int leftSteps = 0, rightSteps = 0;
            int count = dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/; // counting the neighbor view to check
            int posInGrid = dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? frame.getColNumber() : frame.getRowNumber();// Get the position of the view in the row


            if (DEBUG)
                Log.d(TAG, "calcMovementOptions, DropZoneImage Size: " + dropZoneImage.getSize() + ", Orientation: " + ( dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? "Horizontal." : "Vertical.") );

         /*   boolean stepUpright = true, stepDownLeft = true;

            do{
                stepDownLeft = stepDownLeft &&
                        ControllerLayout.this.getChildAt(frame.getId() - count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) ControllerLayout.this.getChildAt(frame.getId() - count)).isEmpty(); // Check that the view is empty;
            }while (true);
*/


            do
            {
                canGoLeft = canGoLeft && // Making sure the last check was ok to
                        posInGrid - ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) ) >= 0 && // Check that we are still on the same row or column depends on oreintation.
                        FramesControllerLayout.this.getChildAt(frame.getId() - count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() - count)).isEmpty(); // Check that the view is empty;

                canGoRight = canGoRight && // Making sure the last check was ok to
                        posInGrid + ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount) )  < columnsAmount && // Check that we are still on the same row
                        FramesControllerLayout.this.getChildAt(frame.getId() + count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() + count)).isEmpty();

                if (canGoLeft)
                {
                    leftSteps++;
                }

                if (canGoRight)
                {
                    rightSteps++;
                }

                if (DEBUG)
                {
                    Log.d(TAG, "Checked Id's = " + (frame.getId() - count) + ", " + (frame.getId() + count));

                    Log.d(TAG, "Checked Position's = " + (posInGrid - ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) ) )
                            + ", " + (posInGrid + ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) ) ) );

                    Log.d(TAG, "Count: " + count
                            + (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoLeft: " : "Can go Down") + String.valueOf(canGoLeft) + " Steps: " + leftSteps
                            +  (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoRight: " : " Can Go Up ") + String.valueOf(canGoRight) + " Steps: " + rightSteps);
                }

                count += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/;

            } while ( (canGoLeft || canGoRight) && count < dropZoneImage.getSize() * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) );

            return processMovementOptionFindings(frame, canGoLeft, canGoRight, leftSteps, rightSteps);
        }

        private List<DropZoneFrame> processMovementOptionFindings(DropZoneFrame frame, boolean canGoLeft, boolean canGoRight, int leftSteps, int rightSteps){
            int count;

            List<DropZoneFrame> relevantFrame = new ArrayList<DropZoneFrame>();

            // If there is a place from left to right of the view
            if (rightSteps + leftSteps + 1 == dropZoneImage.getSize())
            {
//                                Log.d(TAG, "There is a place with right and left combine");

                for (int i = frame.getId() - (leftSteps * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/)) ;
                     i <= frame.getId() + (rightSteps * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) ) ;
                     i += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/)
                {
                    if (DEBUG)
                        Log.d(TAG, "Process, I: " + i);

                    ((DropZoneFrame) FramesControllerLayout.this.getChildAt(i)).setToAvailableMode();
                    relevantFrame.add(((DropZoneFrame) FramesControllerLayout.this.getChildAt(i)));
                }
            }
      /*                      else if (rightSteps + leftSteps + 1 > dropZoneImage.getSize()) // TODO May cause some problems
                            {
                                count = 1 ;
                                frame.setToAvailableForDrop();
                                highlightedZones.add(frame);

                                int num = 1;
                                while (count < dropZoneImage.getSize())
                                {
                                    ((DropZoneFrame) mainView.findViewById(frame.getId() - num)).setToAvailableForDrop();
                                    highlightedZones.add(((DropZoneFrame) mainView.findViewById(frame.getId() - num)));

                                    num = -num;
                                    count++;
                                }

                            }*/
            else if (canGoLeft || canGoRight)
            {
                count = 0;

                if (DEBUG)
                    Log.d(TAG, "Going To The " + ( canGoLeft ? "Left" : "Right" ));

                while (count < dropZoneImage.getSize() * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/) )
                {
                    if (canGoLeft)
                    {
                        ((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() - count)).setToAvailableMode();
                        relevantFrame.add(((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() - count)));
                    }
                    else
                    {
                        ((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() + count)).setToAvailableMode();
                        relevantFrame.add(((DropZoneFrame) FramesControllerLayout.this.getChildAt(frame.getId() + count)));
                    }

                    count += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : columnsAmount/*?rowsAmount*/;

                    if (DEBUG)
                        Log.d(TAG, " Count: " + count);
                }
            }
            else
            {
                frame.setNotAvailableMode();
                relevantFrame.add(frame);
            }

            return relevantFrame;
        }

        private DropZoneFrame getMostLeftFrame(){

            if (DEBUG)
                Log.d(TAG, "highlightedZones size: " + highlightedZones.size());

            DropZoneFrame frame = null;

            if (highlightedZones.size() > 0)
            {
                frame = highlightedZones.get(0);

                for (DropZoneFrame d : highlightedZones)
                {
                    if (DEBUG)
                        Log.d(TAG, "Highlighted Zone id: " + d.getId());
//                Log.d(TAG, "Pos in row: " + d.getColNumber());
                    if (d.getColNumber() < frame.getColNumber())
                    {
                        frame = d;
                    }
                }

                if (DEBUG)
                    Log.d(TAG, "MostLeft: " + (frame != null ? frame.getColNumber() : " frame is null") );
            }
            else
                Log.e(TAG, "No highlighted zones");



            return frame;
        }

        private DropZoneFrame getTopFrame(){

            if (DEBUG)
                Log.d(TAG, "highlightedZones size: " + highlightedZones.size());

            DropZoneFrame frame = null;

            if (highlightedZones.size() > 0)
            {
                frame = highlightedZones.get(0);

                for (DropZoneFrame d : highlightedZones)
                {
                    if (DEBUG)
                        Log.d(TAG, "Highlighted Zone id: " + d.getId());

    //                Log.d(TAG, "Pos in row: " + d.getColNumber());
                    if (d.getRowNumber() < frame.getRowNumber())
                    {
                        frame = d;
                    }
                }

                if (DEBUG)
                    Log.d(TAG, "Most Top: " + (frame != null ? frame.getRowNumber() : "frame is null" ) );
            }
            else
                Log.e(TAG, "No highlighted zones");



            return frame;
        }
    }

    class DeleteButtonZoneListener implements View.OnDragListener{

        private DropZoneImage dropZoneImage;
        private FrameLayout frameLayout;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            dropZoneImage = (DropZoneImage) event.getLocalState();

            frameLayout = (FrameLayout)v;

            if (editing)
            {
                switch (event.getAction()) {

                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (DEBUG)
                            Log.d(TAG, "Delete, Drag Entered");
                        frameLayout.setBackgroundResource(android.R.color.holo_red_dark);
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:

                        if (DEBUG)
                            Log.d(TAG, "Delete, Drag Exited");
                        frameLayout.setBackgroundResource(android.R.color.transparent);
                        break;

                    case DragEvent.ACTION_DROP:

                        if (DEBUG)
                            Log.d(TAG, "Delete, Dropped");

                        frameLayout.setBackgroundResource(android.R.color.transparent);
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();

                        if (controllerLayoutListener != null)
                            controllerLayoutListener.onButtonRemoved(dropZoneImage.getTaggedButton().getId());

                        // Removing the button from the controller list.
                        customController.getButtons().remove(dropZoneImage.getTaggedButton());

                        dropZoneImage.setTaggedButton(null);

                        break;

                    case DragEvent.ACTION_DRAG_ENDED:

                        if (DEBUG)
                            Log.d(TAG, "Delete, Drag Ended");

                        break;
                }
            }

            return true;
        }
    }

    /* Notify about button changes and drag ending.*/
    public interface ControllerLayoutListener{
        public void onButtonAdded(CustomButton customButton, View view);
        public void onDragEnded();
        public void onButtonRemoved(long buttonId);
        public void onButtonChanged(CustomButton customButton, View view);
    }

    /** Set a new custom controller for this layout. The controller will be inflated again for this layout.*/
    public void setCustomController(CustomController customController){
        this.customController = customController;

        if (DEBUG)
        {
            Log.d(TAG, "setCustomController, Id: " + customController.getId() + ", Name: " + customController.getName() + ", Number of buttons: " + customController.getButtons().size() );
        }

        if (onClickListener == null)
            Log.e(TAG, "click listener is null");

        if (this.getChildCount() > 0) {

            if (DEBUG)
                Log.d(TAG, "Removing views, Sum: " + this.getChildCount());

            this.removeAllViews();
        }

        initFramesForController(customController);
    }

    /* Getter & Setters*/
    public void setControllerLayoutListener(ControllerLayoutListener controllerLayoutListener) {
        this.controllerLayoutListener = controllerLayoutListener;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener == null)
            Log.e(TAG, "set on click listener is null");
        this.onClickListener = onClickListener;
    }

    public void setSlideButtonListener(SlideButtonLayout.SlideButtonListener slideButtonListener) {
        this.slideButtonListener = slideButtonListener;
    }

    public boolean isEditing() {
        return editing;
    }
}
