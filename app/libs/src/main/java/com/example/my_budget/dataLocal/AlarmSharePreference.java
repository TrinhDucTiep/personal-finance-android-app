package com.example.my_budget.dataLocal;

import android.content.Context;
import android.content.SharedPreferences;

public class AlarmSharePreference {
    private static final String Alarm_Preference = "Alarm_Preference";
    private Context mContext;

    public AlarmSharePreference(Context mContext) {
        this.mContext = mContext;
    }

    public void putBooleanValue(String key, boolean value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Alarm_Preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Alarm_Preference, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public void putIntValue(String key, int value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Alarm_Preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntValue(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Alarm_Preference, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
}
