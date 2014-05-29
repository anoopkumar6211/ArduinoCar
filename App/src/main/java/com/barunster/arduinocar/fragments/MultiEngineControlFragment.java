package com.barunster.arduinocar.fragments;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.fragments.bottom_menu.BottomMenuFragment;
import com.barunster.arduinocar.interfaces.ControllerLayoutEventListener;
import com.barunster.arduinocar.views.BrickControllerLayout;
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
    private long openControllerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = (LinearLayout) inflater.inflate(R.layout.verical_test, null);

        Point p = new Point((int) getScreenWidth(),(int) getScreenHeight());
        Log.i(TAG, p.x + " : " + p.y);
        controllerLayout = new BrickControllerLayout(getActivity(), p/*(int) (getScreenHeight()/ MainActivity.TOP_MENU_SIZE_DIVIDER)*/);

        if (app.getCustomDBManager().getAllControllers().isEmpty())
        {
            onControllerSelected(app.getCustomDBManager().addController(new CustomController("Default",6,6 )));
        }
        else if (controllerId != -1)
            onControllerSelected(controllerId);
        else
            onControllerSelected(1); // TODO fix default

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

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onControllerSelected(long id) {
        super.onControllerSelected(id);
        Log.v(TAG, "onControllerSelected, ID: " + id);
        if (!isAdded()){ Log.e(TAG, "Fragement not added"); return;}
        if (app == null)
        {
            if (getActivity() == null)
            {
                Log.e(TAG, "Activity is null");
                return;
            }
            app = (ArduinoCarAppObj) getActivity().getApplication();
        }

        openControllerId = id;

        controllerLayout.setOutputConnection(app.getConnection());

        CustomController controller = CustomDBManager.getInstance().getControllerById(id);
        if (controller == null)
        {
            Log.e(TAG, "Controller is null");
            return;
        }

        controllerLayout.setController(controller);
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
    public void openAddButtonOption() {
        controllerLayout.showAddButtonView();
    }

    @Override
    public void onEditModeChanged(boolean editing) {
        super.onEditModeChanged(editing);
        if (editing)
            controllerLayout.enterEditMode();
        else
            controllerLayout.exitEditMode();
    }
}
