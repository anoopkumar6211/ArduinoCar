package com.barunster.arduinocar.views.edit_command;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.barunster.arduinocar.R;

/**
 * Created by itzik on 4/18/2014.
 */
public class AddCommandView extends LinearLayout {

    public AddCommandView(Context context) {
        super(context);

        init();
    }

    public AddCommandView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        this.addView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_edit_button, null));
    }
}
