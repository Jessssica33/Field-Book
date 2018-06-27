package com.fieldbook.tracker;

/**
 * Created by jessica on 3/29/18.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothServer {

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mDevice = null;
    private BluetoothSocket mSocket;

    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;

    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final String TAG = "BluetoothServer";

    private Intent mIntentSender;
    private Intent mIntentVoiceCmd;
    private Context mContext;

    public BluetoothServer(Context context) {
        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;

        mIntentSender = new Intent();
        mIntentSender.setAction("com.fieldbook.tracker.BluetoothServer.STATUSCHANGE");
        mIntentVoiceCmd = new Intent();
        mIntentVoiceCmd.setAction("com.fieldbook.tracker.BluetoothServer.VOICECMD");
    }

    public int init(BluetoothDevice device, BluetoothAdapter adapter) {

        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Cannot get bluetooth adapter");
            return -1;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, deviceName + ": " + deviceHardwareAddress);
                if (deviceName.equals("vm300")) {
                    mDevice = device;
                    break;
                }
            }
        }*/

        mDevice = device;
        mBluetoothAdapter = adapter;

        if (mDevice == null) {
            Log.e(TAG, "Cannot find pairedDevice");
            return -1;
        }

        mAcceptThread = new AcceptThread(mDevice);
        mAcceptThread.start();

        return 0;
    }

    public void write(byte[] bytes) {
        if (mConnectedThread == null) {
            return;
        }
        mConnectedThread.write(bytes);
    }

    public void cancelConnection() {
        if (mAcceptThread !=null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /*public void reConnect() {
        if (mConnectedThread != null && (mConnectedThread.isAlive()
                || mConnectedThread.isDaemon() || mConnectedThread.isInterrupted())) {
            mConnectedThread.cancel();
            mAcceptThread.cancel();
        }

        mAcceptThread = new AcceptThread(mDevice);
        mAcceptThread.start();

    }


    //need more adjust
    public boolean checkConnectedStatus() {
        if (mConnectedThread == null || !mConnectedThread.isAlive()) {
            return false;
        }
        return true;
    }*/


    private void manageMyConnectedSocket(BluetoothSocket socket) {

        mSocket = socket;

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;
        private final BluetoothDevice mmDevice;

        public AcceptThread(BluetoothDevice device) {
            BluetoothServerSocket tmp = null;

            mmDevice = device;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothServer", MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }

            mmServerSocket = tmp;
        }

        public void run() {

            BluetoothSocket socket = null;

            while (true) {

                try {
                    socket = mmServerSocket.accept();
                } catch(IOException e) {

                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {

                    manageMyConnectedSocket(socket);

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Close server socket failed", e);
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {

            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {

                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    String s = new String(mmBuffer);
                    Log.i(TAG, "Receive message from DataReceiver: " + s);
                    mIntentVoiceCmd.putExtra("voiceCmd", s);
                    mContext.sendBroadcast(mIntentVoiceCmd);

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    mIntentSender.putExtra("message", "Socket disconnected");
                    mContext.sendBroadcast(mIntentSender);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
