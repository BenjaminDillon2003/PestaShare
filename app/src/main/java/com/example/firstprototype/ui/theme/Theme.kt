package com.example.firstprototype.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * PestaShare Premium Color Scheme (Light Mode)
 */
private val LightColorScheme = lightColorScheme(
    primary = PestaBlue,
    secondary = PestaGreen,
    background = BackgroundSurface,
    surface = CardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderLight
)

/**
 * PestaShare Premium Color Scheme (Dark Mode)
 */
private val DarkColorScheme = darkColorScheme(
    primary = PestaBlue,
    secondary = PestaGreen,
    background = Color(0xFF0F172A), // Slate 900 for an elegant dark mode
    surface = Color(0xFF1E293B),    // Slate 800 for cards
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF334155)
)

/**
 * Main theme wrapper for the PestaShare application.
 * Configures the Material3 color scheme, typography, and content.
 *
 * @param darkTheme Whether the dark color scheme should be used.
 * @param dynamicColor Whether to use dynamic color (Material You) on supported devices.
 *                     Default is false to maintain brand consistency.
 * @param content The composable content to be styled by this theme.
 */
@Composable
fun FirstPrototypeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}