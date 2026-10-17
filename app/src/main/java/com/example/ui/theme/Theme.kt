package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TerracottaOrange,
    onPrimary = Color.White,
    primaryContainer = TerracottaContainer,
    onPrimaryContainer = Color(0xFF4C1D06),
    secondary = OliveSageGreen,
    onSecondary = Color.White,
    secondaryContainer = OliveSageContainer,
    onSecondaryContainer = Color(0xFF1B2917),
    tertiary = MarigoldYellow,
    background = CreamBackground,
    onBackground = CharcoalText,
    surface = CreamSurface,
    onSurface = CharcoalText,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = MutedCharcoalText,
    outline = DelicateBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF421C08),
    onPrimaryContainer = Color(0xFFFFDBCD),
    secondary = Color(0xFF8CA080),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF233020),
    onSecondaryContainer = Color(0xFFD4E6CE),
    tertiary = MarigoldYellow,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMutedText,
    outline = DarkBorder
)

@Composable
fun TazkeerTheme(
    darkTheme: Boolean = false, // Warm cream mode from logo by default!
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
