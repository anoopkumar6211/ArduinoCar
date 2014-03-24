package com.barunster.arduinocar.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;

import braunster.btconnection.Command;

/**
 * Created by itzik on 3/9/14.
 */
public class EngineControlFragment extends ArduinoLegoFragment {

    //TODO App Remembers channel state direction and speed.

    private static final String TAG = EngineControlFragment.class.getSimpleName();

    private ArduinoCarAppObj app;

    /* Views*/
    private View mainView;
    private Button btnToggleEngine, btnToggleDirection;
    private NumberPicker speedPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_engine_control, null);

        InitViews();

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void InitViews(){

        btnToggleEngine = (Button) mainView.findViewById(R.id.btn_on_off);
        btnToggleDirection = (Button) mainView.findViewById(R.id.btn_forw_bacw);

        speedPicker = (NumberPicker) mainView.findViewById(R.id.speed_picker);


        // Adding the channels option.
        TextView txtChannel;

        for (final String channel : getResources().getStringArray((R.array.motor_channels)) )
        {
            txtChannel = (TextView) getActivity().getLayoutInflater().inflate(R.layout.simple_text_view, null);

            txtChannel.setText(channel);

            if (mainView.findViewById(R.id.linear_channels).getTag() == null)
                mainView.findViewById(R.id.linear_channels).setTag(txtChannel);

            txtChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ( (TextView) mainView.findViewById(R.id.linear_channels).getTag()).setTextColor(Color.LTGRAY);
                    mainView.findViewById(R.id.linear_channels).setTag(v);
                }
            });

              if ( ( (TextView) mainView.findViewById(R.id.linear_channels).getTag()).getText().equals(channel))
                txtChannel.setTextColor(Color.WHITE);

              ( (LinearLayout) mainView.findViewById(R.id.linear_channels)).addView(txtChannel);
        }


        speedPicker.setMinValue(0);
        speedPicker.setMaxValue(200);
        speedPicker.setValue(speedPicker.getMinValue());
        speedPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, "On Value Changed, Val: " +  newVal);

                if (btnToggleEngine.getTag().equals( getResources().getString(R.string.on)))
                {
                    writeEngineCommand();
                }
            }
        });

        btnToggleEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag().equals( getResources().getString(R.string.on)))
                {
                    writeEngineStopCommand();

                    btnToggleEngine.setText(getResources().getString(R.string.off));
                    btnToggleEngine.setTag(getString(R.string.off));
                }
                else
                {
                    writeEngineCommand();

                    btnToggleEngine.setText(getResources().getString(R.string.on));
                    btnToggleEngine.setTag(getString(R.string.on));
                }
            }
        });

        btnToggleDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag().equals(getResources().getString(R.string.tag_engine_direction_forwards)))
                {
                    btnToggleDirection.setText(getResources().getString(R.string.backward));
                    btnToggleDirection.setTag(getResources().getString(R.string.tag_engine_direction_backwards));
                }
                else if ( v.getTag().equals(getResources().getString(R.string.tag_engine_direction_backwards)) ){
                    btnToggleDirection.setText(getResources().getString(R.string.forward));
                    btnToggleDirection.setTag(getResources().getString(R.string.tag_engine_direction_forwards));
                }

                if (btnToggleEngine.getTag().equals(getString(R.string.on))){
                    writeEngineCommand();
                }

            }
        });
    }

    private void writeEngineCommand(){
        if (!app.getConnection().write( ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText()  + String.valueOf(btnToggleDirection.getTag()) + String.valueOf(speedPicker.getValue())))
            Toast.makeText(getActivity(), "The Application isn't connected to any device.", Toast.LENGTH_LONG).show();
    }

    private void writeEngineStopCommand(){
        if (!app.getConnection().write( String.valueOf(Command.STOP) + ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText()) )
            Toast.makeText(getActivity(), "The Application isn't connected to any device.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
    }

    @Override
    public void onConnecting() {
        super.onConnecting();
    }
}
