package com.barunster.arduinocar.fragments.top_menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.MenuFragmentPageAdapter;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import java.util.List;
import java.util.Vector;

/**
 * Created by itzik on 3/24/14.
 */
public class TopMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TopMenuFragment.class.getSimpleName();

    private static final boolean DEBUG = true;

    /* Views*/
    private View mainView;
    private Button btnConnectionInfo ,btnAppSettings, btnControllerSelection, btnEdit, btnFullScreen, btnClose;
    private ViewPager mViewPager;
    private MenuFragmentPageAdapter mPagerAdapter;

    private List<Fragment> menuFragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            if (DEBUG)
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

        if (DEBUG)
            Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG)
            Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void initMenuButtons(){
        btnConnectionInfo = (Button) mainView.findViewById(R.id.btn_connection_info);
        btnAppSettings = (Button) mainView.findViewById(R.id.btn_settings);
        btnControllerSelection = (Button) mainView.findViewById(R.id.btn_select_controller);
        btnFullScreen = (Button) mainView.findViewById(R.id.btn_full_screen);
//        btnEdit = (Button) mainView.findViewById(R.id.btn_edit);
        btnClose = (Button) mainView.findViewById(R.id.btn_close);

        btnConnectionInfo.setOnClickListener(this);

        btnAppSettings.setOnClickListener(this);

        btnControllerSelection.setOnClickListener(this);

        btnFullScreen.setOnClickListener(this);

        btnClose.setVisibility(View.GONE);
    }

    private void initViewPager(){
        Log.d(TAG, "InitViewPager");
        menuFragments = new Vector<Fragment>();
        menuFragments.add(Fragment.instantiate(getActivity(), ConnectionInfoFragment.class.getName()));
        menuFragments.add(Fragment.instantiate(getActivity(), SettingsFragment.class.getName()));
        menuFragments.add(Fragment.instantiate(getActivity(), SelectControllerFragment.class.getName()));
        mPagerAdapter = new MenuFragmentPageAdapter( getChildFragmentManager(), menuFragments) ;

        mViewPager.setAdapter(this.mPagerAdapter);
    }

    private void showCloseButton(){
        if (btnClose != null)
        {
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setOnClickListener(this);
        }
    }

    private void hideCloseButton(){
        if (btnClose != null) {
            btnClose.setVisibility(View.GONE);
            btnClose.setOnClickListener(null);
        }
    }

    private void showFullScreenButton(){
        if (btnFullScreen != null)
        {
            btnFullScreen.setVisibility(View.VISIBLE);
            btnFullScreen.setOnClickListener(this);
        }
    }

    private void hideFullScreenButton(){
        if (btnFullScreen != null) {
            btnFullScreen.setVisibility(View.GONE);
            btnFullScreen.setOnClickListener(null);
        }
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

            case  R.id.btn_full_screen:
                ((MainActivity)getActivity()).setFullScreen();
                break;

            case R.id.btn_close:
                ((MainActivity)getActivity()).getSlidingUpPanelLayoutMain().collapsePane();
                break;
        }

        if (page != -1) {
            mViewPager.setCurrentItem(page);
            ((MainActivity) getActivity()).getSlidingUpPanelLayoutMain().expandPane();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser)
        {
            if (mViewPager != null && mPagerAdapter != null)
            {
                if (DEBUG)
                    Log.d(TAG, "viewPagerSize: "  + mPagerAdapter.getCount());

                if (mPagerAdapter.getCount() == 0)
                {

                }
                mPagerAdapter.getFragmentByPosition(0).setUserVisibleHint(true);

                showCloseButton();
                hideFullScreenButton();
            }
        }
        else
        {
            hideCloseButton();
            showFullScreenButton();
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    /*@Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (DEBUG)
            Log.d(TAG, "onPanelSlide");

    }

    @Override
    public void onPanelCollapsed(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelCollapsed");

//        hideCloseButton();
    }

    @Override
    public void onPanelExpanded(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelExpanded");

//        showCloseButton();
    }

    @Override
    public void onPanelAnchored(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelAnchored");
    }*/
}
