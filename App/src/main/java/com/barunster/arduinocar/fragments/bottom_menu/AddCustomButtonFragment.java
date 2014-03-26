package com.barunster.arduinocar.fragments.bottom_menu;


import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.barunster.arduinocar.MainActivity;
import com.barunster.arduinocar.R;
import com.barunster.arduinocar.adapters.ButtonGridSelectionAdapter;
import com.barunster.arduinocar.views.DropZoneImage;

/**
 * Created by itzik on 3/24/14.
 */
public class AddCustomButtonFragment extends Fragment {
    /*Views*/
    private View mainView;
    private DropZoneImage dropZoneImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_add_custom_button, null);

        initViews();

        return mainView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initViews() {
        ButtonGridSelectionAdapter buttonGridSelectionAdapter = new ButtonGridSelectionAdapter(getActivity());
        ((GridView) mainView.findViewById(R.id.grid_buttons)).setAdapter(buttonGridSelectionAdapter);

        ((GridView) mainView.findViewById(R.id.grid_buttons)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

//                Log.d(TAG, "GridItemSelected");
                ClipData data = ClipData.newPlainText("", "");

                dropZoneImage = (DropZoneImage) v;
                dropZoneImage.setOnDrag(true);

                v.startDrag(data,  // the data to be dragged
                        ImageDragShadowBuilder.fromResource(getActivity(), (Integer) v.getTag()),  // the drag shadow builder
                        v,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );

                ((MainActivity)getActivity()).getSlidingUpPanelLayoutContainer().collapsePane();

                return false;
            }
        });
    }

    public DropZoneImage getDropZoneImage() {
        return dropZoneImage;
    }
}

class ImageDragShadowBuilder extends View.DragShadowBuilder {
    private Drawable shadow;

    private ImageDragShadowBuilder() {
        super();
    }

    public static View.DragShadowBuilder fromResource(Context context, int drawableId) {
        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = context.getResources().getDrawable(drawableId);
        if (builder.shadow == null) {
            throw new NullPointerException("Drawable from id is null");
        }

        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder fromBitmap(Context context, Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

        builder.shadow = new BitmapDrawable(context.getResources(), bmp);
        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.x = shadow.getMinimumWidth();
        shadowSize.y = shadow.getMinimumHeight();

        shadowTouchPoint.x = (int)(shadowSize.x / 2);
        shadowTouchPoint.y = (int)(shadowSize.y / 2);
    }
}

