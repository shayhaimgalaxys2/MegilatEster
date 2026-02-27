package com.mobilegiants.megila

import android.app.Application
import com.mobilegiants.megila.managers.PreferencesManager
import com.mobilegiants.megila.managers.RemoteConfigManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
        RemoteConfigManager.fetchRemoteConfigValues()
    }
}
