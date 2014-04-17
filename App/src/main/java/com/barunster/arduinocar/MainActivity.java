package com.barunster.arduinocar;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.os.Build;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.not_used.MultiEngineControlFragment;
import com.barunster.arduinocar.fragments.bottom_menu.BottomMenuFragment;
import com.barunster.arduinocar.fragments.top_menu.ConnectionInfoFragment;
import com.barunster.arduinocar.fragments.top_menu.TopMenuFragment;
import com.barunster.arduinocar.interfaces.SlideMenuListener;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import java.util.ArrayList;

import braunster.btconnection.BTConnection;

public class MainActivity extends FragmentActivity {

    // TODO fix animation maybe problem with smaller screens fromXdelte to Xdelta
    // TODO xml data transfer or json
    // TODO combainnig several commands from several channels to one command

    private final String TAG = MainActivity.class.getSimpleName();

    private static final float TOP_MENU_OFFSET = 0.9f;
    private static final int TOP_MENU_SIZE_DIVIDER = 10;
    private static final boolean DEBUG = false;

    public static final String BLUETOOTH_DEVICE_NAME = "bluetooth_device_name";
    public static final String MAIN_FRAGMENT_TAG = "main_fragment_tag";
    public static final String FULL_SCREEN_MODE  = "full_screen_mode";
    public static final String CONTROLLER_ID  = "controller_id";


    // Bluetooth connection related
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private SimpleListAdapter simpleListAdapter;
    private boolean isScanning = false, isFullScreenMode = false, doubleBackToExitPressedOnce = false;
    private Dialog connectToDeviceDialog;
    private long controllerId = -1;
    private ArduinoCarAppObj app;

    private int MeasuredWidth, MeasuredHeight;

    /* Views*/
    private SlidingUpPanelLayout slidingUpPanelLayoutMain;

    /* Animations */
    Animation fadeOut, fadeIn;
    private FragmentTransaction ft;

