package com.barunster.arduinocar.custom_controllers_obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomController {

    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_LARGE = 3;

    private long id;
    private String name;
    private int rows = -1, columns = -1;
    private List<CustomButton> buttons = new ArrayList<CustomButton>();
    private int brickSize = -1;

    public CustomController(String name, int rows, int columns){
        this.name = name;
        this.rows = rows;
        this.columns = columns;
    }

    public CustomController(String name, int brickSize){
        this.name = name;
        this.brickSize = brickSize;
    }

    /** For the database*/
    public CustomController(long id , String name, int rows, int columns, int brickSize){
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.brickSize = brickSize;
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

    public CustomButton getCustomButtonById(long id){
        for (CustomButton btn : buttons)
            if (btn.getId() == id)
                return btn;

        return null;
    }

    public int getBrickSize() {
        return brickSize;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
