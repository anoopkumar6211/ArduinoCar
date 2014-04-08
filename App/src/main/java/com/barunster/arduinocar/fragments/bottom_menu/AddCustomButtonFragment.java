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
import com.barunster.arduinocar.custom_controllers_obj.ImageDragShadowBuilder;
import com.barunster.arduinocar.fragments.CustomControllerFragment;
import com.barunster.arduinocar.fragments.MenuFragment;
import com.barunster.arduinocar.views.DropZoneImage;

/**
 * Created by itzik on 3/24/14.
 */
public class AddCustomButtonFragment extends MenuFragment {
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

                int draggedViewDrawableResourceId = R.drawable.stick_button; // TODO do a switch method to select the resource id by the button type.

                v.startDrag(data,  // the data to be dragged
                        ImageDragShadowBuilder.fromResource(getActivity(), draggedViewDrawableResourceId),  // the drag shadow builder
                        v,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );

                ((CustomControllerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAGMENT_TAG)).closeBottomMenu();

                return false;
            }
        });
    }
}


