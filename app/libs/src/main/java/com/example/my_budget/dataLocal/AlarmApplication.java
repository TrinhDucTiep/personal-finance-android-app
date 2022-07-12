package com.example.my_budget.dataLocal;

import android.app.Application;

public class AlarmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
    }
}
