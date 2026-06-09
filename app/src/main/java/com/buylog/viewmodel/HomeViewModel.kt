package com.buylog.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.buylog.utils.getValidLinkFromClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _showClipboardDialog = MutableStateFlow(false)
    val showClipboardDialog: StateFlow<Boolean> = _showClipboardDialog.asStateFlow()

    private val _detectedLink = MutableStateFlow("")
    val detectedLink: StateFlow<String> = _detectedLink.asStateFlow()

    private var lastProcessedLink: String? = null

    fun checkClipboardOnResume(context: Context) {
        val currentLink = context.getValidLinkFromClipboard()

        // 💡 核心判定逻辑：
        // 1. 剪切板里确实有有效链接
        // 2. 这个链接不能和上一次处理过的链接一模一样（去重）
        if (currentLink != null && currentLink != lastProcessedLink) {
            _detectedLink.value = currentLink
            _showClipboardDialog.value = true
        }
    }

    fun dismissDialog() {
        _showClipboardDialog.value = false
        lastProcessedLink = _detectedLink.value
    }

    fun confirmParse() {
        _showClipboardDialog.value = false
        val linkToParse = _detectedLink.value
        lastProcessedLink = _detectedLink.value
        println("解析数据：$linkToParse")
    }
}