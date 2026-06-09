package com.buylog.utils

import android.content.ClipboardManager
import android.content.Context

fun Context.getValidLinkFromClipboard(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // 检查是否有文本内容
    if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true) {
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString() ?: return null

        // 简单的正则表达式：提取包含 http 或 https 的链接
        val urlRegex = "(https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])".toRegex()
        val matchResult = urlRegex.find(text)

        return matchResult?.value
    }

    return null
}