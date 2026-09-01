package com.example.nprojetoartesanato.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TauaRed,
    secondary = WoodBrown,
    tertiary = LeafGreen,
    background = Color(0xFF201A19),
    surface = Color(0xFF201A19),
)

private val LightColorScheme = lightColorScheme(
    primary = TauaRed,
    onPrimary = Color.White,
    primaryContainer = TauaRedLight,
    onPrimaryContainer = TauaRedDark,
    secondary = WoodBrown,
    onSecondary = Color.White,
    secondaryContainer = WoodBrownLight,
    onSecondaryContainer = WoodBrownDark,
    tertiary = LeafGreen,
    onTertiary = Color.White,
    tertiaryContainer = LeafGreenLight,
    onTertiaryContainer = LeafGreenDark,
    background = CraftBackground,
    surface = CraftSurface,
    onBackground = Color(0xFF201A19),
    onSurface = Color(0xFF201A19),
)

@Composable
fun NProjetoArtesanatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}