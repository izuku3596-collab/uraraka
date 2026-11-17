package com.example.ui.theme

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
    primary = UravityPink,
    onPrimary = Color.White,
    primaryContainer = UravityPinkDark,
    onPrimaryContainer = Color.White,
    secondary = DekuEmerald,
    onSecondary = Color.Black,
    secondaryContainer = DekuGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = HeroGold,
    onTertiary = Color.Black,
    background = DarkHeroBg,
    onBackground = TextPrimaryDark,
    surface = DarkHeroSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkHeroSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = UravityPink.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = UravityPinkDark,
    onPrimary = Color.White,
    primaryContainer = UravityPinkLight,
    onPrimaryContainer = Color.Black,
    secondary = DekuGreenDark,
    onSecondary = Color.White,
    tertiary = HeroOrange,
    background = Color(0xFFFFF0F5),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFFFE4EC),
    onSurfaceVariant = Color(0xFF4A148C),
    outline = UravityPink
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to immersive dark anime hero theme
    dynamicColor: Boolean = false, // Keep signature Ochako/Deku hero palette
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
