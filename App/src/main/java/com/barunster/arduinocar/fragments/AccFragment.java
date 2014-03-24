package com.barunster.arduinocar.fragments;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.AccelerometerHandler;

import braunster.btconnection.Command;


public class AccFragment extends ArduinoLegoFragment implements SensorEventListener, AccelerometerHandler.AccelerometerEventListener{

    private static final String TAG = AccFragment.class.getSimpleName();

    private AccelerometerHandler accHandler = AccelerometerHandler.getInstance();

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private ArduinoCarAppObj app;

    // Views
    private View mainView;
    private TextView txtMessage, txtX, txtY, txtZ;
    private TextView txtUp, txtDown, txtRight, txtLeft;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_acc, null);

        // Initializing Views
        txtMessage = (TextView) mainView.findViewById(R.id.txt_message_recived);
        txtX = (TextView) mainView.findViewById(R.id.txt_x_data);
        txtY = (TextView) mainView.findViewById(R.id.txt_y_data);
        txtZ = (TextView) mainView.findViewById(R.id.txt_z_data);

        txtUp = (TextView) mainView.findViewById(R.id.txt_up);
        txtDown = (TextView) mainView.findViewById(R.id.txt_down);
        txtRight = (TextView) mainView.findViewById(R.id.txt_right);
        txtLeft = (TextView) mainView.findViewById(R.id.txt_left);

        accHandler.setAccelerometerEventListener(this);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        accHandler.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        accHandler.unregister();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        txtX.setText( Float.toString( Math.round( sensorEvent.values[X] ) ) );
        txtY.setText( Float.toString( Math.round( sensorEvent.values[Y] ) ) );
        txtZ.setText( Float.toString( Math.round( sensorEvent.values[Z] ) ) );

        boolean isFORWARDorBACKWARD = true;
        boolean isRIGHTorLEFT = true;

        String msg = null;

        // Controlling Backwards
        if ( sensorEvent.values[X] < -2 )
        {
            if ( Math.round( sensorEvent.values[X] )  > -5 )
            {
//                connectedThread.write("4");
            }
            else if ( Math.round( sensorEvent.values[X] )  > -7 )
            {

//                connectedThread.write("5");

            }
            else
            {

//                connectedThread.write("6");

            }
        }
        // Controlling Forward
        else if ( Math.round( sensorEvent.values[Y] )  > 2 )
        {
            if ( Math.round( sensorEvent.values[Y] )  < 5 )
            {

//                connectedThread.write("1");

            }
            else if ( Math.round( sensorEvent.values[Y] )  < 7 )
            {
//                connectedThread.write("2");
            }
            else
            {
//                 connectedThread.write("3");
            }

        } else { isFORWARDorBACKWARD = false ; }

        // Controlling Left
        if ( Math.round( sensorEvent.values[1] ) < -2 )
        {
            if ( Math.round( sensorEvent.values[1] )  > -5 )
            {
//                connectedThread.write("7");
            }
            else if ( Math.round( sensorEvent.values[1] )  > -7 )
            {
//                connectedThread.write("8");
            }
            else if ( Math.round( sensorEvent.values[1] )  > - 9 )
            {
//                connectedThread.write("9");
            }
            else
            {
//                connectedThread.write("z");
            }
        }
        // Controlling Right
        else if ( Math.round( sensorEvent.values[1] )  > 2 )
        {

            if ( Math.round( sensorEvent.values[1] )   < 5 )
            {
//                connectedThread.write("A");
            }
            else if ( Math.round( sensorEvent.values[1] )  < 7 )
            {
//                connectedThread.write("B");
            }
            else if ( Math.round( sensorEvent.values[1] )  < 9 )
            {
//                connectedThread.write("C");
            }
            else
            {
//                connectedThread.write("X");
            }
        } else { isRIGHTorLEFT = false ; }

        if (!isFORWARDorBACKWARD && !isRIGHTorLEFT)
        {
//            connectedThread.write("0");
        }

        if (msg != null )
        {
            txtMessage.setText(msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onForward(int speed) {
        Log.d(TAG, "onForward");
        app.getConnection().write( "B" + String.valueOf(1) + String.valueOf(speed));

        txtUp.setTextColor(Color.GREEN);
        txtUp.setText(String.valueOf(speed));
        txtDown.setTextColor(Color.WHITE);
        txtDown.setText("Down");
    }

    @Override
    public void onBackwards(int speed) {
        Log.d(TAG, "onBackwards");
        app.getConnection().write( "B" + String.valueOf(0) + String.valueOf(speed));
        txtDown.setTextColor(Color.GREEN);
        txtDown.setText(String.valueOf(speed));
        txtUp.setTextColor(Color.WHITE);
        txtUp.setText("Up");
    }

    @Override
    public void onRight(int amount) {
        Log.d(TAG, "onRight");
        app.getConnection().write( "A" + String.valueOf(0) + String.valueOf(amount));
        txtRight.setTextColor(Color.GREEN);
        txtRight.setText(String.valueOf(amount));
        txtLeft.setTextColor(Color.WHITE);
        txtLeft.setText("Left");
    }

    @Override
    public void onLeft(int amount) {
        Log.d(TAG, "onLeft");
        app.getConnection().write( "A" + String.valueOf(1) + String.valueOf(amount));
        txtLeft.setTextColor(Color.GREEN);
        txtLeft.setText(String.valueOf(amount));
        txtRight.setTextColor(Color.WHITE);
        txtRight.setText("Right");
    }

    @Override
    public void onStraightAhead() {
        Log.d(TAG, "onStraightAhead");
        app.getConnection().write( String.valueOf(Command.STOP) + "A");
        txtLeft.setTextColor(Color.WHITE);
        txtRight.setTextColor(Color.WHITE);
        txtRight.setText("Right");
        txtLeft.setText("Left");
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped");
        app.getConnection().write( String.valueOf(Command.STOP) + "B");
        txtUp.setTextColor(Color.WHITE);
        txtDown.setTextColor(Color.WHITE);
        txtUp.setText("Up");
        txtDown.setText("Down");
    }

    @Override
    public void onChangeDeltas(float[] deltas) {
        txtX.setText( Float.toString( deltas[X] ) );
        txtY.setText( Float.toString( deltas[Y] ) );
        txtZ.setText( Float.toString( deltas[Z] ) );
    }
}

