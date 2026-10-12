@file:OptIn(ExperimentalTextApi::class)

package com.shishir.shree.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.shishir.shree.R

private fun baloo(weight: Int) = Font(
    resId = R.font.baloo2_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val Baloo = FontFamily(
    baloo(400),
    baloo(500),
    baloo(600),
    baloo(700),
)
