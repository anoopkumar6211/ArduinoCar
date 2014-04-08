package com.barunster.arduinocar.fragments.not_used;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.bottom_menu.AddCustomButtonFragment;
import com.barunster.arduinocar.fragments.bottom_menu.AddCustomCommandFragment;
import com.barunster.arduinocar.views.ControllerLayout;
import com.barunster.arduinocar.views.SlideButtonLayout;

/**
 * Created by itzik on 3/9/14.
 */
public class MultiEngineControlFragment extends ArduinoLegoFragment {

    // Views
    private LinearLayout mainView;
    private ControllerLayout controllerLayout;
    ArduinoCarAppObj app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (LinearLayout) inflater.inflate(R.layout.verical_test, null);

        controllerLayout = (ControllerLayout) mainView.findViewById(R.id.controller_layout);

        ((TextView)mainView.findViewById(R.id.txt_controller_name)).setText(app.getCustomDBManager().getAllControllers().get(0).getName());

        controllerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(3000);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Toast.makeText(getActivity(), "End", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                controllerLayout.startAnimation(animation);
            }
        }, 1000 * 5);
         return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
