package com.barunster.arduinocar.fragments.top_menu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.fragments.MenuFragment;

/**
 * Created by itzik on 3/24/14.
 */
public class SettingsFragment extends MenuFragment {
    /*Views*/
    private View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_settings , null);

        return mainView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser)
        {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
