package com.barunster.arduinocar.fragments.bottom_menu;

import android.graphics.Color;
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
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.top_menu.ConnectionInfoFragment;
import com.barunster.arduinocar.fragments.top_menu.SelectControllerFragment;
import com.barunster.arduinocar.fragments.top_menu.SettingsFragment;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

import java.util.List;
import java.util.Vector;

/**
 * Created by itzik on 3/24/14.
 */
public class BottomMenuFragment extends Fragment implements View.OnClickListener, SlidingUpPanelLayout.PanelSlideListener{

    private static final String TAG = BottomMenuFragment.class.getSimpleName();
    private static final boolean DEBUG = false;

    /* Views*/
    private View mainView;
    private Button btnAddButton ,btnEditButton, btnClose;
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

        mainView = inflater.inflate(R.layout.fragment_bottom_menu, null);

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
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void initMenuButtons(){
        btnAddButton = (Button) mainView.findViewById(R.id.btn_add_button);
        btnEditButton = (Button) mainView.findViewById(R.id.btn_edit_button);
        btnClose = (Button) mainView.findViewById(R.id.btn_close);

        btnAddButton.setOnClickListener(this);

        btnEditButton.setOnClickListener(this);

        hideCloseButton();
    }

    private void initViewPager(){
//        Log.d(TAG, "InitViewPager");
        menuFragments = new Vector<Fragment>();
        menuFragments.add(Fragment.instantiate(getActivity(), AddCustomButtonFragment.class.getName()));
        menuFragments.add(Fragment.instantiate(getActivity(), AddCustomCommandFragment.class.getName()));
        mPagerAdapter = new MenuFragmentPageAdapter( getChildFragmentManager(), menuFragments) ;

        mViewPager.setAdapter(this.mPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    editMode();
                }
                else {
                    exitEditMode();
                    addMode();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addMode(){
        ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).setEditButtonMode(false);
        btnEditButton.setSelected(false);
        btnEditButton.setBackgroundColor(Color.DKGRAY);
        ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).openBottomMenu();
        ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).getControllerLayout().showAvailableFrames();
    }

    private void editMode(){
        ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).setEditButtonMode(true);

        btnEditButton.setSelected(true);
        btnEditButton.setBackgroundColor(Color.WHITE);
    }

    private void exitEditMode(){
        ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).setEditButtonMode(false);

        btnEditButton.setSelected(false);
        btnEditButton.setBackgroundColor(Color.DKGRAY);
    }

    private void showCloseButton(){
        btnClose.setVisibility(View.VISIBLE);
        btnClose.setOnClickListener(this);
    }

    private void hideCloseButton(){
        btnClose.setVisibility(View.GONE);
        btnClose.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_add_button:
                addMode();
                mViewPager.setCurrentItem(0);
                break;

            case R.id.btn_edit_button:
                if (btnEditButton.isSelected())
                {
                    exitEditMode();
                    ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).getControllerLayout().hideAvailableFrames();
                    ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).closeBottomMenu();
                }
                else
                {
                    editMode();
                    ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).closeBottomMenu();
                }
                mViewPager.setCurrentItem(1);
                break;

            case R.id.btn_close:
                ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).closeBottomMenu();
                ((CustomControllerFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).getControllerLayout().hideAvailableFrames();
                break;
        }
    }

    public AddCustomCommandFragment getEditButtonFragment(){
        return (AddCustomCommandFragment) mPagerAdapter.getFragmentByPosition(1);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (DEBUG)
            Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

        if (slideOffset < 1.0f)
        {
            showCloseButton();
        }
        else
        {
            hideCloseButton();
        }
    }

    @Override
    public void onPanelCollapsed(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelCollapsed");
    }

    @Override
    public void onPanelExpanded(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelExpanded");
    }

    @Override
    public void onPanelAnchored(View panel) {
        if (DEBUG)
            Log.d(TAG, "onPanelAnchored");
    }
}
