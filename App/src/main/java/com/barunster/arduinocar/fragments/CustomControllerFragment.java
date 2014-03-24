package com.barunster.arduinocar.fragments;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.ButtonGridSelectionAdapter;
import com.barunster.arduinocar.adapters.ControllersListAdapter;
import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.custom_controllers_obj.AccelerometerHandler;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.database.ControllersDataSource;
import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.views.DropZoneFrame;
import com.barunster.arduinocar.views.DropZoneImage;
import com.barunster.arduinocar.views.SimpleButton;
import com.barunster.arduinocar.views.SlideButtonLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import braunster.btconnection.Command;

/**
 * Created by itzik on 3/11/14.
 */
public class CustomControllerFragment extends ArduinoLegoFragment implements View.OnClickListener, SlideButtonLayout.SlideButtonListener, AccelerometerHandler.AccelerometerEventListener{

    // TODO handle onpause and on resume for registering and unregistering the accelrometer handler.

    private static final String TAG = CustomControllerFragment.class.getSimpleName();

    public static final int GRID_COLUMN_NUMBER = 4;

    /*Views*/
    private RelativeLayout mainView, reFrames;
    private SlideButtonLayout slideButton;
    private PopupWindow popupButtonSelection;
    private GridView gridDropZone;
    private DropZoneImage dropZoneImage;
    private List<DropZoneFrame> highlightedZones = new ArrayList<DropZoneFrame>();
    private Button btnEdit, btnAddController, btnLoadController;

    private final int columnAmount = 4;
    private final int rowAmount = 4;
    private int columnSpace, rowSpace, cellSize ;

    private ArduinoCarAppObj app;
    private CustomController customeController;

