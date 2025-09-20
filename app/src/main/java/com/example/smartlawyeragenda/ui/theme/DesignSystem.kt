package com.example.smartlawyeragenda.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Professional Design System for Smart Lawyer Agenda App
 * Provides consistent colors, typography, spacing, and component styles
 */

// ==================== COLORS ====================

object AppColors {
    // Primary Colors - Professional Legal Theme
    val Primary = Color(0xFF1E3A8A) // Deep Professional Blue
    val PrimaryVariant = Color(0xFF1E40AF)
    val OnPrimary = Color(0xFFFFFFFF)
    
    // Secondary Colors - Gold Accent
    val Secondary = Color(0xFFD97706) // Professional Gold
    val SecondaryVariant = Color(0xFFB45309)
    val OnSecondary = Color(0xFFFFFFFF)
    
    // Tertiary Colors - Success Green
    val Tertiary = Color(0xFF059669) // Professional Green
    val TertiaryVariant = Color(0xFF047857)
    val OnTertiary = Color(0xFFFFFFFF)
    
    // Surface Colors
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF8FAFC)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceVariant = Color(0xFF475569)
    
    // Background Colors
    val Background = Color(0xFFFEFEFE)
    val OnBackground = Color(0xFF0F172A)
    
    // Status Colors
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)
    
    // Neutral Colors - Enhanced Scale
    val Neutral50 = Color(0xFFF8FAFC)
    val Neutral100 = Color(0xFFF1F5F9)
    val Neutral200 = Color(0xFFE2E8F0)
    val Neutral300 = Color(0xFFCBD5E1)
    val Neutral400 = Color(0xFF94A3B8)
    val Neutral500 = Color(0xFF64748B)
    val Neutral600 = Color(0xFF475569)
    val Neutral700 = Color(0xFF334155)
    val Neutral800 = Color(0xFF1E293B)
    val Neutral900 = Color(0xFF0F172A)
    
    // Session Status Colors - Enhanced
    val Scheduled = Color(0xFF3B82F6) // Blue
    val Completed = Color(0xFF10B981) // Green
    val Postponed = Color(0xFFF59E0B) // Amber
    val Cancelled = Color(0xFFEF4444) // Red
    
    // Gradient Colors
    val GradientStart = Color(0xFF1E3A8A)
    val GradientEnd = Color(0xFF3B82F6)
    val GradientTertiary = Color(0xFFD97706)
    
    // Dark Theme Colors
    val DarkPrimary = Color(0xFF60A5FA)
    val DarkSecondary = Color(0xFFFBBF24)
    val DarkTertiary = Color(0xFF34D399)
    val DarkSurface = Color(0xFF1E293B)
    val DarkSurfaceVariant = Color(0xFF334155)
    val DarkBackground = Color(0xFF0F172A)
    val DarkOnSurface = Color(0xFFF1F5F9)
    val DarkOnSurfaceVariant = Color(0xFFCBD5E1)
    val DarkOnBackground = Color(0xFFF1F5F9)
    
    // Special Colors
    val Accent = Color(0xFF8B5CF6) // Purple accent
    val Highlight = Color(0xFFFEF3C7) // Light yellow highlight
    val Border = Color(0xFFE2E8F0)
    val Shadow = Color(0x1A000000)
}

// ==================== TYPOGRAPHY ====================

object AppTypography {
    // Font Family - Professional Legal Typography
    val DefaultFontFamily: FontFamily = FontFamily.Default
    val BoldFontFamily: FontFamily = FontFamily.Default
    val LightFontFamily: FontFamily = FontFamily.Default
    
    // Display Styles
    val DisplayLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    )
    
    val DisplayMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )
    
    val DisplaySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    )
    
    // Headline Styles
    val HeadlineLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
    
    val HeadlineMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    
    val HeadlineSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    
    // Title Styles
    val TitleLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    
    val TitleMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    
    val TitleSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    // Body Styles
    val BodyLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    
    val BodyMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    
    val BodySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // Label Styles
    val LabelLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    val LabelMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    val LabelSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

// ==================== SPACING ====================

