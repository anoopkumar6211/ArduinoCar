/*
package com.barunster.arduinocar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.AccelerometerHandler;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.fragments.bottom_menu.AddCustomButtonFragment;
import com.barunster.arduinocar.fragments.bottom_menu.AddCustomCommandFragment;
import com.barunster.arduinocar.views.DropZoneFrame;
import com.barunster.arduinocar.views.SimpleButton;
import com.barunster.arduinocar.views.SlideButtonLayout;

import java.util.ArrayList;
import java.util.List;

import braunster.btconnection.Command;

*/
/**
 * Created by itzik on 3/11/14.
 *//*

public class CustomControllerFragmentBackup extends ArduinoLegoFragment implements View.OnClickListener, SlideButtonLayout.SlideButtonListener, AccelerometerHandler.AccelerometerEventListener{

    // TODO handle onpause and on resume for registering and unregistering the accelrometer handler.

    private static final String TAG = CustomControllerFragmentBackup.class.getSimpleName();

    public static final String CONTROLLER_ID  = "controller_id";

    */
/*Views*//*

    private RelativeLayout mainView, reFrames;
    private SlideButtonLayout slideButton;
    private List<DropZoneFrame> highlightedZones = new ArrayList<DropZoneFrame>();
    private Button btnEdit;

    private int columnAmount = 4;
    private int rowAmount = 4;
    private int columnSpace, rowSpace, cellSize ;

    private ArduinoCarAppObj app;
    private CustomController customController;

    private AddCustomButtonFragment addCustomButtonFragment;

    private boolean editing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj)getActivity().getApplication();

        if (savedInstanceState == null)
        {
            // if no controller found generate empty frames
            if (app.getCustomDBManager().getControllersDataSource().getAllControllers().size() == 0) {
                customController = new CustomController("Default", 3, 3);
                long id  = app.getCustomDBManager().getControllersDataSource().addController(customController);
                customController = app.getCustomDBManager().getControllerById(id);
            }
            else
                // inflating the first controller on the list.
                customController = app.getCustomDBManager().getControllerById(
                        app.getCustomDBManager().getControllersDataSource().getAllControllers().get(0).getId() );
        }
        else
        {
            // inflating the controller by id saved on the savedInstanceBundle. if no id saved open the first controller.
            long id = savedInstanceState.getLong(CONTROLLER_ID, -1);
            customController = app.getCustomDBManager().getControllerById( id != -1 ? id :
                    app.getCustomDBManager().getControllersDataSource().getAllControllers().get(0).getId() ) ;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CONTROLLER_ID, customController.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (RelativeLayout) inflater.inflate(R.layout.fragment_custom_controller, null);

        btnEdit = (Button) mainView.findViewById(R.id.btn_edit);

        reFrames = (RelativeLayout) mainView.findViewById(R.id.relative_frames);
        reFrames.setTag("");

        initFramesForController(customController);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initButtons();
    }

    */
/* Views initialization and manipulation. *//*

    */
