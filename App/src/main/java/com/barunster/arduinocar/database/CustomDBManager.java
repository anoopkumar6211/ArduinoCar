package com.barunster.arduinocar.database;

import android.content.Context;
import android.util.Log;

import com.barunster.arduinocar.custom_controllers_obj.CustomButton;
import com.barunster.arduinocar.custom_controllers_obj.CustomCommand;
import com.barunster.arduinocar.custom_controllers_obj.CustomController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by itzik on 3/14/14.
 */
public class CustomDBManager {

    // TODO Reduce the amount of opening and closing the database.
    private static final String TAG = CustomDBManager.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static CustomDBManager instance;

    private CustomCommandsDataSource customCommandsDataSource;
    private ControllersDataSource controllersDataSource;
    private CustomButtonsDataSource customButtonsDataSource;
    private OnControllerDataChanged onControllerDataChanged;

    public static CustomDBManager getInstance(){
        if (instance == null)
            throw new NullPointerException("Custom DB Manager is not initialized");

        return instance;
    }
    private List<CustomButton> customButtonList = new ArrayList<CustomButton>();

    public CustomDBManager(Context context){
        if (instance != null)
            throw new ExceptionInInitializerError("Custom DB already has an instance");

        customButtonsDataSource = new CustomButtonsDataSource(context);
        controllersDataSource = new ControllersDataSource(context);
        customCommandsDataSource = new CustomCommandsDataSource(context);

        instance = this;
    }

    /* Controller */
    public CustomController getControllerById(long id ){

        BaseDataSource.leaveOpen = true;

        customButtonList = new ArrayList<CustomButton>();

        if (DEBUG)
            Log.i(TAG, "Getting controller by id, Id: " + id);

        CustomController customController = controllersDataSource.get(DB.Column.ID, String.valueOf(id));

        for (CustomButton btn : customButtonsDataSource.getList(DB.Column.ID_CONTROLLER, String.valueOf(id))) {
            btn.setCustomCommand(getCommandByButtonId( (int) btn.getId()) );

            if (DEBUG && btn.getCustomCommand() != null)
                Log.i(TAG, "Button has command");

            // Adding the button tot the list
            customButtonList.add(btn);
        }

        // Setting the button list to the controller.
        if (customController != null)
        {
            customController.setButtons(customButtonList);
        }

        BaseDataSource.leaveOpen = false;
        Log.e(TAG, "end");
        customCommandsDataSource.close();

        return customController;
    }

    public List<CustomController> getAllControllers(){
        List<CustomController> list = new ArrayList<CustomController>();
        List<CustomController> tmpList;

        tmpList = controllersDataSource.getAll();

        for (CustomController controller : tmpList)
        {
            list.add(getControllerById(controller.getId()));
        }

        return list;
    }

    public List<String> controllersToStringList(List<CustomController> list){
        List<String> result = new ArrayList<String>();

        for (CustomController controller : list)
        {
            result.add(controller.getName());
        }

        return result;
    }

    public long addController(CustomController customController){
        return controllersDataSource.add(customController);
    }

    public boolean updateController(CustomController customController){
        return controllersDataSource.update(customController, DB.Column.ID, String.valueOf(customController.getId()));
    }

    public void deleteControllerById(long id) {
        controllersDataSource.delete(DB.Column.ID, String.valueOf(id));
        customButtonsDataSource.delete(DB.Column.ID_CONTROLLER, String.valueOf(id));
        // TODO delete commands
    }

    /* Button */
    public CustomButton getButtonById(long buttonId) {
        return customButtonsDataSource.get(DB.Column.ID, String.valueOf(buttonId));
    }

    public long addButton(CustomButton customButton){
        return customButtonsDataSource.add(customButton);
    }

    public boolean deleteButtonById(long id){
        boolean isDeleted = customButtonsDataSource.delete(DB.Column.ID, String.valueOf(id));
        customCommandsDataSource.delete(DB.Column.ID_BUTTON, String.valueOf(id));
        dispatchControllerChangedEvent();
        return isDeleted;
    }

    public boolean updateButtonById(CustomButton customButton){
        boolean isUpdated= customButtonsDataSource.update(customButton, DB.Column.ID, String.valueOf(customButton.getId()));
        dispatchControllerChangedEvent();
        return isUpdated;
    }

    /* Command */
    public CustomCommand getCommandByButtonId(int buttonId) {
       return customCommandsDataSource.get(DB.Column.ID_BUTTON, String.valueOf(buttonId));
    }

    public void addCommand(CustomCommand customCommand) {
        customCommandsDataSource.add(customCommand);
        dispatchControllerChangedEvent();
    }

    public interface OnControllerDataChanged{
        public void onChanged();
    }

    public void setOnControllerDateChanged(OnControllerDataChanged onControllerDataChanged) {
        this.onControllerDataChanged = onControllerDataChanged;
    }

    /*Interface */
    private void dispatchControllerChangedEvent(){
        if (onControllerDataChanged != null)
            onControllerDataChanged.onChanged();
    }
}
