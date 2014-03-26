package com.barunster.arduinocar.fragments.top_menu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.SimpleListAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by itzik on 3/24/14.
 */
public class SelectControllerFragment extends MenuFragment {

    private static final String TAG = SelectControllerFragment.class.getSimpleName();

    private ArduinoCarAppObj app;

    private List<CustomController> controllerList;

    /*Views*/
    private View mainView;
    private Button btnAddController;
    private EditText etControllerName;
    private NumberPicker pickRows, pickColumns;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_select_controller , null);

        initControllerList();
        initAddControllerViews();

        return mainView;
    }

    private void initAddControllerViews(){
        btnAddController = (Button) mainView.findViewById(R.id.btn_add_controller);
        etControllerName = (EditText) mainView.findViewById(R.id.edit_enter_controller_name);
        pickRows = (NumberPicker) mainView.findViewById(R.id.picker_rows);
        pickColumns = (NumberPicker) mainView.findViewById(R.id.picker_columns);

        pickRows.setMinValue(2);
        pickRows.setMaxValue(5);
        pickRows.setValue(pickRows.getMinValue());

        pickColumns.setMinValue(2);
        pickColumns.setMaxValue(5);
        pickColumns.setValue(pickRows.getMinValue());

        btnAddController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ((EditText) mainView.findViewById(R.id.edit_enter_controller_name)).getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter the controller name", Toast.LENGTH_SHORT).show();
                    return;
                }

                long id = app.getCustomDBManager().getControllersDataSource().addController(new CustomController(
                        ((EditText) mainView.findViewById(R.id.edit_enter_controller_name)).getText().toString(),
                        pickRows.getValue(), pickColumns.getValue()
                ));

                // Opens the new controller.
                ((MainActivity)getActivity()).onControllerSelected(id);
                ((MainActivity)getActivity()).getSlidingUpPanelLayoutMain().collapsePane();

                getControllersListData();
                refreshList();
            }
        });
    }

    private void initControllerList(){

        SimpleListAdapter adapter = new SimpleListAdapter(getActivity(), getControllersListData());
        adapter.setTextColor(Color.WHITE);

        ((ListView) mainView.findViewById(R.id.list_controllers)).setAdapter(adapter);
        ((ListView) mainView.findViewById(R.id.list_controllers)).setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((MainActivity)getActivity()).onControllerSelected(controllerList.get(position).getId());
                ((MainActivity)getActivity()).getSlidingUpPanelLayoutMain().collapsePane();
            }
        });
    }

    private List<String> getControllersListData(){
        controllerList =  app.getCustomDBManager().getAllControllers();

        return app.getCustomDBManager().controllersToStringList(controllerList);
    }

    private void refreshList(){
        ((SimpleListAdapter) ((ListView) mainView.findViewById(R.id.list_controllers)).getAdapter()).setListData(getControllersListData());
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
            Log.d(TAG, "visible");
            if ( mainView != null && ((ListView) mainView.findViewById(R.id.list_controllers)).getAdapter() != null )
                refreshList();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


}
