package com.barunster.arduinocar.views;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.ButtonGridSelectionAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CommandsExecutor;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.custom_controllers_obj.ImageDragShadowBuilder;
import com.barunster.arduinocar.interfaces.BottomMenuGateKeeper;
import com.barunster.arduinocar.interfaces.BottomMenuInterface;
import com.barunster.arduinocar.interfaces.ControllerInterface;
import com.barunster.arduinocar.interfaces.ControllerLayoutEventListener;

import braunster.btconnection.BTConnection;

/**
 * Created by itzik on 4/13/2014.
 */
public class ControllerLayout extends SlidingUpPanelLayout implements BottomMenuInterface, ControllerInterface,View.OnClickListener, View.OnLongClickListener {

    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;

    public static final int FROM = 0;
    public static final int TO = 1;

    public static final int ROW = 0;
    public static final int COLUMN = 1;

    public static final int X = 0;
    public static final int Y = 1;

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    public static final int MIN_BRICK_SIZE = 100;

    private static final String TAG = ControllerLayout.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_LISTENERS = true;

    public static final float BOTTOM_MENU_OFFSET_FULL = 0.5f;

    private View slidePanelMain;// The main view for the sliding up panel.
    private int paneHeight, brickSize;

    /* Controller */
    CustomController customController;

    /* Menu Buttons*/
    private Button addBtn, editBtn, closeBtn;

    /* Interfaces*/
    private ControllerLayoutEventListener controllerLayoutEventListener;

    /* BTConnection - The output connection for the controller*/
    private BTConnection connection;

    /* Handle all click, slide accelerometer events.*/
    private CommandsExecutor commandsExecutor;

    /* Flags*/
    private boolean isEditing = false;

