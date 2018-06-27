package com.fieldbook.tracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jessica on 3/29/18.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Set;

public class DataTransferService extends Service {

    private BluetoothServer mBluetoothServer;
    private ActivityReceiver mActivityReceiver;
    private IntentFilter mIntentFilter;
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice = null;

    private final String TAG = "DataTransferService";
    //private Set<BluetoothDevice> mPairedDevices;


    @Override
    public void onCreate() {

        mActivityReceiver = new ActivityReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.fieldbook.tracker.DATA_CHANGE");
        mIntentFilter.addAction("com.fieldbook.tracker.BluetoothServer.STATUSCHANGE");
        mIntentFilter.addAction("com.fieldbook.tracker.BluetoothServer.DEVICECHANGE");
        registerReceiver(mActivityReceiver, mIntentFilter);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothServer = new BluetoothServer(this);
        startBluetoothConnection();

    }

    private void startBluetoothConnection() {
        getDefaultDevice();
        if (mDevice != null) {
            mBluetoothServer.init(mDevice, mAdapter);
        }
    }

    private void getDefaultDevice() {
        SharedPreferences setting = getSharedPreferences("Settings", 0);
        String name = setting.getString("BluetoothDevice", "DEFAULT");
        if (!name.equals("DEFAULT")) {
            Set<BluetoothDevice> paired = mAdapter.getBondedDevices();
            for (BluetoothDevice device: paired) {
                if (device.getName().equals(name)) {
                    mDevice = device;
                    Log.i(TAG, "Try to creat bluetooth connection to " + device.getName());
                    break;
                }
            }
        } else {
            Log.e("DataTransferService", "No boundled device, please go to setting choose devices");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mActivityReceiver);
        mBluetoothServer.cancelConnection();
        super.onDestroy();
    }


    private class ActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Receiver", "Broadcast received: " + action);

            String data;
            if (action.equals("com.fieldbook.tracker.DATA_CHANGE")) {
                data = intent.getExtras().getString("data");
                Log.i(TAG, data);
                mBluetoothServer.write(data.getBytes());

            } else if (action.equals("com.fieldbook.tracker.BluetoothServer.STATUSCHANGE")) {
                data = intent.getExtras().getString("message");
                mBluetoothServer.cancelConnection();
                startBluetoothConnection();

            } else if (action.equals("com.fieldbook.tracker.BluetoothServer.DEVICECHANGE")) {
                mBluetoothServer.cancelConnection();
                startBluetoothConnection();

            }


        }
    }

}