/* Initialize Views and Views Logic*//*

    private void initButtons(){
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing)
                {
                    exitEditMode();
                }
                else
                {
                    setBottomPanelToAddButton();

                    enterEditMode();
                }

                editing = !editing;
            }
        });
    }

    private void initFramesLayoutData(){
        rowAmount = customController.getRows();
        columnAmount = customController.getColumns();

        Log.d(TAG, " Rows = " + rowAmount + ", Columns = " + columnAmount);
    }

    private void initFrames(){
        initFramesLayoutData();

        DropZoneFrame frame;
        RelativeLayout.LayoutParams frameParams;
        cellSize = rowAmount >= columnAmount ? (int)getScreenHeight() / (rowAmount + 1) : (int)getScreenWidth() / (columnAmount + 1)  ;
        columnSpace = (int) ( getScreenWidth() - (cellSize * columnAmount) ) / (columnAmount+1);
        rowSpace = (int) ( getScreenHeight() - (cellSize * rowAmount) ) / (rowAmount + 1);

        Log.d(TAG, "row space = " + rowSpace + " Column space: "  + columnSpace + " Cell Size: " + cellSize);

        if (reFrames.getChildCount() > 0)
            reFrames.removeAllViews();

        for (int i = 0; i < rowAmount  ; i++)
        {
            for (int j = 0 ; j < columnAmount; j++)
            {
                frame = new DropZoneFrame(getActivity());
                frame.setId(i * columnAmount + j);
                frame.setOnDragListener(new ButtonDropZoneListener());
                frame.setColNumber(j);
                frame.setRowNumber(i);

                frameParams = new RelativeLayout.LayoutParams(cellSize, cellSize);
                frameParams.leftMargin = (j + 1) * columnSpace+ (cellSize*j);
                frameParams.topMargin = (i + 1) * rowSpace + (cellSize*i); //  Calc of spacing from the top. Space * the row number + the amount of cells above size

//                Log.d(TAG, "frame id: " + frame.getId() + " Left Margin: " + frameParams.leftMargin + " Top Margin: " + frameParams.topMargin);

                frame.setLayoutParams(frameParams);

                reFrames.addView(frame);
            }
        }
    }

    private void initFramesForController(CustomController controller){

        customController = controller;

        Log.d(TAG, "initFramesForController, Name: " + controller.getName());

        initFrames();

        DropZoneFrame frame;

        for (CustomButton btn : customController.getButtons())
        {
//            Log.d(TAG, "Btn ID: " + btn.getId());

            frame = (DropZoneFrame) reFrames.getChildAt(btn.getPosition());

            RelativeLayout.LayoutParams frameParams;

            if (btn.getOrientation() == LinearLayout.HORIZONTAL)
            {
                frameParams = new RelativeLayout.LayoutParams(
                        (cellSize * btn.getSize()) + ( columnSpace * (btn.getSize() - 1) ), cellSize);

                // Hiding not relevant frames
                for (int i = btn.getPosition() + 1 ; i < (btn.getPosition() + btn.getSize()) ; i++)
                {
//                    Log.d(TAG," Hiding in pos = " + i);

                    reFrames.getChildAt(i).setVisibility(View.GONE);
                    ((DropZoneFrame)reFrames.getChildAt(i)).setParentId(frame.getId());
                    ((DropZoneFrame)reFrames.getChildAt(i)).setEmpty(false);
                }
            }
            else
            {
                frameParams = new RelativeLayout.LayoutParams(
                        cellSize, (cellSize * btn.getSize()) + ( rowSpace * (btn.getSize() - 1) ));

                // Hiding not relevant frames
                for (int i = btn.getPosition() + rowAmount ; i < rowAmount*columnAmount ; i+=rowAmount)
                {
//                    Log.d(TAG," Hiding in pos = " + i);

                    reFrames.getChildAt(i).setVisibility(View.GONE);
                    ((DropZoneFrame)reFrames.getChildAt(i)).setParentId(frame.getId());
                    ((DropZoneFrame)reFrames.getChildAt(i)).setEmpty(false);
                }
            }

            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin   ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin  ;
            frame.setLayoutParams(frameParams);

            // Setting the frame to full
            frame.setEmpty(false);

            frame.setTag(btn.getType());

            createButtonForFrame(btn.getId(), frame, btn.getType());
        }
    }

    private void initButtonCommand(View v){

        CustomCommand customCommand;

        if ( app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(v.getId()) != null)
        {
            customCommand = app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(v.getId());
        }
        else
            return;

        if (v instanceof SlideButtonLayout)
        {

        }
        else if (v instanceof SimpleButton)
        {
            int buttonState = 0;

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:
                    buttonState = SimpleButton.STATE_OFF;
                    break;
            }

            ((SimpleButton) v).setState(buttonState);
        }
    }

    */
