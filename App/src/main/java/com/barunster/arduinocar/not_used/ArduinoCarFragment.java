
package com.barunster.arduinocar.not_used;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import braunster.btconnection.Command;

public class ArduinoCarFragment extends ArduinoLegoFragment implements View.OnTouchListener {

    // TODO Change auto connect mode to bluetooth device.

    private final String TAG = ArduinoCarFragment.class.getSimpleName();

    public static final String PREFS_SPEED_POINTS = "speed_points";
    public static final String SCREEN_WIDTH = "screen_width";
    public static final String BLUETOOTH_DEVICE_NAME = "bluetooth_device_name";

    // Bluetooth connection related
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private SimpleListAdapter simpleListAdapter;
    private boolean isScanning = false;
    private Dialog connectToDeviceDialog;

    private ArduinoCarAppObj app;

    // Views
    private View mainView;
    private ImageView btnStickL, btnStickR;
    private TextView  txtSpeedR, txtSpeedL, txtPoints;
    private ProgressBar progressBar;

    private float  screenHeight, startingY, point;
    private int stickSize, speedPoints;

    // For formatting a float to have only one number after the decimal point
    private DecimalFormat df = new DecimalFormat("0.0");

    // Extras
    private Bundle extras;

    @Override
    public void setArguments(Bundle args) {
        extras = args;
        super.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();

        // Get Window Size
        if (savedInstanceState != null)
            screenHeight = savedInstanceState.getFloat(ArduinoLegoFragment.SCREEN_HEIGHT);
        else if (extras != null)
            screenHeight = getScreenHeight();

        else Toast.makeText(getActivity(), "No screen width", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(ArduinoLegoFragment.SCREEN_HEIGHT, screenHeight);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_stick, null);

        // Initialize Views
        txtPoints = (TextView) mainView.findViewById(R.id.txt_points);
        txtSpeedR = (TextView) mainView.findViewById(R.id.txt_r_speed);
        txtSpeedL = (TextView) mainView.findViewById(R.id.txt_l_speed);

        progressBar = (ProgressBar) mainView.findViewById(R.id.progressBar);
        btnStickL = (ImageView) mainView.findViewById(R.id.btn_stick_left);
        btnStickR = (ImageView) mainView.findViewById(R.id.btn_stick_right);

        btnStickL.setOnTouchListener(this);
        btnStickR.setOnTouchListener(this);

        app.getConnection().setContext(getActivity())
        ;//        connection.openScanAndConnectDialog(new BTConnection.DialogResultListener() {
//            @Override
//            public void onResult(String s) {
//                Log.i(TAG, "Result: " + s);
//            }
//        });


        // Get the value the user already insert in his last session
        speedPoints = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(PREFS_SPEED_POINTS, 0) ;

        // If the system found a value
        if ( speedPoints != 0 )
        {
            txtPoints.setText(String.valueOf(speedPoints));

            point = Float.parseFloat(df.format((screenHeight / 2) / speedPoints));
        }
        else
        {
            speedPoints = 200;

            point = Float.parseFloat(df.format((screenHeight / 2) / speedPoints));
            // TODO handle no points.
        }

        return mainView;
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        initButtons();

        // Get the position of the view in the screen
        btnStickL.post(new Runnable() {
            @Override
            public void run() {
                startingY = btnStickL.getY();
                stickSize = btnStickL.getMeasuredHeight();
            }
        });

        super.onResume();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        String direction = "", motor = "";

        Log.d(TAG, "D: " + ( (  ((int) motionEvent.getRawY()) - (stickSize/2)) % 2));


        if (view.getId() == R.id.btn_stick_right)
            motor = "R";
        else motor = "L";

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_MOVE:

                if (view.getId() == R.id.btn_stick_right)
                    btnStickR.setY( motionEvent.getRawY() - (stickSize/2));
                else btnStickL.setY( motionEvent.getRawY() - (stickSize/2));

                if ( motionEvent.getRawY() - (stickSize/2) < startingY )
                {
                    direction = "F";
                }
                else
                {
                    direction = "B";
                }
                break;

            case MotionEvent.ACTION_UP:

                // Reset all position's
                if (view.getId() == R.id.btn_stick_right)
                {
                    btnStickR.setY(startingY);
                    txtSpeedR.setText("0");
                }
                else
                {
                    btnStickL.setY(startingY);
                    txtSpeedL.setText("0");
                }

                app.getConnection().write(String.valueOf(Command.STOP) + motor );
//                if (!app.getConnection().write( String.valueOf(Command.STOP) + motor ) )
//                    Toast.makeText(getActivity(), "The Application isn't connected to any device.", Toast.LENGTH_LONG).show();

                break;
        }

        if ( !motor.equals("") && !direction.equals("") && ( ((int) motionEvent.getRawY()) - (stickSize/2) ) % 2 == 0)
        {
            app.getConnection().write(motor + direction + getSpeed((int) motionEvent.getRawY()));

//            if( !app.getConnection().write(motor + direcion + getSpeed((int) motionEvent.getRawY())) )
//                Toast.makeText(getActivity(), "The Application isn't connected to any device.", Toast.LENGTH_LONG).show();t


            if (motor.equals("R"))
                txtSpeedR.setText(getSpeed( (int) motionEvent.getRawY() ));
            else
                txtSpeedL.setText(getSpeed( (int) motionEvent.getRawY() ));
        }

        return true;
    }

    private void initButtons(){

        txtPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create a dialog for entering the points
                final Dialog dialog = new Dialog(getActivity());

                dialog.setContentView(R.layout.dialog_edit_text);
                dialog.setTitle("Set Points");

                // Enter Text
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text_dialog);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint("Points = 255 - MIN_SP");

                // Positive Button
                ( (Button) dialog.findViewById(R.id.btn_positive_dialog)).setText("Set");
                (dialog.findViewById(R.id.btn_positive_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (editText.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please enter some points", Toast.LENGTH_SHORT).show();
                        } else {

                            speedPoints = Integer.parseInt(((EditText) dialog.findViewById(R.id.edit_text_dialog)).getText().toString()) ;
                            txtPoints.setText( ((EditText) dialog.findViewById(R.id.edit_text_dialog)).getText().toString() );

                            // Save th user input fot next use of the application
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt(PREFS_SPEED_POINTS, speedPoints).commit();

                            point = Float.parseFloat(df.format((screenHeight / 2) / speedPoints));

                            dialog.dismiss();
                        }
                    }
                });

                // Cancel Button
                ( (Button) dialog.findViewById(R.id.btn_negative_dialog)).setText("Cancel");
                (dialog.findViewById(R.id.btn_negative_dialog) ).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private String getSpeed( int pos){

        int speed = 0;

        if ( pos > screenHeight/2 )
        {
           pos =  pos - ( (int) screenHeight / 2 )  ;
        }
        else
        {
            pos = ( (int) screenHeight / 2 ) - pos ;
        }

        speed = Math.round(pos / point);

        return  String.valueOf(speed);

    }

    @Override
    public void onConnected() {

        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDisconnected() {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnecting() {
        super.onConnecting();

        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }


}

