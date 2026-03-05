package com.mobilegiants.megila.v2.managers

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mobilegiants.megila.v2.R

object RemoteConfigManager {

    private const val TAG = "RemoteConfigManager"
    const val AD_UNIT_BANNER_ID = "adUnitBannerId"
    const val AD_UNIT_INTERSTITIAL_ID = "adUnitInterstitialId"

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        initConfigUpdateListener()
    }

    private fun initConfigUpdateListener() {
        firebaseRemoteConfig.addOnConfigUpdateListener(
            object : com.google.firebase.remoteconfig.ConfigUpdateListener {
                override fun onUpdate(configUpdate: com.google.firebase.remoteconfig.ConfigUpdate) {
                    Log.d(TAG, "Updated keys: ${configUpdate.updatedKeys}")
                    firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
                        Log.d(TAG, "Config activated after update")
                    }
                }

                override fun onError(error: com.google.firebase.remoteconfig.FirebaseRemoteConfigException) {
                    Log.w(TAG, "Config update error with code: ${error.code}", error)
                }
            }
        )
    }

    fun fetchRemoteConfigValues() {
        firebaseRemoteConfig.fetch()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Remote config values fetched successfully")
                    firebaseRemoteConfig.activate()
                } else {
                    Log.e(TAG, "Error fetching remote config values: ${task.exception?.message}")
                }
            }
    }

    fun getParameter(paramName: String): String =
        firebaseRemoteConfig.getString(paramName)

    fun getLongParameter(paramName: String): Long =
        firebaseRemoteConfig.getLong(paramName)

    fun getBooleanParameter(paramName: String): Boolean =
        firebaseRemoteConfig.getBoolean(paramName)

    // Legacy Java compatibility - getInstance() returns the object itself
    @JvmStatic
    fun getInstance(): RemoteConfigManager = this
}