object AppSpacing {
    val None = 0.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Huge = 48.dp
    val Massive = 64.dp
    val Giant = 80.dp
    
    // Component specific spacing
    val CardPadding = 16.dp
    val ScreenPadding = 16.dp
    val ButtonPadding = 12.dp
    val IconPadding = 8.dp
    val TextPadding = 4.dp
}

// ==================== CORNER RADIUS ====================

object AppCornerRadius {
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
    val Round = 24.dp
    val Circle = 50.dp
}

// ==================== ELEVATION ====================

object AppElevation {
    val Level0 = 0.dp
    val Level1 = 1.dp
    val Level2 = 3.dp
    val Level3 = 6.dp
    val Level4 = 8.dp
    val Level5 = 12.dp
    val Level6 = 16.dp
    val Level7 = 20.dp
}

// ==================== GRADIENTS ====================

object AppGradients {
    val Primary = listOf(AppColors.GradientStart, AppColors.GradientEnd)
    val Secondary = listOf(AppColors.Secondary, AppColors.SecondaryVariant)
    val Tertiary = listOf(AppColors.Tertiary, AppColors.TertiaryVariant)
    val Rainbow = listOf(
        AppColors.Primary,
        AppColors.Secondary,
        AppColors.Tertiary,
        AppColors.Accent
    )
    val Neutral = listOf(AppColors.Neutral100, AppColors.Neutral200)
    val Success = listOf(AppColors.Success, AppColors.Tertiary)
    val Warning = listOf(AppColors.Warning, AppColors.Secondary)
    val Error = listOf(AppColors.Error, Color(0xFFDC2626))
}

// ==================== COMPONENT STYLES ====================

object AppShapes {
    val Small = RoundedCornerShape(AppCornerRadius.Small)
    val Medium = RoundedCornerShape(AppCornerRadius.Medium)
    val Large = RoundedCornerShape(AppCornerRadius.Large)
    val ExtraLarge = RoundedCornerShape(AppCornerRadius.ExtraLarge)
    val Round = RoundedCornerShape(AppCornerRadius.Round)
}

// ==================== CARD STYLES ====================

@Composable
fun appCardStyle() = CardDefaults.cardColors(
    containerColor = AppColors.Surface,
    contentColor = AppColors.OnSurface
)

@Composable
fun appCardElevation() = CardDefaults.cardElevation(
    defaultElevation = AppElevation.Level2
)

// ==================== BUTTON STYLES ====================

@Composable
fun primaryButtonStyle() = ButtonDefaults.buttonColors(
    containerColor = AppColors.Primary,
    contentColor = AppColors.OnPrimary
)

@Composable
fun secondaryButtonStyle() = ButtonDefaults.buttonColors(
    containerColor = AppColors.Secondary,
    contentColor = AppColors.OnSecondary
)

@Composable
fun outlinedButtonStyle() = ButtonDefaults.outlinedButtonColors(
    contentColor = AppColors.Primary
)

// ==================== CHIP STYLES ====================

@Composable
fun statusChipStyle(status: String) = when (status) {
    "SCHEDULED" -> AssistChipDefaults.assistChipColors(
        containerColor = AppColors.Scheduled.copy(alpha = 0.1f),
        labelColor = AppColors.Scheduled
    )
    "COMPLETED" -> AssistChipDefaults.assistChipColors(
        containerColor = AppColors.Completed.copy(alpha = 0.1f),
        labelColor = AppColors.Completed
    )
    "POSTPONED" -> AssistChipDefaults.assistChipColors(
        containerColor = AppColors.Postponed.copy(alpha = 0.1f),
        labelColor = AppColors.Postponed
    )
    "CANCELLED" -> AssistChipDefaults.assistChipColors(
        containerColor = AppColors.Cancelled.copy(alpha = 0.1f),
        labelColor = AppColors.Cancelled
    )
    else -> AssistChipDefaults.assistChipColors()
}

// ==================== GRADIENT COLORS ====================

val AppGradient = listOf(
    AppColors.GradientStart,
    AppColors.GradientEnd
)
