package com.buylog.data.network

import com.buylog.data.model.Product

expect class HaodankuApiClient() {
    suspend fun fetchProductInfo(content: String): Product?
}
