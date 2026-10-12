package com.shishir.shree.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ShreeColors = lightColorScheme(
    primary = DeepBlue,
    onPrimary = Color.White,
    secondary = SkyBlue,
    onSecondary = Color.White,
    background = Canvas,
    onBackground = DeepBlue,
    surface = CardWhite,
    onSurface = DeepBlue,
    surfaceVariant = SoftBlue,
    onSurfaceVariant = DeepBlue,
)

@Composable
fun ShreeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShreeColors,
        typography = ShreeTypography,
        content = content,
    )
}
