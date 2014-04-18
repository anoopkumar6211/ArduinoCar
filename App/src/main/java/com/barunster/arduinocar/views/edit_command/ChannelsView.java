package com.barunster.arduinocar.views.edit_command;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barunster.arduinocar.R;

import java.util.zip.Inflater;

/**
 * Created by itzik on 4/17/2014.
 */
public class ChannelsView extends LinearLayout {

    private int channelNumber;
    private TextView txtChannel;

    public ChannelsView(Context context) {
        super(context);

        init();
    }

    public ChannelsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        int count = 0;
        for (String channel : getResources().getStringArray(R.array.motor_channels))
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            txtChannel = (TextView) inflater.inflate(R.layout.simple_text_view, null);
            txtChannel.setText(channel);
            txtChannel.setTextColor(Color.RED);
            txtChannel.setId(count);

            txtChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (v.getId() != channelNumber)
                    {
                        setSelectedChannel(v.getId(), channelNumber);
                    }
                }
            });

            // Add the view
            addView(txtChannel);

            count++;
        }

        // Set default to the first channel.
        setSelectedChannel(0,-1);
    }

    private void setSelectedChannel(int newNumber, int oldNumber){
        if (oldNumber > -1)
            ((TextView)this.getChildAt(oldNumber)).setTextColor(Color.RED);

        ((TextView)this.getChildAt(newNumber)).setTextColor(Color.WHITE);
        this.channelNumber = newNumber;
    }

    public void setSelectedChannel(String channel){
        ((TextView)this.getChildAt(channelNumber)).setTextColor(Color.RED);
        for (int i = 0; i < this.getChildCount() ; i++)
        {
            if ( ((TextView) this.getChildAt(i)).getText().toString().equals(channel))
            {
                ((TextView) this.getChildAt(i)).setTextColor(Color.WHITE);
                this.channelNumber = i;
            }
        }
    }

    public String getSelectedChannel(){
        return ((TextView) this.getChildAt(channelNumber)).getText().toString();
    }


}
