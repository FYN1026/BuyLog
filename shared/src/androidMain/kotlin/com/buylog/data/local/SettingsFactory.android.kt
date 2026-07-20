package com.buylog.data.local

import android.preference.PreferenceManager
import com.buylog.platform.AndroidPlatformContext
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual fun createSettings(): Settings {
    val context = AndroidPlatformContext.context ?: throw IllegalStateException("Context not initialized")
    return SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(context))
}
