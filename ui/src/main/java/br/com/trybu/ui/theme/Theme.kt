package br.com.trybu.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = orange_500,
    onPrimary = gray_0,
    primaryContainer = orange_100,
    onPrimaryContainer = orange_500,
    secondary = orange_50,
    onSecondary = orange_600,
    secondaryContainer = gray_50,
    onSecondaryContainer = orange_500,
    tertiary = white,
    onTertiary = gray_1000,
    tertiaryContainer = gray_100,
    onTertiaryContainer = gray_1000,
    error = danger_300,
    onError = gray_0,
    errorContainer = danger_50,
    onErrorContainer = danger_300,
    background = surface,
    onBackground = gray_1000,
    surface = surface,
    onSurface = gray_1000,
    outlineVariant = OutlineVariant
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}