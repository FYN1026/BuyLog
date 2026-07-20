package com.buylog.ui.components
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(val icon: ImageVector, val label: String)

@Composable
fun CapsuleBottomNav(items: List<BottomNavItem>, selectedIndex: Int, onItemSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(bottom = 20.dp).navigationBarsPadding(), contentAlignment = Alignment.Center) {
        Surface(modifier = Modifier.wrapContentWidth().height(60.dp), shape = RoundedCornerShape(30.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), shadowElevation = 12.dp, border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) {
            Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val backgroundColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "bg")
                    val contentColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "c")
                    Box(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(backgroundColor).clickable { onItemSelected(index) }.padding(horizontal = 16.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = item.icon, contentDescription = item.label, tint = contentColor)
                            AnimatedVisibility(visible = isSelected, enter = expandHorizontally() + fadeIn(), exit = shrinkHorizontally() + fadeOut()) { Text(item.label, color = contentColor, fontSize = 14.sp, maxLines = 1) }
                        }
                    }
                }
            }
        }
    }
}
