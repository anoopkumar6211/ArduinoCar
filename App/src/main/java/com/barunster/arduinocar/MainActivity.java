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
import android.widget.PopupWindow;
import android.widget.Toast;

import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.top_menu.ConnectionInfoFragment;
import com.barunster.arduinocar.fragments.top_menu.TopMenuFragment;
import com.barunster.arduinocar.interfaces.SlideMenuListener;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import java.util.ArrayList;

import braunster.btconnection.BTConnection;

public class MainActivity extends FragmentActivity implements SlideMenuListener {

    // TODO fix animation maybe problem with smaller screens fromXdelte to Xdelta
    // TODO xml data transfer or json
    // TODO combainnig several commands from several channels to one command

    private final String TAG = MainActivity.class.getSimpleName();

    private static final float TOP_MENU_OFFSET = 0.8f;
    public static final float BOTTOM_MENU_OFFSET = 0.5f;

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
    private Button btnMenu;
    private SlidingUpPanelLayout slidingUpPanelLayoutMain, slidingUpPanelLayoutContainer;

    /* Popups */
    private PopupWindow popupSlideFadeMenu;

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
            createFragment(ArduinoLegoFragment.FRAGMENT_TYPE_CUSTOM_CONTROLLER);
        }
        else
        {
            createFragment(savedInstanceState.getString(ArduinoLegoFragment.FRAGMENT_TYPE));
        }

        btnMenu = (Button) findViewById(R.id.btn_menu);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayoutMain != null) {
                    slidingUpPanelLayoutMain.expandPane(TOP_MENU_OFFSET);
                    slidingUpPanelLayoutContainer.collapsePane();
                }
            }
        });

//        app.setSlideFadeMenu(new SlideFadeMenu(this));
//        app.getSlideFadeMenu().setSlideMenuListener(this);
//        ((FrameLayout) findViewById(R.id.container)).addView(app.getSlideFadeMenu()); // TODO chanege

        initSlidingUpPanel();

    }

    @Override
    protected void onResume() {
        super.onResume();

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

        if(ArduinoCarAppObj.prefs.getBoolean(ConnectionInfoFragment.PREFS_AUTO_DISCONNECT, true))
            app.getConnection().close();

        unregisterReceiver(connectivityChangesReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fragment != null)
            outState.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragment.getType());

        super.onSaveInstanceState(outState);
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

        /* Top Panel*/
        slidingUpPanelLayoutMain = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_main);
        slidingUpPanelLayoutMain.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));

        createSlidePanelFragment();

        slidingUpPanelLayoutMain.setEnableDragViewTouchEvents(true);


//        slidingUpPanelLayout.setPanelHeight(200);
        slidingUpPanelLayoutMain.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                // When menu is only slightly shown set a click listener to the frame so iw will close it when click.
                if (slideOffset == TOP_MENU_OFFSET)
                {
                    findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slidingUpPanelLayoutMain.collapsePane();
                        }
                    });
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
                Log.i(TAG, "onPanelExpanded");
                slidingUpPanelLayoutMain.setSlidingEnabled(false);

                // Closing the bottom menu.
                slidingUpPanelLayoutContainer.collapsePane();
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                slidingUpPanelLayoutMain.setSlidingEnabled(true);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }
        });


        /* Bottom Panel*/
        slidingUpPanelLayoutContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_container);
//        slidingUpPanelLayoutContainer.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));

        slidingUpPanelLayoutContainer.setEnableDragViewTouchEvents(true);

        slidingUpPanelLayoutContainer.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

                // When menu is only slightly shown set a click listener to the frame so iw will close it when click.
                if (slideOffset == BOTTOM_MENU_OFFSET)
                {
                    slidingUpPanelLayoutContainer.setSlidingEnabled(false);

                    findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slidingUpPanelLayoutContainer.collapsePane();
                        }
                    });
                }
                // When closed full eliminating the listener and enabling the slide.
                else if ( slideOffset == 1.0f)
                {
                    findViewById(R.id.container).setOnClickListener(null);
                }
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
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_MULTIPLE)){
            fragment = new MultiEngineControlFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_CUSTOM_CONTROLLER)){
            fragment = new CustomControllerFragment();
        }
        else if (fragmentType.equals(ArduinoLegoFragment.FRAGMENT_TYPE_ACCELEROMETER)){
            fragment = new AccFragment();
        }*/

        fragment = new CustomControllerFragment();

        Bundle extras = new Bundle();
        extras.putString(ArduinoLegoFragment.FRAGMENT_TYPE, fragmentType);
        extras.putFloat(ArduinoLegoFragment.SCREEN_WIDTH, MeasuredWidth);
        extras.putFloat(ArduinoLegoFragment.SCREEN_HEIGHT, MeasuredHeight);

        fragment.setArguments(extras);

        ft.replace(R.id.container, fragment)
                .commit();
    }

    private void createSlidePanelFragment(){

        ft = getSupportFragmentManager().beginTransaction();

        topMenuFragment = new TopMenuFragment();

        // Used later for refreshing to connection fragment when menu is open.
        topMenuFragment.setUserVisibleHint(false);

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
    }

    @Override
    public void onSlideMenuClosed() {
        if (fragment != null)
            fragment.onSlideMenuClosed();
    }

    @Override
    public void onSettingsChanged() {

    }

    @Override
    public void onControllerSelected(long id) {
        fragment.onControllerSelected(id);
    }

    /* Getters & Setters */

    public SlidingUpPanelLayout getSlidingUpPanelLayoutMain() {
        return slidingUpPanelLayoutMain;
    }

    public SlidingUpPanelLayout getSlidingUpPanelLayoutContainer() {
        return slidingUpPanelLayoutContainer;
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
