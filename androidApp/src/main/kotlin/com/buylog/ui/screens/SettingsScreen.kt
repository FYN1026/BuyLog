package com.buylog.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buylog.data.model.PlatformConfig
import com.buylog.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val nickname by viewModel.nickname.collectAsState()
    val platformConfigs by viewModel.platformConfigs.collectAsState()
    val showEditNameDialog by viewModel.showEditNameDialog.collectAsState()
    var selectedPlatformForConfig by remember { mutableStateOf<PlatformConfig?>(null) }

    if (showEditNameDialog) {
        EditNicknameDialog(currentName = nickname, onDismiss = { viewModel.closeEditNameDialog() }, onConfirm = { viewModel.updateNickname(it); viewModel.closeEditNameDialog() })
    }
    selectedPlatformForConfig?.let { config ->
        PlatformConfigDialog(config = config, onDismiss = { selectedPlatformForConfig = null }, onConfirm = { phone, _ ->
            viewModel.updatePlatformConfig(config.copy(phone = phone, isConfigured = true, cookieCreatedAt = System.currentTimeMillis()))
            selectedPlatformForConfig = null
        })
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("设置", fontWeight = FontWeight.ExtraBold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            item { PersonalInfoHeader(nickname = nickname, onEditClick = { viewModel.openEditNameDialog() }) }
            item { Text("购物平台集成", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) }
            items(platformConfigs) { config -> PlatformConfigCard(config = config, onConfigClick = { selectedPlatformForConfig = config }) }
        }
    }
}

@Composable
fun EditNicknameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var inputText by remember { mutableStateOf(currentName) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("修改昵称", fontWeight = FontWeight.Bold) }, text = { OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("请输入新昵称") }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = { onConfirm(inputText) }) { Text("保存") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } })
}

@Composable
fun PersonalInfoHeader(nickname: String, onEditClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, "Avatar", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp)) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text(nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
fun PlatformConfigCard(config: PlatformConfig, onConfigClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(modifier = Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape).clickable { expanded = !expanded }.animateContentSize()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, config.name, tint = MaterialTheme.colorScheme.secondary); Spacer(modifier = Modifier.width(16.dp))
                Text(config.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val color = if (config.isConfigured) Color(0xFF4CAF50) else Color.Gray
                    Canvas(Modifier.size(8.dp)) { drawCircle(color) }; Spacer(Modifier.width(8.dp))
                    Text(if (config.isConfigured) "已配置" else "未配置", style = MaterialTheme.typography.labelMedium)
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)).padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(thickness = 0.5.dp)
                    DetailRow(label = "配对账号", value = config.phone.ifBlank { "尚未绑定" })
                    val dateStr = if (config.cookieCreatedAt > 0) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(config.cookieCreatedAt)) else "无记录"
                    DetailRow(label = "配对时间", value = dateStr)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) { IconButton(onClick = onConfigClick, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, "配置平台", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) } }
                }
            }
        }
    }
}

@Composable
fun PlatformConfigDialog(config: PlatformConfig, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var phone by remember { mutableStateOf(config.phone) }; var code by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(0) }
    LaunchedEffect(countdown) { if (countdown > 0) { kotlinx.coroutines.delay(1000); countdown-- } }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("${config.name} 配置") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("手机号码") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("验证码") }, modifier = Modifier.weight(1f)); Button(onClick = { countdown = 60 }, enabled = countdown == 0 && phone.length == 11, modifier = Modifier.align(Alignment.CenterVertically).padding(top = 8.dp)) { Text(if (countdown > 0) "${countdown}s" else "获取") } }
        }
    }, confirmButton = { Button(onClick = { onConfirm(phone, code) }, enabled = code.isNotBlank()) { Text("确认") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } })
}

@Composable
fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) }
}
