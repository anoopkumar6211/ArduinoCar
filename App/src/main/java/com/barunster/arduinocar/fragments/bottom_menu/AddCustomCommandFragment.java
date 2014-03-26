package com.barunster.arduinocar.fragments.bottom_menu;


import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.barunster.arduinocar.ArduinoCarAppObj;
import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.RadioGroupGridAdapter;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;

/**
 * Created by itzik on 3/24/14.
 */
public class AddCustomCommandFragment extends Fragment {

    /*Views*/
    private View mainView;

    private int buttonType;
    private long buttonId;


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

        mainView = inflater.inflate(R.layout.fragment_add_command , null);

        initView();

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


    private void initView(){
        // Channel Selection
        TextView txtChannel;
        for (String channel : getResources().getStringArray(R.array.motor_channels))
        {
            txtChannel = (TextView) getActivity().getLayoutInflater().inflate(R.layout.simple_text_view, null);
            txtChannel.setText(channel);

            if (mainView.findViewById(R.id.linear_channels).getTag() == null)
                mainView.findViewById(R.id.linear_channels).setTag(txtChannel);

            txtChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ( (TextView) mainView.findViewById(R.id.linear_channels).getTag()).setTextColor(Color.LTGRAY);
                    mainView.findViewById(R.id.linear_channels).setTag(v);
                }
            });

            if ( ( (TextView) mainView.findViewById(R.id.linear_channels).getTag()).getText().equals(channel))
                txtChannel.setTextColor(Color.WHITE);

            ( (LinearLayout) mainView.findViewById(R.id.linear_channels)).addView(txtChannel);

        }

        // Command Type Selection
        RadioButton radioButton;
        int list[] = new int[0];
        switch (buttonType)
        {
            case CustomButton.BUTTON_TYPE_SIMPLE:
                list = CustomCommand.regularButtonCommandTypes;
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_HORIZONTAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;

            case CustomButton.BUTTON_TYPE_SLIDE_VERTICAL:
                list = CustomCommand.slideButtonLayoutCommandTypes;
                break;
        }

        final RadioGroupGridAdapter radioGroupGridAdapter = new RadioGroupGridAdapter(getActivity(), list);
        ((GridView) mainView.findViewById(R.id.grid_radio_group)).setAdapter(radioGroupGridAdapter);

        // Submit Button
        mainView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioGroupGridAdapter.getSelectedButton() != null)
                {
                    int commandType = radioGroupGridAdapter.getSelectedButton().getId();
                    CustomCommand customCommand = new CustomCommand(buttonId,
                            commandType,
                            ((TextView)mainView.findViewById(R.id.linear_channels).getTag()).getText().toString());

                    app.getCustomDBManager().getCustomCommandsDataSource().addCommand(customCommand);

                    ((MainActivity)getActivity()).getSlidingUpPanelLayoutContainer().collapsePane();
                }
                else
                    Toast.makeText(getActivity(), "Please select a command", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
