package com.buylog.data.model

data class Product(
    val id: Int = 0,
    val title: String,
    val imageUrl: String,
    val productUrl: String,
    val platform: String,
    val price: String = "",
    val images: String = "",
    val addedTime: Long = 0L
)
