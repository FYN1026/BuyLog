package com.buylog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buylog.data.local.SettingsManager
import com.buylog.data.model.PlatformConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val _showEditNameDialog = MutableStateFlow(false)
    val showEditNameDialog: StateFlow<Boolean> = _showEditNameDialog.asStateFlow()

    val nickname: StateFlow<String> = settingsManager.nicknameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "加载中...")

    private val defaultPlatforms = listOf(
        PlatformConfig(id = "taobao", name = "淘宝商城", isConfigured = false),
        PlatformConfig(id = "jd", name = "京东", isConfigured = false),
        PlatformConfig(id = "pdd", name = "拼多多", isConfigured = false)
    )

    val platformConfigs: StateFlow<List<PlatformConfig>> = settingsManager.getPlatformConfigsFlow(defaultPlatforms)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultPlatforms)

    fun updateNickname(newName: String) { if (newName.isNotBlank()) { viewModelScope.launch { settingsManager.saveNickname(newName) } } }
    fun updatePlatformConfig(config: PlatformConfig) { viewModelScope.launch { settingsManager.savePlatformConfig(config) } }
    fun openEditNameDialog() { _showEditNameDialog.value = true }
    fun closeEditNameDialog() { _showEditNameDialog.value = false }
}
