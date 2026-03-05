package com.mobilegiants.megila.v2

import android.app.Application
import com.mobilegiants.megila.v2.managers.PreferencesManager
import com.mobilegiants.megila.v2.managers.RemoteConfigManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
        RemoteConfigManager.fetchRemoteConfigValues()
    }
}
