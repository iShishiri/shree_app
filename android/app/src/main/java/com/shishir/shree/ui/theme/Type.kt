package com.shishir.shree.ui.theme

import androidx.compose.material3.Typography

private val d = Typography()

// Baloo 2 applied across the whole type scale.
val ShreeTypography = Typography(
    displayLarge = d.displayLarge.copy(fontFamily = Baloo),
    displayMedium = d.displayMedium.copy(fontFamily = Baloo),
    displaySmall = d.displaySmall.copy(fontFamily = Baloo),
    headlineLarge = d.headlineLarge.copy(fontFamily = Baloo),
    headlineMedium = d.headlineMedium.copy(fontFamily = Baloo),
    headlineSmall = d.headlineSmall.copy(fontFamily = Baloo),
    titleLarge = d.titleLarge.copy(fontFamily = Baloo),
    titleMedium = d.titleMedium.copy(fontFamily = Baloo),
    titleSmall = d.titleSmall.copy(fontFamily = Baloo),
    bodyLarge = d.bodyLarge.copy(fontFamily = Baloo),
    bodyMedium = d.bodyMedium.copy(fontFamily = Baloo),
    bodySmall = d.bodySmall.copy(fontFamily = Baloo),
    labelLarge = d.labelLarge.copy(fontFamily = Baloo),
    labelMedium = d.labelMedium.copy(fontFamily = Baloo),
    labelSmall = d.labelSmall.copy(fontFamily = Baloo),
)
