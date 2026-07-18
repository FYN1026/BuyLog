package com.buylog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buylog.data.db.AppDatabase
import com.buylog.data.model.Product
import com.buylog.data.network.HaodankuApiClient
import com.buylog.platform.getValidUrlFromClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val database: AppDatabase,
    private val apiClient: HaodankuApiClient
) : ViewModel() {
    private val _showClipboardDialog = MutableStateFlow(false)
    val showClipboardDialog: StateFlow<Boolean> = _showClipboardDialog.asStateFlow()
    private val _detectedLink = MutableStateFlow("")
    val detectedLink: StateFlow<String> = _detectedLink.asStateFlow()
    private val _parsedProduct = MutableStateFlow<Product?>(null)
    val parsedProduct: StateFlow<Product?> = _parsedProduct.asStateFlow()
    private val _showProductCard = MutableStateFlow(false)
    val showProductCard: StateFlow<Boolean> = _showProductCard.asStateFlow()
    private val _isParsing = MutableStateFlow(false)
    val isParsing: StateFlow<Boolean> = _isParsing.asStateFlow()
    private var lastProcessedLink: String? = null

    fun checkClipboardOnResume() {
        val currentLink = getValidUrlFromClipboard()
        if (currentLink != null && currentLink != lastProcessedLink) { _detectedLink.value = currentLink; _showClipboardDialog.value = true }
    }
    fun dismissDialog() { _showClipboardDialog.value = false; lastProcessedLink = _detectedLink.value }
    fun confirmParse() { _showClipboardDialog.value = false; lastProcessedLink = _detectedLink.value; parseUrl(_detectedLink.value) }
    fun parseUrl(url: String) {
        if (url.isBlank()) return
        viewModelScope.launch {
            _isParsing.value = true
            try { val product = apiClient.fetchProductInfo(url); if (product != null) { _parsedProduct.value = product; _showProductCard.value = true } }
            finally { _isParsing.value = false }
        }
    }
    fun closeProductCard() { _showProductCard.value = false }
}
