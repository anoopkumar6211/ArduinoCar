package com.barunster.arduinocar.views;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.ButtonGridSelectionAdapter;
import com.barunster.arduinocar.adapters.RadioGroupGridAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CommandsExecutor;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.custom_controllers_obj.CustomUtils;
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



    private static final String TAG = ControllerLayout.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LISTENERS = false;

    public static final float BOTTOM_MENU_OFFSET_FULL = 0.5f;

    private View slidePanelMain;// The main view for the sliding up panel.
    BrickBackGroundView brickBackGroundView;

    private LinearLayout liBottomMenu, liButtonGrid, liButtonCommand, liAddButton;
    DropZoneFrame deleteFrame;
    Point screenSize;
    private int paneHeight = 0;

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

    public ControllerLayout(Context context, Point screenSize) {
        super(context);
        this.screenSize = screenSize;

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG) Log.v(TAG, "OnMeasure");
    }

    private void init(){
        commandsExecutor = new CommandsExecutor(getContext(), connection);
    }

    /* Bottom Menu */
    public void initSlidingBottomMenu(View main){
        if (slidePanelMain == null) {
            slidePanelMain = main;

            // Setting the params
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM;
            this.setLayoutParams(params);

            this.setPanelHeight(paneHeight);
            this.setSlidingEnabled(true);
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
//                    ControllerLayout.this.setSlidingEnabled(false);
                    }
                    // When closed full eliminating the listener and enabling the slide.
                    else if (slideOffset == 1.0f) {

                    }
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
    }

    private View initBottomMenu(){
        liBottomMenu = new LinearLayout(getContext());
        liBottomMenu.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP;

        liBottomMenu.setLayoutParams(params);
//        liBottomMenu.addView(initMenuButtons());
        initMenuButtons();

        /* Add Button - Buttons Grid*/
        liButtonGrid = (LinearLayout) initButtonGridView();
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        viewParams.weight = 0.5f;
        viewParams.gravity = Gravity.TOP;
        liButtonGrid.setLayoutParams(viewParams);

        /* Add Button - Buttons Grid*/
        liButtonCommand = (LinearLayout) initButtonCommandView();
        liButtonCommand.setLayoutParams(viewParams);

        /* Add Button - Adjustable Button*/
        liAddButton = (LinearLayout) initAddButtonView();
        liAddButton.setLayoutParams(viewParams);

        liBottomMenu.addView(liAddButton);

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
//                    openButtonGridView();
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

//            linearLayout.addView(addBtn);
//            linearLayout.addView(editBtn);
//            linearLayout.addView(closeBtn);
//            linearLayout.addView(deleteFrame);

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

                createDropShadowForImage(v, (DropZoneImage) v, brickBackGroundView.getBrickSize());

                gateKeeper.closeMenu();

                return false;
            }
        });

        return mainView;
    }

    private View initAddButtonView(){
        if (DEBUG) Log.v(TAG, "initAddButtonView");
        final View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate(R.layout.view_add_button, null);

        final NumberPicker pickerHeight = ((NumberPicker)view.findViewById(R.id.picker_height)), pickerWidth = ((NumberPicker)view.findViewById(R.id.picker_width));

        pickerWidth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(TAG, "Width - onValueChange, newVal: " + newVal);
                if (pickerHeight.getValue() != newVal)
                    pickerHeight.setValue(newVal);
            }
        });

        pickerHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(TAG, "Height - onValueChange, newVal: " + newVal);
                if (pickerWidth.getValue() != newVal)
                    pickerWidth.setValue(newVal);
            }
        });

        ((RadioGroup)view.findViewById(R.id.grp_button_type)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(DEBUG) Log.v(TAG, "onCheckedChanged");
                if (checkedId == R.id.radio_static_button)
                {
                    if(DEBUG) Log.v(TAG, "onCheckedChanged - Static!");
                    pickerWidth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.v(TAG, "Width - onValueChange, newVal: " + newVal);
                            if (pickerHeight.getValue() != newVal)
                                pickerHeight.setValue(newVal);
                        }
                    });

                    pickerHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.v(TAG, "Height - onValueChange, newVal: " + newVal);
                            if (pickerWidth.getValue() != newVal)
                                pickerWidth.setValue(newVal);
                        }
                    });

                    // Set the max to the lowest of the two: width and height.
                    if (pickerHeight.getMaxValue() > pickerWidth.getMaxValue())
                        pickerHeight.setMaxValue(pickerWidth.getMaxValue());
                    else pickerWidth.setMaxValue(pickerHeight.getMaxValue());
                }
                else
                {
                    ((NumberPicker)view.findViewById(R.id.picker_width)).setOnValueChangedListener(null);
                    ((NumberPicker)view.findViewById(R.id.picker_height)).setOnValueChangedListener(null);

                    // Retain normal values
                    setMinMaxValues();
                }
            }
        });

        view.findViewById(R.id.view_drag_button).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int type = 0;
                // The amount of columns this button will cover
                final int colAmount = pickerWidth.getValue();
                // The amount of rows this button will cover
                final int rowAmount = pickerHeight.getValue();
                Log.v(TAG, "OnButtonStartDrag, rowAmount: " + rowAmount + ", colAmount: " + colAmount);

                switch (((RadioGroup)view.findViewById(R.id.grp_button_type)).getCheckedRadioButtonId())
                {
                    case R.id.radio_static_button:
                        type = CustomButton.BUTTON_TYPE_SIMPLE;
                        break;

                    case R.id.radio_sliding_button:
                        if (rowAmount > colAmount)
                            type = CustomButton.BUTTON_TYPE_SLIDE_VERTICAL;
                        else
                            type = CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL;

                        break;
                }

                DropZoneImage buttonImage = new DropZoneImage(getContext());
                buttonImage.setType(type);
                buttonImage.setDimensions(new int[]{rowAmount, colAmount});

                createDropShadowForImage(v,
                        buttonImage,
                        brickBackGroundView.getBrickSize() );

                gateKeeper.closeMenu();

                return true;
            }
        });

        return view;
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
        if (liBottomMenu.getChildAt(0).equals(liAddButton))
        {
            liBottomMenu.removeView(liAddButton);
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

        liButtonCommand.post(new Runnable() {
            @Override
            public void run() {
                /*if (DEBUG) Log.i(TAG, "Layout getMeasuredHeight() = " + ((float)getMeasuredHeight()));
                if (DEBUG) Log.i(TAG, "liBottomMenu getMeasuredHeight() = " + ((float)liBottomMenu.getMeasuredHeight()));
                if (DEBUG) Log.i(TAG, "liButtonCommand getMeasuredHeight() = " + ((float)liButtonCommand.getMeasuredHeight()));
                if (DEBUG) Log.i(TAG, "Calc, liButtonCommand.getMeasuredHeight() / 7201 = " + (1f - ((double)liButtonCommand.getMeasuredHeight()) / (double) getMeasuredHeight()) );*/
                gateKeeper.openMenu( 1f -  (((float)liButtonCommand.getMeasuredHeight()) / (float) getMeasuredHeight()));
//                gateKeeper.openMenu(0.9f);
            }
        });
    }

    private void openButtonGridView(){
        // Removing the Button Grid View if needed.
        if (liBottomMenu.getChildAt(0).equals(liButtonCommand))
        {
            liBottomMenu.removeView(liButtonCommand);
            liBottomMenu.addView(liButtonGrid);
        }
        gateKeeper.openMenu( 1f -  (((float)liButtonGrid.getMeasuredHeight()) / (float) getMeasuredHeight()));
    }

    private void openAddButtonView(){
        if (DEBUG) Log.v(TAG, "openAddButtonView");
        setMinMaxValues();

        // Removing the Button Grid View if needed.
        if (liBottomMenu.getChildAt(0).equals(liButtonCommand))
        {
            liBottomMenu.removeView(liButtonCommand);
            liBottomMenu.addView(liAddButton);
        }

        liAddButton.post(new Runnable() {
            @Override
            public void run() {
                gateKeeper.openMenu(1f - (((float) liAddButton.getMeasuredHeight()) / (float) getMeasuredHeight()));
            }
        });
    }

    void setMinMaxValues(){
        if (DEBUG) Log.v(TAG, "setMinMaxValues, rows: " + customController.getRows() + ", columns: " + customController.getColumns());

        ((NumberPicker)liAddButton.findViewById(R.id.picker_width)).setMinValue(1);
        ((NumberPicker)liAddButton.findViewById(R.id.picker_height)).setMinValue(1);

        if (((RadioGroup)liAddButton.findViewById(R.id.grp_button_type)).getCheckedRadioButtonId() == R.id.radio_static_button)
        {
            if (customController.getColumns() > customController.getRows()) {
                ((NumberPicker) liAddButton.findViewById(R.id.picker_width)).setMaxValue(customController.getRows());
                ((NumberPicker) liAddButton.findViewById(R.id.picker_height)).setMaxValue(customController.getRows());
            }
            else
            {
                ((NumberPicker) liAddButton.findViewById(R.id.picker_width)).setMaxValue(customController.getColumns());
                ((NumberPicker) liAddButton.findViewById(R.id.picker_height)).setMaxValue(customController.getColumns());
            }
        }
        else
        {
            ((NumberPicker)liAddButton.findViewById(R.id.picker_width)).setMaxValue(customController.getColumns());
            ((NumberPicker)liAddButton.findViewById(R.id.picker_height)).setMaxValue(customController.getRows());
        }


        ((NumberPicker)liAddButton.findViewById(R.id.picker_width)).setValue(((NumberPicker)liAddButton.findViewById(R.id.picker_width)).getMinValue());
        ((NumberPicker)liAddButton.findViewById(R.id.picker_height)).setValue(((NumberPicker)liAddButton.findViewById(R.id.picker_width)).getMinValue());
    }

    /*-----------------------------------------*/

    /* Layout Events*/
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
        if (commandsExecutor == null)
            init();

        gateKeeper.closeMenu();

        commandsExecutor.setControllerId(customController.getId());
    }

    @Override
    public void setOutputConnection(BTConnection connection) {
        this.connection = connection;
        commandsExecutor.setConnection(connection);
    }

    /* Delete and Drop Frame Drag Class */
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

    public void showAddButtonView(){
        exitEditMode();
        editBtn.setSelected(false);
//        openButtonGridView();
        openAddButtonView();
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

    void createDropShadowForImage(View pressedView, DropZoneImage dropZoneImage, int brickSize){

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

        // If the main view is "FrameLayout"  Get the brick_w200_h200 size and start drag with brickShadow.
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
            // TODO adjust to open for view size " 1f -  (((float)liAddButton.getMeasuredHeight()) / (float) getMeasuredHeight())"
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

    /* Implementation */
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
}
