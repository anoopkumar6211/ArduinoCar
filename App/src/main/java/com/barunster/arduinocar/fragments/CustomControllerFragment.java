package com.barunster.arduinocar.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.AccelerometerHandler;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.fragments.bottom_menu.BottomMenuFragment;
import com.barunster.arduinocar.views.BrickControllerLayout;
import com.barunster.arduinocar.views.FramesControllerLayout;
import com.barunster.arduinocar.views.SimpleButton;
import com.barunster.arduinocar.views.SlideButtonLayout;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import braunster.btconnection.Command;

/**
 * Created by itzik on 3/11/14.
 */
public class CustomControllerFragment extends ArduinoLegoFragment implements View.OnClickListener,FramesControllerLayout.ControllerLayoutListener, SlideButtonLayout.SlideButtonListener, AccelerometerHandler.AccelerometerEventListener{

    // TODO handle onpause and on resume for registering and unregistering the accelrometer handler.

    private static final String TAG = CustomControllerFragment.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final float BOTTOM_MENU_OFFSET_FULL = 0.5f;
    private static final int TOP_MENU_SIZE_DIVIDER = 10;

    /*Views*/
    private LinearLayout mainView;
    private FramesControllerLayout controllerLayout;
    private BrickControllerLayout brickControllerLayout;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    private BottomMenuFragment bottomMenuFragment;

