package com.fieldbook.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class DataTransferService extends Service {

    private BluetoothServer mBluetoothServer;
    private ActivityReceiver mActivityReceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate() {

        mBluetoothServer = new BluetoothServer(this);
        mActivityReceiver = new ActivityReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.fieldbook.tracker.DATA_CHANGE");
        mIntentFilter.addAction("com.fieldbook.tracker.BluetoothServer.STATUSCHANGE");
        registerReceiver(mActivityReceiver, mIntentFilter);
        mBluetoothServer.init();

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

                mBluetoothServer.write(data.getBytes());
            } else if (action.equals("com.fieldbook.tracker.BluetoothServer.STATUSCHANGE")) {
                data = intent.getExtras().getString("message");
                mBluetoothServer.cancelConnection();
                mBluetoothServer.init();

            }


        }
    }

}
