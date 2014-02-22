package com.barunster.arduinocar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by itzik on 10/5/13.
 */


public class SimpleListAdapter extends BaseAdapter {

    Activity mActivity;

    List<String> listData = new ArrayList<String>();

    //View
    View row;

    TextView textView;

    public SimpleListAdapter(Activity activity){
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        row = view;


        if ( row == null)
        {
            row =  ( (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate(R.layout.row_text_view, null);

        }

        textView = (TextView) row.findViewById(R.id.txt_simple_row);

        textView.setText(listData.get(position));

        return row;
    }

    public void addRow(String data){
        listData.add(data);

        notifyDataSetChanged();
    }
}
