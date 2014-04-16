package com.barunster.arduinocar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.barunster.arduinocar.R;
import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.fragments.ArduinoLegoFragment;
import com.barunster.arduinocar.views.DropZoneImage;

/**
 * Created by itzik on 3/11/14.
 */
public class ButtonGridSelectionAdapter extends BaseAdapter {

    // Views
    private DropZoneImage buttonImage;

    private Context context;
    public ButtonGridSelectionAdapter(Context context){
        this.context = context;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            buttonImage = new DropZoneImage(context);
            buttonImage.setLayoutParams(new GridView.LayoutParams(100, 100));
            buttonImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            buttonImage.setPadding(8, 8, 8, 8);
        } else {
            buttonImage = (DropZoneImage) convertView;
        }

        buttonImage.setImageDrawable(context.getResources().getDrawable(mThumbIds[position]));

        if (position == 1)
        {
            buttonImage.setSize(4);
            buttonImage.setOrientation(LinearLayout.HORIZONTAL);
            buttonImage.setType(CustomButton.buttonTags.get(position));
            buttonImage.setDimensions(new int[]{ 2, 4});
        }
        else if (position == 2)
        {
            buttonImage.setSize(4);
            buttonImage.setOrientation(LinearLayout.VERTICAL);
            buttonImage.setType(CustomButton.buttonTags.get(position));
            buttonImage.setDimensions(new int[]{ 4, 1});
        }
        else if (position == 3)
        {
            buttonImage.setSize(3);
            buttonImage.setOrientation(LinearLayout.HORIZONTAL);
            buttonImage.setType(CustomButton.buttonTags.get(1));
            buttonImage.setDimensions(new int[]{ 1, 3});
        }
        else if (position == 4)
        {
            buttonImage.setSize(3);
            buttonImage.setOrientation(LinearLayout.VERTICAL);
            buttonImage.setType(CustomButton.buttonTags.get(2));
            buttonImage.setDimensions(new int[]{3, 1});
        }
        else if (position == 5)
        {
            buttonImage.setSize(2);
            buttonImage.setOrientation(LinearLayout.HORIZONTAL);
            buttonImage.setType(CustomButton.buttonTags.get(1));
            buttonImage.setDimensions(new int[]{1, 2});
        }
        else if (position == 6)
        {
            buttonImage.setSize(2);
            buttonImage.setOrientation(LinearLayout.VERTICAL);
            buttonImage.setType(CustomButton.buttonTags.get(2));
            buttonImage.setDimensions(new int[]{2, 1});
        }
        else
        {
            buttonImage.setSize(position + 1);
            buttonImage.setType(CustomButton.buttonTags.get(0));
            buttonImage.setDimensions(new int[]{1, 1});
        }

        return buttonImage;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.stick_button,
            R.drawable.abc_ic_go,
            android.R.drawable.presence_online,
            android.R.drawable.arrow_up_float,
            android.R.drawable.btn_plus,
            android.R.drawable.btn_minus,
            android.R.drawable.btn_star
    };


}
