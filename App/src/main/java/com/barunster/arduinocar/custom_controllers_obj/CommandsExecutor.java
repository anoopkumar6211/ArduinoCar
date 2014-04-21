package com.barunster.arduinocar.custom_controllers_obj;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.views.SlideButtonLayout;

import braunster.btconnection.BTConnection;
import braunster.btconnection.Command;

/**
 * Created by itzik on 4/17/2014.
 */
public class CommandsExecutor implements View.OnClickListener, SlideButtonLayout.SlideButtonListener, AccelerometerHandler.AccelerometerEventListener , CustomDBManager.OnControllerDataChanged{

    private static final String TAG = CommandsExecutor.class.getSimpleName();
    private static final boolean DEBUG = true;

    private Context context;
    private BTConnection connection;
    private CustomDBManager customControllerManager = CustomDBManager.getInstance();
    private long controllerId;
    private CustomController customController;

    public CommandsExecutor(Context context, BTConnection connection){
        this.connection = connection;
        this.context = context;

        customController = customControllerManager.getControllerById(controllerId);

        customControllerManager.setOnControllerDateChanged(this);
    }

    public Context getContext() {
        return context;
    }

    public void setControllerId(long controllerId) {
        this.controllerId = controllerId;

        customController = customControllerManager.getControllerById(controllerId);
    }

    public void setConnection(BTConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onClick(View v) {

        if (DEBUG)
            Log.d(TAG, "OnClick");

        if (customController == null) {
            if(DEBUG)
                Log.e(TAG, "No custom controller found in the db");
            return;
        }

        // If not editing do nothing when on click is called on SlidingLayout.
        if (v instanceof SlideButtonLayout)
            return;
        else if (connection == null || !connection.isConnected())
        {
            Toast.makeText(getContext(), " No connection...", Toast.LENGTH_SHORT).show();
        }
        else if ( customController.getCustomButtonById(v.getId()) != null && customController.getCustomButtonById(v.getId()).getCustomCommand() != null)
        {
            CustomCommand customCommand = customController.getCustomButtonById(v.getId()).getCustomCommand();

            if (customCommand == null)
                return;

            switch (customCommand.getType())
            {
                case CustomCommand.TYPE_ON_OFF:
                    connection.write(String.valueOf(Command.TOGGLE_STATE) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_DIRECTION_LEFT:
                    connection.write(String.valueOf(Command.DIRECTION_LEFT) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_DIRECTION_RIGHT:
                    connection.write(String.valueOf(Command.DIRECTION_RIGHT) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_TOGGLE_DIRECTION:
                    connection.write(String.valueOf(Command.TOGGLE_DIRECTION) + customCommand.getChannel());
                    break;

                case CustomCommand.TYPE_SPEED_UP:
                    connection.write(String.valueOf(Command.SPEED_UP) + customCommand.getChannel() + customCommand.getExtraSpeedData());
                    break;

                case CustomCommand.TYPE_SPEED_DOWN:
                    connection.write(String.valueOf(Command.SPEED_DOWN) + customCommand.getChannel() + customCommand.getExtraSpeedData());
                    break;

                case CustomCommand.TYPE_ACC_CONTROL:

                    if (AccelerometerHandler.getInstance().isRegistered())
                        AccelerometerHandler.getInstance().unregister();
                    else {
                        AccelerometerHandler.getInstance().register();
                        AccelerometerHandler.getInstance().setAccelerometerEventListener(this);
                        AccelerometerHandler.getInstance().setAssociatedChannel(customCommand.getChannel());
                    }

                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getContext().getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getContext(), "Press on the EditButton button and then select a button to edit.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onSlideStop(SlideButtonLayout slideButtonLayout, int pos) {
        if (DEBUG)
            Log.d(TAG, "onSlideStop");

        if ( customController.getCustomButtonById(slideButtonLayout.getId()) != null && customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null)
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
                Log.d(TAG, "Button has command, Type: " + getContext().getResources().getString(customCommand.getType()));
        }

        return /*!isEditing*/ true; // TODO get edit mode from controller layout
    }

    @Override
    public boolean onSlideStarted(SlideButtonLayout slideButtonLayout) {
        if (DEBUG)
            Log.d(TAG, "onSlideStarted");

        return /*!isEditing*/ true; // TODO get edit mode from controller layout
    }

    @Override
    public boolean onSliding(SlideButtonLayout slideButtonLayout, int direction, int speed) {
        if (DEBUG)
            Log.d(TAG, "onSliding");

        if ( customController.getCustomButtonById(slideButtonLayout.getId()) != null && customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand() != null) {
            CustomCommand customCommand = customController.getCustomButtonById(slideButtonLayout.getId()).getCustomCommand();

            if (DEBUG)
                Log.d(TAG, "onSliding, Direction: " + String.valueOf(direction) + " Speed: " + speed);

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getContext().getResources().getString(customCommand.getType()));

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

        return /*!isEditing*/ true; // TODO get edit mode from controller layout
    }

    @Override
    public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int position) {
        if (DEBUG)
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
                    connection.write( customCommand.getChannel() + String.valueOf(direction) + String.valueOf(position));
                    break;
            }

            if (DEBUG)
                Log.d(TAG, "Button has command, Type: " + getContext().getResources().getString(customCommand.getType()));
        }
        else
            Toast.makeText(getContext(), "Press long for setting command", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onForward(int speed) {
        if (DEBUG)
            Log.d(TAG, "onForward");
    }

    @Override
    public void onBackwards(int speed) {
        if (DEBUG)
            Log.d(TAG, "onBackwards");
    }

    @Override
    public void onRight(int amount) {
        if (DEBUG)
            Log.d(TAG, "onRight");
        connection.write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(0) + String.valueOf(amount));
    }

    @Override
    public void onLeft(int amount) {
        if (DEBUG)
            Log.d(TAG, "onLeft");

        connection.write( AccelerometerHandler.getInstance().getAssociatedChannel() + String.valueOf(1) + String.valueOf(amount));
    }

    @Override
    public void onStopped() {
        if (DEBUG)
            Log.d(TAG, "onStopped");
    }

    @Override
    public void onStraightAhead() {
        if (DEBUG)
            Log.d(TAG, "onStraightAhead");

        connection.write( String.valueOf(Command.STOP) + AccelerometerHandler.getInstance().getAssociatedChannel());
    }

    @Override
    public void onChangeDeltas(float[] deltas) {
        if (DEBUG)
            Log.d(TAG, "onChangeDeltas");
    }

    @Override
    public void onChanged() {
        customController = customControllerManager.getControllerById(controllerId);
    }
}
