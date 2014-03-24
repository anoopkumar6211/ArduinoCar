package com.barunster.arduinocar.custom_controllers_obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomController {

    private long id;
    private String name;
    private List<CustomButton> buttons = new ArrayList<CustomButton>();

    public CustomController(long id){
        this.id = id;
    }

    public CustomController(long id , String name){
        this.id = id;
        this.name = name;
    }

    public CustomController(String name){
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setButtons(List<CustomButton> buttons) {
        this.buttons = buttons;
    }

    public List<CustomButton> getButtons() {
        return buttons;
    }
}
