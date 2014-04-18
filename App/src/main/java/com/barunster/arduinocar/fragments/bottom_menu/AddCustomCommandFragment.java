package com.barunster.arduinocar.fragments.bottom_menu;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.RadioGroupGridAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.MenuFragment;
import com.barunster.arduinocar.views.DropZoneFrame;
import com.barunster.arduinocar.views.SimpleButton;

/**
 * Created by itzik on 3/24/14.
 */
public class AddCustomCommandFragment extends MenuFragment {

    private static final String TAG = AddCustomCommandFragment.class.getSimpleName();

    /*Views*/
    private View mainView;

    private int buttonType;
    private long buttonId;
    private CustomCommand customCommand;
    private CustomButton customButton;

    private ArduinoCarAppObj app;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args != null) {
            buttonType = args.getInt(CustomButton.BUTTON_TYPE, -1);
            buttonId = args.getLong(CustomButton.BUTTON_ID, -1);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ArduinoCarAppObj) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_edit_button, null);

        refresh(buttonType, (int) buttonId);

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


    private void initChannels(){
        Log.d(TAG, "initChannels" + ((customCommand == null) ? ", Custom command is null" : " ") );
        // Channel Selection
        TextView txtChannel;

        // If not initialized yet.
        if ( ((LinearLayout) mainView.findViewById(R.id.linear_channels)).getChildCount() == 0)
        {


        }
        else
        {
            for (int i = 0 ; i < ((LinearLayout) mainView.findViewById(R.id.linear_channels)).getChildCount() ; i++)
            {
                txtChannel = ((TextView)((LinearLayout) mainView.findViewById(R.id.linear_channels)).getChildAt(i));
                if (customCommand != null && txtChannel.getText().toString().equals(customCommand.getChannel()))
                {
                    txtChannel.setTextColor(Color.WHITE);
                    ( (TextView) mainView.findViewById(R.id.linear_channels).getTag()).setTextColor(Color.LTGRAY);
                    mainView.findViewById(R.id.linear_channels).setTag(txtChannel);
                }
            }
        }

    }

    private void initView(){

        // Hiding the extra speed data if not needed.
        if (customCommand == null || (customCommand.getType() != CustomCommand.TYPE_SPEED_UP && customCommand.getType() != CustomCommand.TYPE_SPEED_DOWN) )
            mainView.findViewById(R.id.et_command_extra).setVisibility(View.GONE);

        /* Select the proper command list by the button type.*/
        int list[] = new int[0];
        switch (buttonType)
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                list = CustomCommand.regularButtonCommandTypes;

                mainView.findViewById(R.id.linear_slide_data).setVisibility(View.GONE);
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;

                mainView.findViewById(R.id.linear_slide_data).setVisibility(View.VISIBLE);

                ((CheckBox)mainView.findViewById(R.id.check_show_marks)).setChecked(customButton.showMarks());
                ((CheckBox)mainView.findViewById(R.id.check_auto_center)).setChecked(customButton.centerAfterDrop());
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;

                mainView.findViewById(R.id.linear_slide_data).setVisibility(View.VISIBLE);
                ((CheckBox)mainView.findViewById(R.id.check_show_marks)).setChecked(customButton.showMarks());
                ((CheckBox)mainView.findViewById(R.id.check_auto_center)).setChecked(customButton.centerAfterDrop());
                break;
        }

        /*Creates the radio group grid for command type selection.*/
        final RadioGroupGridAdapter radioGroupGridAdapter = new RadioGroupGridAdapter(getActivity(), list, (customCommand != null)? customCommand.getType() : -1);
        ((GridView) mainView.findViewById(R.id.grid_radio_group)).setAdapter(radioGroupGridAdapter);

        // Listen to command selection.
        radioGroupGridAdapter.setRadioCheckedListener(new RadioGroupGridAdapter.RadioCheckedListener() {
            @Override
            public void onRadioChecked(int id) {
                // If command is speed up/down show the extra speed data editText.
                if (id == CustomCommand.TYPE_SPEED_DOWN || id == CustomCommand.TYPE_SPEED_UP)
                {
                    mainView.findViewById(R.id.et_command_extra).setVisibility(View.VISIBLE);
                    ((EditText) mainView.findViewById(R.id.et_command_extra)).setHint("Enter the amount to speed up/down");
                }
                else
                {
                    mainView.findViewById(R.id.et_command_extra).setVisibility(View.GONE);
                }
            }
        });

        // Submit Button
        mainView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there's a command selected in the grid.
                if (radioGroupGridAdapter.getSelectedButton() != null)
                {
                    int commandType = radioGroupGridAdapter.getSelectedButton().getId();
                    CustomCommand customCommand = new CustomCommand(buttonId,
                            commandType,
                            ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText().toString());

                    // Adding the speed data if needed.
                    if (mainView.findViewById(R.id.et_command_extra).getVisibility() == View.VISIBLE) {
                        if (((EditText) mainView.findViewById(R.id.et_command_extra)).getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please enter extra data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                            customCommand.setExtraSpeedData(Integer.parseInt(((EditText) mainView.findViewById(R.id.et_command_extra)).getText().toString()));
                    }

                    // Add the command to the database.
                    app.getCustomDBManager().addCommand(customCommand);

                    // Adding new data to the button if needed. else refresh the button so new command type will be applied.
                    if (mainView.findViewById(R.id.linear_slide_data).getVisibility() == View.VISIBLE)
                    {
                        // Change the custom button settings.
                        CustomButton customButton = app.getCustomDBManager().getButtonById(buttonId);
                        customButton.setShowMarks( ((CheckBox)mainView.findViewById(R.id.check_show_marks)).isChecked() );
                        customButton.setCenterAfterDrop( ((CheckBox) mainView.findViewById(R.id.check_auto_center)).isChecked());
                        buttonId = app.getCustomDBManager().updateButtonById(customButton);

                        if (buttonId == -1)
                            Log.e(TAG, "Problem adding button to the database.");

                        // Recreate the controller TODO refresh only relevant button and not the whole layout.
                        ((CustomControllerFragment)getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).onControllerSelected(customButton.getControllerId());
                    }
                    else
                    {
                        // Changing the button command type. (The Button Drawable Resource will be changed.)
                        ((SimpleButton) ((DropZoneFrame) ((CustomControllerFragment) getActivity().getSupportFragmentManager()
                                .findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG))
                                .getControllerLayout().getChildAt(customButton.getPosition()))
                                .getChildAt(0))
                                .setCommandType(commandType);
                    }

                    // Closing the bottom menu and exiting edit mode.
                    ((CustomControllerFragment)getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).closeBottomMenu();
                }
                else
                    Toast.makeText(getActivity(), "Please select a command", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh(int buttonType, int buttonId){

        if (buttonId != this.buttonId || buttonType != this.buttonType)
        {
            this.buttonType = buttonType;
            this.buttonId = buttonId;

            customCommand = app.getCustomDBManager().getCommandByButtonId(buttonId);
            customButton = app.getCustomDBManager().getButtonById(buttonId);

            if (customCommand == null)
                Log.e(TAG, "CustomCommand is null");
            else
                Log.i(TAG, "Custom command id: " + customCommand.getId() + ", type: " + getResources().getString(customCommand.getType()) + ", channel: " + customCommand.getChannel());

            if (customButton == null) {
                Log.e(TAG, "CustomButton is null");
                return;
            }
            else
                Log.i(TAG, "Custom button id: " + customButton.getId() + ", type: " + customButton.getType());

            initChannels();
            initView();
        }
    }
}
