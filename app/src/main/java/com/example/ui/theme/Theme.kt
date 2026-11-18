package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val TravelGuideColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = SurfaceWhite,
    primaryContainer = CoralSoft,
    onPrimaryContainer = Coral,
    secondary = Navy,
    onSecondary = SurfaceWhite,
    secondaryContainer = IndigoSoft,
    onSecondaryContainer = Navy,
    tertiary = Indigo,
    onTertiary = SurfaceWhite,
    background = Lavender,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Lavender,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = DangerRed,
    onError = SurfaceWhite
)

val TravelGuideShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun TravelGuideTheme(
    isHindi: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TravelGuideColorScheme,
        typography = buildAppTypography(isHindi),
        shapes = TravelGuideShapes,
        content = content
    )
}
