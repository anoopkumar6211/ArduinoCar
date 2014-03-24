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
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 10/5/13.
 */


public class ControllersListAdapter extends BaseAdapter {

    private static final String TAG = ControllersListAdapter.class.getSimpleName();

    private Activity mActivity;

    private List<CustomController> listData = new ArrayList<CustomController>();

    //View
    private View row;

    private TextView textView;

    public ControllersListAdapter(Activity activity){
        mActivity = activity;
    }

    public ControllersListAdapter(Activity activity, List<CustomController> listData){
        mActivity = activity;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public CustomController getItem(int i) {
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

        textView.setText(listData.get(position).getName());

        return row;
    }
}
