package com.barunster.arduinocar.custom_controllers_obj;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by itzik on 3/20/14.
 */
public class AccelerometerHandler implements SensorEventListener {

    private static final String TAG = AccelerometerHandler.class.getSimpleName();

    private Sensor mSensorAcc;
    private SensorManager mSensorManager;

    private static AccelerometerHandler instance;

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private boolean isUpDown = true, isRightLeft = true, isRegistered = false;
    private int speed = 0;
    private float [] deltas = new float[3], lastDeltas = new float[3];
    private float pointF, pointB, pointRL;

    private String associatedChannel;

    // For formatting a float to have only one number after the decimal point
    private DecimalFormat df = new DecimalFormat("0.0");

    private AccelerometerEventListener accelerometerEventListener;

    public AccelerometerHandler(Context context){
        if (instance == null) {
            // Initializing the sensor
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

            Log.i(TAG, "Sensors list size: " + list.size());

            pointF = ArduinoLegoFragment.DEFAULT_SPEED_POINTS / 70f;
            pointB = ArduinoLegoFragment.DEFAULT_SPEED_POINTS / 25f;
            pointRL = ArduinoLegoFragment.DEFAULT_SPEED_POINTS / 70f;
            Log.i(TAG, "PointF: " + pointF + ", pintB: " + pointB + ", pointRL: " + pointRL);

            instance = this;
        }
    }

    public static AccelerometerHandler getInstance() {
        return instance;
    }

    public void register(){
        if (!isRegistered) {
            Log.d(TAG, "register accelerometer handler");
            mSensorManager.registerListener(AccelerometerHandler.this, mSensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
            isRegistered = true;
        }
    }

    public void unregister(){
        if (isRegistered)
        {
            Log.d(TAG, "unregister accelerometer handler");
            mSensorManager.unregisterListener(AccelerometerHandler.this);
            accelerometerEventListener = null;
            isRegistered = false;
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Last deltas
        lastDeltas[X] = deltas[X];
        lastDeltas[Y] = deltas[Y];
        lastDeltas[Z] = deltas[Z];

        // new deltas
        deltas[X] = Float.parseFloat(df.format(sensorEvent.values[X]));
        deltas[Y] = Float.parseFloat(df.format(sensorEvent.values[Y]));
        deltas[Z] = Float.parseFloat(df.format(sensorEvent.values[Z]));

         /* X, States: x = 8 - 10 is the neutral state, 10 > x > 3 && z > 0 is forward.*/
        /* Z X > 0 && z < 0 is backwards.*/
        /* Y, States: y = (-1) - 1 is the neutral state, 1 > y < 9 is right, -1 > y > -9 is left. */

        // Controlling Forward
        if ( deltas[X] <= 8f && deltas[X] >= 1f && deltas[Z] >= 1.5f )
        {
            isUpDown = true;

            int x = ((int) (deltas[X] * 10)) - 10;
            x = (70 - x);

            Log.d(TAG, "x = " + x + " x*pointF = " + (x*pointF));

            if (accelerometerEventListener != null)
            {
                accelerometerEventListener.onForward((int) (x * pointF));
            }
            else Log.e(TAG, "No accelerometer event listener");
        }
        // Controlling Backwards
        else if ( deltas[X] >= 7f   && deltas[X] <= 9.5f && deltas[Z] <=  -1f)
        {
            isUpDown = true;

            int x = ((int) (deltas[X] * 10)) - 60;
            x = (25 - x);

            Log.d(TAG, "x = " + x + " x*pointB = " + (x*pointB));

            if (accelerometerEventListener != null)
            {
                accelerometerEventListener.onBackwards((int) (x * pointB));
            }
            else Log.e(TAG, "No accelerometer event listener");
        }
        else if (isUpDown)
        {
            isUpDown = false ;

            if (accelerometerEventListener != null)
            {
                accelerometerEventListener.onStopped();
            }
            else Log.e(TAG, "No accelerometer event listener");
        }

        if (lastDeltas[Y] != deltas[Y])
        {
            // Controlling Left
            if ( deltas[Y] <= -1.5f && deltas[Y] >= -8f)
            {
                isRightLeft = true;

                int x = ((int) (Math.abs(deltas[Y]) * 10)) - 15;

                Log.d(TAG, "x = " + x + " x*pointRL = " + (x*pointRL));

                if (accelerometerEventListener != null)
                {
                    accelerometerEventListener.onLeft((int) (x*pointRL));
                }
                else Log.e(TAG, "No accelerometer event listener");
            }
            // Controlling Right
            else if ( deltas[Y]  >= 1.5f && deltas[Y] <= 8f)
            {

                isRightLeft = true;

                int x = ((int) (deltas[Y] * 10)) - 15;

                Log.d(TAG, "x = " + x + " x*pointRL = " + (x*pointRL));

                if (accelerometerEventListener != null)
                {
                    accelerometerEventListener.onRight((int) (x*pointRL));
                }
                else Log.e(TAG, "No accelerometer event listener");
            }
            else if (isRightLeft)
            {
                isRightLeft = false ;

                if (accelerometerEventListener != null)
                {
                    accelerometerEventListener.onStraightAhead();
                }
                else Log.e(TAG, "No accelerometer event listener");
            }
        }

        //TODO stop
        if (accelerometerEventListener != null)
        {
            accelerometerEventListener.onChangeDeltas(deltas);
        }
        else Log.e(TAG, "No accelerometer event listener");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface AccelerometerEventListener{
        public void onForward(int speed);
        public void onBackwards(int speed);
        public void onRight(int amount);
        public void onLeft(int amount);

        public void onStopped();
        public void onStraightAhead();

        public void onChangeDeltas(float [] deltas);
    }

    public float[] getDeltas() {
        return deltas;
    }

    /* Getters & Setters*/
    public void setAccelerometerEventListener(AccelerometerEventListener accelerometerEventListener) {
        this.accelerometerEventListener = accelerometerEventListener;
    }

    public void setAssociatedChannel(String associatedChannel) {
        this.associatedChannel = associatedChannel;
    }

    public String getAssociatedChannel() {
        return associatedChannel;
    }
}
