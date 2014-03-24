package com.barunster.arduinocar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.fragments.AccFragment;
import com.barunster.arduinocar.fragments.ArduinoCarFragment;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.EngineControlFragment;
import com.barunster.arduinocar.fragments.MultiEngineControlFragment;
import com.barunster.arduinocar.fragments.ServoControlFragment;
import com.barunster.arduinocar.fragments.TopMenuFragment;
import com.barunster.arduinocar.interfaces.SlideMenuListener;
import com.barunster.arduinocar.views.SlideFadeMenu;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

import braunster.btconnection.BTConnection;

public class MainActivity extends Activity implements SlideMenuListener {

    // TODO fix animation maybe problem with smaller screens fromXdelte to Xdelta
    // TODO xml data transfer or json
    // TODO combainnig several commands from several channels to one command

    private final String TAG = MainActivity.class.getSimpleName();

    public static final String BLUETOOTH_DEVICE_NAME = "bluetooth_device_name";

    // Bluetooth connection related
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private SimpleListAdapter simpleListAdapter;
    private boolean isScanning = false;
    private Dialog connectToDeviceDialog;

    private ArduinoCarAppObj app;

    private int MeasuredWidth, MeasuredHeight;

    /* Views*/
    private LinearLayout linearControl;
    private Button btnToggleConnection, btnChangeFrag, btnSetting;
    private SlidingUpPanelLayout slidingUpPanelLayoutMain, slidingUpPanelLayoutContainer;

    /* Popups */
    private PopupWindow popupSlideFadeMenu;

    /* Animations */
    Animation fadeOut, fadeIn;
    private FragmentTransaction ft;

    /* Fragments*/
    private ArduinoLegoFragment fragment;
    private TopMenuFragment topMenuFragment;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_main_sliding_panel);

        app = (ArduinoCarAppObj) getApplication();

        fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);

        getDisplaySize();

        if (savedInstanceState == null) {
            createFragment(ArduinoLegoFragment.FRAGMENT_TYPE_CUSTOM_CONTROLLER);
        }
        else
        {
            this.savedInstanceState = savedInstanceState;
        }

        linearControl = (LinearLayout) findViewById(R.id.linear_control);
        linearControl.setVisibility(View.GONE);

        app.setSlideFadeMenu(new SlideFadeMenu(this));

        app.getSlideFadeMenu().setSlideMenuListener(this);

        ((FrameLayout) findViewById(R.id.container)).addView(app.getSlideFadeMenu()); // TODO chanege

        btnToggleConnection = (Button) findViewById(R.id.btn_toggle_connection);
        btnChangeFrag = (Button) findViewById(R.id.btn_change_frag);

        initSlidingUpPanel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initControlLinear();

        // Reconnect to device
        if (bluetoothDevice != null)
            ConnectToDevice(bluetoothDevice.getName());
        else if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).contains(BLUETOOTH_DEVICE_NAME))
            ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(BLUETOOTH_DEVICE_NAME, ""));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(connectivityChangesReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(connectivityChangesReceiver);

        app.getConnection().close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragment.getType());

        super.onSaveInstanceState(outState);
    }

    private void ConnectToDevice(String deviceName){

        if (fragment == null)
        {
            createFragment(savedInstanceState.getString(ArduinoLegoFragment.FRAGMENT_TYPE));
        }

        fragment.onConnecting();
        btnToggleConnection.setText(R.string.connecting);
        btnToggleConnection.setTag(BTConnection.CONNECTING);

        Toast.makeText(this, "Connecting...Device Name: " + deviceName, Toast.LENGTH_SHORT).show();

        app.getConnection().setConnectionStateChangeListener(new BTConnection.ConnectionStateChangeListener() {
            @Override
            public void onConnected(int connectionType, Object tag) {
                Toast.makeText(MainActivity.this, "Connected!.", Toast.LENGTH_LONG).show();

                btnToggleConnection.setText(R.string.connected);
                fragment.onConnected();
                btnToggleConnection.setTag(BTConnection.CONNECTED);
            }

            @Override
            public void onConnectionChangeState(int connectionType, String state) {

            }

            @Override
            public void onConnectionFailed(String issue, Object obj) {
                fragment.onDisconnected();
                Toast.makeText(MainActivity.this, "Connection Failed, Issue: " + issue, Toast.LENGTH_LONG).show();

                btnToggleConnection.setText(R.string.disconnected);
                fragment.onDisconnected();
                btnToggleConnection.setTag(BTConnection.DISCONNECTED);
            }
        });

        app.getConnection().setOnConnectionLost(new BTConnection.onConnectionLostListener() {
            @Override
            public void onConnectionLost(int i, String issue) {
                Log.d(TAG, "OnConnectionLost, Issue: " + issue);

                Toast.makeText(MainActivity.this, "Connection is lost, Issue: " + issue, Toast.LENGTH_LONG).show();

                btnToggleConnection.setText(R.string.disconnected);
                fragment.onDisconnected();
                btnToggleConnection.setTag(BTConnection.DISCONNECTED);
            }
        });

        app.getConnection().start(deviceName);
    }

    private void  initControlLinear(){

        app.getSlideFadeMenu().setOnToggleConnectionStateClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearControl.getAlpha() != 0.0f)
                    if (btnToggleConnection.getTag().equals(BTConnection.CONNECTED)) {
                        // Disconnect/**/
                        app.getConnection().close();

                        fragment.onDisconnected();

                        bluetoothDevice = null;

                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

                        btnToggleConnection.setText(R.string.disconnected);

                        btnToggleConnection.setTag(BTConnection.DISCONNECTED);
                    } else if (btnToggleConnection.getTag().equals(BTConnection.DISCONNECTED)) {
                        if (!bluetoothAdapter.isEnabled())
                            Toast.makeText(MainActivity.this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
                        else {
                            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).contains(BLUETOOTH_DEVICE_NAME)) {
                                ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(BLUETOOTH_DEVICE_NAME, ""));
                            } else {
                                connectToDeviceDialog = new Dialog(MainActivity.this);

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

                                ((ListView) connectToDeviceDialog.findViewById(R.id.list_devices)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (bluetoothDevices != null) {
                                            bluetoothDevice = bluetoothDevices.get(position);

                                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString(BLUETOOTH_DEVICE_NAME, bluetoothDevice.getName()).commit();

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
                    } else {

                    }
            }
        });

        app.getSlideFadeMenu().setOnControllerSelectionClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "setOnControllerSettingsClicked");

                showPopupSelectController();
            }
        });

        app.getSlideFadeMenu().setOnAppSettingClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Settings
            }
        });

    }

    private void initSlidingUpPanel(){
        slidingUpPanelLayoutMain = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_main);
        slidingUpPanelLayoutMain.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));

        createSlidePanelFragment();

        slidingUpPanelLayoutMain.setEnableDragViewTouchEvents(true);


