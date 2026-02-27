package com.mobilegiants.megila.managers

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.mobilegiants.megila.BuildConfig
import com.mobilegiants.megila.R

object AdManager {

    // Google test ad unit IDs - used in debug builds only
    private const val DEBUG_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val DEBUG_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun getInterstitialAdUnitId(): String {
        return if (BuildConfig.DEBUG) {
            DEBUG_INTERSTITIAL_ID
        } else {
            RemoteConfigManager.getParameter(RemoteConfigManager.AD_UNIT_INTERSTITIAL_ID)
        }
    }

    fun getBannerAdUnitId(): String {
        return if (BuildConfig.DEBUG) {
            DEBUG_BANNER_ID
        } else {
            RemoteConfigManager.getParameter(RemoteConfigManager.AD_UNIT_BANNER_ID)
        }
    }
}
