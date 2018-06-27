package com.fieldbook.tracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jessica on 3/8/18.
 */

public class DataMonitor {

    //info bar
    private int drop1;
    private String content1;
    private int drop2;
    private String content2;
    private int drop3;
    private String content3;

    //range and plot
    private String rangeName;
    private String plotName;
    private String rangeValue;
    private String plotValue;

    //trait and value
    private String traitName;
    private String traitValue;

    private Context context;
    private Intent intent;

    public DataMonitor(Context context) {
        this.context = context;
        intent = new Intent();
        intent.setAction("com.fieldbook.tracker.DATA_CHANGE");
    }

    //public String getDrop1 () {return drop1;}
    public void setDrop1(int drop1, String content1) {

        this.drop1 = drop1;
        this.content1 = content1;
        //intent.putExtra("data", "drop1 " + drop1 + " " + content1 + " ");
        intent.putExtra("data", "& drop1 " + drop1 + ": " + content1 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop1 + " " + content1);
    }

    //public String getDrop2 () {return drop2;}
    public void setDrop2(int drop2, String content2) {

        this.drop2 = drop2;
        this.content2 = content2;
        //intent.putExtra("data", "drop2 " + drop2 + " " + content2 + " ");
        intent.putExtra("data",  "& drop2 " + drop2 + ": " + content2 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop2 + " " + content2);
    }

    //public String getDrop3 () {return drop3;}
    public void setDrop3(int drop3, String content3) {

        this.drop3 = drop3;
        this.content3 = content3;
        //intent.putExtra("data", "drop3 " + drop3 + " " + content3 + " ");
        intent.putExtra("data",  "& drop3 " + drop3 + ": " + content3 + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + drop3 + " " + content3);
    }

    public void setRangePlotName(String range, String plot) {
        rangeName = range;
        plotName = plot;
    }

    public void setRangePlotValue(String r, String p) {
        rangeValue = r;
        plotValue = p;

        intent.putExtra("data", "& plot " + rangeName.charAt(0) + ": " + rangeValue + " / " +
                plotName.charAt(0) + ": " + plotValue + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + rangeName.charAt(0) + ": " + rangeValue + " / " + plotName.charAt(0) + ": " + plotValue);
    }

    public void setTraitName(String trait) {
        traitName = trait;
    }

    public void setTraitValue(String value) {
        traitValue = value;
        intent.putExtra("data", "& trait: " + traitName + "  value: " + traitValue + " ");
        context.sendBroadcast(intent);
        Log.i("info", "sendBroadcast: " + traitName + " " + traitValue);
    }
}
