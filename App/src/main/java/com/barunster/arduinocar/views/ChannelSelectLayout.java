package com.barunster.arduinocar.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barunster.arduinocar.R;

/**
 * Created by itzik on 3/18/14.
 */
public class ChannelSelectLayout extends LinearLayout {
    public ChannelSelectLayout(Context context) {
        super(context);

        init();
    }

    public ChannelSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){

        TextView txtChannel;
        for (String channel : getResources().getStringArray(R.array.motor_channels))
        {
            txtChannel = new TextView(getContext());

            txtChannel.setText(channel);

            if (getTag() == null)
                setTag(txtChannel);

            txtChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ((TextView)getTag()).setTextColor(Color.LTGRAY);
                    setTag(v);
                }
            });

            if ( ((EditText)getTag()).getText().equals(channel))
                txtChannel.setTextColor(Color.WHITE);

            addView(txtChannel);

        }
    }
}
