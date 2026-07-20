package com.buylog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.buylog.di.androidModule
import com.buylog.platform.AndroidPlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidPlatformContext.init(applicationContext)
        try {
            startKoin {
                androidContext(this@MainActivity)
                modules(androidModule)
            }
        } catch (e: Exception) {
            // Koin already started
        }
        setContent { App() }
    }
}
