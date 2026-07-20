package com.buylog.di

import com.buylog.data.local.SettingsManager
import com.buylog.data.local.createSettings
import org.koin.dsl.module

val sharedModule = module {
    single { createSettings() }
    single { SettingsManager(get()) }
}
