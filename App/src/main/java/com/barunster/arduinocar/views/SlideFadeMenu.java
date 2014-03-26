package com.barunster.arduinocar.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.barunster.arduinocar.interfaces.SlideMenuListener;

/**
 * Created by itzik on 3/9/14.
 */
public class SlideFadeMenu extends LinearLayout {

    // TODO fix animation to load from XML
    // TODO when menu closed menu button half faded
    // TODO Audo Closing of the menu after some idle time

    private static final String TAG = SlideFadeMenu.class.getSimpleName();

    private Button btnMenuVisibility, btnToggleConnectionState,btnAppSettings, btnControllerSelection, btnControllerOptions;

    private boolean isShowing = false;

    private Animation slideFadeIn, slideFadeOut;

    private final static int spaceDurationBetweenButtons = 200, FADE_DURATION = 300;

    private OnClickListener clickMenu, clickToggleConnection, clickAppSettings, clickControllerSelection;

    private SlideMenuListener slideMenuListener;

    public SlideFadeMenu(Context context) {
        super(context);

        Log.d(TAG, "Created");
        setOrientation(HORIZONTAL);

        initAnimations();
        initButtons();
        initListeners();
        initMenuVisibilityListener();
    }

    private void initButtons(){
        btnMenuVisibility = new Button(getContext());
        btnMenuVisibility.setText("M");
        addView(btnMenuVisibility);

        btnToggleConnectionState = new Button(getContext());
        btnToggleConnectionState.setText("C");
        btnToggleConnectionState.setAlpha(0.0f);
        addView(btnToggleConnectionState);

        btnAppSettings = new Button(getContext());
        btnAppSettings.setAlpha(0.0f);
        btnAppSettings.setText("S");
        addView(btnAppSettings);

        btnControllerSelection = new Button(getContext());
        btnControllerSelection.setAlpha(0.0f);
        btnControllerSelection.setText("CS");
        addView(btnControllerSelection);

        btnControllerOptions = new Button(getContext());
        btnControllerOptions.setAlpha(0.0f);
        btnControllerOptions.setText("CO");
        addView(btnControllerOptions);
    }

    private void initAnimations(){
//        slideFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_fade_in);
//        slideFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_fade_out);
        slideFadeIn = AnimationUtils.makeInAnimation(getContext(), true);
        slideFadeOut = AnimationUtils.makeOutAnimation(getContext(), false);
    }

    private void initMenuVisibilityListener(){
        btnMenuVisibility.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Menu Clicked");

                if (isShowing)
                {
                    if ( ((FrameLayout)SlideFadeMenu.this.getParent()).getChildAt(1) != null);
                        getParent().bringChildToFront(((FrameLayout)SlideFadeMenu.this.getParent()).getChildAt(1));

                    slideFadeOut.setDuration(1 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnToggleConnectionState.startAnimation(slideFadeOut);

                    slideFadeOut.setDuration(2 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnAppSettings.startAnimation(slideFadeOut);

                    slideFadeOut.setDuration(3 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnControllerSelection.startAnimation(slideFadeOut);

                    slideFadeOut.setDuration(4 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnControllerOptions.startAnimation(slideFadeOut);

                    slideFadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            btnControllerSelection.setAlpha(0.0f);
                            btnAppSettings.setAlpha(0.0f);
                            btnToggleConnectionState.setAlpha(0.0f);
                            btnControllerOptions.setAlpha(0.0f);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    if (slideMenuListener != null)
                        slideMenuListener.onSlideMenuClosed();
                    else Log.e(TAG, "No slide menu listener");

                }
                else
                {

                    getParent().bringChildToFront(SlideFadeMenu.this);

                    btnToggleConnectionState.setAlpha(1.0f);
                    slideFadeIn.setDuration(1 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnToggleConnectionState.startAnimation(slideFadeIn);

                    btnAppSettings.setAlpha(1.0f);
                    slideFadeIn.setDuration(2 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnAppSettings.startAnimation(slideFadeIn);

                    btnControllerSelection.setAlpha(1.0f);
                    slideFadeIn.setDuration(3 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnControllerSelection.startAnimation(slideFadeIn);

                    btnControllerOptions.setAlpha(1.0f);
                    slideFadeIn.setDuration(3 * spaceDurationBetweenButtons + FADE_DURATION);
                    btnControllerOptions.startAnimation(slideFadeIn);

                    if (slideMenuListener != null)
                        slideMenuListener.onSlideMenuOpen();
                    else Log.e(TAG, "No slide menu listener");

                }

                isShowing = !isShowing;
            }
        });
    }

    private void initListeners(){
        btnToggleConnectionState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing)
                {
                    if (clickToggleConnection != null)
                        clickToggleConnection.onClick(v);
                    else Log.e(TAG, "No toggle connection click listener");

                    btnMenuVisibility.performClick();
                }
            }
        });

        btnAppSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing)
                {
                    if (clickAppSettings != null)
                        clickAppSettings.onClick(v);
                    else Log.e(TAG, "No app setting click listener");

                    btnMenuVisibility.performClick();
                }
            }
        });

        btnControllerSelection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing)
                {
                    if (clickControllerSelection != null)
                        clickControllerSelection.onClick(v);
                    else Log.e(TAG, "No controller setting click listener");

                    btnMenuVisibility.performClick();
                }
            }
        });

        btnControllerOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing)
                {
                   /* if (slideMenuListener != null)
                        slideMenuListener.onControllerSelected("");
                    else Log.e(TAG, "No controller setting click listener");*/

                    btnMenuVisibility.performClick();
                }
            }
        });
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void closeMenu(){
        isShowing = true;
        btnMenuVisibility.performClick();
    }

    public void openMenu(){
        isShowing = false;
        btnMenuVisibility.performClick();
    }

    public void setSlideMenuListener(SlideMenuListener slideMenuListener) {
        this.slideMenuListener = slideMenuListener;
    }

    public void setOnAppSettingClicked(OnClickListener onAppSettingClicked) {
        this.clickAppSettings = onAppSettingClicked;
    }

    public void setOnControllerSelectionClicked(OnClickListener onControllerOptionsClicked) {
        this.clickControllerSelection = onControllerOptionsClicked;
    }

    public void setOnToggleConnectionStateClicked(OnClickListener onToggleConnectionStateClicked){
        this.clickToggleConnection = onToggleConnectionStateClicked;
    }
}
