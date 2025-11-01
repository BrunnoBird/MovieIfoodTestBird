package com.example.movieifoodtest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = LightGray,
    tertiary = Green,
    background = DarkGray,
    surface = DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MovieIfoodTestTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = dynamicDarkColorScheme(context)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
