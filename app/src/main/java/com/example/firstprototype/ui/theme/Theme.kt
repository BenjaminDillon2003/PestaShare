package com.example.firstprototype.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores Premium para PestaShare (Luz)
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

// Esquema de colores Premium para PestaShare (Oscuro)
private val DarkColorScheme = darkColorScheme(
    primary = PestaBlue,
    secondary = PestaGreen,
    background = Color(0xFF0F172A), // Slate 900 para un modo oscuro elegante
    surface = Color(0xFF1E293B),    // Slate 800 para las tarjetas
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF334155)
)

@Composable
fun FirstPrototypeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Forzamos dynamicColor a false para mantener la consistencia de tu marca premium
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