package com.buylog.platform

import android.content.ClipboardManager
import android.content.Context

actual fun getValidUrlFromClipboard(): String? {
    val context = AndroidPlatformContext.context ?: return null
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return null

    if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true) {
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString() ?: return null

        val urlRegex = "(https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])".toRegex()
        val matchResult = urlRegex.find(text)

        return matchResult?.value
    }

    return null
}