/* Creating a button inside a frame *//*

    private void createButtonForFrame(final long buttonId, DropZoneFrame frame,final int type){

//        Log.d(TAG, "creating button for frame in row = " + frame.getRowNumber() + ", column = " + frame.getColNumber());

        final SlideButtonLayout slideButtonLayout;
        final SimpleButton button;

        // Removing all old views that can cause some problems with interface.
        frame.removeAllViews();

        // Making sure no background will interferes.
        frame.setBackgroundResource(android.R.color.transparent);

        switch (type)
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                button = new SimpleButton(getActivity());

                // Assign image
                button.setBackgroundResource(R.drawable.stick_button);
                // Id
                button.setId((int)buttonId);
                // type
                button.setType(type);

                // Listeners
                button.setOnClickListener(this);
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        Log.d(TAG, "New button long click");
                        // Showing the set command popup
                        if (editing)
                            setBottomPanelToAddCommand(button, type);
                        return true;
                    }
                });

                // Adding to the frame.
                frame.addView(button);

                initButtonCommand(button);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:

                slideButtonLayout =
                        new SlideButtonLayout(getActivity(), LinearLayout.HORIZONTAL, cellSize, true, false);

                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins(0, ((RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin,0, 0);

                slideButtonLayout.setOnSlideButtonLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (editing)
                            setBottomPanelToAddCommand(slideButtonLayout, type);
                        return true;
                    }
                });

                slideButtonLayout.setId((int) buttonId);

                slideButtonLayout.setSlideButtonListener(this);

                frame.addView(slideButtonLayout);

                initButtonCommand(slideButtonLayout);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:

                slideButtonLayout =
                        new SlideButtonLayout(getActivity(), LinearLayout.VERTICAL, cellSize, true, false);

                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins( ((RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin, 0 ,0 , 0);

                slideButtonLayout.setOnSlideButtonLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (editing)
                            setBottomPanelToAddCommand(slideButtonLayout, type);

                        return true;
                    }
                });

                slideButtonLayout.setId((int) buttonId);

                slideButtonLayout.setSlideButtonListener(this);

                frame.addView(slideButtonLayout);

                initButtonCommand(slideButtonLayout);

                break;
        }

        reFrames.invalidate();
    }

    */
/* Change empty frames background so it would be visible or hidden*//*

    private void enterEditMode(){
        for (int i = 0 ; i < reFrames.getChildCount() ; i++)
        {
            final DropZoneFrame drop = (DropZoneFrame) reFrames.getChildAt(i);

            if(drop.isEmpty() && drop.getVisibility() == View.VISIBLE)
            {
                drop.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
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
    private void exitEditMode(){
        for (int i = 0 ; i < reFrames.getChildCount() ; i++)
        {
            final DropZoneFrame drop = (DropZoneFrame) reFrames.getChildAt(i);
            if(drop.isEmpty() && drop.getVisibility() == View.VISIBLE)
            {
                drop.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
                drop.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        drop.setNormalState();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        }
    }

    */
/** Creates a addCustomCommand fragment in the bottom panel and then show the panel.*//*

    private void setBottomPanelToAddCommand(View v, int type){
        AddCustomCommandFragment addCustomCommandFragment = new AddCustomCommandFragment();

        Bundle extras = new Bundle();
        extras.putLong(CustomButton.BUTTON_ID, v.getId());
        extras.putInt(CustomButton.BUTTON_TYPE, type);

        addCustomCommandFragment.setArguments(extras);

        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.container_slide_panel_container, addCustomCommandFragment).commit();

        openBottomPanel(MainActivity.BOTTOM_MENU_OFFSET);
    }

    */
/** Creates a addCustomButton fragment in the bottom panel and then show the panel.*//*

    private void setBottomPanelToAddButton(){
        addCustomButtonFragment = new AddCustomButtonFragment();

        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.container_slide_panel_container, addCustomButtonFragment).commit();

        openBottomPanel(MainActivity.BOTTOM_MENU_OFFSET);
    }

    private void openBottomPanel(float offset){
        ((MainActivity)getActivity()).getSlidingUpPanelLayoutContainer().expandPane(offset);
    }

    private void closeBottomPanel(){
        ((MainActivity)getActivity()).getSlidingUpPanelLayoutContainer().collapsePane();
    }

    */
/* Fragment Mode && Listeners*//*

    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
    }

    @Override
    public void onConnecting() {
        super.onConnecting();
    }

    @Override
    public void onControllerSelected(long id) {
        super.onControllerSelected(id);
        customController = app.getCustomDBManager().getControllerById(id);
        initFramesForController(customController);
    }

    @Override
    public void onSlideMenuOpen() {
        super.onSlideMenuOpen();

        if (editing){
            reFrames.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
            reFrames.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                reFrames.setTag("Animating");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reFrames.setAlpha(0.0f);
                reFrames.setTag("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        }
    }

    @Override
    public void onSlideMenuClosed() {
        super.onSlideMenuClosed();

        Log.d(TAG, "onSlideMenuClosed, Framses Alpha: " + reFrames.getAlpha());

        if ( (reFrames.getAlpha() != 1.0f || reFrames.getTag().equals("Animating")) && editing)
        {
            reFrames.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
            reFrames.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    reFrames.setAlpha(1.0f);
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

    @Override
    public void onClick(View v) {
        if ( app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(v.getId()) != null)
        {
            CustomCommand customCommand = app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(v.getId() );
            int buttonState = ((SimpleButton) v).getState();

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    app.getConnection().write( String.valueOf(Command.TOGGLE_STATE) + customCommand.getChannel());

                    */
