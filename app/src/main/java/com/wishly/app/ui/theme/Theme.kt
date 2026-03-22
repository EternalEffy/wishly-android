package com.wishly.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7C5DFA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9D7CFB),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF00D4AA),
    onSecondary = Color(0xFF0F0F1A),
    secondaryContainer = Color(0xFF00E5C1),
    onSecondaryContainer = Color(0xFF0F0F1A),

    tertiary = Color(0xFFFF6B6B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFF8E8E),
    onTertiaryContainer = Color.White,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A2E),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF6B7280),

    error = Color(0xFFE53935),
    onError = Color.White,

    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFD1D5DB)
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C5DFA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9D7CFB),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF00D4AA),
    onSecondary = Color(0xFF0F0F1A),
    secondaryContainer = Color(0xFF00E5C1),
    onSecondaryContainer = Color(0xFF0F0F1A),

    tertiary = Color(0xFFFF6B6B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFF8E8E),
    onTertiaryContainer = Color.White,

    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF1A1A2E),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1E1E32),
    onSurfaceVariant = Color(0xFF9CA3AF),

    error = Color(0xFFE53935),
    onError = Color.White,

    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF4B5563)
)

@Composable
fun WishlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
}