//        slidingUpPanelLayout.setPanelHeight(200);
        slidingUpPanelLayoutMain.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");


            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");

            }
        });

        slidingUpPanelLayoutContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_container);
//        slidingUpPanelLayoutContainer.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));

        createSlidePanelFragment();
        slidingUpPanelLayoutContainer.setEnableDragViewTouchEvents(true);


//        slidingUpPanelLayout.setPanelHeight(200);
        slidingUpPanelLayoutContainer.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded, Container");


            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed, Container");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored, Container.");

            }
        });
    }
    private void animateControlLinear(){
        Log.d(TAG, "animate control, Alpha = " + linearControl.getAlpha() + " Tag = " + linearControl.getTag());

        if (linearControl.getAlpha() == 1.0f && linearControl.getTag().equals(getResources().getString(R.string.tag_no_animation_assigned))) {
            Log.d(TAG, "no animation assigned");
            linearControl.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Animation Fading Out");

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Log.d(TAG, "Animation Start");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Log.d(TAG, "Animation End");
                            linearControl.setAlpha(0.0f);

                            linearControl.setTag(getResources().getString(R.string.tag_no_animation_assigned));


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    linearControl.startAnimation(fadeOut);

                    linearControl.setTag(getResources().getString(R.string.tag_animation_assigned));
                }
            }, app.getConnection().isConnected() ? 5 * 1000 : 10 * 1000);
        } else {
            if (linearControl.getAlpha() == 0.0f)
            {
                Log.d(TAG, "Animation Fading In");

                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        linearControl.setAlpha(1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        if (linearControl.getTag().equals(getResources().getString(R.string.tag_no_animation_assigned)))
                            linearControl.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                            Log.d(TAG, "Animation Start");
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            Log.d(TAG, "Animation End");
                                            linearControl.setAlpha(0.0f);

                                            linearControl.setTag(getResources().getString(R.string.tag_no_animation_assigned));


                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });

                                    linearControl.startAnimation(fadeOut);

                                    linearControl.setTag(getResources().getString(R.string.tag_animation_assigned));
                                }
                            }, app.getConnection().isConnected() ? 5 * 1000 : 10 * 1000);


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                linearControl.startAnimation(fadeIn);
            }
        }
    }

    private void showPopupSelectController(){
        if (popupSlideFadeMenu != null && popupSlideFadeMenu.isShowing())
            popupSlideFadeMenu.dismiss();

        View popupView  = getLayoutInflater().inflate(R.layout.popup_controller_selection, null);
        SimpleListAdapter adapter = new SimpleListAdapter(MainActivity.this, Arrays.asList(getResources().getStringArray(R.array.controllers_types)), ArduinoLegoFragment.controllersTags);

        ((ListView) popupView.findViewById(R.id.list_controllers)).setAdapter(adapter);
        ((ListView) popupView.findViewById(R.id.list_controllers)).setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OnItemClicked");
                createFragment((String) view.getTag());
                popupSlideFadeMenu.dismiss();
            }
        });

        popupSlideFadeMenu = new PopupWindow(MainActivity.this);
        popupSlideFadeMenu.setFocusable(true);
        popupSlideFadeMenu.setContentView(popupView);
        popupSlideFadeMenu.setOutsideTouchable(true);
        popupSlideFadeMenu.setBackgroundDrawable(new BitmapDrawable());
        popupSlideFadeMenu.setWidth(app.getSlideFadeMenu().getWidth());
        popupSlideFadeMenu.setHeight(popupView.getLayoutParams().WRAP_CONTENT);
        popupSlideFadeMenu.setAnimationStyle(R.style.PopupAnimation);

        popupSlideFadeMenu.showAsDropDown(app.getSlideFadeMenu());
    }

    private void createFragment(String fragmentType){

        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_left, R.anim.out_right);

        Log.d(TAG, "CreateFragment, Fragment Type: " + fragmentType);

        if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_SERVO))
        {
            fragment = new ServoControlFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_STICK))
        {
            fragment = new ArduinoCarFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_MOTOR)){
            fragment = new EngineControlFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_MULTIPLE)){
            fragment = new MultiEngineControlFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_CUSTOM_CONTROLLER)){
            fragment = new CustomControllerFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_ACCELEROMETER)){
            fragment = new AccFragment();
        }

        Bundle extras = new Bundle();
        extras.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragmentType);
        extras.putFloat(ArduinoLegoFragment.SCREEN_WIDTH, MeasuredWidth);
        extras.putFloat(ArduinoLegoFragment.SCREEN_HEIGHT, MeasuredHeight);

        fragment.setArguments(extras);

        ft.replace(R.id.container, fragment)
                .commit();
    }

    private void createSlidePanelFragment(){

        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_left, R.anim.out_right);

        topMenuFragment = new TopMenuFragment();

        Bundle extras = new Bundle();


        topMenuFragment.setArguments(extras);

        ft.replace(R.id.container_slide_panel_main, topMenuFragment)
                .commit();
    }

    /** Get Display Size */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void getDisplaySize(){
        Point size = new Point();
        WindowManager w = getWindowManager();

        w.getDefaultDisplay().getSize(size);


        /* Screen X and Y */
        MeasuredWidth = size.x;
        MeasuredHeight = size.y;

	    Log.d(TAG, " Width - " + MeasuredWidth + " Height: " + MeasuredHeight);
    }

    @Override
    public void onSlideMenuOpen() {
        if (fragment != null)
            fragment.onSlideMenuOpen();

//        slidingUpPanelLayoutMain.setAnchorPoint(100f);
        slidingUpPanelLayoutMain.expandPane(0.8f);
//        slidingUpPanelLayoutMain.setAnchorPoint(0.5f);
//        slidingUpPanelLayoutContainer.expandPane(0.6f);
    }

    @Override
    public void onSlideMenuClosed() {
        if (fragment != null)
            fragment.onSlideMenuClosed();

        slidingUpPanelLayoutMain.collapsePane();
//        slidingUpPanelLayoutContainer.collapsePane();
    }

    @Override
    public void onSettingsPressed() {

    }

    @Override
    public void onControllerOptionPressed() {
        if (fragment != null)
            fragment.onControllerOptionPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
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

                        simpleListAdapter = new SimpleListAdapter(MainActivity.this);
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
                        Toast.makeText(MainActivity.this, "Scanning finished and no device found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
