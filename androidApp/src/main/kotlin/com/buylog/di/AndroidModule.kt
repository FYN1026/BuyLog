package com.buylog.di

import com.buylog.data.db.AppDatabase
import com.buylog.data.db.createAppDatabase
import com.buylog.data.local.SettingsManager
import com.buylog.data.local.createSettings
import com.buylog.data.network.HaodankuApiClient
import com.buylog.viewmodel.HomeViewModel
import com.buylog.viewmodel.SettingsViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    includes(sharedModule)
    single<AppDatabase> { createAppDatabase() }
}
