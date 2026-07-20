package com.buylog.ui.navigation
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.buylog.ui.components.BottomNavItem
import com.buylog.ui.components.CapsuleBottomNav
import com.buylog.ui.screens.HomeScreen
import com.buylog.ui.screens.RecordsScreen
import com.buylog.ui.screens.SettingsScreen

@Composable
fun MainScreen() {
    var selectedIndex by remember { mutableStateOf(0) }
    var isBottomBarVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -50) isBottomBarVisible = false else if (available.y > 50) isBottomBarVisible = true
                return Offset.Zero
            }
        }
    }
    val navItems = remember { listOf(BottomNavItem(Icons.Default.Home, "首页"), BottomNavItem(Icons.AutoMirrored.Filled.List, "记录"), BottomNavItem(Icons.Default.Settings, "设置")) }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            when (selectedIndex) { 0 -> HomeScreen(); 1 -> RecordsScreen(); 2 -> SettingsScreen() }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            AnimatedVisibility(visible = isBottomBarVisible, enter = slideInVertically(initialOffsetY = { it }) + fadeIn(), exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()) {
                CapsuleBottomNav(items = navItems, selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it })
            }
        }
    }
}
