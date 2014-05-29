package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.database.CustomDBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 3/27/14.
 */
public class BrickControllerLayout extends ControllerLayout  {

    // TODO fix problem with frames and buttons has same id that causes cast errors

    private static final String TAG = BrickControllerLayout.class.getSimpleName();
    private static final boolean DEBUG = true;
    private FrameLayout mainView;

    public BrickControllerLayout(Context context, int paneHeight) {
        super(context, paneHeight);

        init();
    }

    public BrickControllerLayout(Context context, Point screenSize) {
        super(context, screenSize);

        init();
    }

    private List<View> buttons = new ArrayList<View>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG) Log.v(TAG, "OnMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (DEBUG)
            Log.i(TAG, "onLayout,  mainView Children: " + mainView.getChildCount());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (DEBUG)
            Log.v(TAG, "onSizeChanged, Width: " + w + ", Height: " + h + ", Old Width: " + oldw + ", OldHeight: " + oldh);

        if (oldh != 0 || oldw != 0)
        {
            brickBackGroundView.initSizes();
//            reCalcAllButtonPositions();
            // TODO change method to the reCalc. Button is not redrawing themselves in the right position.
            removeAllButtons();

            mainView.post(new Runnable() {
                @Override
                public void run() {
                    addButtons(BrickControllerLayout.this.customController.getButtons());
                }
            });
        }
    }

    private void init(){
        mainView = new FrameLayout(getContext());
        mainView.setLayoutParams(new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initSlidingBottomMenu(mainView); // Adding the main view to the Sliding Layout.
    }

    /** Initialize the BrickBackgroundView*/
    private void initBackground(){
        // Removing the old background view from the main view.
        if (brickBackGroundView != null)
            mainView.removeView((View) brickBackGroundView.getParent());

        FrameLayout.LayoutParams frameParams;

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setId(9999);
        frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(frameParams);

        if (customController.getBrickSize() != -1)
            brickBackGroundView = new BrickBackGroundView(getContext(), customController.getBrickSize());
        else brickBackGroundView = new BrickBackGroundView(getContext(), customController.getRows(), customController.getColumns());

        brickBackGroundView.setDropListener(this);
        frameLayout.addView(brickBackGroundView);

        mainView.addView(frameLayout);
    }

    /** Add a button to the view. Position size and other calculation included.*/
    private View addButtonToView(CustomButton customButton){

        // Mark the spave the button takes as full.
        brickBackGroundView.markBricksAsFull(customButton.getStartPosition(), customButton.getDimensions());

        if (DEBUG)
            Log.i(TAG, "Adding button to the view, id: " + customButton.getId() + ", Type: " + customButton.getType());

        View view = null;
        SlideButtonLayout slideButtonLayout;

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                customButton.getDimensions()[COLUMN] * brickBackGroundView.getBrickSize(),
                customButton.getDimensions()[ROW] * brickBackGroundView.getBrickSize());

        buttonParams.setMargins(
                brickBackGroundView.getColumnsRemainder()/2 + (brickBackGroundView.getBrickSize() * (customButton.getStartPosition()[COLUMN])), // Margin from the left
                brickBackGroundView.getRowsRemainder()/2 + (brickBackGroundView.getBrickSize() * (customButton.getStartPosition()[ROW])) , // Margin from the top
                0, 0); // Margin from the right and bottom

        switch (customButton.getType()) {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                SimpleButton button = new SimpleButton(getContext());

                button.setLayoutParams(buttonParams);

                // Assign image
                if (customButton.getCustomCommand() != null) {
                    if (DEBUG)
                        Log.d(TAG, "Button has command, Type: " + getResources().getString(customButton.getCustomCommand().getType()));

                    button.setCommandType(customButton.getCustomCommand().getType());
                } else
                    button.setCommandType(0);

                // type
                button.setType(customButton.getType());

                button.setOnClickListener(this);
                button.setOnLongClickListener(this);

                button.setId((int) customButton.getId());

                view = button;

                mainView.addView(button);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:

                slideButtonLayout =
                        new SlideButtonLayout(getContext(), SlideButtonLayout.SLIDE_HORIZONTALLY,
                                brickBackGroundView.getBrickSize() * customButton.getDimensions()[ROW],
                                customButton.centerAfterDrop(), customButton.showMarks());

                slideButtonLayout.setType(customButton.getType());

                slideButtonLayout.setLayoutParams(buttonParams);

                // Adding the distance from the top.
                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins(buttonParams.leftMargin, 0, 0, 0);

                slideButtonLayout.setId((int) customButton.getId());

                slideButtonLayout.setSlideButtonListener(getCommandsExecutor());
                slideButtonLayout.setOnLongClickListener(this);
                slideButtonLayout.setOnClickListener(this);

                view = slideButtonLayout;


                mainView.addView(slideButtonLayout);

                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:

                slideButtonLayout =
                        new SlideButtonLayout(getContext(), SlideButtonLayout.SLIDE_VERTICALLY,
                                brickBackGroundView.getBrickSize() * customButton.getDimensions()[COLUMN], customButton.centerAfterDrop(), customButton.showMarks());

                slideButtonLayout.setType(customButton.getType());

                slideButtonLayout.setLayoutParams(buttonParams);

                // Adding the distance from the top.
                // Adding the margins(real margin and the amount of frames from the side) to the button so it will know how much to slide.
                slideButtonLayout.setMargins(0, buttonParams.topMargin, 0, 0);

                slideButtonLayout.setId((int) customButton.getId());

                slideButtonLayout.setSlideButtonListener(getCommandsExecutor());
                slideButtonLayout.setOnLongClickListener(this);
                slideButtonLayout.setOnClickListener(this);

                view = slideButtonLayout;

                mainView.addView(slideButtonLayout);

                break;
        }

        return view;
    }

    /** Adjusting button position, Used after layout size is changed.*/
    private void reCalcAllButtonPositions(){
        if(DEBUG) Log.v(TAG, "reCalcAllButtonPositions");
        FrameLayout.LayoutParams buttonParams;
        View v;
        for (CustomButton btn : BrickControllerLayout.this.customController.getButtons())
        {
            buttonParams = new FrameLayout.LayoutParams(
                    btn.getDimensions()[COLUMN] * brickBackGroundView.getBrickSize(),
                    btn.getDimensions()[ROW] * brickBackGroundView.getBrickSize());

            buttonParams.setMargins(
                    brickBackGroundView.getColumnsRemainder()/2 + (brickBackGroundView.getBrickSize() * (btn.getStartPosition()[COLUMN])), // Margin from the left
                    brickBackGroundView.getRowsRemainder()/2 + (brickBackGroundView.getBrickSize() * (btn.getStartPosition()[ROW])) , // Margin from the top
                    0, 0); // Margin from the right and bottom

            v = getButtonView((int) btn.getId());
            if (v != null)
            {
                v.setLayoutParams(buttonParams);
                v.requestLayout();
            }
        }
    }

    /** Get the button view by id*/
    private View getButtonView(int id){
        for (View v : buttons)
            if (v.getId() == id)
                return v;

        return null;
    }

    private void removeAllButtons(){
        for (int i = 0 ; i < buttons.size() ; i++)
        {
            mainView.removeView(buttons.get(i));
        }
    }

    private void addButtons(List<CustomButton> list){
        // Adding the controller buttons.
        buttons = new ArrayList<View>();
        for (CustomButton btn : list)
        {
            buttons.add(addButtonToView(btn));
        }
    }

    /* Implement Methods*/
    @Override
    public void onButtonAdded(CustomButton customButton) {
        super.onButtonAdded(customButton);

        if (DEBUG)
            Log.d(TAG, "onButtonAdded");

        customButton.setControllerId(customController.getId());

        // Adding the button to the database
        long id = CustomDBManager.getInstance().addButton(customButton);

        // Adding the button obj to the custom controller obj and setting the button id.
        customButton.setId(id);
        customController.getButtons().add(customButton);

        // Adding the button view and setting his id to the id of the button in the database.
        View view = addButtonToView(customButton);
        view.setId((int) customButton.getId());

        // Adding it to the buttons view list.
        buttons.add(view);
    }

    @Override
    public void onButtonDeleted(CustomButton customButton) {
        super.onButtonDeleted(customButton);

        if (DEBUG)
            Log.d(TAG, "onButtonDeleted");

        customController.removeButtonById(customButton.getId());

        CustomDBManager.getInstance().deleteButtonById(customButton.getId());

        deleteFrame.setVisibility(INVISIBLE);
    }

    @Override
    public void onButtonChangedPosition(CustomButton customButton) {
        super.onButtonChangedPosition(customButton);

        if (DEBUG)
            Log.d(TAG, "onButtonChangedPosition");

        CustomDBManager.getInstance().updateButtonById(customButton);

        // Adding the button view and setting his id to the id of the button in the database.
        View view = addButtonToView(customButton);
        view.setId((int) customButton.getId());

        // Adding it to the buttons view list.
        buttons.add(view);

        // Hiding the delete frame.
        deleteFrame.setVisibility(INVISIBLE);
    }

    @Override
    public void setController(final CustomController customController) {
        super.setController(customController);
        if (DEBUG) Log.v(TAG, "setController");

        initBackground();

        if (customController.getBrickSize() != -1)
        {
            brickBackGroundView.calcBrickSize(customController.getBrickSize());
        }

        if (customController.getColumns() == -1 && customController.getRows() == -1)
            brickBackGroundView.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "brickBackGroundView POST, rows: " + brickBackGroundView.getRowsAmount() + ", columns: " + brickBackGroundView.getColumnsAmount());
                    customController.setRows(brickBackGroundView.getRowsAmount());
                    customController.setColumns(brickBackGroundView.getColumnsAmount());
                    setMinMaxValues();
                    if(CustomDBManager.getInstance().updateController(customController))
                    {
                        if (DEBUG) Log.d(TAG, "Controller is updated!");
                    }
                    else if (DEBUG) Log.d(TAG, "Controller isn't updated!");
                }
            });

        if (DEBUG)
            Log.d(TAG, "setController, name: " + customController.getName() + ", Id: " + customController.getId());

        // Removing old views.
        removeAllButtons();

        mainView.post(new Runnable() {
            @Override
            public void run() {
                // Adding the controller buttons.
                addButtons(BrickControllerLayout.this.customController.getButtons());
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {

        if (DEBUG)
            Log.d(TAG, "onLongClick");

        if (isEditing())
        {
            if (v instanceof SlideButton)
                v = (View) v.getParent();

            CustomButton customButton = customController.getCustomButtonById(v.getId());

            brickBackGroundView.markBricksAsEmpty(customButton.getStartPosition(), customButton.getDimensions());

            createDropShadowForImage(v, DropZoneImage.crateDropZoneImageForButton(getContext(), customButton), brickBackGroundView.getBrickSize());

            mainView.removeView(v);

            deleteFrame.setVisibility(VISIBLE);
        }

        return super.onLongClick(v);
    }
}
