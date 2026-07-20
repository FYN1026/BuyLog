package com.buylog

import androidx.compose.runtime.Composable
import com.buylog.di.iosModule
import com.buylog.di.sharedModule
import com.buylog.ui.navigation.MainScreen
import com.buylog.ui.theme.BuyLogTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = { modules(sharedModule, iosModule) }) {
        BuyLogTheme {
            MainScreen()
        }
    }
}
