package com.barunster.arduinocar.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.views.DropZoneImage;

/**
 * Created by itzik on 3/11/14.
 */
public class RadioGroupGridAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = RadioGroupGridAdapter.class.getSimpleName();
    private static final boolean DEBUG = false;

    // Views
    private RadioButton radioButton, selectedButton;
    private int [] listId;
    private Context context;
    private RadioCheckedListener radioCheckedListener;
    private int selectedId;

    public RadioGroupGridAdapter(Context context, int[] listId, int selectedId){
        this.context = context;
        this.listId = listId;
        this.selectedId = selectedId;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listId.length;
    }

    @Override
    public Object getItem(int position) {
        return listId[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            radioButton = new RadioButton(context);
            radioButton.setTextColor(Color.WHITE);
            radioButton.setTextSize(15f);
            radioButton.setOnCheckedChangeListener(this);
        } else {
            radioButton = (RadioButton) convertView;
        }

        radioButton.setId(listId[position]);
        radioButton.setText(context.getResources().getString(listId[position]));


        if (DEBUG)
            Log.d(TAG, "selected " + selectedId + " cur " + radioButton.getId());

        // Selecting the type already assigned for the button.
        if (selectedId == radioButton.getId()) {
            radioButton.setChecked(true);
            selectedButton = radioButton;
        }

        return radioButton;
    }

    public RadioButton getSelectedButton() {
        return selectedButton;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//        Log.d(TAG, "onCheckedChanged");

        if (selectedButton == null) {
            selectedButton = (RadioButton) buttonView;
            radioCheckedListener.onRadioChecked(selectedButton.getId());
            return;
        }

        if (buttonView != selectedButton)
        {
            selectedButton.setChecked(false);
            selectedButton = (RadioButton) buttonView;

            if (radioCheckedListener != null)
                radioCheckedListener.onRadioChecked(selectedButton.getId());
        }

    }

    public interface RadioCheckedListener{
        public void onRadioChecked(int id);
    }

    public void setRadioCheckedListener(RadioCheckedListener radioCheckedListener) {
        this.radioCheckedListener = radioCheckedListener;
    }
}
