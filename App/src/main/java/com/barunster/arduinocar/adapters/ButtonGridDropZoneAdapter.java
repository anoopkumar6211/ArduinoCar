/*
package com.barunster.arduinocar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


*/
/**
 * Created by itzik on 3/11/14.
 *//*

public class ButtonGridDropZoneAdapter extends BaseAdapter {

    // Views
    private ImageView buttonImage;

    private Context context;
    private CustomControllerFragment.MyDragListener myDragListener;
    private int cellSize;

    public ButtonGridDropZoneAdapter(Context context, CustomControllerFragment.MyDragListener dragListener, int cellSize){
        this.context = context;
        this.myDragListener = dragListener;
        this.cellSize = cellSize;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return CustomControllerFragment.GRID_COLUMN_NUMBER * CustomControllerFragment.GRID_COLUMN_NUMBER ;
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
            buttonImage = new ImageView(context);
            buttonImage.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));
            buttonImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            buttonImage.setPadding(8, 8, 8, 8);
        } else {
            buttonImage = (ImageView) convertView;
        }

        buttonImage.setImageDrawable(context.getResources().getDrawable(R.drawable.abc_ab_bottom_solid_dark_holo));

        buttonImage.setOnDragListener(myDragListener);

        return buttonImage;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.abc_ab_bottom_solid_dark_holo
    };
}
*/