/*if (buttonState == SimpleButton.STATE_ON) {
                        app.getConnection().write(String.valueOf(Command.STOP) + customCommand.getChannel());
                        ((SimpleButton) v).setState(SimpleButton.STATE_OFF);
                    }
                    else if (buttonState == SimpleButton.STATE_OFF)
                    {
                        app.getConnection().write( String.valueOf(Command.GO) + customCommand.getChannel());
                        ((SimpleButton) v).setState(SimpleButton.STATE_ON);
                    }*//*


                    break;

                case CustomCommand.TYPE_DIRECTION_LEFT:
                    app.getConnection().write( String.valueOf(Command.DIRECTION_LEFT) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_DIRECTION_RIGHT:
                    app.getConnection().write( String.valueOf(Command.DIRECTION_RIGHT) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:
                    app.getConnection().write( String.valueOf(Command.TOGGLE_DIRECTION) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_SPEED_UP:
                    app.getConnection().write( String.valueOf(Command.SPEED_UP) + customCommand.getChannel() + "50");
                    break;

                case CustomCommand.TYPE_SPEED_DOWN:
                    app.getConnection().write( String.valueOf(Command.SPEED_DOWN) + customCommand.getChannel() + "50");
                    break;

                case CustomCommand.TYPE_ACC_CONTROL:

                    if (AccelerometerHandler.getInstance().isRegistered())
                        AccelerometerHandler.getInstance().unregister();
                    else {
                        AccelerometerHandler.getInstance().register();
                        AccelerometerHandler.getInstance().setAccelerometerEventListener(CustomControllerFragmentBackup.this);
                        AccelerometerHandler.getInstance().setAssociatedChannel(customCommand.getChannel());
                    }

                    break;
            }

            Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press long for setting command", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSlideStop(SlideButtonLayout slideButtonLayout, int pos) {
        if ( app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() ) != null)
        {
            CustomCommand customCommand = app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() );

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                    app.getConnection().write(String.valueOf(Command.STOP) + customCommand.getChannel());
                    break;
            }


            Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press long for setting command", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideStarted(SlideButtonLayout slideButtonLayout) {

    }

    @Override
    public void onSliding(SlideButtonLayout slideButtonLayout, String direction, int speed) {
        if ( app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() ) != null)
        {
            Log.d(TAG, "onSliding, Direction: " + direction + " Speed: " + speed);

            CustomCommand customCommand = app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() );

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                    app.getConnection().write( customCommand.getChannel() + String.valueOf(direction) + String.valueOf(speed));
                    break;
            }


            Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press long for setting command", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int position) {
        if ( app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() ) != null)
        {
            Log.d(TAG, "onMarkedPositionPressed, Direction: " + direction + " Position: " + position + " PosNumber: " + PosNumber);

            CustomCommand customCommand = app.getCustomDBManager().getCustomCommandsDataSource().getCommandByButtonId(slideButtonLayout.getId() );

            Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press long for setting command", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onForward(int speed) {

    }

    @Override
    public void onBackwards(int speed) {

    }

    @Override
    public void onRight(int amount) {
        Log.d(TAG, "onRight");
        app.getConnection().write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(0) + String.valueOf(amount));
    }

    @Override
    public void onLeft(int amount) {
        Log.d(TAG, "onLeft");
        app.getConnection().write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(1) + String.valueOf(amount));
    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onStraightAhead() {
        Log.d(TAG, "onStraightAhead");
        app.getConnection().write( String.valueOf(Command.STOP) + AccelerometerHandler.getInstance().getAssociatedChannel());
    }

    @Override
    public void onChangeDeltas(float[] deltas) {

    }

    */
