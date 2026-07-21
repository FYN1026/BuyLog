package com.buylog.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.buylog.composeapp.resources.Res
import com.buylog.composeapp.resources.NotoSansSC_Bold
import com.buylog.composeapp.resources.NotoSansSC_Regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun rememberAppFontFamily(): FontFamily? {
    val regular = Font(Res.font.NotoSansSC_Regular)
    val bold = Font(Res.font.NotoSansSC_Bold, FontWeight.Bold)
    return remember(regular, bold) { FontFamily(regular, bold) }
}