    private boolean editing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj)getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (RelativeLayout) inflater.inflate(R.layout.fragment_custom_controller, null);

        btnEdit = (Button) mainView.findViewById(R.id.btn_edit);
        btnAddController = (Button) mainView.findViewById(R.id.btn_add_controller);
        btnLoadController = (Button) mainView.findViewById(R.id.btn_load_controler);

        reFrames = (RelativeLayout) mainView.findViewById(R.id.relative_frames);
        reFrames.setTag("");

        // if no controller found generate empty frames
        if (app.getCustomDBManager().getControllersDataSource().getAllControllers().size() == 0) {
            initFrames();
//            showAddControllerPopup();
            customeController = new CustomController("Defualt");
            long id  = app.getCustomDBManager().getControllersDataSource().addController(customeController);
            customeController = app.getCustomDBManager().getControllerById(id);
        }
        else
           initFramesForController(app.getCustomDBManager().getControllerById(
                   app.getCustomDBManager().getControllersDataSource().getAllControllers().get(0).getId()) );

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initButtons();
    }

    /* Popups*/
    private void showPopupSelectButtons(){
        if (popupButtonSelection != null && popupButtonSelection.isShowing())
            popupButtonSelection.dismiss();

        View popupView  = getActivity().getLayoutInflater().inflate(R.layout.popup_buttons_selection, null);

        ButtonGridSelectionAdapter buttonGridSelectionAdapter = new ButtonGridSelectionAdapter(getActivity());
        ((GridView) popupView.findViewById(R.id.grid_buttons)).setAdapter(buttonGridSelectionAdapter);
        ((GridView) popupView.findViewById(R.id.grid_buttons)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

//                Log.d(TAG, "GridItemSelected");
                ClipData data = ClipData.newPlainText("", "");

                dropZoneImage = (DropZoneImage) v;
                dropZoneImage.setOnDrag(true);

                v.startDrag(data,  // the data to be dragged
                        ImageDragShadowBuilder.fromResource(getActivity(), (Integer) v.getTag()),  // the drag shadow builder
                        v,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );

                popupButtonSelection.dismiss();


                return false;
            }
        });




        popupButtonSelection = new PopupWindow(getActivity());
        popupButtonSelection.setFocusable(true);
        popupButtonSelection.setContentView(popupView);
        popupButtonSelection.setOutsideTouchable(true);
        popupButtonSelection.setBackgroundDrawable(new BitmapDrawable());
        popupButtonSelection.setWidth((int) getScreenWidth() / 3);
        popupButtonSelection.setHeight((int)getScreenHeight());
        popupButtonSelection.setAnimationStyle(R.style.PopupAnimation);

        popupButtonSelection.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*if (dropZoneImage == null || !dropZoneImage.isOnDrag())
                    exitEditMode();*/
            }
        });

        popupButtonSelection.showAsDropDown(app.getSlideFadeMenu());


    }

    private void showLoadControllerPopup(){

        final PopupWindow popupWindow = new PopupWindow(getActivity());

        View popupView  = getActivity().getLayoutInflater().inflate(R.layout.popup_controller_selection, null);
        final ControllersListAdapter adapter = new ControllersListAdapter(getActivity(),
                app.getCustomDBManager().getControllersDataSource().getAllControllers());

        ((ListView) popupView.findViewById(R.id.list_controllers)).setAdapter(adapter);
        ((ListView) popupView.findViewById(R.id.list_controllers)).setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OnItemClicked");


                initFramesForController(app.getCustomDBManager().getControllerById(adapter.getItem(position).getId()));

                popupWindow.dismiss();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth((int) getScreenWidth() / 3);
        popupWindow.setHeight((int)getScreenHeight());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*if (dropZoneImage == null || !dropZoneImage.isOnDrag())
                    exitEditMode();*/
            }
        });

        popupWindow.showAsDropDown(app.getSlideFadeMenu());


    }

    private void showAddControllerPopup(){

        final PopupWindow popupWindow = new PopupWindow(getActivity());

        final View popupView  = getActivity().getLayoutInflater().inflate(R.layout.popup_add_controller, null);

        popupView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !((EditText) popupView.findViewById(R.id.et_controller_name)).getText().toString().isEmpty() )
                {
                    long id = app.getCustomDBManager().getControllersDataSource().addController(
                            new CustomController(((EditText) popupView.findViewById(R.id.et_controller_name)).getText().toString()) );

                    initFramesForController(app.getCustomDBManager().getControllerById(id));

                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth((int) getScreenWidth() / 3);
        popupWindow.setHeight((int)getScreenHeight());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*if (dropZoneImage == null || !dropZoneImage.isOnDrag())
                    exitEditMode();*/
            }
        });

        popupWindow.showAsDropDown(app.getSlideFadeMenu());

    }

    private void showSetButtonCommandPopup(final View relatedButton,final int buttonType){

        final PopupWindow popupWindow = new PopupWindow(getActivity());

        final View popupView  = getActivity().getLayoutInflater().inflate(R.layout.popup_add_command, null);

        // Channel Selection
        TextView txtChannel;
        for (String channel : getResources().getStringArray(R.array.motor_channels))
        {
            txtChannel = (TextView) getActivity().getLayoutInflater().inflate(R.layout.simple_text_view, null);
            txtChannel.setTextSize(15f);
            txtChannel.setText(channel);

            if (popupView.findViewById(R.id.linear_channels).getTag() == null)
                popupView.findViewById(R.id.linear_channels).setTag(txtChannel);

            txtChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ( (TextView) popupView.findViewById(R.id.linear_channels).getTag()).setTextColor(Color.LTGRAY);
                    popupView.findViewById(R.id.linear_channels).setTag(v);
                }
            });

            if ( ( (TextView) popupView.findViewById(R.id.linear_channels).getTag()).getText().equals(channel))
                txtChannel.setTextColor(Color.WHITE);

            ( (LinearLayout) popupView.findViewById(R.id.linear_channels)).addView(txtChannel);

        }

        // Command Type Selection
        RadioButton radioButton;
        int list[] = new int[0];
        switch (buttonType)
        {
            case ArduinoLegoFragment.BUTTON_TYPE_SIMPLE:
               list = CustomCommand.regularButtonCommandTypes;
                break;

            case ArduinoLegoFragment.BUTTON_TYPE_SLIDE_HORIZONTAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;

            case ArduinoLegoFragment.BUTTON_TYPE_SLIDE_VERTICAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;
        }

        for ( int id : list)
        {
            radioButton = new RadioButton(getActivity());
            radioButton.setId(id);
            radioButton.setTextSize(15f);
            radioButton.setText(getResources().getString(id));
            ( (RadioGroup) popupView.findViewById(R.id.radio_grp_button_type)).addView(radioButton);
        }

        ( (RadioGroup) popupView.findViewById(R.id.radio_grp_button_type)).getChildAt(0).setSelected(true);

        // Submit Button
        popupView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int commandType = ( (RadioGroup) popupView.findViewById(R.id.radio_grp_button_type)).getCheckedRadioButtonId();
                CustomCommand customCommand = new CustomCommand(relatedButton.getId(),
                       commandType,
                        ((TextView)popupView.findViewById(R.id.linear_channels).getTag()).getText().toString());

                app.getCustomDBManager().getCustomCommandsDataSource().addCommand(customCommand);

                initButtonCommand(relatedButton);

                popupWindow.dismiss();
            }
        });

        // Popup Settings
        popupWindow.setFocusable(true);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth((int) getScreenWidth() / 3);
        popupWindow.setHeight((int) getScreenHeight() - app.getSlideFadeMenu().getMeasuredHeight());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.showAsDropDown(app.getSlideFadeMenu());

    }

    /* Views initialization and manipulation. */
    /* Initialize Views and Views Logic*/
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
                    if (app.getSlideFadeMenu().isShowing())
                        app.getSlideFadeMenu().closeMenu();

                    showPopupSelectButtons();

                    enterEditMode();                }

                editing = !editing;
            }
        });

        btnAddController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddControllerPopup();
            }
        });

        btnLoadController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadControllerPopup();
            }
        });
    }

    private void initFrames(){


        DropZoneFrame frame;
        RelativeLayout.LayoutParams frameParams;
        cellSize = (int)getScreenHeight() / 5;
        columnSpace = (int) ( getScreenWidth() - (cellSize * columnAmount) ) / (columnAmount+1);
        rowSpace = (int) ( getScreenHeight() - (cellSize * rowAmount) ) / (rowAmount + 1);

        Log.d(TAG, "row space = " + rowSpace + " Column space: "  + columnSpace + "Cell Size: " + cellSize);

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

        customeController = controller;

        Log.d(TAG, "initFramesForController, Name: " + controller.getName());

        initFrames();

        DropZoneFrame frame;

        for (CustomButton btn : customeController.getButtons())
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
    /* Creating a button inside a frame */
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
            case ArduinoLegoFragment.BUTTON_TYPE_SIMPLE:
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
                            showSetButtonCommandPopup(button, type);
                        return true;
                    }
                });

                // Adding to the frame.
                frame.addView(button);

                initButtonCommand(button);

                break;

            case ArduinoLegoFragment.BUTTON_TYPE_SLIDE_VERTICAL:

                slideButtonLayout =
                        new SlideButtonLayout(getActivity(), LinearLayout.HORIZONTAL, cellSize, true, false);

                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins(0, ((RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin,0, 0);

                slideButtonLayout.setOnSlideButtonLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (editing)
                            showSetButtonCommandPopup(slideButtonLayout, type);
                        return false;
                    }
                });

                slideButtonLayout.setId((int) buttonId);

                slideButtonLayout.setSlideButtonListener(this);

                frame.addView(slideButtonLayout);

                initButtonCommand(slideButtonLayout);

                break;

            case ArduinoLegoFragment.BUTTON_TYPE_SLIDE_HORIZONTAL:

                slideButtonLayout =
                        new SlideButtonLayout(getActivity(), LinearLayout.VERTICAL, cellSize, true, false);

                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins( ((RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin, 0 ,0 , 0);

                slideButtonLayout.setOnSlideButtonLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (editing)
                            showSetButtonCommandPopup(slideButtonLayout, type);
                        return false;
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

    /* Change empty frames background so it would be visible or hidden*/
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

    /* Fragment Mode && Listeners*/
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
    public void onControllerOptionPressed() {
        Log.d(TAG, "ContollerSettingPressed");
//        enterEditMode();
//        showPopupSelectButtons();
        super.onControllerOptionPressed();
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

                    if (buttonState == SimpleButton.STATE_ON) {
                        app.getConnection().write(String.valueOf(Command.STOP) + customCommand.getChannel());
                        ((SimpleButton) v).setState(SimpleButton.STATE_OFF);
                    }
                    else if (buttonState == SimpleButton.STATE_OFF)
                    {
                        app.getConnection().write( String.valueOf(Command.GO) + customCommand.getChannel());
                        ((SimpleButton) v).setState(SimpleButton.STATE_ON);
                    }

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
                        AccelerometerHandler.getInstance().setAccelerometerEventListener(CustomControllerFragment.this);
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

    /* Drag Listener*/
    class ButtonDropZoneListener implements View.OnDragListener {
//        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
//        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        private DropZoneFrame frame;
        private int posInGrid;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            if (dropZoneImage != null)
            {
                frame = ((DropZoneFrame)v);
                posInGrid = dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? frame.getColNumber() : frame.getRowNumber();// Get the position of the view in the row

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
                            if (dropZoneImage.getSize() > 1)
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
                        if (dropZoneImage != null && frame.canDrop())
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
                        showPopupSelectButtons();

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

            if (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL)
            {
                // Getting the most left frame.
                frame = getMostLeftFrame();

                frameParams = new RelativeLayout.LayoutParams(
                        (cellSize * dropZoneImage.getSize()) + ( columnSpace * (dropZoneImage.getSize() - 1) ), cellSize);
            }
            else
            {
                // Getting the to[ frame.
                frame = getTopFrame();

                frameParams = new RelativeLayout.LayoutParams(
                        cellSize, (cellSize * dropZoneImage.getSize()) + ( rowSpace * (dropZoneImage.getSize() - 1) ));
            }

            // Adding the margin to the SlideLayout, The final margin is the actual frame margin + column/row number + cellSize r.g row 3 * 144
            frameParams.leftMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).leftMargin ;
            frameParams.topMargin = ( (RelativeLayout.LayoutParams) frame.getLayoutParams()).topMargin;
            frame.setLayoutParams(frameParams);

            // Setting the frame to full
            frame.setEmpty(false);

            frame.setTag(dropZoneImage.getType());

            // Adding the button to the database
            CustomButton customButton = new CustomButton(customeController.getId(), dropZoneImage.getType(), dropZoneImage.getSize(), dropZoneImage.getOrientation(), frame.getId());
            long id = app.getCustomDBManager().getCustomButtonsDataSource().addButton(customButton);

            createButtonForFrame(id, frame, dropZoneImage.getType());

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
            int count = dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount; // counting the neighbor view to check

            do
            {
                canGoLeft = canGoLeft && // Making sure the last check was ok to
                        posInGrid - ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) >= 0 && // Check that we are still on the same row
                        reFrames.getChildAt(frame.getId() - count) != null && //  Check that the view isnt null
                        ((DropZoneFrame) reFrames.getChildAt(frame.getId() - count)).isEmpty(); // Check that the view is empty;

                canGoRight = canGoRight && // Making sure the last check was ok to
                        posInGrid + ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) )  < columnAmount && // Check that we are still on the same row
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

                  /*              Log.d(TAG, "Checked Id's = " + (frame.getId() - count) + ", " + (frame.getId() + count));

                                Log.d(TAG, "Checked Position's = " + (posInGrid - ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) )
                                        + ", " + (posInGrid + ( count / (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) ) );

                                Log.d(TAG, "Count: " + count
                                        + (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoLeft: " : "Can go Down") + String.valueOf(canGoLeft) + " Steps: " + leftSteps
                                        +  (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? " CanGoRight: " : " Can Go Up ") + String.valueOf(canGoRight) + " Steps: " + rightSteps);*/
                count += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount;

            } while ( (canGoLeft || canGoRight) && count < dropZoneImage.getSize() * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) );

            processMovementOptionFindings(canGoLeft, canGoRight, leftSteps, rightSteps);
        }

        private void processMovementOptionFindings(boolean canGoLeft, boolean canGoRight, int leftSteps, int rightSteps){
            int count;

            // If there is a place from left to right of the view
            if (rightSteps + leftSteps + 1 == dropZoneImage.getSize())
            {
//                                Log.d(TAG, "There is a place with right and left combine");

                for (int i = frame.getId() - (leftSteps * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount)) ;
                     i <= frame.getId() + (rightSteps * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) ) ;
                     i += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount)
                {
//                                    Log.d(TAG, "I: " + i);
                    ((DropZoneFrame) reFrames.getChildAt(i)).setToAvailableForDrop();
                    highlightedZones.add(((DropZoneFrame) reFrames.getChildAt(i)));
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

//                                Log.d(TAG, "Going To The " + ( canGoLeft ? "Left" : "Right" ));

                while (count < dropZoneImage.getSize() * (dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount) )
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

                    count += dropZoneImage.getOrientation() == LinearLayout.HORIZONTAL ? 1 : rowAmount;
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

class ImageDragShadowBuilder extends View.DragShadowBuilder {
    private Drawable shadow;

    private ImageDragShadowBuilder() {
        super();
    }

    public static View.DragShadowBuilder fromResource(Context context, int drawableId) {
        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = context.getResources().getDrawable(drawableId);
        if (builder.shadow == null) {
            throw new NullPointerException("Drawable from id is null");
        }

        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder fromBitmap(Context context, Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = new BitmapDrawable(context.getResources(), bmp);
        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.x = shadow.getMinimumWidth();
        shadowSize.y = shadow.getMinimumHeight();

        shadowTouchPoint.x = (int)(shadowSize.x / 2);
        shadowTouchPoint.y = (int)(shadowSize.y / 2);
    }
}
