package com.buylog.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buylog.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val nickname by viewModel.nickname.collectAsState()
    val platformConfigs by viewModel.platformConfigs.collectAsState()

    // 🌟 1. 收集弹窗状态
    val showEditNameDialog by viewModel.showEditNameDialog.collectAsState()

    // 🌟 2. 挂载弹窗组件
    if (showEditNameDialog) {
        EditNicknameDialog(
            currentName = nickname,
            onDismiss = { viewModel.closeEditNameDialog() },
            onConfirm = { newName ->
                viewModel.updateNickname(newName)
                viewModel.closeEditNameDialog()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // 🌟 3. 把打开弹窗的事件传给头部组件
                PersonalInfoHeader(
                    nickname = nickname,
                    onEditClick = { viewModel.openEditNameDialog() }
                )
            }

            item {
                Text(
                    text = "购物平台集成",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(platformConfigs) { config ->
                PlatformConfigCard(
                    name = config.name,
                    isConfigured = config.isConfigured,
                    phone = config.phone,
                    onClick = { /* TODO: 导航到详情配置页 */ }
                )
            }
        }
    }
}

// 🌟 新增：修改昵称的专用弹窗组件
@Composable
fun EditNicknameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // 这是一个局部的 UI 状态，用来记录用户正在输入框里敲击的文字
    var inputText by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改昵称", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("请输入新昵称") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(inputText) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 修改原有的 Header 组件，接收点击事件
@Composable
fun PersonalInfoHeader(nickname: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, "Avatar", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("开发者模式已开启", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // 🌟 触发点击事件
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun PlatformConfigCard(name: String, isConfigured: Boolean, phone: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ShoppingCart, name, tint = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (phone.isNotBlank()) {
                    Text(phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 状态指示灯
            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = if (isConfigured) Color(0xFF4CAF50) else Color.Gray
                Canvas(Modifier.size(8.dp)) { drawCircle(color) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isConfigured) "已配置" else "未配置",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}