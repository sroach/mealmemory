package gy.roach.health.mealmemory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// iOS-style colors
private val IOSBlue = Color(0xFF007AFF)
private val IOSGray = Color(0xFF8E8E93)
private val IOSLightGray = Color(0xFFF2F2F7)
private val IOSSystemGray = Color(0xFF48484A)
private val IOSSystemGray2 = Color(0xFF636366)
private val IOSSystemGray3 = Color(0xFF48484A)
private val IOSSystemGray4 = Color(0xFF3A3A3C)
private val IOSSystemGray5 = Color(0xFF2C2C2E)
private val IOSSystemGray6 = Color(0xFF1C1C1E)

private val IOSLightColorScheme = lightColorScheme(
    primary = IOSBlue,
    onPrimary = Color.White,
    secondary = IOSGray,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = IOSLightGray,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = IOSSystemGray2,
    outline = Color(0xFFD1D1D6),
    outlineVariant = Color(0xFFE5E5EA)
)

private val IOSDarkColorScheme = darkColorScheme(
    primary = IOSBlue,
    onPrimary = Color.White,
    secondary = IOSGray,
    onSecondary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = IOSSystemGray6,
    onSurface = Color.White,
    surfaceVariant = IOSSystemGray5,
    onSurfaceVariant = IOSSystemGray2,
    outline = IOSSystemGray4,
    outlineVariant = IOSSystemGray5
)

// iOS-style typography
private val IOSTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.41).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.41).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.24).sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.08).sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.41).sp
    )
)

@Composable
fun IOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        IOSDarkColorScheme
    } else {
        IOSLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = IOSTypography,
        content = content
    )
}