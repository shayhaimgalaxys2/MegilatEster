package com.mobilegiants.megila;

import android.app.Application;
import android.content.ContextWrapper;

import com.mobilegiants.megila.managers.RemoteConfigManager;
import com.pixplicity.easyprefs.library.Prefs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initEasyPrefs();
        initRemoteConfig();
    }

    private void initEasyPrefs() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    private void initRemoteConfig() {
        RemoteConfigManager remoteConfigManager = RemoteConfigManager.getInstance();
        remoteConfigManager.fetchRemoteConfigValues();
    }
}
