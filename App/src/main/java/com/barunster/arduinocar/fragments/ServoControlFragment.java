package com.barunster.arduinocar.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.views.ServoSemiCircle;
import com.barunster.arduinocar.views.SlideButtonLayout;

/**
 * Created by itzik on 3/10/14.
 */
public class ServoControlFragment extends ArduinoLegoFragment {

    private static final String TAG = ServoControlFragment.class.getSimpleName();

    private ArduinoCarAppObj app;

    /* Views*/
    private RelativeLayout mainView;
    private ServoSemiCircle servoSemiCircle;
    private SlideButtonLayout slideButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj)getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (RelativeLayout) inflater.inflate(R.layout.fragment_servo_control, null);

        initChannels();

        slideButton = (SlideButtonLayout) mainView.findViewById(R.id.slide_btn);

        slideButton.setSlideButtonListener(new SlideButtonLayout.SlideButtonListener() {
            @Override
            public void onSlideStop(SlideButtonLayout slideButtonLayout, int pos) {
                Log.d(TAG, "onSlideStop");

//                app.getConnection().write(String.valueOf(Command.STOP) + ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText());
            }

            @Override
            public void onSlideStarted(SlideButtonLayout slideButtonLayout) {
                Log.d(TAG, "onSlideStarted");
            }

            @Override
            public void onSliding(SlideButtonLayout slideButtonLayout, String direction, int speed) {
                Log.d(TAG, "onSliding, Direction: " + direction + " Speed: " + speed);

                app.getConnection().write( ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText() + String.valueOf(direction) + String.valueOf(speed));
            }

            @Override
            public void onMarkedPositionPressed(SlideButtonLayout slideButtonLayout, String direction, int PosNumber, int speed) {
                Log.d(TAG, "onMarkedPositionPressed, Direction: " + direction + " Speed: " + speed + " PosNumber: " + PosNumber);
                app.getConnection().write( ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText() + String.valueOf(direction) + String.valueOf(speed));
            }
        });
        /*ImageView servoBack = (ImageView) mainView.findViewById(R.id.imageView);
        servoSemiCircle = new ServoSemiCircle(Color.WHITE, ServoSemiCircle.Direction.TOP, 500);
//        servoSemiCircle.setBounds(100,100,100,100);
        servoBack.setImageDrawable(servoSemiCircle);*/

        return mainView;
    }

    private void initChannels(){
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
    }

    @Override
    public void onConnecting() {
        super.onConnecting();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
    }

    @Override
    public void onConnected() {
        super.onConnected();
    }
}
