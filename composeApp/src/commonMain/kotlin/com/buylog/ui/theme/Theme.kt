package com.buylog.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme

private val DarkColorScheme = darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)
private val LightColorScheme = lightColorScheme(primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40)

@Composable
fun BuyLogTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val fontFamily = rememberAppFontFamily()
    if (fontFamily == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = appTypography(fontFamily),
        content = content,
    )
}
