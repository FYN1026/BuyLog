package com.buylog.di

import com.buylog.data.local.SettingsManager
import com.buylog.data.local.createSettings
import com.buylog.data.network.HaodankuApiClient
import com.buylog.viewmodel.HomeViewModel
import com.buylog.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {
    single { createSettings() }
    single { SettingsManager(get()) }
    single { HaodankuApiClient() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
