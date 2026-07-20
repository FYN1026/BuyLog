package com.buylog.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.buylog.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val showProductCard by viewModel.showProductCard.collectAsState()
    val parsedProduct by viewModel.parsedProduct.collectAsState()
    val isParsing by viewModel.isParsing.collectAsState()
    var manualUrl by remember { mutableStateOf("") }
    val topPadding by animateDpAsState(
        targetValue = if (showProductCard) 0.dp else 120.dp,
        animationSpec = tween(durationMillis = 600)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topPadding))
            AnimatedVisibility(visible = !showProductCard) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "BuyLog",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "简单记录你的每一次购物",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(if (showProductCard) 16.dp else 28.dp),
                color = if (showProductCard) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                tonalElevation = if (showProductCard) 0.dp else 2.dp,
                shadowElevation = if (showProductCard) 0.dp else 4.dp,
                border = if (showProductCard) null else BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(modifier = Modifier.padding(if (showProductCard) 4.dp else 16.dp)) {
                    TextField(
                        value = manualUrl,
                        onValueChange = { manualUrl = it },
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = if (showProductCard) 48.dp else 150.dp),
                        placeholder = {
                            Text(
                                if (showProductCard) "粘贴新链接..." else "粘贴淘宝/京东商品链接",
                                style = if (showProductCard) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = if (showProductCard) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                        singleLine = showProductCard,
                        maxLines = if (showProductCard) 1 else 10,
                        trailingIcon = if (showProductCard && manualUrl.isNotBlank()) {
                            {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { manualUrl = "" }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            "Clear"
                                        )
                                    }; IconButton(onClick = {
                                    viewModel.parseUrl(manualUrl); manualUrl = ""
                                }) { Icon(Icons.Default.Search, "Parse") }
                                }
                            }
                        } else null)
                    if (!showProductCard && manualUrl.isNotBlank()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                manualUrl = ""
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Clear,
                                        null,
                                        modifier = Modifier.size(18.dp)
                                    ); Spacer(modifier = Modifier.width(4.dp)); Text("清空输入")
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.parseUrl(manualUrl); manualUrl = "" },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Search,
                                        null,
                                        modifier = Modifier.size(18.dp)
                                    ); Spacer(modifier = Modifier.width(4.dp)); Text("立即解析")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(
                visible = showProductCard && parsedProduct != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                parsedProduct?.let { product ->
                    ProductEditSection(
                        initialTitle = product.title,
                        initialPrice = product.price,
                        platform = product.platform,
                        mainImageUrl = product.imageUrl,
                        carouselImages = if (product.images.isNotBlank()) product.images.split(",") else emptyList(),
                        onCancel = { viewModel.closeProductCard() },
                        onSave = { viewModel.closeProductCard() })
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
        if (isParsing) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.4f)) {
                Box(contentAlignment = Alignment.Center) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(strokeWidth = 4.dp); Spacer(
                            modifier = Modifier.height(
                                20.dp
                            )
                        ); Text("正在解析", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        ClipboardDialog(viewModel = viewModel)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductEditSection(
    initialTitle: String,
    initialPrice: String,
    platform: String,
    mainImageUrl: String,
    carouselImages: List<String>,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var price by remember(initialPrice) { mutableStateOf(initialPrice) }
    var selectedCategory by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    val allImages = remember(mainImageUrl, carouselImages) {
        (listOf(mainImageUrl) + carouselImages)
            .filter { it.isNotBlank() }
            .distinct()
    }
    var selectedImageUrl by remember(mainImageUrl) { mutableStateOf(mainImageUrl) }
    val categories = listOf("上衣", "裤子", "鞋子", "配饰", "数码", "其他")
    val needsSize = selectedCategory in listOf("上衣", "裤子", "鞋子")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "选择封面图",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            ); Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(allImages) { url ->
                    val isSelected = selectedImageUrl == url
                    Box(
                        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)).border(
                            3.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        ).clickable { selectedImageUrl = url }) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (isSelected) Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("商品名称") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("到手价格") },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("¥ ") },
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = platform,
                    onValueChange = {},
                    label = { Text("来源") },
                    modifier = Modifier.weight(0.7f),
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp)); Text(
            "商品类别",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        ); Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            AnimatedVisibility(visible = needsSize) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp)); OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("输入规格 / 尺码") },
                    placeholder = { Text("例如: 白色 XL 或 42码") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.height(56.dp).weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("取消", style = MaterialTheme.typography.titleMedium) }
                Button(
                    onClick = onSave,
                    modifier = Modifier.height(56.dp).weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("确认保存", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardDialog(viewModel: HomeViewModel) {
    val showDialog by viewModel.showClipboardDialog.collectAsState()
    val detectedLink by viewModel.detectedLink.collectAsState()
    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissDialog() },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp)) {
                Text("发现商品链接", style = MaterialTheme.typography.headlineSmall); Spacer(
                modifier = Modifier.height(16.dp)
            )
                Text(
                    "检测到剪切板包含链接，是否需要解析并记录？",
                    style = MaterialTheme.typography.bodyLarge
                ); Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        detectedLink,
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
                    TextButton(onClick = { viewModel.dismissDialog() }) { Text("忽略") }; Spacer(
                    modifier = Modifier.width(8.dp)
                ); Button(onClick = { viewModel.confirmParse() }) { Text("立即解析") }
                }
            }
        }
    }
}
