package com.buylog.platform

import platform.UIKit.UIPasteboard

actual fun getValidUrlFromClipboard(): String? {
    val pasteboard = UIPasteboard.generalPasteboard
    val text = pasteboard.string ?: return null
    if (text.isEmpty()) return null

    val urlRegex = "(https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])".toRegex()
    val matchResult = urlRegex.find(text)
    return matchResult?.value
}
