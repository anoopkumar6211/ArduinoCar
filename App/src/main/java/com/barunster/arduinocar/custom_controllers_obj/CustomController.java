package com.barunster.arduinocar.custom_controllers_obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomController {

    private long id;
    private String name;
    private int rows, columns;
    private List<CustomButton> buttons = new ArrayList<CustomButton>();

    public CustomController(String name, int rows, int columns){
        this.name = name;
        this.rows = rows;
        this.columns = columns;
    }

    public CustomController(long id , String name, int rows, int columns){
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
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

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean removeButtonById(long id){

        for (CustomButton btn : buttons)
            if (btn.getId() == id) {
                buttons.remove(btn);
                return true;
            }

        return false;
    }

    public CustomButton getCustomButtonById(long id)
    {
        for (CustomButton btn : buttons)
            if (btn.getId() == id)
                return btn;

        return null;
    }
}