/* Drag Listener*//*

    class ButtonDropZoneListener implements View.OnDragListener {
//        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
//        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        private DropZoneFrame frame;
        private int posInGrid;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            if (addCustomButtonFragment != null && addCustomButtonFragment.getDropZoneImage() != null)
            {
                frame = ((DropZoneFrame)v);
                posInGrid = addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? frame.getColNumber() : frame.getRowNumber();// Get the position of the view in the row

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        highlightedZones = new ArrayList<DropZoneFrame>();

//                    Log.d(TAG, "Dropped view size is: " + dropZoneImage.getSize() + " Pos in row is: " + posInGrid);

                        // If the frame is empty
                        if (frame.isEmpty())
                        {
                            // If the size is more then one calculate if the position is available for drop.
                            if (addCustomButtonFragment.getDropZoneImage().getSize() > 1)
                            {
                                calcMovementOptions();
                            }
                            // size is one show available for drop
                            else
                            {
                                frame.setToAvailableForDrop();
                                highlightedZones.add(frame);
                            }
                        }
                        // The frame is occupied show un available and hide the existing content for now
                        else
                        {
                            frame.getChildAt(0).setAlpha(0.0f);
                            frame.setNotAvailableForDrop();
                            highlightedZones.add(frame);
                        }
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        // Exit an empty frame set back do edit mode
                        if (frame.isEmpty())
                        {
                            for (DropZoneFrame d : highlightedZones)
                                d.setToEditMode();
                        }
                        // Frame is full set back to normal state(transparent) and set the view alpha back to one so it would be visible
                        else
                        {
                            frame.setNormalState();
                            frame.getChildAt(0).setAlpha(1.0f);
                        }
                        break;

                    case DragEvent.ACTION_DROP:

                        // If the spot is available for drop.
                        if (addCustomButtonFragment != null && addCustomButtonFragment.getDropZoneImage() != null && frame.canDrop())
                        {
                            // Setting the image from the dragged view to the container picked.
                            dropButtonToFrame();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Can't drop here." , Toast.LENGTH_LONG ).show();
                            for (DropZoneFrame d : highlightedZones)
                                if(!d.isEmpty()) {
                                    d.setNormalState();
                                    frame.getChildAt(0).setAlpha(1.0f);
                                }
                                else
                                {
                                    d.setToEditMode();
                                }
                        }

                        break;

                    case DragEvent.ACTION_DRAG_ENDED:

//                    dropZoneImage.setOnDrag(false);
//                    exitEditMode();
                        setBottomPanelToAddButton();

                        break;

                    default:
                        break;
                }
            }
            else
                Log.e(TAG, "dropZoneImage is null");

            return true;
        }

        private void dropButtonToFrame(){
            RelativeLayout.LayoutParams frameParams;

            if (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL)
            {
                // Getting the most left frame.
                frame = getMostLeftFrame();

                frameParams = new RelativeLayout.LayoutParams(
                        (cellSize * addCustomButtonFragment.getDropZoneImage().getSize()) + ( columnSpace * (addCustomButtonFragment.getDropZoneImage().getSize() - 1) ), cellSize);
            }
            else
            {
                // Getting the to[ frame.
                frame = getTopFrame();

                frameParams = new RelativeLayout.LayoutParams(
                        cellSize, (cellSize * addCustomButtonFragment.getDropZoneImage().getSize()) + ( rowSpace * (addCustomButtonFragment.getDropZoneImage().getSize() - 1) ));
            }

            // Adding the margin to the SlideLayout, The final margin is the actual frame margin + column/row number + cellSize r.g row 3 * 144
            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin;
            frame.setLayoutParams(frameParams);

            // Setting the frame to full
            frame.setEmpty(false);

            frame.setTag(addCustomButtonFragment.getDropZoneImage().getType());

            // Adding the button to the database
            CustomButton customButton = new CustomButton(customController.getId(), addCustomButtonFragment.getDropZoneImage().getType(), addCustomButtonFragment.getDropZoneImage().getSize(), addCustomButtonFragment.getDropZoneImage().getOrientation(), frame.getId());
            long id = app.getCustomDBManager().getCustomButtonsDataSource().addButton(customButton);

            createButtonForFrame(id, frame, addCustomButtonFragment.getDropZoneImage().getType());

            // Hiding the not relevant Zones
            for (DropZoneFrame d : highlightedZones)
            {
                if (!d.equals(frame))
                {
                    d.setParentId(frame.getId());
                    d.setVisibility(View.GONE);
                    d.setEmpty(false);
                }
            }
        }

        private void calcMovementOptions(){
            boolean canGoLeft = true;
            boolean canGoRight = true;
            int leftSteps = 0, rightSteps = 0;
            int count = addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount; // counting the neighbor view to check

            do
            {
                canGoLeft = canGoLeft && // Making sure the last check was ok to
                        posInGrid - ( count / (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) >= 0 && // Check that we are still on the same row
                        reFrames.getChildAt(frame.getId() - count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) reFrames.getChildAt(frame.getId() - count)).isEmpty(); // Check that the view is empty;

                canGoRight = canGoRight && // Making sure the last check was ok to
                        posInGrid + ( count / (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) )  < columnAmount && // Check that we are still on the same row
                        reFrames.getChildAt(frame.getId() + count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) reFrames.getChildAt(frame.getId() + count)).isEmpty();

                if (canGoLeft)
                {
                    leftSteps++;
                }

                if (canGoRight)
                {
                    rightSteps++;
                }

                  */
