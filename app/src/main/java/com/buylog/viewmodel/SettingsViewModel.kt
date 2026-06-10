package com.buylog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buylog.data.local.DataStoreManager
import com.buylog.data.model.PlatformConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 🌟 1. 继承 AndroidViewModel 并传入 application
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // 🌟 2. 初始化持久化工具类
    private val dataStoreManager = DataStoreManager(application)

    // 控制弹窗的状态（属于 UI 临时状态，不需要存入 DataStore，留在内存即可）
    private val _showEditNameDialog = MutableStateFlow(false)
    val showEditNameDialog: StateFlow<Boolean> = _showEditNameDialog.asStateFlow()

    // 🌟 3. 读取昵称：使用 stateIn 将底层 Flow 转换为 Compose 需要的 StateFlow
    val nickname: StateFlow<String> = dataStoreManager.nicknameFlow
        .stateIn(
            scope = viewModelScope,
            // WhileSubscribed 意味着当界面不可见时，自动停止监听以节省性能
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "加载中..."
        )

    // 定义初始的平台骨架
    private val defaultPlatforms = listOf(
        PlatformConfig(id = "taobao", name = "淘宝商城", isConfigured = false),
        PlatformConfig(id = "jd", name = "京东", isConfigured = false),
        PlatformConfig(id = "pdd", name = "拼多多", isConfigured = false)
    )

    // 🌟 4. 读取平台配置：同样转换为 StateFlow
    val platformConfigs: StateFlow<List<PlatformConfig>> = dataStoreManager.getPlatformConfigsFlow(defaultPlatforms)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = defaultPlatforms
        )

    // ================= 业务操作 =================

    fun updateNickname(newName: String) {
        if (newName.isNotBlank()) {
            // 🌟 5. 启动协程，调用挂起函数写入文件。
            // 写完后 DataStore 会自动通过 Flow 通知 UI 刷新，不需要我们手动改变量！
            viewModelScope.launch {
                dataStoreManager.saveNickname(newName)
            }
        }
    }

    // 更新平台配置（待后续 Cookie 输入框调用）
    fun updatePlatformConfig(config: PlatformConfig) {
        viewModelScope.launch {
            dataStoreManager.savePlatformConfig(config)
        }
    }

    fun openEditNameDialog() { _showEditNameDialog.value = true }
    fun closeEditNameDialog() { _showEditNameDialog.value = false }
}