package com.buylog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import com.buylog.ui.components.BottomNavItem
import com.buylog.ui.components.CapsuleBottomNav
import com.buylog.ui.screens.HomeScreen
import com.buylog.ui.screens.RecordsScreen
import com.buylog.ui.screens.SettingsScreen
import com.buylog.ui.theme.BuyLogTheme
import com.buylog.viewmodel.HomeViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuyLogTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedIndex by remember { mutableStateOf(0) }
    
    val navItems = remember {
        listOf(
            BottomNavItem(Icons.Default.Home, "首页"),
            BottomNavItem(Icons.AutoMirrored.Filled.List, "记录"),
            BottomNavItem(Icons.Default.Settings, "设置")
        )
    }

    Scaffold(
        bottomBar = {
            CapsuleBottomNav(
                items = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> RecordsScreen()
                2 -> SettingsScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardLinkHandler(viewModel: HomeViewModel) {
    val context = LocalContext.current
    // 获取当前窗口的状态信息
    val windowInfo = LocalWindowInfo.current

    val showDialog by viewModel.showClipboardDialog.collectAsState()
    val detectedLink by viewModel.detectedLink.collectAsState()

    // 使用 LaunchedEffect 监听 isWindowFocused 的变化
    LaunchedEffect(windowInfo.isWindowFocused) {
        // 只有当窗口真正获得焦点（比如刚打开 App 画面加载完毕，或者从桌面切回 App）
        if (windowInfo.isWindowFocused) {
            viewModel.checkClipboardOnResume(context)
        }
    }

    // 使用 ModalBottomSheet
    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                // 当用户点击背景遮罩或向下滑动关闭时触发
                viewModel.dismissDialog()
            },
            sheetState = rememberModalBottomSheetState()
        ) {
            // 这里是抽屉内部的内容布局
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp) // 预留底部安全距离
            ) {
                Text(
                    text = "发现商品链接",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "检测到剪切板包含链接，是否需要解析并记录？",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 展示链接预览
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = detectedLink,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.dismissDialog() }) {
                        Text("忽略")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.confirmParse() }) {
                        Text("立即解析")
                    }
                }
            }
        }
    }
}
