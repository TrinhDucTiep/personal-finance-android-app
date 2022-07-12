package com.example.my_budget.dataLocal;

import android.content.Context;

public class DataLocalManager {

    private static final String ONOFF = "ONOFF";
    private static final String HOUR = "HOUR";
    private static final String MINUTE = "MINUTE";
    private static DataLocalManager instance;
    private AlarmSharePreference alarmSharePreference;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.alarmSharePreference = new AlarmSharePreference(context);
    }

    public static DataLocalManager getInstance(){
        if(instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setOnOffAlarm(boolean isOn){
        DataLocalManager.getInstance().alarmSharePreference.putBooleanValue(ONOFF, isOn);
    }

    public static boolean getOnOffAlarm(){
        return DataLocalManager.getInstance().alarmSharePreference.getBooleanValue(ONOFF);
    }

    public static void setHourAlarm(int hour){
        DataLocalManager.getInstance().alarmSharePreference.putIntValue(HOUR, hour);
    }

    public static int getHourAlarm(){
        return DataLocalManager.getInstance().alarmSharePreference.getIntValue(HOUR);
    }

    public static void setMinuteAlarm(int minute){
        DataLocalManager.getInstance().alarmSharePreference.putIntValue(MINUTE, minute);
    }

    public static int getMinuteAlarm(){
        return DataLocalManager.getInstance().alarmSharePreference.getIntValue(MINUTE);
    }
}