    public ControllerLayout(Context context, int paneHeight) {
        super(context);
        this.paneHeight = paneHeight;

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(){
        calcBrickSize();

        commandsExecutor = new CommandsExecutor(getContext(), connection);
    }

    public void initSlidingBottomMenu(View main){

        slidePanelMain = main;

        // Setting the params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM;
        this.setLayoutParams(params);

        this.setPanelHeight(paneHeight);
        this.setSlidingEnabled(false);
        this.setEnableDragViewTouchEvents(true);

        // Adding the views.
        this.addView(main);
        this.addView(initBottomMenu());

        /* Bottom Panel*/
        this.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

                if (slideOffset == BOTTOM_MENU_OFFSET_FULL) {
                    ControllerLayout.this.setSlidingEnabled(false);
                }
                // When closed full eliminating the listener and enabling the slide.
                else if (slideOffset == 1.0f) {

                }

//                    bottomMenuFragment.onPanelSlide(slidingUpPanelLayout, slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded, Container");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed, Container");
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored, Container.");

            }
        });
    }

    private View initBottomMenu(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setWeightSum(9);

        linearLayout.addView(initMenuButtons());

        /* Add Button - Buttons Grid*/
        View view = initButtonGridView();
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewParams.weight = 4;
        view.setLayoutParams(viewParams);

        linearLayout.addView(view);

        return linearLayout;
    }

    private LinearLayout initMenuButtons(){
        if (addBtn == null)
        {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearParams.weight = 8;
            linearLayout.setLayoutParams(linearParams);

            // Button Params.
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            btnParams.weight = 1;

        /* Add Button */
            addBtn = new Button(getContext());
            addBtn.setLayoutParams(btnParams);
            addBtn.setText("Add");
            addBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gateKeeper.openMenu();
                    isEditing = false;
                    editBtn.setSelected(false);
                }
            });

        /* Edit Button */
            editBtn = new Button(getContext());
            editBtn.setLayoutParams(btnParams);
            editBtn.setText("Edit");
            editBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gateKeeper.closeMenu();
                    isEditing = true;
                    v.setSelected(true);
                }
            });

        /* Edit Button */
            closeBtn = new Button(getContext());
            closeBtn.setLayoutParams(btnParams);
            closeBtn.setText("Close");
            closeBtn.setVisibility(INVISIBLE);
            closeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gateKeeper.closeMenu();
                }
            });

            linearLayout.addView(addBtn);
            linearLayout.addView(editBtn);
            linearLayout.addView(closeBtn);

            return linearLayout;
        }

        return null;
    }

    private View initButtonGridView() {
        View mainView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate(R.layout.fragment_add_custom_button, null);

        ButtonGridSelectionAdapter buttonGridSelectionAdapter = new ButtonGridSelectionAdapter(getContext());
        ((GridView) mainView.findViewById(R.id.grid_buttons)).setAdapter(buttonGridSelectionAdapter);

        ((GridView) mainView.findViewById(R.id.grid_buttons)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

//                Log.d(TAG, "GridItemSelected");
                ClipData data = ClipData.newPlainText("", "");

                DropZoneImage dropZoneImage = (DropZoneImage) v;

                // If the main view is "FrameLayout"  Get the brick size and start drag with brickShadow.
                if (slidePanelMain instanceof FrameLayout)
                {
                    if (DEBUG)
                        Log.d(TAG, "Main View is FrameLayout");

                    v.startDrag(data,  // the data to be dragged
                            ImageDragShadowBuilder.drawBricksShadow(getContext(), new int[]{ dropZoneImage.getDimensions()[COLUMN] * brickSize, // the drag shadow builder
                                    dropZoneImage.getDimensions()[ROW] * brickSize}),  // the drag shadow builder
                            v,      // no need to use local data
                            0          // flags (not currently used, set to 0)
                    );
                }

                gateKeeper.closeMenu();

                return false;
            }
        });

        return mainView;
    }

    private int calcBrickSize(){
        // TODO calc the brick size
        brickSize = MIN_BRICK_SIZE;
        return brickSize;
    }

    /* Interfaces */
    @Override
    public void openMenu() {
        closeBtn.setVisibility(VISIBLE);
        this.expandPane(BOTTOM_MENU_OFFSET_FULL);
    }

    @Override
    public void closeMenu() {
        closeBtn.setVisibility(INVISIBLE);
        this.collapsePane();
    }

    @Override
    public void enterEditMode() {

    }

    @Override
    public void exitEditMode() {

    }

    @Override
    public void setController(CustomController customController) {
        this.customController = customController;
        commandsExecutor.setControllerId(customController.getId());
    }

    @Override
    public void setOutputConnection(BTConnection connection) {
        this.connection = connection;
    }

    /* Dispatch Event*/

    void dispatchButtonAddedEvent(CustomButton customButton, View view){
        if (controllerLayoutEventListener != null)
            controllerLayoutEventListener.onButtonAdded(customButton, view);
        else if (DEBUG)
            Log.e(TAG, "no Controller Layout Event Listener");
    }

    /* Getters & Setters*/

    public int getBrickSize() {
        return brickSize;
    }

    public BottomMenuGateKeeper getGateKeeper() {
        return gateKeeper;
    }

    public BTConnection getConnection() {
        return connection;
    }

    public CommandsExecutor getCommandsExecutor() {
        return commandsExecutor;
    }

    public void setControllerLayoutEventListener(ControllerLayoutEventListener controllerLayoutEventListener) {
        this.controllerLayoutEventListener = controllerLayoutEventListener;
    }

    BottomMenuGateKeeper gateKeeper = new BottomMenuGateKeeper() {
        @Override
        public void openMenu() {
            ControllerLayout.this.openMenu();
        }

        @Override
        public void closeMenu() {
            ControllerLayout.this.closeMenu();
        }
    };

    @Override
    public void onClick(View v) {

        if (DEBUG_LISTENERS)
            Log.d(TAG, "OnClick");

        if (customController == null) {
            if(DEBUG_LISTENERS)
                Log.e(TAG, "No custom controller set");
            return;
        }

        if (isEditing)
        {
            // TODO show the button data.
            gateKeeper.openMenu();
        }
        else commandsExecutor.onClick(v);

    }

    @Override
    public boolean onLongClick(View v) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onLongClick");

        // TODO make long click move button in edit mode.

        return isEditing;
    }

 /*   @Override
    public boolean onSlideStop(SlideButtonLayout slideButtonLayout, int pos) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onSlideStop");

        else if ( customController.getCustomButtonById(slideButtonLayout.getId()) != null && customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null)
        {
            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                    // Send stop command only if the button want to be center after the slide stops.
                    if (slideButtonLayout.isCenterWhenSlideStop())
                        connection.write(String.valueOf(Command.STOP) + customCommand.getChannel());
                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }

        return !isEditing;
    }

    @Override
    public boolean onSlideStarted(SlideButtonLayout slideButtonLayout) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onSlideStarted");

        return !isEditing;
    }

    @Override
    public boolean onSliding(SlideButtonLayout slideButtonLayout, int direction, int speed) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onSliding");

        if ( customController.getCustomButtonById(slideButtonLayout.getId()) != null && customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null) {
            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

            if (DEBUG)
                Log.d(TAG, "onSliding, Direction: " + String.valueOf(direction) + " Speed: " + speed);

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));

            switch (customCommand.getType()) {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                    connection.write(customCommand.getChannel() + String.valueOf(direction) + String.valueOf(speed));
                    break;
            }
        }



        return !isEditing;
    }

    @Override
    public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int position) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onMarkedPositionPressed");

        if ( customController.getCustomButtonById(slideButtonLayout.getId()) != null && customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null) {
            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

            if (DEBUG)
                Log.d(TAG, "onMarkedPositionPressed, Direction: " + direction + " Position: " + position + " PosNumber: " + PosNumber);

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                   connection.write(customCommand.getChannel() + String.valueOf(direction) + String.valueOf(position));
                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getContext(), "Press long for setting command", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onForward(int speed) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onForward");
    }

    @Override
    public void onBackwards(int speed) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onBackwards");
    }

    @Override
    public void onRight(int amount) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onRight");
        connection.write(AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(0) + String.valueOf(amount));
    }

    @Override
    public void onLeft(int amount) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onLeft");

        connection.write(AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(1) + String.valueOf(amount));
    }

    @Override
    public void onStopped() {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onStopped");
    }

    @Override
    public void onStraightAhead() {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onStraightAhead");

        connection.write(String.valueOf(Command.STOP) + AccelerometerHandler.getInstance().getAssociatedChannel());
    }

    @Override
    public void onChangeDeltas(float[] deltas) {
        if (DEBUG_LISTENERS)
            Log.d(TAG, "onChangeDeltas");
    }*/
}
