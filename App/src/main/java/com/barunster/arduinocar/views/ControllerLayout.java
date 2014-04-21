package com.barunster.arduinocar.views;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.ButtonGridSelectionAdapter;
import com.barunster.arduinocar.adapters.RadioGroupGridAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CommandsExecutor;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.custom_controllers_obj.ImageDragShadowBuilder;
import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.interfaces.ControllerInterface;
import com.barunster.arduinocar.interfaces.ControllerLayoutEventListener;
import com.barunster.arduinocar.interfaces.MenuGateKeeper;
import com.barunster.arduinocar.views.edit_command.ChannelsView;
import com.barunster.arduinocar.views.edit_command.CommandsSelectionGrid;

import braunster.btconnection.BTConnection;

/**
 * Created by itzik on 4/13/2014.
 */
public class ControllerLayout extends SlidingUpPanelLayout implements
        ControllerInterface,View.OnClickListener, View.OnLongClickListener, BrickBackGroundView.DropListener {

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
    private LinearLayout liBottomMenu, liButtonGrid, liButtonCommand;
    DropZoneFrame deleteFrame;
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
                if (DEBUG_LISTENERS)
                    Log.i(TAG, "onPanelExpanded, Container");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                if (DEBUG_LISTENERS)
                    Log.i(TAG, "onPanelCollapsed, Container");
            }

            @Override
            public void onPanelAnchored(View panel) {
                if (DEBUG_LISTENERS)
                    Log.i(TAG, "onPanelAnchored, Container.");

            }
        });
    }

    private View initBottomMenu(){
        liBottomMenu = new LinearLayout(getContext());
        liBottomMenu.setOrientation(LinearLayout.VERTICAL);
        liBottomMenu.setLayoutParams(new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        liBottomMenu.setWeightSum(9);

        liBottomMenu.addView(initMenuButtons());

        /* Add Button - Buttons Grid*/
        liButtonGrid = (LinearLayout) initButtonGridView();
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewParams.weight = 0.5f;
        liButtonGrid.setLayoutParams(viewParams);

        /* Add Button - Buttons Grid*/
        liButtonCommand = (LinearLayout) initButtonCommandView();
        LinearLayout.LayoutParams commandParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewParams.weight = 0.5f;
        liButtonCommand.setLayoutParams(commandParams);

        liBottomMenu.addView(liButtonGrid);

        return liBottomMenu;
    }

    private LinearLayout initMenuButtons(){
        if (addBtn == null)
        {
            int BTN_PADDING = 10;
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearParams.weight = 8;
            linearLayout.setLayoutParams(linearParams);
//            linearLayout.setPadding(BTN_PADDING, BTN_PADDING,BTN_PADDING,BTN_PADDING);

            // Button Params.
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(paneHeight, paneHeight);

            /* Add Button */
            addBtn = new Button(getContext());
            addBtn.setLayoutParams(btnParams);
//            addBtn.setPadding(BTN_PADDING, BTN_PADDING, BTN_PADDING, BTN_PADDING);
            addBtn.setBackgroundResource(R.drawable.add_button_selector);
            addBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gateKeeper.openMenu(BOTTOM_MENU_OFFSET_FULL);
                    exitEditMode();
                    editBtn.setSelected(false);
                    openButtonGridView();
                }
            });

            /* Edit Button */
            editBtn = new Button(getContext());
            editBtn.setLayoutParams(btnParams);
//            editBtn.setPadding(BTN_PADDING, BTN_PADDING, BTN_PADDING, BTN_PADDING);
            editBtn.setBackgroundResource(R.drawable.edit_button_selector);
            editBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());

                    if (v.isSelected())
                        enterEditMode();
                    else
                        exitEditMode();

                    gateKeeper.closeMenu();
                }
            });

             /* Close Button */
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(paneHeight, paneHeight);
            cancelParams.gravity = Gravity.CENTER;
            closeBtn = new Button(getContext());
            closeBtn.setLayoutParams(cancelParams);
            closeBtn.setVisibility(INVISIBLE);
