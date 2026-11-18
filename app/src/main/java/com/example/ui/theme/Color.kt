package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF282A6B)
val NavyDark = Color(0xFF1D1F52)
val Coral = Color(0xFFF4634F)
val CoralSoft = Color(0xFFFFEDE9)
val Indigo = Color(0xFF4F46C8)
val IndigoSoft = Color(0xFFECEBFF)
val Green = Color(0xFF17A673)
val GreenSoft = Color(0xFFE6F8F1)
val Lavender = Color(0xFFEEF0F8)
val Amber = Color(0xFFF7B13B)
val AmberSoft = Color(0xFFFFF5E0)

val SurfaceWhite = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFC2C9E2)
val TextPrimary = Color(0xFF1C1E42)
val TextSecondary = Color(0xFF6B708B)
val TextMuted = Color(0xFF9196B0)
val DividerLight = Color(0xFFE0E4F0)
val DangerRed = Color(0xFFD9383A)

val SkyBlue = Color(0xFF3B82F6)
val SkySoft = Color(0xFFE8F2FF)
val PlumPurple = Color(0xFF9B59B6)
val TealAccent = Color(0xFF16A085)

// Colorful Home Tile Tints & Borders
val TileIndigoBg = Color(0xFFEFEEFF)
val TileIndigoBorder = Color(0xFFB4AEF5)

val TileAmberBg = Color(0xFFFFF6E2)
val TileAmberBorder = Color(0xFFF3C66E)

val TileCoralBg = Color(0xFFFFECE8)
val TileCoralBorder = Color(0xFFF6ABA0)

val TileSkyBg = Color(0xFFE9F3FF)
val TileSkyBorder = Color(0xFF9BC6FA)

val TileGreenBg = Color(0xFFE6F9F1)
val TileGreenBorder = Color(0xFF88D7B9)

val SplashGradient = Brush.verticalGradient(
    colors = listOf(Coral, Color(0xFF7B49B8), Navy)
)

val IndigoBannerGradient = Brush.linearGradient(
    colors = listOf(Indigo, Navy)
)

val CoralCardGradient = Brush.linearGradient(
    colors = listOf(Coral, Color(0xFFF7945D))
)

fun cardGradientForKey(key: String): Brush {
    return when (key.lowercase()) {
        "coral", "rajasthan", "jaipur", "india" -> Brush.linearGradient(
            colors = listOf(Color(0xFFF4634F), Color(0xFFF7975E))
        )
        "indigo", "kerala", "jodhpur", "thailand" -> Brush.linearGradient(
            colors = listOf(Color(0xFF4F46C8), Color(0xFF7A72E8))
        )
        "green", "goa", "ajmer", "uttarakhand", "dubai" -> Brush.linearGradient(
            colors = listOf(Color(0xFF17A673), Color(0xFF39C692))
        )
        "amber", "himachal", "jaisalmer", "japan" -> Brush.linearGradient(
            colors = listOf(Color(0xFFF39C12), Color(0xFFF7C35F))
        )
        "sky", "udaipur", "singapore" -> Brush.linearGradient(
            colors = listOf(Color(0xFF3B82F6), Color(0xFF6BB1FF))
        )
        "plum", "pushkar", "madhya pradesh", "vietnam" -> Brush.linearGradient(
            colors = listOf(Color(0xFF8E44AD), Color(0xFFBB74D6))
        )
        else -> Brush.linearGradient(
            colors = listOf(Navy, Indigo)
        )
    }
}
