package com.barunster.arduinocar.fragments.top_menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.MenuFragmentPageAdapter;

import java.util.List;
import java.util.Vector;

/**
 * Created by itzik on 3/24/14.
 */
public class TopMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TopMenuFragment.class.getSimpleName();

    /* Views*/
    private View mainView;
    private Button btnConnectionInfo ,btnAppSettings, btnControllerSelection, btnClose;
    private ViewPager mViewPager;
    private MenuFragmentPageAdapter mPagerAdapter;

    private List<Fragment> menuFragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            Log.d(TAG, "saved Instance is not null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_top_menu, null);

        mViewPager = (ViewPager) mainView.findViewById(R.id.view_pager_menu);

        initMenuButtons();

        initViewPager();

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

    private void initMenuButtons(){
        btnConnectionInfo = (Button) mainView.findViewById(R.id.btn_connection_info);
        btnAppSettings = (Button) mainView.findViewById(R.id.btn_settings);
        btnControllerSelection = (Button) mainView.findViewById(R.id.btn_select_controller);
        btnClose = (Button) mainView.findViewById(R.id.btn_close);

        btnConnectionInfo.setOnClickListener(this);

        btnAppSettings.setOnClickListener(this);

        btnControllerSelection.setOnClickListener(this);

        btnClose.setOnClickListener(this);
    }

    private void initViewPager(){
        Log.d(TAG, "InitViewPager");
        menuFragments = new Vector<Fragment>();
        menuFragments.add(Fragment.instantiate(getActivity(), ConnectionInfoFragment.class.getName()));
        menuFragments.add(Fragment.instantiate(getActivity(), SettingsFragment.class.getName()));
        menuFragments.add(Fragment.instantiate(getActivity(), SelectControllerFragment.class.getName()));
        mPagerAdapter = new MenuFragmentPageAdapter( getActivity().getSupportFragmentManager(), menuFragments) ;

        mViewPager.setAdapter(this.mPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        int page = -1;

        switch (v.getId())
        {
            case R.id.btn_connection_info:
                page = 0;
                break;

            case R.id.btn_settings:
                page = 1;
                break;

            case R.id.btn_select_controller:
                page = 2;
                break;
        }

        if (page == -1)
        {
            ((MainActivity)getActivity()).getSlidingUpPanelLayoutMain().collapsePane();
            return;
        }

        mViewPager.setCurrentItem(page);

        ((MainActivity)getActivity()).getSlidingUpPanelLayoutMain().expandPane();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser)
        {
            if (mViewPager != null && mPagerAdapter != null)
            {
                mPagerAdapter.getFragmentByPosition(0).setUserVisibleHint(true);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
