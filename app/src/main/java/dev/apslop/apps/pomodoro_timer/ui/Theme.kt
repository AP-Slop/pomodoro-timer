package dev.apslop.apps.pomodoro_timer.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFD9483B),
    secondary = Color(0xFF775651),
    tertiary = Color(0xFF146C4F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4A5),
    secondary = Color(0xFFE7BDB6),
    tertiary = Color(0xFF7DD8B4),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
