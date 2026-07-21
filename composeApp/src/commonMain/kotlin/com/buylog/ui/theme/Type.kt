package com.buylog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun rememberAppFontFamily(): FontFamily?

fun appTypography(fontFamily: FontFamily): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.withFont(fontFamily, FontWeight.Black),
        displayMedium = base.displayMedium.withFont(fontFamily, FontWeight.Bold),
        displaySmall = base.displaySmall.withFont(fontFamily, FontWeight.Bold),
        headlineLarge = base.headlineLarge.withFont(fontFamily, FontWeight.Bold),
        headlineMedium = base.headlineMedium.withFont(fontFamily, FontWeight.Bold),
        headlineSmall = base.headlineSmall.withFont(fontFamily, FontWeight.Bold),
        titleLarge = base.titleLarge.withFont(fontFamily, FontWeight.Bold),
        titleMedium = base.titleMedium.withFont(fontFamily, FontWeight.Bold),
        titleSmall = base.titleSmall.withFont(fontFamily, FontWeight.Bold),
        bodyLarge = base.bodyLarge.withFont(fontFamily, FontWeight.Normal),
        bodyMedium = base.bodyMedium.withFont(fontFamily, FontWeight.Normal),
        bodySmall = base.bodySmall.withFont(fontFamily, FontWeight.Normal),
        labelLarge = base.labelLarge.withFont(fontFamily, FontWeight.Normal),
        labelMedium = base.labelMedium.withFont(fontFamily, FontWeight.Normal),
        labelSmall = base.labelSmall.withFont(fontFamily, FontWeight.Normal),
    )
}

private fun TextStyle.withFont(fontFamily: FontFamily, fontWeight: FontWeight): TextStyle =
    copy(fontFamily = fontFamily, fontWeight = fontWeight, fontSize = if (fontSize == TextStyle().fontSize) 16.sp else fontSize)
