package com.buylog.data.model

data class PlatformConfig (
    val id: String,
    val name: String,
    val isConfigured: Boolean,
    val cookie: String = "",
    val cookieCreatedAt: Long = 0L,
    val phone: String = ""
)