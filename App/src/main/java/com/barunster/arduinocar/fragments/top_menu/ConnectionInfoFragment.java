package com.barunster.arduinocar.fragments.top_menu;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.util.ArrayList;
import java.util.List;

import braunster.btconnection.BTConnection;

/**
 * Created by itzik on 3/24/14.
 */
public class ConnectionInfoFragment extends MenuFragment {

    private static final String TAG = ConnectionInfoFragment.class.getSimpleName();

    public static final String PREFS_AUTO_CONNECT = "prefs_auto_connect";
    public static final String PREFS_AUTO_DISCONNECT = "prefs_auto_disconnect";

    /*Views*/
    private View mainView;
    private Button btnScan, btnConnect;
    private ProgressBar progressBarScan, progressBarConnection;
    private ListView listScanResult;
    private CheckBox chkAutoConnect, chkAutoDisconnect;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

    private ArduinoCarAppObj app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_connection_info , null);

        initViews();

        initListView();
        initButtons();

        initPrefs();

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (app.getConnection().isConnected())
            btnConnect.setText(getResources().getString(R.string.connected));
        else
            btnConnect.setText(getResources().getString(R.string.disconnected));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void ConnectToDevice(String deviceName){

        BTConnection.getBluetoothAdapter().cancelDiscovery();

        Toast.makeText(getActivity(), "Connecting...Device Name: " + deviceName, Toast.LENGTH_SHORT).show();

        app.getConnection().setConnectionStateChangeListener(new BTConnection.ConnectionStateChangeListener() {
            @Override
            public void onConnected(int connectionType, Object tag) {
                Toast.makeText(getActivity(), "Connected!.", Toast.LENGTH_LONG).show();
                btnConnect.setText(getResources().getString(R.string.connected));

                progressBarConnection.setVisibility(View.GONE);
            }

            @Override
            public void onConnectionChangeState(int connectionType, String state) {

            }

            @Override
            public void onConnectionFailed(String issue, Object obj) {
                Toast.makeText(getActivity(), "Connection Failed, Issue: " + issue, Toast.LENGTH_LONG).show();

                progressBarConnection.setVisibility(View.GONE);
            }
        });

        app.getConnection().setOnConnectionLost(new BTConnection.onConnectionLostListener() {
            @Override
            public void onConnectionLost(int i, String issue) {
                Log.d(TAG, "OnConnectionLost, Issue: " + issue);

                progressBarConnection.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Connection is lost, Issue: " + issue, Toast.LENGTH_LONG).show();
            }
        });

        app.getConnection().start(deviceName);
    }

    private void initViews() {
        btnScan = (Button) mainView.findViewById(R.id.btn_start_stop_scan);
        btnConnect = (Button) mainView.findViewById(R.id.btn_connect);

        progressBarScan = (ProgressBar) mainView.findViewById(R.id.progress_scan);
        progressBarConnection = (ProgressBar) mainView.findViewById(R.id.progress_connect);
        listScanResult = (ListView) mainView.findViewById(R.id.list_devices);
        chkAutoConnect = (CheckBox) mainView.findViewById(R.id.check_auto_connect);
        chkAutoDisconnect = (CheckBox) mainView.findViewById(R.id.check_auto_disconnect);
    }

    private void initListView() {

        listScanResult.setAdapter(new SimpleListAdapter(getActivity()));
        ((SimpleListAdapter)listScanResult.getAdapter()).setTextColor(Color.WHITE);

        listScanResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(MainActivity.BLUETOOTH_DEVICE_NAME, bluetoothDevices.get(position).getName()).commit();

                ConnectToDevice(bluetoothDevices.get(position).getName());
            }
        });
    }

    private void initButtons() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((Button) v).getText().toString().equals(getResources().getString(R.string.scan))) {
                    if (BTConnection.getBluetoothAdapter().isEnabled()) {

                        bluetoothDevices = new ArrayList<BluetoothDevice>();

                        if ( !app.getConnection().isConnected())
                        {
                            app.getConnection().preformScan(new BTConnection.BluetoothScanningListener() {
                                @Override
                                public void onDiscoveryFinished() {
                                    progressBarScan.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Scan is finished" + (bluetoothDevices.size() == 0 ? ", No Device Found." : "." ), Toast.LENGTH_SHORT).show();
                                    btnScan.setText(getResources().getString(R.string.scan));
                                }

                                @Override
                                public void onDeviceFound(BluetoothDevice bluetoothDevice) {
                                    Log.d(TAG, "Device Found");

                                    // first device found
                                    if (bluetoothDevices.size() == 0)
                                    {
                                        listScanResult.setVisibility(View.VISIBLE);
                                    }

                                    ((SimpleListAdapter) listScanResult.getAdapter()).addRow(bluetoothDevice.getName());
                                    bluetoothDevices.add(bluetoothDevice);
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Cant scan while connected to another device.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        listScanResult.setVisibility(View.GONE);
                        progressBarScan.setVisibility(View.VISIBLE);

                        ((Button) v).setText(getResources().getString(R.string.cancel));

                    } else
                        Toast.makeText(getActivity(), "Please enable bluetooth", Toast.LENGTH_SHORT).show();

                } else {
                    ((Button) v).setText(getResources().getString(R.string.scan));
                    progressBarScan.setVisibility(View.GONE);
                }

            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button) v).getText().toString().equals(getResources().getString(R.string.connected))) {
                    ((Button) v).setText(getResources().getString(R.string.disconnected));
                    app.getConnection().close();
                    Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
                } else if (((Button) v).getText().toString().equals(getResources().getString(R.string.disconnected))) {
                    if (BTConnection.getBluetoothAdapter().isEnabled()) {
                        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(MainActivity.BLUETOOTH_DEVICE_NAME)) {
                            ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MainActivity.BLUETOOTH_DEVICE_NAME, ""));
                        } else {
                            Toast.makeText(getActivity(), "No Device is saved in preference", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((Button) v).setText(getResources().getString(R.string.connecting));

                        progressBarConnection.setVisibility(View.VISIBLE);
                    } else
                        Toast.makeText(getActivity(), "Please enable bluetooth", Toast.LENGTH_SHORT).show();

                } else if (((Button) v).getText().toString().equals(getResources().getString(R.string.connecting))) {
                    ((Button) v).setText(getResources().getString(R.string.disconnected));

                    progressBarConnection.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initPrefs(){
        chkAutoConnect.setChecked(ArduinoCarAppObj.prefs.getBoolean(PREFS_AUTO_CONNECT, false));
        chkAutoDisconnect.setChecked(ArduinoCarAppObj.prefs.getBoolean(PREFS_AUTO_DISCONNECT, false));

        chkAutoConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArduinoCarAppObj.prefs.edit().putBoolean(PREFS_AUTO_CONNECT, isChecked).commit();
            }
        });

        chkAutoDisconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArduinoCarAppObj.prefs.edit().putBoolean(PREFS_AUTO_DISCONNECT, isChecked).commit();
            }
        });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && app != null && btnConnect != null)
        {
            Log.d(TAG, "visible");
            if (app.getConnection().isConnected())
                btnConnect.setText(getResources().getString(R.string.connected));
            else
                btnConnect.setText(getResources().getString(R.string.disconnected));
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
