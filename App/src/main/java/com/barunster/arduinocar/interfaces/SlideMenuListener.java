package com.barunster.arduinocar.interfaces;

/**
 * Created by itzik on 3/13/14.
 */
public interface SlideMenuListener {
    public void onSlideMenuOpen();
    public void onSlideMenuClosed();
    public void onSettingsChanged();
    public void onControllerSelected(long id);
}
