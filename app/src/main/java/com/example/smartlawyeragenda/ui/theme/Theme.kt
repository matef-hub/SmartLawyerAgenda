package com.example.smartlawyeragenda.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.DarkPrimary,
    secondary = AppColors.DarkSecondary,
    tertiary = AppColors.DarkTertiary,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onPrimary = AppColors.OnPrimary,
    onSecondary = AppColors.OnSecondary,
    onTertiary = AppColors.OnTertiary,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnSurface,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,
    error = AppColors.Error,
    onError = AppColors.OnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    tertiary = AppColors.Tertiary,
    background = AppColors.Background,
    surface = AppColors.Surface,
    surfaceVariant = AppColors.SurfaceVariant,
    onPrimary = AppColors.OnPrimary,
    onSecondary = AppColors.OnSecondary,
    onTertiary = AppColors.OnTertiary,
    onBackground = AppColors.OnBackground,
    onSurface = AppColors.OnSurface,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    error = AppColors.Error,
    onError = AppColors.OnPrimary
)

@Composable
fun SmartLawyerAgendaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use custom colors
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
            // Use the modern approach for status bar color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SmartLawyerAgendaThemeWithManager(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val isDarkMode = themeState.getCurrentTheme()
    
    SmartLawyerAgendaTheme(
        darkTheme = isDarkMode,
        content = content
    )
}