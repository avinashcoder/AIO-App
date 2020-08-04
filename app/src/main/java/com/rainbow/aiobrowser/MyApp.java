package com.rainbow.aiobrowser;

import android.app.Application;

public class MyApp extends Application {

    private static MyApp singleton;

    public static MyApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
    }
}
