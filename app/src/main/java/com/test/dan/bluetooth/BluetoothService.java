package com.test.dan.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by dan on 2015/11/16.
 */
public class BluetoothService {
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private Handler handler;
    private BluetoothDevice bluetoothDevice;
    private int mState;

    private static final UUID UUID_OTHER_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//for devices like hc-05
    private static final UUID UUID_ANDROID_DEVICE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private String TAG = "Bluetooth Service";

    // constructor
    public BluetoothService(BluetoothDevice bt_device,Handler hd){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bt_device;
        handler = hd;
        mState = BluetoothState.STATE_NONE;
    }

    public synchronized void setHandler(Handler hd){
        //这里需要添加一些同步的措施吗?
        this.handler = hd;
    }

    // Set the current state of the chat connection
    // state : An integer defining the current connection state
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        handler.obtainMessage(BluetoothState.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // Return the current connection state.
    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect() {
        // Cancel any thread attempting to make a connection
        if (mState == BluetoothState.STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();
        setState(BluetoothState.STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = handler.obtainMessage(BluetoothState.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothState.DEVICE_NAME, device.getName());
//        bundle.putString(BluetoothState.DEVICE_ADDRESS, device.getAddress());
//        msg.setData(bundle);
//        handler.sendMessage(msg);

        setState(BluetoothState.STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();

            // stop the thread and before exit??
            // how to do it
            // connectThread.interrupt();
            // connectThread.stop();

            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(BluetoothState.STATE_NONE);
    }

    // Write to the ConnectedThread in an unsynchronized manner
    // out : The bytes to write
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != BluetoothState.STATE_CONNECTED) {
                Log.e("error","try to write data to device , but you didn't connect a device");
                return;
            }
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }


    //connect thread for connect to another device
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final BluetoothAdapter mBluetoothAdapter;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);
            } catch (IOException e) {
            }
            mmSocket = tmp;

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
            }

            // Do work to manage the connection (in a separate thread)
            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            // Start the connected thread(after connected a bluetooth device)
            connected(mmSocket, mmDevice);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    //manage data sharing between bluetooth devices
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private final int WEAK_UP_BYTE = 0xFF;

        private int startFlag = 0;
        private int wake_up_count = 0;
        private final String TAG = "ConnectedThread";

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
                e.printStackTrace();
                Log.i(TAG + " Excep",e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            startFlag = 0;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            ArrayList<Integer> arr_byte = new ArrayList<Integer>();

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    //we just read data byte by byte
                    //Blocks until one byte has been read, the end of the source
                    //stream is detected or an exception is thrown.
                    int data = mmInStream.read();

                    arr_byte.add(data);

                    if(data == WEAK_UP_BYTE) {
                        if(startFlag == 0){
                            //idle state
                            if(wake_up_count == 0 || arr_byte.get(arr_byte.size() - 2) == WEAK_UP_BYTE){
                                wake_up_count++;
                            }
                            else wake_up_count = 0;

                            if(wake_up_count >= 4){
                                startFlag = 1;
                                wake_up_count = 0;
                                //clear array list for next time
                                arr_byte.clear();
                            }
                        } else if(startFlag == 1) {
                            //connection state
                            if(arr_byte.size() != 0 && (wake_up_count == 0 || arr_byte.get(arr_byte.size() - 2) == WEAK_UP_BYTE))wake_up_count++;
                            else wake_up_count = 0;

                            if(wake_up_count >= 4) {
                                //we just send received data to current fragment
                                buffer = new byte[arr_byte.size()];
                                for(int i = 0 ; i < arr_byte.size() -  wake_up_count; i++) {
                                    buffer[i] = arr_byte.get(i).byteValue();
                                }
                                // Send the obtained bytes to the UI Activity
                                handler.obtainMessage(BluetoothState.MESSAGE_READ
                                        , buffer.length, -1, buffer).sendToTarget();
                                wake_up_count = 0;
                                startFlag = 0;
                            }
                        }
                    }

/*                    if(data == StartByte) {

                    } else if(data == EndByte) {
                        //the end byte(0x0D) occur , we just send received data to current fragment
                        buffer = new byte[arr_byte.size()];
                        for(int i = 0 ; i < arr_byte.size() ; i++) {
                            buffer[i] = arr_byte.get(i).byteValue();
                        }
                        // Send the obtained bytes to the UI Activity
                        handler.obtainMessage(BluetoothState.MESSAGE_READ
                                , buffer.length, -1, buffer).sendToTarget();
                        //in next circle , we store received data to a new arrayList
                        if(arr_byte == null) {
                            arr_byte = new ArrayList<Integer>();
                        } else {
                            arr_byte.clear();
                        }
                    } else {
                        //add data to arraylist until 0x0D occurs
                        arr_byte.add(data);
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("exception",e.getMessage());
                    break;
                }
            }
        }

        public void flushInstream(){
            //mmInStream
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                handler.obtainMessage(BluetoothState.MESSAGE_WRITE
                        , -1, -1, bytes).sendToTarget();
            } catch (IOException e){
                e.printStackTrace();
                Log.i("Exception",e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                //mmInStream.close();
                //mmOutStream.close();
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}