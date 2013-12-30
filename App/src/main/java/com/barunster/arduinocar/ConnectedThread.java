package com.barunster.arduinocar;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by itzik on 10/1/13.
 */
public class ConnectedThread extends Thread {

    final String TAG = ConnectedThread.class.getSimpleName();

    // Basic
    public static final char COMM_END = '#';
    public static final char COMM_START = '$';
    public static final char COMM_LOG = '^';

    // Car
    public static final char COMM_STICK_DRIVE = '@';
    public static final char COMM_ACCELEROMETER_DRIVE = '*';
    public static final char COMM_STOP = '!';

    // IR
    public static final char COMM_READ_IR_SIGNAL = '*';
    public static final char COMM_SEND_IR_SIGNAL = 'S';

    private final BluetoothSocket mmSocket;
    private BufferedReader input;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    BufferedReader reader;

    private String line, command;
    private long time;

    public ConnectedThread(BluetoothSocket socket) {


        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("itzik", "Connected Thread Stream Exception");}

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        input = new BufferedReader(new InputStreamReader(mmInStream));
    }

    public void run() {


        while (!Thread.currentThread().isInterrupted() && mmSocket.isConnected())
        {

//            if (outputPrinter.checkError())
//            {
//                Log.d(TAG, "Printer Error");
//            }

            try {
                currentThread().sleep(100);
            } catch (InterruptedException e) {
                interrupt();
                e.printStackTrace();
            }
//            try {
//                while (input.ready())
//                {
//                    line = input.readLine();
//
//                    // printing received data.
//                    Log.d(TAG, " TCP incoming data: " + line);
//
//                    if (line.length() > 1)
//                    {
//                        int counter = 0;
//                        String temp;
//
//                        // If line doesn't have start command on it it will find it.
//                        if (line.charAt(0) != Command.START)
//                        {
//                            while (counter < line.length() && line.charAt(counter) != Command.START)
//                            {
//                                counter++;
//                            }
//
//                            temp = line.substring(counter);
//                            line = temp
//                            ;
//
//                            if (line.length() < 3)
//                            {
//                                return;
//                            }
//
//                        }
//
//                        // Command end
//                        if ( line.length() >= 3 && line.charAt( line.length() -1 ) == Command.END )
//                        {
//                            command = line.substring(1, line.length() -1 );
//
//                            Log.d("itzik", " Save Command: " + command);
//
//                            if ( messagesListener != null ) { messagesListener.onMessageReceived(command); }
//                            else { Log.d(TAG, " Dosent have a Message Listener."); }
//
//                            break;
//                        }
//
//                        // TODO handle half lines, handle more text exceptions
//                    }
//
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        if (!mmSocket.isConnected())
            Log.d(TAG, "socket is disconnected");

//        stopConnection();
    }

    public boolean write(String text){

        Log.d(TAG, "Text sent: " + text + " Length: " + text.length());

        // Adding the command brackets

        byte[] bytes = null;

        try {
            bytes = text.getBytes("UTF-8") ;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        int bytePos = 0;

        try {

            if (bytes != null)
            {
//                Log.d(TAG, "Byte sent length: " + bytes.length);
                int count = 50;

                if (bytes.length > count)
                {
                    do {
                        {
                            if ( (bytes.length - bytePos ) < count)
                            {
                                count = bytes.length - bytePos;
//                                Log.d(TAG, "count: " + count);
                            }

                            mmOutStream.write(bytes, bytePos, count);

                            bytePos += count;

//                            Log.d(TAG, "BytePos: " + bytePos);

                            try {
                                Thread.sleep(100 * 5);
                                //TODO find fastest sending time
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } while (bytePos < bytes.length);
                }
                else
                {
//                    Log.d(TAG, "Bytes sent to arduino");
                    mmOutStream.write(bytes);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            cancel();

            return false;
        }

        return true;
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {

        currentThread().interrupt();

        stopConnection();
    }

    public boolean isConnected(){
        return mmSocket.isConnected();
    }

    private void stopConnection() {
        Log.d(TAG, "Stopping Connection");

        if (input != null) {
            try {input.close();} catch (Exception e) {}
        }

        if (mmOutStream != null) {
            try {mmOutStream.close();} catch (Exception e) {}
        }

        if (mmSocket != null) {
            try {mmSocket.close();} catch (Exception e) {}
        }
    }
}

