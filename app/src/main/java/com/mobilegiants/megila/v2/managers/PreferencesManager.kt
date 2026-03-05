package com.mobilegiants.megila.v2.managers

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    private const val PREFS_NAME = "com.mobilegiants.megila.v2"
    private const val KEY_SCROLL_SPEED = "checked_item_key"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var scrollSpeed: Int
        get() = prefs.getInt(KEY_SCROLL_SPEED, -1)
        set(value) = prefs.edit().putInt(KEY_SCROLL_SPEED, value).apply()
}
