package com.buylog.platform

import android.content.Context

actual class PlatformContext

object AndroidPlatformContext {
    var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }
}