/*              Log.d(TAG, "Checked Id's = " + (frame.getId() - count) + ", " + (frame.getId() + count));

                                Log.d(TAG, "Checked Position's = " + (posInGrid - ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) )
                                        + ", " + (posInGrid + ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) ) );

                                Log.d(TAG, "Count: " + count
                                        + (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoLeft: " : "Can go Down") + String.valueOf(canGoLeft) + " Steps: " + leftSteps
                                        +  (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoRight: " : " Can Go Up ") + String.valueOf(canGoRight) + " Steps: " + rightSteps);*//*

                count += addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount;

            } while ( (canGoLeft || canGoRight) && count < addCustomButtonFragment.getDropZoneImage().getSize() * (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) );

            processMovementOptionFindings(canGoLeft, canGoRight, leftSteps, rightSteps);
        }

        private void processMovementOptionFindings(boolean canGoLeft, boolean canGoRight, int leftSteps, int rightSteps){
            int count;

            // If there is a place from left to right of the view
            if (rightSteps + leftSteps + 1 == addCustomButtonFragment.getDropZoneImage().getSize())
            {
//                                Log.d(TAG, "There is a place with right and left combine");

                for (int i = frame.getId() - (leftSteps * (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount)) ;
                     i <= frame.getId() + (rightSteps * (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) ;
                     i += addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount)
                {
//                                    Log.d(TAG, "I: " + i);
                    ((DropZoneFrame) reFrames.getChildAt(i)).setToAvailableForDrop();
                    highlightedZones.add(((DropZoneFrame) reFrames.getChildAt(i)));
                }
            }
      */
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

                            }*//*

            else if (canGoLeft || canGoRight)
            {
                count = 0;

//                                Log.d(TAG, "Going To The " + ( canGoLeft ? "Left" : "Right" ));

                while (count < addCustomButtonFragment.getDropZoneImage().getSize() * (addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) )
                {
                    if (canGoLeft)
                    {
                        ((DropZoneFrame) reFrames.getChildAt(frame.getId() - count)).setToAvailableForDrop();
                        highlightedZones.add(((DropZoneFrame) reFrames.getChildAt(frame.getId() - count)));
                    }
                    else
                    {
                        ((DropZoneFrame) reFrames.getChildAt(frame.getId() + count)).setToAvailableForDrop();
                        highlightedZones.add(((DropZoneFrame) reFrames.getChildAt(frame.getId() + count)));
                    }

                    count += addCustomButtonFragment.getDropZoneImage().getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount;
                }
            }
            else
            {
                frame.setNotAvailableForDrop();
                highlightedZones.add(frame);
            }
        }

        private DropZoneFrame getMostLeftFrame(){

//            Log.d(TAG, "highlightedZones size: " + highlightedZones.size());

            DropZoneFrame frame = highlightedZones.get(0);

            for (DropZoneFrame d : highlightedZones)
            {
//                Log.d(TAG, "Pos in row: " + d.getColNumber());
                if (d.getColNumber() < frame.getColNumber())
                {
                    frame = d;
                }
            }

//            Log.d(TAG, "MostLeft: " + frame.getColNumber());

            return frame;
        }

        private DropZoneFrame getTopFrame(){

//            Log.d(TAG, "highlightedZones size: " + highlightedZones.size());

            DropZoneFrame frame = highlightedZones.get(0);

            for (DropZoneFrame d : highlightedZones)
            {
//                Log.d(TAG, "Pos in row: " + d.getColNumber());
                if (d.getRowNumber() < frame.getRowNumber())
                {
                    frame = d;
                }
            }

//            Log.d(TAG, "MostLeft: " + frame.getColNumber());

            return frame;
        }
    }
}


*/
