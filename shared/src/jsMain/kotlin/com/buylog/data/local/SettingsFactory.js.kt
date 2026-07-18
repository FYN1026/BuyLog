package com.buylog.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.browser.localStorage

actual fun createSettings(): Settings = StorageSettings(localStorage)
