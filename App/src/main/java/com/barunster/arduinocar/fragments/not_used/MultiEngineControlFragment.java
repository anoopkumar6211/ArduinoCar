package com.barunster.arduinocar.fragments.not_used;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.fragments.bottom_menu.BottomMenuFragment;
import com.barunster.arduinocar.interfaces.ControllerLayoutEventListener;
import com.barunster.arduinocar.views.BrickControllerLayout;
import com.barunster.arduinocar.views.FramesControllerLayout;
import com.barunster.arduinocar.views.SlidingUpPanelLayout;

/**
 * Created by itzik on 3/9/14.
 */
public class MultiEngineControlFragment extends ArduinoLegoFragment {

    private static final String TAG = MultiEngineControlFragment.class.getSimpleName();

    public static final float BOTTOM_MENU_OFFSET_FULL = 0.5f;
    private static final int TOP_MENU_SIZE_DIVIDER = 10;

    // Views
    private LinearLayout mainView;
    private BrickControllerLayout controllerLayout;
    ArduinoCarAppObj app;

    SlidingUpPanelLayout slidingUpPanelLayout;
    BottomMenuFragment bottomMenuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (LinearLayout) inflater.inflate(R.layout.verical_test, null);

        controllerLayout = new BrickControllerLayout(getActivity(), (int) (getScreenHeight()/TOP_MENU_SIZE_DIVIDER));

        if (app.getCustomDBManager().getAllControllers().isEmpty())
        {
            onControllerSelected(app.getCustomDBManager().addController(new CustomController("Default",0,0 )));
        }
        else onControllerSelected(1); // TODO fix default

        controllerLayout.setOutputConnection(app.getConnection());

        controllerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        controllerLayout.setControllerLayoutEventListener(new ControllerLayoutEventListener() {
            @Override
            public void onButtonAdded(CustomButton customButton, View view) {
                // Adding the button to the database and setting the view id to the id from the database.
//                view.setId((int) app.getCustomDBManager().addButton(customButton));
//                Log.d(TAG, "Button Added, id: " + view.getId());
            }

            @Override
            public void onDragEnded() {

            }

            @Override
            public void onButtonRemoved(long buttonId) {

            }

            @Override
            public void onButtonChanged(CustomButton customButton, View view) {

            }
        });

        mainView.addView(controllerLayout);

//        initSlidingUpPanel();

         return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initSlidingUpPanel(){
        slidingUpPanelLayout = (SlidingUpPanelLayout) mainView.findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.setPanelHeight((int) (getScreenHeight()/TOP_MENU_SIZE_DIVIDER));
        slidingUpPanelLayout.setSlidingEnabled(false);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        /* Bottom Panel*/
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide,, Container, offset " + slideOffset);

                if (slideOffset == BOTTOM_MENU_OFFSET_FULL) {
                    slidingUpPanelLayout.setSlidingEnabled(false);
                }
                // When closed full eliminating the listener and enabling the slide.
                else if (slideOffset == 1.0f) {

                }

                bottomMenuFragment.onPanelSlide(slidingUpPanelLayout, slideOffset);
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

        createSlidePanelFragment();
    }

    @Override
    public void onControllerSelected(long id) {
        super.onControllerSelected(id);
        controllerLayout.setOutputConnection(app.getConnection());
        controllerLayout.setController(CustomDBManager.getInstance().getControllerById(id));
    }

    private void createSlidePanelFragment(){
        // Bottom Menu
        bottomMenuFragment = new BottomMenuFragment();

        // Used later for refreshing to connection fragment when menu is open.
        bottomMenuFragment.setUserVisibleHint(false);

        getFragmentManager().beginTransaction().replace(R.id.container_slide_panel_container, bottomMenuFragment)
                .commit();
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
