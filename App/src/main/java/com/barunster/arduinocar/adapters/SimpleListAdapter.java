package com.barunster.arduinocar.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.barunster.arduinocar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/5/13.
 */


public class SimpleListAdapter extends BaseAdapter {

    private static final String TAG = SimpleListAdapter.class.getSimpleName();

    private Activity mActivity;

    private List<String> listData = new ArrayList<String>();
    private List<String> listTags = new ArrayList<String>();

    private int textColor = -1;

    boolean useTags = false;

    //View
    private View row;

    private TextView textView;

    public SimpleListAdapter(Activity activity){
        mActivity = activity;
    }

    public SimpleListAdapter(Activity activity, List<String> listData){
        mActivity = activity;
        this.listData = listData;
    }

    public SimpleListAdapter(Activity activity, List<String> listData, List<String> listTags){
        mActivity = activity;
        this.listData = listData;
        this.listTags = listTags;

        useTags = true;
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

        if (textColor != -1)
            textView.setTextColor(textColor);

        textView.setText(listData.get(position));

        if (useTags)
            row.setTag(listTags.get(position));

        return row;
    }

    public void addRow(String data){

        if (useTags)
        {
            Log.e(TAG, "Trying to add data to a list using tags tags!");
            return;
        }

        listData.add(data);

        notifyDataSetChanged();



        useTags = false;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }
}
