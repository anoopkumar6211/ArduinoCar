
package com.barunster.arduinocar;

import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import braunster.btconnection.BTConnection;
import braunster.btconnection.Command;

// GitHub Chceck
public class ArduinoCarFragment extends Fragment implements View.OnTouchListener {

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
    private BTConnection connection = new BTConnection();

    // Views
    private View mainView;
    private ImageView btnStickL, btnStickR;
    private TextView  txtSpeedR, txtSpeedL, txtPoints;
    private Button btnToggleConnection;
    private ProgressBar progressBar;

    private float  screenWidth, startingY, point;
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

        // Get Window Size
        if (savedInstanceState != null)
            screenWidth = savedInstanceState.getFloat(SCREEN_WIDTH);
        else if (extras != null)
            screenWidth = extras.getFloat(SCREEN_WIDTH);

        else Toast.makeText(getActivity(), "No screen width", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(SCREEN_WIDTH, screenWidth);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_stick, null);

        // Initialize Views
        txtPoints = (TextView) mainView.findViewById(R.id.txt_points);
        txtSpeedR = (TextView) mainView.findViewById(R.id.txt_r_speed);
        txtSpeedL = (TextView) mainView.findViewById(R.id.txt_l_speed);

        btnToggleConnection = (Button) mainView.findViewById(R.id.btn_toggle_connection);
        progressBar = (ProgressBar) mainView.findViewById(R.id.progressBar);
        btnStickL = (ImageView) mainView.findViewById(R.id.btn_stick_left);
        btnStickR = (ImageView) mainView.findViewById(R.id.btn_stick_right);

        btnStickL.setOnTouchListener(this);
        btnStickR.setOnTouchListener(this);

        setToDisconnected();


        connection.setContext(getActivity())
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

            point = Float.parseFloat(df.format((screenWidth / 2) / speedPoints));
        }
        else
        {
            speedPoints = 200;

            point = Float.parseFloat(df.format((screenWidth / 2) / speedPoints));
            // TODO handle no points.
        }

        return mainView;
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(connectivityChangesReceiver);

        connection.close();

        super.onPause();
    }

    @Override
    public void onResume() {

        btnToggleConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnToggleConnection.getTag().equals(BTConnection.CONNECTED))
                {
                    // Disconnect/**/
                    connection.close();

                    setToDisconnected();

                    bluetoothDevice = null;

                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (!bluetoothAdapter.isEnabled())
                        Toast.makeText(getActivity(), "Please enable bluetooth", Toast.LENGTH_SHORT).show();
                    else
                    {
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(BLUETOOTH_DEVICE_NAME))
                        {
                            ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(BLUETOOTH_DEVICE_NAME, ""));
                        }
                        else
                        {
                            connectToDeviceDialog = new Dialog(getActivity());

                            connectToDeviceDialog.setContentView(R.layout.dialog_devices_list);
                            connectToDeviceDialog.setTitle("Device List:");

                            connectToDeviceDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    connectToDeviceDialog.dismiss();

                                    if (bluetoothAdapter.isDiscovering())
                                        bluetoothAdapter.cancelDiscovery();
                                }
                            });

                            connectToDeviceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    isScanning = false;
                                }
                            });

                            ((ListView)connectToDeviceDialog.findViewById(R.id.list_devices)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (bluetoothDevices != null)                                    {
                                        bluetoothDevice = bluetoothDevices.get(position);

                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(BLUETOOTH_DEVICE_NAME, bluetoothDevice.getName()).commit();

                                        ConnectToDevice(bluetoothDevice.getName());

                                        connectToDeviceDialog.dismiss();
                                    }
                                }
                            });

                            connectToDeviceDialog.show();

                            bluetoothAdapter.startDiscovery();

                            isScanning = true;
                        }

                    }
                }

            }
        });

        // Get the position of the view in the screen
        btnStickL.post(new Runnable() {
            @Override
            public void run() {
                startingY = btnStickL.getY();
                stickSize = btnStickL.getMeasuredHeight();
            }
        });

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

                            point = Float.parseFloat(df.format((screenWidth / 2) / speedPoints));

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

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        // Reconnect to device
        if (bluetoothDevice != null)
            ConnectToDevice(bluetoothDevice.getName());
        else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(BLUETOOTH_DEVICE_NAME))
            ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(BLUETOOTH_DEVICE_NAME, ""));

        getActivity().registerReceiver(connectivityChangesReceiver, filter);
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

                writeToArduino( String.valueOf(Command.STOP) + motor );

                break;
        }

        if ( !motor.equals("") && !direction.equals("") && ( ((int) motionEvent.getRawY()) - (stickSize/2) ) % 2 == 0)
        {
            writeToArduino(motor + direction + getSpeed((int) motionEvent.getRawY()));

            if (motor.equals("R"))
                txtSpeedR.setText(getSpeed( (int) motionEvent.getRawY() ));
            else
                txtSpeedL.setText(getSpeed( (int) motionEvent.getRawY() ));
        }

        return true;
    }

    void writeToArduino(final String text){

        // Check to see if the application is connected via bluetooth
        if ( connection.isConnected())
        {
            connection.write(text);
        }


    }

    private String getSpeed( int pos){

        int speed = 0;

        if ( pos > screenWidth/2 )
        {
           pos =  pos - ( (int) screenWidth / 2 )  ;
        }
        else
        {
            pos = ( (int) screenWidth / 2 ) - pos ;
        }

        speed = Math.round(pos / point);

        return  String.valueOf(speed);

    }

    private void ConnectToDevice(String deviceName){

        Toast.makeText(getActivity(), "Connecting...Device Name: " + deviceName, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        connection.setConnectionStateChangeListener(new BTConnection.ConnectionStateChangeListener() {
            @Override
            public void onConnected(int connectionType, Object tag) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "Connected!." , Toast.LENGTH_LONG).show();
                setToConnected();
            }

            @Override
            public void onConnectionChangeState(int connectionType, String state) {

            }

            @Override
            public void onConnectionFailed(String issue, Object obj) {
                progressBar.setVisibility(View.INVISIBLE);
                setToDisconnected();
                Toast.makeText(getActivity(), "Connection Failed, Issue: " + issue , Toast.LENGTH_LONG).show();
            }
        });

        connection.setOnConnectionLost(new BTConnection.onConnectionLostListener() {
            @Override
            public void onConnectionLost(int i, String issue) {
                Log.d(TAG, "OnConnectionLost, Issue: " + issue);

                setToDisconnected();

                Toast.makeText(getActivity(), "Connection is lost, Issue: " + issue , Toast.LENGTH_LONG).show();
            }
        });

        connection.start(deviceName);

