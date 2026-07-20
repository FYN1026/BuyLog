package com.buylog.data.local

import com.buylog.data.model.PlatformConfig
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SettingsManager(private val settings: Settings) {
    companion object {
        const val KEY_NICKNAME = "user_nickname"
    }

    val nicknameFlow: Flow<String> = flow {
        emit(settings.getStringOrNull(KEY_NICKNAME) ?: "Niko")
    }

    suspend fun saveNickname(name: String) {
        settings.putString(KEY_NICKNAME, name)
    }

    fun getPlatformConfigsFlow(defaultPlatforms: List<PlatformConfig>): Flow<List<PlatformConfig>> {
        return flow {
            val savedJson = settings.getStringOrNull("platform_configs_json")
            if (savedJson != null) {
                try {
                    val restoredList: List<SavedPlatformConfig> = Json.decodeFromString(ListSerializer(SavedPlatformConfig.serializer()), savedJson)
                    emit(defaultPlatforms.map { default ->
                        val saved = restoredList.find { it.id == default.id }
                        if (saved != null) default.copy(cookie = saved.cookie, phone = saved.phone, cookieCreatedAt = saved.cookieCreatedAt, isConfigured = saved.cookie.isNotBlank())
                        else default
                    })
                } catch (_: Exception) { emit(defaultPlatforms) }
            } else emit(defaultPlatforms)
        }
    }

    suspend fun savePlatformConfig(config: PlatformConfig) {
        val savedJson = settings.getStringOrNull("platform_configs_json")
        val list = if (savedJson != null) {
            try { Json.decodeFromString(ListSerializer(SavedPlatformConfig.serializer()), savedJson).toMutableList() } catch (_: Exception) { mutableListOf() }
        } else mutableListOf()
        val existing = list.indexOfFirst { it.id == config.id }
        val saved = SavedPlatformConfig(id = config.id, cookie = config.cookie, phone = config.phone, cookieCreatedAt = config.cookieCreatedAt)
        if (existing >= 0) list[existing] = saved else list.add(saved)
        settings.putString("platform_configs_json", Json.encodeToString(ListSerializer(SavedPlatformConfig.serializer()), list))
    }
}

@kotlinx.serialization.Serializable
data class SavedPlatformConfig(val id: String, val cookie: String, val phone: String, val cookieCreatedAt: Long)
