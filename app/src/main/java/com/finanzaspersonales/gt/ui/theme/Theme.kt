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

/** Shared iPisto palette. Screen code should use MaterialTheme.colorScheme or these semantic accents. */
object IpistoPalette {
    val Background = Color(0xFF1F2638)
    val Surface = Color(0xFF2A3350)
    val Elevated = Color(0xFF343E5E)
    val Outline = Color(0xFF3F4A6B)
    val Accent = Color(0xFF8FA8F5)
    val AccentPressed = Color(0xFFB3C4FA)
    val AccentContent = Color(0xFF1B2133)
    val Expense = Color(0xFFF2909B)
    val Income = Color(0xFF85D9B5)
    val Balance = Color(0xFF8CC8F2)
    val Lavender = Color(0xFFB7A6F0)
    val Peach = Color(0xFFF5C28A)
    val Rose = Color(0xFFF2A9C9)
    val Turquoise = Color(0xFF7FD3D6)
    val Butter = Color(0xFFF3DF93)
    val Sage = Color(0xFFA8D5A2)
    val Text = Color(0xFFEEF1FA)
    val TextSecondary = Color(0xFFA9B3CC)
    val Disabled = Color(0xFF6F7A99)

    // Light theme colors - professional, clean
    val BackgroundLight = Color(0xFFF5F7FA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val ElevatedLight = Color(0xFFFAFBFF)
    val OutlineLight = Color(0xFFE1E5EF)
    val AccentLight = Color(0xFF2E5AAC)
    val AccentPressedLight = Color(0xFF3F73D1)
    val AccentContentLight = Color(0xFFFFFFFF)
    val TextLight = Color(0xFF1A2233)
    val TextSecondaryLight = Color(0xFF5B6788)
    val DisabledLight = Color(0xFFA9B3CC)
}

private val IpistoDarkColors = darkColorScheme(
    primary = IpistoPalette.Accent,
    onPrimary = IpistoPalette.AccentContent,
    primaryContainer = Color(0xFF3A4770),
    onPrimaryContainer = IpistoPalette.Text,
    secondary = IpistoPalette.Lavender,
    onSecondary = IpistoPalette.AccentContent,
    secondaryContainer = Color(0xFF46405F),
    onSecondaryContainer = IpistoPalette.Text,
    tertiary = IpistoPalette.Balance,
    onTertiary = IpistoPalette.AccentContent,
    tertiaryContainer = Color(0xFF30475F),
    onTertiaryContainer = IpistoPalette.Text,
    error = IpistoPalette.Expense,
    onError = IpistoPalette.AccentContent,
    errorContainer = Color(0xFF5A3542),
    onErrorContainer = IpistoPalette.Text,
    background = IpistoPalette.Background,
    onBackground = IpistoPalette.Text,
    surface = IpistoPalette.Surface,
    onSurface = IpistoPalette.Text,
    surfaceVariant = IpistoPalette.Elevated,
    onSurfaceVariant = IpistoPalette.TextSecondary,
    outline = IpistoPalette.Outline,
    outlineVariant = IpistoPalette.Outline,
    scrim = Color(0x990B1020),
    inverseSurface = IpistoPalette.Text,
    inverseOnSurface = IpistoPalette.Background,
    inversePrimary = Color(0xFF526BB7)
)

private val IpistoLightColors = lightColorScheme(
    primary = IpistoPalette.AccentLight,
    onPrimary = IpistoPalette.AccentContentLight,
    primaryContainer = Color(0xFFDEE7FF),
    onPrimaryContainer = IpistoPalette.TextLight,
    secondary = Color(0xFF6B5CB8),
    onSecondary = IpistoPalette.AccentContentLight,
    secondaryContainer = Color(0xFFE6DEFF),
    onSecondaryContainer = IpistoPalette.TextLight,
    tertiary = Color(0xFF2C7BBF),
    onTertiary = IpistoPalette.AccentContentLight,
    tertiaryContainer = Color(0xFFD6E6FF),
    onTertiaryContainer = IpistoPalette.TextLight,
    error = Color(0xFFD73C50),
    onError = IpistoPalette.AccentContentLight,
    errorContainer = Color(0xFFFFD9DD),
    onErrorContainer = IpistoPalette.TextLight,
    background = IpistoPalette.BackgroundLight,
    onBackground = IpistoPalette.TextLight,
    surface = IpistoPalette.SurfaceLight,
    onSurface = IpistoPalette.TextLight,
    surfaceVariant = IpistoPalette.ElevatedLight,
    onSurfaceVariant = IpistoPalette.TextSecondaryLight,
    outline = IpistoPalette.OutlineLight,
    outlineVariant = IpistoPalette.OutlineLight,
    scrim = Color(0x33000000),
    inverseSurface = IpistoPalette.TextLight,
    inverseOnSurface = IpistoPalette.BackgroundLight,
    inversePrimary = Color(0xFF8FA8F5)
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