//        if (bluetoothDevice!= null)
//        {
//            connectAsClientThread = new ConnectAsClientThread(bluetoothDevice);
//            connectAsClientThread.setHandler(handler);
//            connectAsClientThread.start();
//
//            return true;
//        }
//        else return false;
    }

    /* Connectivity Changes Receiver */
    BroadcastReceiver connectivityChangesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_UUID.equals(action)) {

                if (isScanning)
                {
                    // Get the BluetoothDevice object from the Intent and add it to the list.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevices.add(device);

                    if(bluetoothDevices.size() == 1)
                    {
                        // Hide progress bar
                        connectToDeviceDialog.findViewById(R.id.progress_scan).setVisibility(View.GONE);
                        // Show list
                        connectToDeviceDialog.findViewById(R.id.list_devices).setVisibility(View.VISIBLE);

                        simpleListAdapter = new SimpleListAdapter(getActivity());
                        ((ListView) connectToDeviceDialog.findViewById(R.id.list_devices)).setAdapter(simpleListAdapter);
                    }

                    simpleListAdapter.addRow(device.getName());

                    Log.i(TAG, "Scanning Found Device Found Bluetooth Device, Name: " + (device.getName()));
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Log.i(TAG, "Discovery Started");

                // restart the list.
                if (isScanning)
                    bluetoothDevices = new ArrayList<BluetoothDevice>();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.i(TAG, "Discovery Finished");

                if (isScanning)
                {
                    isScanning = !isScanning;

                    if (bluetoothDevices.size() == 0)
                    {
                        connectToDeviceDialog.dismiss();
                        Toast.makeText(getActivity(), "Scanning finished and no device found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void setToConnected(){
        btnToggleConnection.setTag(BTConnection.CONNECTED);
        btnToggleConnection.setText("Disconnect");
    }

    private void setToDisconnected(){
        btnToggleConnection.setTag(BTConnection.DISCONNECTED);
        btnToggleConnection.setText("Connect");
    }
}

