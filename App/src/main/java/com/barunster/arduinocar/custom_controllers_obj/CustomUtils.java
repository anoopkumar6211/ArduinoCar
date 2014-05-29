package com.barunster.arduinocar.custom_controllers_obj;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by itzik on 5/25/2014.
 */
public class CustomUtils {

    public static float getDensityMultiple(Resources res){
        float multi = 1.0f;

        switch (res.getDisplayMetrics().densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                multi = 0.75f;
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                multi = 1.0f;
                break;

            case DisplayMetrics.DENSITY_HIGH:
                multi = 1.5f;
                break;

            case DisplayMetrics.DENSITY_XHIGH:
                multi = 2.f;
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
                multi = 3.f;
                break;
        }

        return multi;
    }
}
