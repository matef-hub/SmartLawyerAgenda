package com.example.smartlawyeragenda.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * Theme Manager for Smart Lawyer Agenda App
 * Handles dark mode support and theme switching
 */

@Composable
fun rememberThemeState(): ThemeState {
    return remember { ThemeState() }
}

class ThemeState {
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode.value

    private val _isSystemTheme = mutableStateOf(true)
    val isSystemTheme: Boolean get() = _isSystemTheme.value

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        _isSystemTheme.value = false
    }

    fun setDarkMode(darkMode: Boolean) {
        _isDarkMode.value = darkMode
        _isSystemTheme.value = false
    }

    fun setSystemTheme() {
        _isSystemTheme.value = true
    }

    fun getCurrentTheme(): Boolean {
        return if (_isSystemTheme.value) {
            // هنا ممكن تستخدم إعداد النظام
            false
        } else {
            _isDarkMode.value
        }
    }
}

// ==================== DARK MODE COLORS ====================

object DarkModeColors {
    val Primary = Color(0xFF60A5FA)
    val PrimaryVariant = Color(0xFF3B82F6)
    val OnPrimary = Color(0xFF000000)

    val Secondary = Color(0xFFFBBF24)
    val SecondaryVariant = Color(0xFFF59E0B)
    val OnSecondary = Color(0xFF000000)

    val Tertiary = Color(0xFF34D399)
    val TertiaryVariant = Color(0xFF10B981)
    val OnTertiary = Color(0xFF000000)

    val Surface = Color(0xFF1E293B)
    val SurfaceVariant = Color(0xFF334155)
    val OnSurface = Color(0xFFF1F5F9)
    val OnSurfaceVariant = Color(0xFFCBD5E1)

    val Background = Color(0xFF0F172A)
    val OnBackground = Color(0xFFF1F5F9)

    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F3)

    val Neutral50 = Color(0xFF0F172A)
    val Neutral100 = Color(0xFF1E293B)
    val Neutral200 = Color(0xFF334155)
    val Neutral300 = Color(0xFF475569)
    val Neutral400 = Color(0xFF64748B)
    val Neutral500 = Color(0xFF94A3B8)
    val Neutral600 = Color(0xFFCBD5E1)
    val Neutral700 = Color(0xFFE2E8F0)
    val Neutral800 = Color(0xFFF1F5F9)
    val Neutral900 = Color(0xFFFFFFFF)

    val Scheduled = Color(0xFF3B82F3)
    val Completed = Color(0xFF10B981)
    val Postponed = Color(0xFFF59E0B)
    val Cancelled = Color(0xFFEF4444)

    val Accent = Color(0xFF8B5CF6)
    val Highlight = Color(0xFF1E293B)
    val Border = Color(0xFF334155)
    val Shadow = Color(0x1A000000)
}

// ==================== THEME UTILITIES ====================

@Composable
fun getAppColors(isDarkMode: Boolean): Any {
    return if (isDarkMode) {
        DarkModeAppColors
    } else {
        LightModeAppColors
    }
}

object DarkModeAppColors {
    val Primary = DarkModeColors.Primary
    val PrimaryVariant = DarkModeColors.PrimaryVariant
    val OnPrimary = DarkModeColors.OnPrimary

    val Secondary = DarkModeColors.Secondary
    val SecondaryVariant = DarkModeColors.SecondaryVariant
    val OnSecondary = DarkModeColors.OnSecondary

    val Tertiary = DarkModeColors.Tertiary
    val TertiaryVariant = DarkModeColors.TertiaryVariant
    val OnTertiary = DarkModeColors.OnTertiary

    val Surface = DarkModeColors.Surface
    val SurfaceVariant = DarkModeColors.SurfaceVariant
    val OnSurface = DarkModeColors.OnSurface
    val OnSurfaceVariant = DarkModeColors.OnSurfaceVariant

    val Background = DarkModeColors.Background
    val OnBackground = DarkModeColors.OnBackground

    val Success = DarkModeColors.Success
    val Warning = DarkModeColors.Warning
    val Error = DarkModeColors.Error
    val Info = DarkModeColors.Info

    val Neutral50 = DarkModeColors.Neutral50
    val Neutral100 = DarkModeColors.Neutral100
    val Neutral200 = DarkModeColors.Neutral200
    val Neutral300 = DarkModeColors.Neutral300
    val Neutral400 = DarkModeColors.Neutral400
    val Neutral500 = DarkModeColors.Neutral500
    val Neutral600 = DarkModeColors.Neutral600
    val Neutral700 = DarkModeColors.Neutral700
    val Neutral800 = DarkModeColors.Neutral800
    val Neutral900 = DarkModeColors.Neutral900

    val Scheduled = DarkModeColors.Scheduled
    val Completed = DarkModeColors.Completed
    val Postponed = DarkModeColors.Postponed
    val Cancelled = DarkModeColors.Cancelled

    val GradientStart = DarkModeColors.Primary
    val GradientEnd = DarkModeColors.Secondary
    val GradientTertiary = DarkModeColors.Tertiary

    val Accent = DarkModeColors.Accent
    val Highlight = DarkModeColors.Highlight
    val Border = DarkModeColors.Border
    val Shadow = DarkModeColors.Shadow
}

object LightModeAppColors {
    val Primary = AppColors.Primary
    val PrimaryVariant = AppColors.PrimaryVariant
    val OnPrimary = AppColors.OnPrimary

    val Secondary = AppColors.Secondary
    val SecondaryVariant = AppColors.SecondaryVariant
    val OnSecondary = AppColors.OnSecondary

    val Tertiary = AppColors.Tertiary
    val TertiaryVariant = AppColors.TertiaryVariant
    val OnTertiary = AppColors.OnTertiary

    val Surface = AppColors.Surface
    val SurfaceVariant = AppColors.SurfaceVariant
    val OnSurface = AppColors.OnSurface
    val OnSurfaceVariant = AppColors.OnSurfaceVariant

    val Background = AppColors.Background
    val OnBackground = AppColors.OnBackground

    val Success = AppColors.Success
    val Warning = AppColors.Warning
    val Error = AppColors.Error
    val Info = AppColors.Info

    val Neutral50 = AppColors.Neutral50
    val Neutral100 = AppColors.Neutral100
    val Neutral200 = AppColors.Neutral200
    val Neutral300 = AppColors.Neutral300
    val Neutral400 = AppColors.Neutral400
    val Neutral500 = AppColors.Neutral500
    val Neutral600 = AppColors.Neutral600
    val Neutral700 = AppColors.Neutral700
    val Neutral800 = AppColors.Neutral800
    val Neutral900 = AppColors.Neutral900

    val Scheduled = AppColors.Scheduled
    val Completed = AppColors.Completed
    val Postponed = AppColors.Postponed
    val Cancelled = AppColors.Cancelled

    val GradientStart = AppColors.GradientStart
    val GradientEnd = AppColors.GradientEnd
    val GradientTertiary = AppColors.GradientTertiary

    val Accent = AppColors.Accent
    val Highlight = AppColors.Highlight
    val Border = AppColors.Border
    val Shadow = AppColors.Shadow
}
