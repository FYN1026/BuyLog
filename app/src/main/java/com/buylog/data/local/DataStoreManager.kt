package com.buylog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.buylog.data.model.PlatformConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. 在 Context 上扩展一个单例的 dataStore 属性，名字叫 "user_prefs"
// 官方推荐写在文件顶层，确保全局只有一个 DataStore 实例，避免多实例读写冲突
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        // 定义固定的键：用户昵称
        val KEY_NICKNAME = stringPreferencesKey("user_nickname")
    }

    // ================= 个人信息读写 =================

    /**
     * 读取昵称（向外暴露一个 Flow 大喇叭）
     * 如果本地没存过，默认返回 "Android 开发者"
     */
    val nicknameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NICKNAME] ?: "Niko"
        }

    /**
     * 写入/更新昵称（这是一个挂起函数，必须在协程中调用）
     */
    suspend fun saveNickname(name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NICKNAME] = name
        }
    }

    // ================= 购物平台凭证动态读写 =================

    /**
     * 核心高级函数：从 DataStore 动态组装整个平台列表
     * @param defaultPlatforms ViewModel 传过来的初始平台框架列表（包含 id 和 name）
     * @return 返回一个带有完整本地持久化数据的 Flow 列表
     */
    fun getPlatformConfigsFlow(defaultPlatforms: List<PlatformConfig>): Flow<List<PlatformConfig>> {
        return context.dataStore.data.map { preferences ->
            // 遍历初始框架，去 DataStore 里捞出各自对应的动态数据
            defaultPlatforms.map { default ->
                // 动态生成每一个平台专属的 Key
                val cookieKey = stringPreferencesKey("${default.id}_cookie")
                val phoneKey = stringPreferencesKey("${default.id}_phone")
                val passwordKey = stringPreferencesKey("${default.id}_password")
                val timeKey = longPreferencesKey("${default.id}_cookie_created_at")

                val savedCookie = preferences[cookieKey] ?: ""

                // 利用 .copy() 组装出带有真实本地数据的全新对象
                default.copy(
                    cookie = savedCookie,
                    phone = preferences[phoneKey] ?: "",
                    cookieCreatedAt = preferences[timeKey] ?: 0L,
                    // 自动化判定：本地捞出来的 Cookie 不为空，说明该平台已配置成功
                    isConfigured = savedCookie.isNotBlank()
                )
            }
        }
    }

    /**
     * 写入/更新某个具体平台的配置
     * @param config 带有最新数据的平台对象
     */
    suspend fun savePlatformConfig(config: PlatformConfig) {
        context.dataStore.edit { preferences ->
            // 同样利用动态键名，精准对号入座写入文件
            preferences[stringPreferencesKey("${config.id}_cookie")] = config.cookie
            preferences[stringPreferencesKey("${config.id}_phone")] = config.phone
            preferences[longPreferencesKey("${config.id}_cookie_created_at")] = config.cookieCreatedAt
        }
    }
}