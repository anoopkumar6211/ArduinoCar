package com.barunster.arduinocar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private int Measuredwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDisplaySize();

        if (savedInstanceState == null) {
            ArduinoCarFragment arduinoCarFragment = new ArduinoCarFragment();
            Bundle extras = new Bundle();
            extras.putFloat(ArduinoCarFragment.SCREEN_WIDTH, Measuredwidth);
            arduinoCarFragment.setArguments(extras);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, arduinoCarFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Get Display Size */
    private void getDisplaySize(){
        Point size = new Point();
        WindowManager w = getWindowManager();

        w.getDefaultDisplay().getSize(size);

        /* Screen X and Y */
        Measuredwidth = size.y;

	    Log.d(TAG, " Width - " + Measuredwidth );
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

}
