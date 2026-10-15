package com.finanzaspersonales.gt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** iPisto brand palette. */
object IpistoPalette {
    val Blue = Color(0xFF1B96E8)
    val Lime = Color(0xFF4AE61A)
    val DarkBlue = Color(0xFF172B3A)
    val IceGray = Color(0xFFF4F7FA)
    val White = Color(0xFFFFFFFF)
    val Orange = Color(0xFFFFB547)
    val Red = Color(0xFFE53935)
    val RedDark = Color(0xFFB91C1C)
    val RedSoft = Color(0xFFFEE2E2)
    val Slate = Color(0xFF64748B)

    // Backward-compatible aliases for older screen code.
    val Peach = Orange
    val Lavender = Blue
    val Turquoise = Blue
    val Rose = Orange
    val Butter = Orange
    val Sage = Lime

    val Background = DarkBlue
    val Surface = Color(0xFF1F3344)
    val Elevated = Color(0xFF263B4E)
    val Outline = Slate
    val Accent = Blue
    val AccentPressed = Color(0xFF1782C8)
    val AccentContent = White
    val Expense = Red
    val Income = Lime
    val Balance = Blue
    val Text = White
    val TextSecondary = Color(0xFFB0BEC5)
    val Disabled = Slate

    val BackgroundLight = IceGray
    val SurfaceLight = White
    val ElevatedLight = IceGray
    val OutlineLight = Slate
    val AccentLight = Blue
    val AccentPressedLight = Color(0xFF1782C8)
    val AccentContentLight = White
    val TextLight = DarkBlue
    val TextSecondaryLight = Slate
    val DisabledLight = Slate
}

private val IpistoDarkColors = darkColorScheme(
    primary = IpistoPalette.Blue,
    onPrimary = IpistoPalette.White,
    primaryContainer = IpistoPalette.Surface,
    onPrimaryContainer = IpistoPalette.White,
    secondary = IpistoPalette.Lime,
    onSecondary = IpistoPalette.DarkBlue,
    secondaryContainer = IpistoPalette.Elevated,
    onSecondaryContainer = IpistoPalette.White,
    tertiary = IpistoPalette.Orange,
    onTertiary = IpistoPalette.DarkBlue,
    tertiaryContainer = IpistoPalette.Elevated,
    onTertiaryContainer = IpistoPalette.White,
    error = IpistoPalette.Red,
    onError = IpistoPalette.White,
    errorContainer = Color(0xFF5A3542),
    onErrorContainer = IpistoPalette.White,
    background = IpistoPalette.DarkBlue,
    onBackground = IpistoPalette.White,
    surface = IpistoPalette.Surface,
    onSurface = IpistoPalette.White,
    surfaceVariant = IpistoPalette.Elevated,
    onSurfaceVariant = IpistoPalette.TextSecondary,
    outline = IpistoPalette.Slate,
    outlineVariant = IpistoPalette.Slate,
    scrim = Color(0x990B1020),
    inverseSurface = IpistoPalette.White,
    inverseOnSurface = IpistoPalette.DarkBlue,
    inversePrimary = IpistoPalette.Blue
)

private val IpistoLightColors = lightColorScheme(
    primary = IpistoPalette.Blue,
    onPrimary = IpistoPalette.White,
    primaryContainer = Color(0xFFDCEFFF),
    onPrimaryContainer = IpistoPalette.DarkBlue,
    secondary = IpistoPalette.Lime,
    onSecondary = IpistoPalette.DarkBlue,
    secondaryContainer = Color(0xFFE8FAD9),
    onSecondaryContainer = IpistoPalette.DarkBlue,
    tertiary = IpistoPalette.Orange,
    onTertiary = IpistoPalette.DarkBlue,
    tertiaryContainer = Color(0xFFFFE8C7),
    onTertiaryContainer = IpistoPalette.DarkBlue,
    error = IpistoPalette.Red,
    onError = IpistoPalette.White,
    errorContainer = Color(0xFFFFD9DD),
    onErrorContainer = IpistoPalette.DarkBlue,
    background = IpistoPalette.IceGray,
    onBackground = IpistoPalette.DarkBlue,
    surface = IpistoPalette.White,
    onSurface = IpistoPalette.DarkBlue,
    surfaceVariant = IpistoPalette.IceGray,
    onSurfaceVariant = IpistoPalette.Slate,
    outline = IpistoPalette.Slate,
    outlineVariant = IpistoPalette.Slate,
    scrim = Color(0x33000000),
    inverseSurface = IpistoPalette.DarkBlue,
    inverseOnSurface = IpistoPalette.White,
    inversePrimary = IpistoPalette.Blue
)

val FinanzasShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun FinanzasPersonalesGTTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (isDark) IpistoDarkColors else IpistoLightColors,
        shapes = FinanzasShapes,
        content = content
    )
}