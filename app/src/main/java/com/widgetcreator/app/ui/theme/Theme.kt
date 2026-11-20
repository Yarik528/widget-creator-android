package com.widgetcreator.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF1E88E5)
private val Indigo = Color(0xFF3949AB)
private val Teal = Color(0xFF00ACC1)

private val LightColors = lightColorScheme(
    primary = Blue,
    secondary = Teal,
    tertiary = Indigo
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF80DEEA),
    tertiary = Color(0xFF9FA8DA)
)

@Composable
fun WidgetCreatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
