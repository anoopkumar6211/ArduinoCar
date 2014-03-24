package com.barunster.arduinocar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.barunster.arduinocar.interfaces.FragmentMode;
import com.barunster.arduinocar.interfaces.SlideMenuListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by itzik on 3/9/14.
 */
public class ArduinoLegoFragment extends Fragment implements FragmentMode, SlideMenuListener{

    private static final String TAG = ArduinoLegoFragment.class.getSimpleName();

    public static final int DEFAULT_SPEED_POINTS = 255;

    /* Fragment Tags*/
    public static final String FRAGMENT_TYPE_STICK = "lego_fragment.type.stick";
    public static final String FRAGMENT_TYPE_MOTOR = "lego_fragment.type.motor";
    public static final String FRAGMENT_TYPE_SERVO = "lego_fragment.type.servo";
    public static final String FRAGMENT_TYPE_MULTIPLE = "lego_fragment.type.multiple";
    public static final String FRAGMENT_TYPE_CUSTOM_CONTROLLER = "lego_fragment.type.custom_controller";
    public static final String FRAGMENT_TYPE_ACCELEROMETER = "lego_fragment.type.accelerometer";

    public static final List<String> controllersTags =
            new ArrayList<String>( Arrays.asList( new String[]{ FRAGMENT_TYPE_SERVO, FRAGMENT_TYPE_MOTOR,
                                                           FRAGMENT_TYPE_STICK, FRAGMENT_TYPE_MULTIPLE,
                                                           FRAGMENT_TYPE_CUSTOM_CONTROLLER, FRAGMENT_TYPE_ACCELEROMETER}  ) );

    /* Button Tags*/
    public static final int BUTTON_TYPE_SIMPLE = 9000;
    public static final int BUTTON_TYPE_SLIDE_HORIZONTAL = 9001;
    public static final int BUTTON_TYPE_SLIDE_VERTICAL = 9002;

    public static final List<Integer> buttonTags = new ArrayList<Integer>( Arrays.asList( new Integer[]{BUTTON_TYPE_SIMPLE, BUTTON_TYPE_SLIDE_HORIZONTAL, BUTTON_TYPE_SLIDE_VERTICAL }));

    /* Keys*/
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String SCREEN_WIDTH = "screen_width";
    public static final String SCREEN_HEIGHT = "screen_height";

    private String type;
    private float screenWidth, screenHeight;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args.containsKey(FRAGMENT_TYPE))
            type = args.getString(FRAGMENT_TYPE);
        else
            Log.e(TAG, "No type");

        if (args.containsKey(SCREEN_HEIGHT))
            screenHeight = args.getFloat(SCREEN_HEIGHT);
        else
            Log.e(TAG, "No height");

        if (args.containsKey(SCREEN_WIDTH))
            screenWidth = args.getFloat(SCREEN_WIDTH);
        else
            Log.e(TAG, "No width");
    }

    public String getType() {
        return type;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onSlideMenuOpen() {

    }

    @Override
    public void onSlideMenuClosed() {

    }

    @Override
    public void onSettingsPressed() {

    }

    @Override
    public void onControllerOptionPressed() {

    }


}
