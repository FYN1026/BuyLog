package com.buylog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buylog.ClipboardLinkHandler
import com.buylog.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    // 挂载剪贴板处理器（它在后台默默监听，该弹窗时自己会弹）
    ClipboardLinkHandler(viewModel = viewModel)

    // 首页的骨架布局
    Column {
        Text("这里是首页的顶部...")
        // TODO: 手动粘贴输入框
        // TODO: 解析结果卡片
    }
}