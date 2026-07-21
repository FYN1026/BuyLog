package com.buylog

import androidx.compose.ui.window.ComposeUIViewController
import com.buylog.di.iosModule
import com.buylog.di.sharedModule
import org.koin.compose.KoinApplication

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = { modules(sharedModule, iosModule) }) {
        App()
    }
}