    private ArduinoCarAppObj app;
    private CustomController customController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj)getActivity().getApplication();

        if (savedInstanceState == null)
        {
            // if no controller found generate empty frames
            if (app.getCustomDBManager().getAllControllers().size() == 0) {
                customController = new CustomController("Default", 3, 3);
                long id  = app.getCustomDBManager().addController(customController);
                customController = app.getCustomDBManager().getControllerById(id);
            }
            else
                // inflating the first controller on the list.
                customController = app.getCustomDBManager().getControllerById(
                        app.getCustomDBManager().getAllControllers().get(0).getId() );
        }
        else
        {
            Toast.makeText(getActivity(), "from savedInstanceBundle fragment", Toast.LENGTH_SHORT).show();
            // inflating the controller by id saved on the savedInstanceBundle. if no id saved open the first controller.
//            long id = savedInstanceState.getLong(CONTROLLER_ID, -1);
//            customController = app.getCustomDBManager().getControllerById( id != -1 ? id :
//                    app.getCustomDBManager().getControllersDataSource().getAllControllers().get(0).getId() ) ;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putLong(CONTROLLER_ID, customController.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (LinearLayout) inflater.inflate(R.layout.fragment_custom_controller, null);

//        initEditButton();
        initSlidingUpPanel();

        controllerLayout = (FramesControllerLayout) mainView.findViewById(R.id.controller_layout);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        controllerLayout.setOnClickListener(this);
        controllerLayout.setSlideButtonListener(this);
        controllerLayout.setControllerLayoutListener(this);

        controllerLayout.post(new Runnable() {
            @Override
            public void run() {
                controllerLayout.setCustomController(customController);
            }
        });
    }

    /* Views initialization and manipulation. */
    /* Initialize Views and Views Logic*/
    private void initSlidingUpPanel(){
        slidingUpPanelLayout = (SlidingUpPanelLayout) mainView.findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.setPanelHeight((int) (getScreenHeight()/TOP_MENU_SIZE_DIVIDER));
        slidingUpPanelLayout.setSlidingEnabled(false);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        /* Bottom Panel*/
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

                if (slideOffset == BOTTOM_MENU_OFFSET_FULL) {
                    slidingUpPanelLayout.setSlidingEnabled(false);
                }
                // When closed full eliminating the listener and enabling the slide.
                else if (slideOffset == 1.0f) {

                }

                bottomMenuFragment.onPanelSlide(slidingUpPanelLayout, slideOffset);
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

        createSlidePanelFragment();
    }

    private void createSlidePanelFragment(){
        // Bottom Menu
        bottomMenuFragment = new BottomMenuFragment();

        // Used later for refreshing to connection fragment when menu is open.
        bottomMenuFragment.setUserVisibleHint(false);

        getFragmentManager().beginTransaction().replace(R.id.container_slide_panel_container, bottomMenuFragment)
                .commit();
    }

    public void openBottomMenu(){
        slidingUpPanelLayout.expandPane(BOTTOM_MENU_OFFSET_FULL);
    }

    public void closeBottomMenu(){
        slidingUpPanelLayout.collapsePane();
    }

    public FramesControllerLayout getControllerLayout() {
        return controllerLayout;
    }

    /* Fragment Mode && Listeners*/
    /* Buttons listeners*/
    @Override
    public void onClick(View v) {

        if (DEBUG)
            Log.d(TAG, "OnClick");

        if (controllerLayout.isEditing())
        {
            bottomMenuFragment.getEditButtonFragment().refresh(((SimpleButton)v).getType(), v.getId());
            slidingUpPanelLayout.expandPane();
        }
        else if ( customController.getCustomButtonById(v.getId()).getCustomCommand() != null)
        {
            CustomCommand customCommand = customController.getCustomButtonById(v.getId()).getCustomCommand();

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:
                    app.getConnection().write( String.valueOf(Command.TOGGLE_STATE) + customCommand.getChannel());
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
                    app.getConnection().write( String.valueOf(Command.SPEED_UP) + customCommand.getChannel() + customCommand.getExtraSpeedData());
                    break;

                case CustomCommand.TYPE_SPEED_DOWN:
                    app.getConnection().write( String.valueOf(Command.SPEED_DOWN) + customCommand.getChannel() + customCommand.getExtraSpeedData());
                    break;

                case CustomCommand.TYPE_ACC_CONTROL:

                    if (AccelerometerHandler.getInstance().isRegistered())
                        AccelerometerHandler.getInstance().unregister();
                    else {
                        AccelerometerHandler.getInstance().register();
                        AccelerometerHandler.getInstance().setAccelerometerEventListener(CustomControllerFragment.this);
                        AccelerometerHandler.getInstance().setAssociatedChannel(customCommand.getChannel());
                    }

                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press on the EditButton button and then select a button to edit.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onSlideStop(SlideButtonLayout slideButtonLayout, int pos) {
        if ( customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null)
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
                        app.getConnection().write(String.valueOf(Command.STOP) + customCommand.getChannel());
                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        return !controllerLayout.isEditing();
    }

    @Override
    public boolean onSlideStarted(SlideButtonLayout slideButtonLayout) {
        if (controllerLayout.isEditing())
        {
//            bottomMenuFragment.getEditButtonFragment().refresh(slideButtonLayout.getType(), slideButtonLayout.getId());
//            slidingUpPanelLayout.expandPane();
        }

        return !controllerLayout.isEditing();
    }

    @Override
    public boolean onSliding(SlideButtonLayout slideButtonLayout, int direction, int speed) {
        if ( customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null)
        {
            if (DEBUG)
                Log.d(TAG, "onSliding, Direction: " + String.valueOf(direction) + " Speed: " + speed);

            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

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

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        return !controllerLayout.isEditing();
    }

    @Override
    public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int position) {
        if (customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null)
        {
            if (DEBUG)
                Log.d(TAG, "onMarkedPositionPressed, Direction: " + direction + " Position: " + position + " PosNumber: " + PosNumber);

            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:

                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:

                    break;

                case CustomCommand.TYPE_SPEED_CONTROL:
                    app.getConnection().write( customCommand.getChannel() + String.valueOf(direction) + String.valueOf(position));
                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getActivity(), "Press long for setting command", Toast.LENGTH_SHORT).show();
    }

    /* Accelerometer Listener - for now only left, right and on onStraightAhead are in used.*/
    @Override
    public void onForward(int speed) {

    }

    @Override
    public void onBackwards(int speed) {

    }

    @Override
    public void onRight(int amount) {
        if (DEBUG)
            Log.d(TAG, "onRight");
        app.getConnection().write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(0) + String.valueOf(amount));
    }

    @Override
    public void onLeft(int amount) {
        if (DEBUG)
            Log.d(TAG, "onLeft");
        app.getConnection().write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(1) + String.valueOf(amount));
    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onStraightAhead() {
        if (DEBUG)
            Log.d(TAG, "onStraightAhead");
        app.getConnection().write( String.valueOf(Command.STOP) + AccelerometerHandler.getInstance().getAssociatedChannel());
    }

    @Override
    public void onChangeDeltas(float[] deltas) {

    }

    /* Controller Layout Listener*/
    @Override
    public void onButtonAdded(CustomButton customButton, View view) {
        long id = app.getCustomDBManager().addButton(customButton);
        view.setId((int) id);

        if (DEBUG)
            if (id == -1)
                Log.e(TAG, "Button had not been added to the DB");
    }

    @Override
    public void onDragEnded() {
        openBottomMenu();
    }

    @Override
    public void onButtonRemoved(long buttonId) {
        boolean isDeleted = app.getCustomDBManager().deleteButtonById(buttonId);

        if (DEBUG)
            Log.d(TAG, isDeleted ? " Button is deleted." : "cant delete button");
    }

    @Override
    public void onButtonChanged(CustomButton customButton, View view) {
        int affectedRows = app.getCustomDBManager().updateButtonById(customButton);

        if (DEBUG)
            Log.d(TAG, "onButtonChanged, Affected Rows = " + affectedRows);
    }

    /* Activity Listener - events that are captured in the main activity and passed to this fragment if needed.*/
    @Override
    public void onControllerSelected(long id) {
        super.onControllerSelected(id);
        if (app == null)
        {
            Log.e(TAG, "App Obj is null");
            if (getActivity() != null)
            {
                app = (ArduinoCarAppObj) getActivity().getApplication();
            }
            else
            {
                Log.e(TAG, "getActivity is null");
                return;
            }

        }

        customController = app.getCustomDBManager().getControllerById(id);
        controllerLayout.setOnClickListener(this);
        controllerLayout.setCustomController(customController);
    }

    @Override
    public void onFullScreen() {
        super.onFullScreen();

        fadeOutBottomMenu();

        fadeOutControllerLayout();

        // Exiting edit mode when on full screen.
        controllerLayout.exitEditMode();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelHeight(0);
                slidingUpPanelLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        slidingUpPanelLayout.collapsePane();
                    }
                });
            }
        }, 700);
    }

    @Override
    public void onExitFullScreen() {
        super.onExitFullScreen();

        fadeOutControllerLayout();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelHeight((int) (getScreenHeight() / TOP_MENU_SIZE_DIVIDER));
                slidingUpPanelLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        fadeInBottomMenu();
                    }
                });
            }
        }, 700);
    }

    /* Worker method for preparing to full screen mode and exit this mode.*/
    private void fadeOutControllerLayout(){
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                controllerLayout.setAlpha(0f);
                controllerLayout.startAnimation(fadeIn);
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        controllerLayout.setAlpha(1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        controllerLayout.startAnimation(fadeOut);

    }

    private void fadeInBottomMenu(){
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeIn.setDuration(700);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mainView.findViewById(R.id.container_slide_panel_container).setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainView.findViewById(R.id.container_slide_panel_container).startAnimation(fadeIn);
    }

    private void fadeOutBottomMenu(){
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        fadeOut.setDuration(700);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainView.findViewById(R.id.container_slide_panel_container).setAlpha(0);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainView.findViewById(R.id.container_slide_panel_container).startAnimation(fadeOut);
    }

    /* Getter & Setters*/
    public void setEditButtonMode(boolean editButtonMode) {

        if (DEBUG)
            Log.d(TAG, "set edit mode to: " + String.valueOf(editButtonMode));

        if (editButtonMode)
        {
            this.controllerLayout.enterEditMode();
        }
        else { this.controllerLayout.exitEditMode(); }
    }

    public long getControllerId(){
        return (customController != null ? customController.getId() : -1);
    }

}