    /* Fragments*/
    private ArduinoLegoFragment fragment;
    private TopMenuFragment topMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_sliding_panel);

        app = (ArduinoCarAppObj) getApplication();

        firstTimeUsingApp();

        fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);

        getDisplaySize();

        if (savedInstanceState == null) {
            createFragment(ArduinoLegoFragment.FRAGMENT_TYPE_MULTIPLE); // TODO change to custom controller fragment
        }
        else
        {
            Toast.makeText(this, "from savedInstanceBundle activity", Toast.LENGTH_SHORT).show();

            if (DEBUG)
                Log.i(TAG, " activity starts from saved instance bundle.");

            createFragment(savedInstanceState.getString(ArduinoLegoFragment.FRAGMENT_TYPE));

            isFullScreenMode = savedInstanceState.getBoolean(FULL_SCREEN_MODE, false);

            controllerId = savedInstanceState.getLong(CONTROLLER_ID, -1);
            onControllerSelected(controllerId);
        }

        initSlidingUpPanel();

        if (isFullScreenMode)
            setFullScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume");

        // Reconnect to device
        if(ArduinoCarAppObj.prefs.getBoolean(ConnectionInfoFragment.PREFS_AUTO_CONNECT, true))
            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).contains(MainActivity.BLUETOOTH_DEVICE_NAME) && !app.getConnection().isConnected())
                ConnectToDevice(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(MainActivity.BLUETOOTH_DEVICE_NAME, ""));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(connectivityChangesReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG)
            Log.d(TAG, "onPause");

        if(ArduinoCarAppObj.prefs.getBoolean(ConnectionInfoFragment.PREFS_AUTO_DISCONNECT, true))
            app.getConnection().close();

        unregisterReceiver(connectivityChangesReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DEBUG)
            Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (fragment != null) {

            outState.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragment.getType());
            outState.putBoolean(FULL_SCREEN_MODE, isFullScreenMode);
            outState.putLong(CONTROLLER_ID, controllerId);
        }
        else
            Toast.makeText(this, "Fragment is null when saving instance bundle", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isFullScreenMode) {


            if (doubleBackToExitPressedOnce) {
                exitFullScreen();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
        else {
            Toast.makeText(this, "Exit!", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    private void ConnectToDevice(String deviceName){

        BTConnection.getBluetoothAdapter().cancelDiscovery();

        Toast.makeText(MainActivity.this, "Connecting...Device Name: " + deviceName, Toast.LENGTH_SHORT).show();

        app.getConnection().setConnectionStateChangeListener(new BTConnection.ConnectionStateChangeListener() {
            @Override
            public void onConnected(int connectionType, Object tag) {
                Toast.makeText(MainActivity.this, "Connected!.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionChangeState(int connectionType, String state) {

            }

            @Override
            public void onConnectionFailed(String issue, Object obj) {
                Toast.makeText(MainActivity.this, "Connection Failed, Issue: " + issue, Toast.LENGTH_LONG).show();
            }
        });

        app.getConnection().setOnConnectionLost(new BTConnection.onConnectionLostListener() {
            @Override
            public void onConnectionLost(int i, String issue) {
                Log.d(TAG, "OnConnectionLost, Issue: " + issue);
                Toast.makeText(MainActivity.this, "Connection is lost, Issue: " + issue, Toast.LENGTH_LONG).show();
            }
        });

        app.getConnection().start(deviceName);
    }

    private void firstTimeUsingApp(){
        if (ArduinoCarAppObj.prefs.getBoolean(ArduinoCarAppObj.PREFS_FIRST_TIME_USING_APP, true))
        {
            ArduinoCarAppObj.prefs.edit().putBoolean(ConnectionInfoFragment.PREFS_AUTO_CONNECT, true).commit();

            ArduinoCarAppObj.prefs.edit().putBoolean(ConnectionInfoFragment.PREFS_AUTO_DISCONNECT, true).commit();

            ArduinoCarAppObj.prefs.edit().putBoolean(ArduinoCarAppObj.PREFS_FIRST_TIME_USING_APP, false).commit();
        }
    }

    private void initSlidingUpPanel(){
        slidingUpPanelLayoutMain = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_main);

        slidingUpPanelLayoutMain.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));

        slidingUpPanelLayoutMain.setEnableDragViewTouchEvents(true);

        slidingUpPanelLayoutMain.setPanelHeight(MeasuredHeight / TOP_MENU_SIZE_DIVIDER);

        /* Top Panel*/
        slidingUpPanelLayoutMain.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (DEBUG)
                    Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                // When menu is only slightly shown set a click listener to the frame so iw will close it when click.
                if (slideOffset == TOP_MENU_OFFSET)
                {
                }
                else if (slideOffset < TOP_MENU_OFFSET)
                {
                    // If not set visible set to visible. That will cause a refresh on the connection info fragment.
                    if (!topMenuFragment.getUserVisibleHint())
                        topMenuFragment.setUserVisibleHint(true);
                }
                // When closed full eliminating the listener and enabling the slide.
                else if ( slideOffset == 1.0f)
                {
                    findViewById(R.id.container).setOnClickListener(null);
                    topMenuFragment.setUserVisibleHint(false);
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                if (DEBUG)
                    Log.i(TAG, "onPanelExpanded");

                slidingUpPanelLayoutMain.setSlidingEnabled(false);

//                if (topMenuFragment != null)
//                    topMenuFragment.onPanelExpanded(panel);

                if (fragment != null)
                    fragment.onSlideMenuOpen();
            }

            @Override
            public void onPanelCollapsed(View panel) {
                if (DEBUG)
                    Log.i(TAG, "onPanelCollapsed");

                slidingUpPanelLayoutMain.setSlidingEnabled(true);

                if (isFullScreenMode)
                {
                    if (DEBUG)
                        Log.i(TAG, "panel collapsed on full screen mode.");

                    /*if (slidingUpPanelLayoutMain.getmSlideOffset() < 1f)
                    {
                        if (DEBUG)
                            Log.i(TAG, "collapsing the panel.");

                        slidingUpPanelLayoutMain.post(new Runnable() {
                            @Override
                            public void run() {
                                slidingUpPanelLayoutMain.collapsePane();
                            }
                        });
                    }*/
                }
//                if (topMenuFragment != null)
//                    topMenuFragment.onPanelCollapsed(panel);

                if (fragment != null)
                    fragment.onSlideMenuClosed();
            }

            @Override
            public void onPanelAnchored(View panel) {
                if (DEBUG)
                    Log.i(TAG, "onPanelAnchored");
            }
        });

        createSlidePanelFragment();
    }

    private void createFragment(String fragmentType){

        ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_left, R.anim.out_right);
        // TODO animation

        /*Log.d(TAG, "CreateFragment, Fragment Type: " + fragmentType);

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
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_CUSTOM_CONTROLLER)){
            fragment = new CustomControllerFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_ACCELEROMETER)){
            fragment = new AccFragment();
        }*/

        if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_MULTIPLE)){
            fragment = new MultiEngineControlFragment();
        }
        else {
            fragment = new CustomControllerFragment();
        }

        Bundle extras = new Bundle();
        extras.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragmentType);
        extras.putFloat(ArduinoLegoFragment.SCREEN_WIDTH, MeasuredWidth);
        extras.putFloat(ArduinoLegoFragment.SCREEN_HEIGHT, MeasuredHeight);

        fragment.setArguments(extras);

        ft.replace(R.id.container, fragment, MAIN_FRAGMENT_TAG)
                .commit();
    }

    private void createSlidePanelFragment(){
        // To Menu
        ft = getSupportFragmentManager().beginTransaction();

        topMenuFragment = new TopMenuFragment();

        // Used later for refreshing to connection fragment when menu is open.
        topMenuFragment.setUserVisibleHint(false);

        ft.replace(R.id.container_slide_panel_main, topMenuFragment)
                .commit();
    }

    public void setFullScreen(){
        fragment.onFullScreen();

        fadeOutTopMenu();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFullScreenMode = true;
                slidingUpPanelLayoutMain.setPanelHeight(0);
                slidingUpPanelLayoutMain.post(new Runnable() {
                    @Override
                    public void run() {
                        slidingUpPanelLayoutMain.collapsePane();
                    }
                });
            }
        }, 700);
    }

    public void exitFullScreen(){
        fragment.onExitFullScreen();

        if (DEBUG)
            Log.d(TAG, "Panel Height: " + MeasuredHeight / TOP_MENU_SIZE_DIVIDER);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFullScreenMode = false;
                slidingUpPanelLayoutMain.setPanelHeight(MeasuredHeight / TOP_MENU_SIZE_DIVIDER);
                slidingUpPanelLayoutMain.post(new Runnable() {
                    @Override
                    public void run() {
                        fadeInTopMenu();
                    }
                });
            }
        }, 700);
    }

    private void fadeOutTopMenu(){
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setDuration(700);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.container_slide_panel_main).setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        findViewById(R.id.container_slide_panel_main).startAnimation(fadeOut);
    }

    private void fadeInTopMenu(){

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setDuration(700);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.container_slide_panel_main).setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        findViewById(R.id.container_slide_panel_main).startAnimation(fadeIn);
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

    public void onControllerSelected(long id) {
        fragment.onControllerSelected(id);
        controllerId  = id;
    }

    /* Getters & Setters */

    public SlidingUpPanelLayout getSlidingUpPanelLayoutMain() {
        return slidingUpPanelLayoutMain;
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
