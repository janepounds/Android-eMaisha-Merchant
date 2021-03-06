package com.cabral.emaishamerchantsapp.utils;

import android.content.Context;
import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;

public class MultiLanguageApp extends MultiDexApplication {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // set App Context
        context = this.getApplicationContext();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/JosefinSans-Regular.ttf"); // font from assets:
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }
}
