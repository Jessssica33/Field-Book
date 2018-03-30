package com.fieldbook.tracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jessica on 3/8/18.
 */

public class DataMonitor {

    private String drop1;
    private String content1;
    private String drop2;
    private String content2;
    private String drop3;
    private String content3;

    private Context context;
    private Intent intent;

    public DataMonitor(Context context) {
        this.context = context;
        intent = new Intent();
        intent.setAction("com.fieldbook.tracker.DATA_CHANGE");
    }

    //public String getDrop1 () {return drop1;}
    public void setDrop1(String drop1, String content1) {

        this.drop1 = drop1;
        this.content1 = content1;
        intent.putExtra("data", "drop1 " + drop1 + " " + content1 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop1 + " " + content1);
    }

    //public String getDrop2 () {return drop2;}
    public void setDrop2(String drop2, String content2) {

        this.drop2 = drop2;
        this.content2 = content2;
        intent.putExtra("data", "drop2 " + drop2 + " " + content2 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop2 + " " + content2);
    }

    //public String getDrop3 () {return drop3;}
    public void setDrop3(String drop3, String content3) {

        this.drop3 = drop3;
        this.content3 = content3;
        intent.putExtra("data", "drop3 " + drop3 + " " + content3 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop3 + " " + content3);
    }
}
