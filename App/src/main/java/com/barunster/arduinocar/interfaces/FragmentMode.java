package com.barunster.arduinocar.interfaces;

/**
 * Created by itzik on 3/9/14.
 */
public interface FragmentMode {
    public void onConnected();
    public void onConnecting();
    public void onDisconnected();
    public void onFullScreen();
    public void onExitFullScreen();
}