//            closeBtn.setPadding(BTN_PADDING,BTN_PADDING,BTN_PADDING,BTN_PADDING);
            closeBtn.setBackgroundResource(R.drawable.close_button_selector);
            closeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gateKeeper.closeMenu();
                }
            });

            /* Delete Frame */
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams( paneHeight * 5, paneHeight);
            deleteFrame = new DropZoneFrame(getContext());
            deleteFrame.setVisibility(INVISIBLE);
            deleteFrame.setOnDragListener(new DeleteFrameDragListener());
            deleteFrame.setLayoutParams(deleteParams);

            linearLayout.addView(addBtn);
            linearLayout.addView(editBtn);
            linearLayout.addView(closeBtn);
            linearLayout.addView(deleteFrame);

//            linearLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    addBtn.setHeight();
//                }
//            })
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

                createDropShadowForImage(v, (DropZoneImage) v);

                gateKeeper.closeMenu();

                return false;
            }
        });

        return mainView;
    }

    private View initButtonCommandView(){
        return  ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate(R.layout.layout_edit_button, null);
    }

    private void openButtonCommandView(final CustomButton customButton,final View v){

        if (customButton == null)
        {
            throw new NullPointerException("can open command view for button, button is null");
        }

        // Removing the Button Grid View if needed.
        if (liBottomMenu.getChildAt(1).equals(liButtonGrid))
        {
            liBottomMenu.removeView(liButtonGrid);
            liBottomMenu.addView(liButtonCommand);
        }

        // If the button already has command show the command channel
        if (customButton.getCustomCommand() != null)
        {
            if (DEBUG)
                Log.d(TAG, "openButtonCommandView, Button has command.");
            ((ChannelsView)liButtonCommand.findViewById(R.id.linear_channels)).setSelectedChannel(customButton.getCustomCommand().getChannel());
        }

        /* Select the proper command list by the button type.*/
        switch (customButton.getType())
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                liButtonCommand.findViewById(R.id.linear_slide_data).setVisibility(View.GONE);
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:
                liButtonCommand.findViewById(R.id.linear_slide_data).setVisibility(View.VISIBLE);

                ((CheckBox)liButtonCommand.findViewById(R.id.check_show_marks)).setChecked(customButton.showMarks());
                ((CheckBox)liButtonCommand.findViewById(R.id.check_auto_center)).setChecked(customButton.centerAfterDrop());
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:
                liButtonCommand.findViewById(R.id.linear_slide_data).setVisibility(View.VISIBLE);
                ((CheckBox)liButtonCommand.findViewById(R.id.check_show_marks)).setChecked(customButton.showMarks());
                ((CheckBox)liButtonCommand.findViewById(R.id.check_auto_center)).setChecked(customButton.centerAfterDrop());
                break;
        }

        ((CommandsSelectionGrid) liButtonCommand.findViewById(R.id.grid_radio_group)).initForButton(customButton);

        // Listen to command selection.
        ((CommandsSelectionGrid) liButtonCommand.findViewById(R.id.grid_radio_group)).setRadioCheckChangedListener(new RadioGroupGridAdapter.RadioCheckedListener() {
            @Override
            public void onRadioChecked(int id) {
                // If command is speed up/down show the extra speed data editText.
                if (id == CustomCommand.TYPE_SPEED_DOWN || id == CustomCommand.TYPE_SPEED_UP)
                {
                    liButtonCommand.findViewById(R.id.et_command_extra).setVisibility(View.VISIBLE);
                    ((EditText) liButtonCommand.findViewById(R.id.et_command_extra)).setHint("Enter the amount to speed up/down");
                }
                else
                {
                    liButtonCommand.findViewById(R.id.et_command_extra).setVisibility(View.GONE);
                }
            }
        });

        // Submit Button
        liButtonCommand.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there's a command selected in the grid.
                if (((CommandsSelectionGrid) liButtonCommand.findViewById(R.id.grid_radio_group)).getRadioGroupGridAdapter().getSelectedButton() != null)
                {
                    int commandType = ((CommandsSelectionGrid) liButtonCommand.findViewById(R.id.grid_radio_group)).getRadioGroupGridAdapter().getSelectedButton().getId();
                    CustomCommand customCommand = new CustomCommand(customButton.getId(),
                            commandType,
                            ((ChannelsView)liButtonCommand.findViewById(R.id.linear_channels)).getSelectedChannel());

                    // Adding the speed data if needed.
                    if (liButtonCommand.findViewById(R.id.et_command_extra).getVisibility() == View.VISIBLE) {
                        if (((EditText) liButtonCommand.findViewById(R.id.et_command_extra)).getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Please enter extra data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                            customCommand.setExtraSpeedData(Integer.parseInt(((EditText) liButtonCommand.findViewById(R.id.et_command_extra)).getText().toString()));
                    }

                    // Add the command to the button.
                    customController.getCustomButtonById(customButton.getId()).setCustomCommand(customCommand);

                    // Add the command to the database.
                    CustomDBManager.getInstance().addCommand(customCommand);

                    // Adding new data to the button if needed. else refresh the button so new command type will be applied.
                    if (liButtonCommand.findViewById(R.id.linear_slide_data).getVisibility() == View.VISIBLE)
                    {
                        // Change the custom button settings.
                        customButton.setShowMarks( ((CheckBox)liButtonCommand.findViewById(R.id.check_show_marks)).isChecked() );
                        customButton.setCenterAfterDrop( ((CheckBox) liButtonCommand.findViewById(R.id.check_auto_center)).isChecked());
                        CustomDBManager.getInstance().updateButtonById(customButton);

                        customController.removeButtonById(customButton.getId());
                        customController.getButtons().add(customButton);

                        // Recreate the controller TODO refresh only relevant button and not the whole layout.
                        setController(customController);
                    }
                    else
                    {
                        // Changing the button command type. (The Button Drawable Resource will be changed.)
                        ((SimpleButton) v).setCommandType(commandType);
                    }

                    // Closing the bottom menu and exiting edit mode.
                    gateKeeper.closeMenu();
                }
                else
                    Toast.makeText(getContext(), "Please select a command", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openButtonGridView(){

        // Removing the Button Grid View if needed.
        if (liBottomMenu.getChildAt(1).equals(liButtonCommand))
        {
            liBottomMenu.removeView(liButtonCommand);
            liBottomMenu.addView(liButtonGrid);
        }
    }

    private int calcBrickSize(){
        // TODO calc the brick size
        brickSize = MIN_BRICK_SIZE;
        return brickSize;
    }

    @Override
    public void onButtonAdded(CustomButton customButton) {
        if (DEBUG)
            Log.d(TAG, "onButtonAdded");
    }

    @Override
    public void onButtonDeleted(CustomButton customButton) {
        if (DEBUG)
            Log.d(TAG, "onButtonDeleted");
    }

    @Override
    public void onButtonChangedPosition(CustomButton customButton) {
        if (DEBUG)
            Log.d(TAG, "onButtonChangedPosition");
    }

    /* Delete Frame Drag Class*/
    class DeleteFrameDragListener implements OnDragListener{
        private DropZoneImage dropZoneImage;


        @Override
        public boolean onDrag(View v, DragEvent event) {
            dropZoneImage = (DropZoneImage) event.getLocalState();

            switch (event.getAction())
            {
                case DragEvent.ACTION_DRAG_ENTERED:
                    deleteFrame.setNotAvailableMode();
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    deleteFrame.setNormalMode();
                    break;

                case DragEvent.ACTION_DROP:
                    deleteFrame.setNormalMode();
                    ControllerLayout.this.onButtonDeleted(dropZoneImage.getTaggedButton());

                    // So no other method will handle this situation.
                    dropZoneImage.setTaggedButton(null);

                    Toast.makeText(getContext(), "Button is deleted.", Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    }

    /* Interfaces */
    private void openMenu(float offset) {
        closeBtn.setVisibility(VISIBLE);
        this.expandPane(offset);
    }

    private void closeMenu() {
        closeBtn.setVisibility(INVISIBLE);
        this.collapsePane();
    }

    @Override
    public void enterEditMode() {
        isEditing = true;
    }

    @Override
    public void exitEditMode() {
        isEditing = false;
    }

    @Override
    public void setController(CustomController customController) {
        this.customController = customController;
        commandsExecutor.setControllerId(customController.getId());

    }

    @Override
    public void setOutputConnection(BTConnection connection) {
        this.connection = connection;
        commandsExecutor.setConnection(connection);
    }

    /* Dispatch Event*/

    void dispatchButtonAddedEvent(CustomButton customButton, View view){
        if (controllerLayoutEventListener != null)
            controllerLayoutEventListener.onButtonAdded(customButton, view);
        else if (DEBUG)
            Log.e(TAG, "no Controller Layout Event Listener");
    }

    void dispatchButtonChangedEvent(CustomButton customButton, View view){
        if (controllerLayoutEventListener != null)
            controllerLayoutEventListener.onButtonChanged(customButton, view);
        else if (DEBUG)
            Log.e(TAG, "no Controller Layout Event Listener");
    }

    /* Getters & Setters*/
    public int getBrickSize() {
        return brickSize;
    }

    public MenuGateKeeper getGateKeeper() {
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

    public void createDropShadowForImage(View pressedView, DropZoneImage dropZoneImage){

        if (dropZoneImage == null)
        {
            if (DEBUG)
                Log.e(TAG, "createDropShadowForImage, DropZoneImage is null");

            return;
        }

        ClipData data = ClipData.newPlainText("", "");
        DragShadowBuilder imageDragShadowBuilder = ImageDragShadowBuilder.drawBricksShadow(getContext(), new int[]{ dropZoneImage.getDimensions()[COLUMN] * brickSize, // the drag shadow builder
                dropZoneImage.getDimensions()[ROW] * brickSize});

        if (imageDragShadowBuilder == null)
        {
            if (DEBUG)
                Log.e(TAG, "createDropShadowForImage, imageDragShadowBuilder is null");

            return;
        }

        // If the main view is "FrameLayout"  Get the brick size and start drag with brickShadow.
        if (slidePanelMain instanceof FrameLayout)
        {
            if (DEBUG)
                Log.d(TAG, "Main View is FrameLayout");

            pressedView.startDrag(data,  // the data to be dragged
                    imageDragShadowBuilder,  // the drag shadow builder
                    dropZoneImage,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );
        }
    }

    MenuGateKeeper gateKeeper = new MenuGateKeeper() {
        @Override
        public void openMenu(float offset) {
            ControllerLayout.this.openMenu(offset);
        }

        @Override
        public void closeMenu() {
            ControllerLayout.this.closeMenu();
        }
    };

    public boolean isEditing() {
        return isEditing;
    }

    @Override
    public void onClick(View v) {

        if (DEBUG_LISTENERS)
            Log.d(TAG, "OnClick, View id: " + v.getId());

        if (customController == null) {
            if(DEBUG_LISTENERS)
                Log.e(TAG, "No custom controller set");
            return;
        }

        if (isEditing)
        {

            if (DEBUG_LISTENERS)
                Log.d(TAG, "custom controller buttons size = " + customController.getButtons().size());

            openButtonCommandView(customController.getCustomButtonById(v.getId()), v);

            gateKeeper.openMenu(BOTTOM_MENU_OFFSET_FULL);
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
