package com.barunster.arduinocar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by itzik on 10/12/13.
 */
public class ConnectAsClientThread extends Thread {

    private final String TAG = ConnectAsClientThread.class.getSimpleName();

    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;

    // Default UUID
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private Handler handler;
    private Message msg;

    public ConnectAsClientThread(BluetoothDevice device) {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // Use the UUID of the device that discovered // TODO Maybe need extra device object
            if (mmDevice != null)
            {
                Log.i(TAG, "Device Name: " + mmDevice.getName());
                Log.i(TAG, "Device UUID: " + mmDevice.getUuids()[0].getUuid());
                tmp = device.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());

            }
            else Log.d(TAG, "Device is null.");
        }
        catch (NullPointerException e)
        {
            Log.d(TAG, " UUID from device is null, Using Default UUID");
            try {
                tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        catch (IOException e) { }


        bluetoothSocket = tmp;
    }

    public void run() {
        Log.d(TAG, "RUN");
        if (bluetoothSocket != null)
        {
//            // Cancel discovery because it will slow down the connection
//            if (mBluetoothAdapter.isDiscovering())
//                mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception

                bluetoothSocket.connect();

                msg = new Message();

                msg.obj = bluetoothSocket;
                msg.what = 1;
                handler.sendMessage(msg);

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    Log.d(TAG, " Unable to connect trying again...");
                    Method m = null;
                    try {
                        m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                        try {
                            bluetoothSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
                            try {
                                bluetoothSocket.connect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                            }

                            msg = new Message();

                            msg.obj = bluetoothSocket;
                            msg.what = 1;
                            handler.sendMessage(msg);

                            return;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }


                    bluetoothSocket.close();
                    connectException.printStackTrace();

                    msg = new Message();
                    msg.obj = mmDevice;
                    msg.what = 2;
                    handler.sendMessage(msg);

                } catch (IOException closeException) { }
                return;
                }

        }
        else
        {

            msg = new Message();
            msg.obj = mmDevice;
            msg.what = 2;
            handler.sendMessage(msg);
        }

        // Close the thread
        Thread.currentThread().interrupt();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) { }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
