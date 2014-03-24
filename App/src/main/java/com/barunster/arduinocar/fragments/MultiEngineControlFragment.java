package com.barunster.arduinocar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.views.SlideButtonLayout;

/**
 * Created by itzik on 3/9/14.
 */
public class MultiEngineControlFragment extends ArduinoLegoFragment {

    // Views
    private LinearLayout mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (LinearLayout) inflater.inflate(R.layout.verical_test, null);

        SlideButtonLayout slideButtonLayout = (SlideButtonLayout) mainView.findViewById(R.id.slide_btn);
//                new SlideButtonLayout(getActivity(), LinearLayout.HORIZONTAL, 181, false, true);

//        mainView.addView(slideButtonLayout);

        return mainView;
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
