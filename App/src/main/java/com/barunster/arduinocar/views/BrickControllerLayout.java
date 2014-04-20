package com.barunster.arduinocar.views;

import android.content.Context;
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
public class BrickControllerLayout extends ControllerLayout implements BrickBackGroundView.ButtonAddedListener {

    // TODO fix problem with frames and buttons has same id that causes cast errors

    private static final String TAG = BrickControllerLayout.class.getSimpleName();
    private static final boolean DEBUG = true;
    private FrameLayout mainView;

    private BrickBackGroundView brickBackGroundView;

    public BrickControllerLayout(Context context, int paneHeight) {
        super(context, paneHeight);

        init();
    }

    private List<View> buttons = new ArrayList<View>();

    private void init(){
        mainView = new FrameLayout(getContext());
        mainView.setLayoutParams(new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initBackground();

        initSlidingBottomMenu(mainView); // Adding the main view to the Sliding Layout.
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
            Log.i(TAG, "onLayout,  mainView Children: " + mainView.getChildCount());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (DEBUG)
            Log.i(TAG, "onSizeChanged, Width: " + w + ", Height: " + h + ", Old Width: " + oldw + ", OldHeight: " + oldh);

        if (oldh != 0 || oldw != 0)
        {
            // TODO Handle size change
        }
    }

    private void initBackground(){
        FrameLayout.LayoutParams frameParams;

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setId(9999);
        frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(frameParams);

        brickBackGroundView = new BrickBackGroundView(getContext(), getBrickSize());
        brickBackGroundView.setButtonAddedListener(this);
        frameLayout.addView(brickBackGroundView);

        mainView.addView(frameLayout);
    }

    private View addButtonToView(CustomButton customButton){

        // Mark the spave the button takes as full.
        brickBackGroundView.markBricksAsFull(customButton.getStartPosition(), customButton.getDimensions());

        if (DEBUG)
            Log.i(TAG, "Adding button to the view, id: " + customButton.getId() + ", Type: " + customButton.getType());

        View view = null;
        SlideButtonLayout slideButtonLayout;

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(customButton.getDimensions()[COLUMN] * getBrickSize(), customButton.getDimensions()[ROW] * getBrickSize());

        buttonParams.setMargins(
                brickBackGroundView.getColumnsRemainder()/2 + (getBrickSize() * (customButton.getStartPosition()[COLUMN])), // Margin from the left
                brickBackGroundView.getRowsRemainder()/2 + (getBrickSize() * (customButton.getStartPosition()[ROW])) , // Margin from the top
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
                                getBrickSize() * customButton.getDimensions()[ROW], customButton.centerAfterDrop(), customButton.showMarks());

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
                                getBrickSize() * customButton.getDimensions()[COLUMN], customButton.centerAfterDrop(), customButton.showMarks());

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


    /* Getters & Setters*/

    /* Implement Methods*/
    @Override
    public void onButtonAdded(CustomButton customButton) {
        if (DEBUG)
            Log.d(TAG, "onButtonAdded");

        // Button is not new, button just changed his position.
        if (customButton.getId() != -1){
            // Updating the database
            CustomDBManager.getInstance().updateButtonById(customButton);
        }
        else
        {
            customButton.setControllerId(customController.getId());

            // Adding the button to the database
            long id = CustomDBManager.getInstance().addButton(customButton);

            // Adding the button obj to the custom controller obj and setting the button id.
            customButton.setId(id);
            customController.getButtons().add(customButton);
        }

        // Adding the button view and setting his id to the id of the button in the database.
        View view = addButtonToView(customButton);
        view.setId((int) customButton.getId());

        // Adding it to the buttons view list.
        buttons.add(view);
    }

    @Override
    public void setController(CustomController customController) {
        super.setController(customController);

        if (DEBUG)
            Log.d(TAG, "setController, name: " + customController.getName() + ", Id: " + customController.getId());

        // Removing old views.
        for (int i = 0 ; i < buttons.size() ; i++)
        {
            mainView.removeView(buttons.get(i));
        }

        mainView.post(new Runnable() {
            @Override
            public void run() {
                // Adding the controller buttons.
                buttons = new ArrayList<View>();
                for (CustomButton btn : BrickControllerLayout.this.customController.getButtons())
                {
                    buttons.add(addButtonToView(btn));
                }
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

            createDropShadowForImage(v, DropZoneImage.crateDropZoneImageForButton(getContext(), customButton));

            mainView.removeView(v);
        }

        return super.onLongClick(v);
    }
}
