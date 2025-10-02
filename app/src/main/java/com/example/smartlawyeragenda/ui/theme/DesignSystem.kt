package com.example.smartlawyeragenda.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartlawyeragenda.R

/**
 * Professional Design System for Smart Lawyer Agenda App
 * Provides consistent colors, typography, spacing, and component styles
 */

// ==================== COLORS ====================

object AppColors {
    val Primary = Color(0xFF1E3A8A)
    val PrimaryVariant = Color(0xFF1E40AF)
    val OnPrimary = Color(0xFFFFFFFF)

    val Secondary = Color(0xFFD97706)
    val SecondaryVariant = Color(0xFFB45309)
    val OnSecondary = Color(0xFFFFFFFF)

    val Tertiary = Color(0xFF059669)
    val TertiaryVariant = Color(0xFF047857)
    val OnTertiary = Color(0xFFFFFFFF)

    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF8FAFC)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceVariant = Color(0xFF475569)

    val Background = Color(0xFFFEFEFE)
    val OnBackground = Color(0xFF0F172A)

    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)

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

    val Scheduled = Color(0xFF3B82F6)
    val Completed = Color(0xFF10B981)
    val Postponed = Color(0xFFF59E0B)
    val Cancelled = Color(0xFFEF4444)

    val GradientStart = Color(0xFF1E3A8A)
    val GradientEnd = Color(0xFF3B82F6)
    val GradientTertiary = Color(0xFFD97706)

    val DarkPrimary = Color(0xFF60A5FA)
    val DarkSecondary = Color(0xFFFBBF24)
    val DarkTertiary = Color(0xFF34D399)
    val DarkSurface = Color(0xFF1E293B)
    val DarkSurfaceVariant = Color(0xFF334155)
    val DarkBackground = Color(0xFF0F172A)
    val DarkOnSurface = Color(0xFFF1F5F9)
    val DarkOnSurfaceVariant = Color(0xFFCBD5E1)
    val DarkOnBackground = Color(0xFFF1F5F9)

    val Accent = Color(0xFF8B5CF6)
    val Highlight = Color(0xFFFEF3C7)
    val Border = Color(0xFFE2E8F0)
    val Shadow = Color(0x1A000000)
}

// ==================== TYPOGRAPHY UTILITIES ====================

/**
 * Typography utility functions to ensure consistent Amiri font usage
 * Use MaterialTheme.typography.* as the primary approach
 */
object TypographyUtils {
    
    /**
     * Creates a TextStyle with Amiri font family preserved when using .copy()
     * Use this when you need to modify typography styles while preserving the font family
     */
    @Composable
    fun withAmiriFont(
        baseStyle: TextStyle,
        fontWeight: FontWeight? = null,
        fontSize: androidx.compose.ui.unit.TextUnit? = null,
        color: Color? = null,
        letterSpacing: androidx.compose.ui.unit.TextUnit? = null,
        lineHeight: androidx.compose.ui.unit.TextUnit? = null
    ): TextStyle {
        return baseStyle.copy(
            fontFamily = AmiriFontFamily, // Always preserve Amiri font
            fontWeight = fontWeight ?: baseStyle.fontWeight,
            fontSize = fontSize ?: baseStyle.fontSize,
            color = color ?: baseStyle.color,
            letterSpacing = letterSpacing ?: baseStyle.letterSpacing,
            lineHeight = lineHeight ?: baseStyle.lineHeight
        )
    }
    
    /**
     * Creates a bold version of a typography style while preserving Amiri font
     */
    @Composable
    fun bold(style: TextStyle): TextStyle {
        return style.copy(
            fontFamily = AmiriFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
    
    /**
     * Creates a colored version of a typography style while preserving Amiri font
     */
    @Composable
    fun withColor(style: TextStyle, color: Color): TextStyle {
        return style.copy(
            fontFamily = AmiriFontFamily,
            color = color
        )
    }
}

// Legacy AppTypography - DEPRECATED: Use MaterialTheme.typography.* instead
// This is kept for backward compatibility but should be migrated to MaterialTheme.typography.*
@Deprecated("Use MaterialTheme.typography.* instead", ReplaceWith("MaterialTheme.typography"))
object AppTypography {
    // Keep existing definitions for backward compatibility during migration
    val DisplayLarge = Typography().displayLarge
    val DisplayMedium = Typography().displayMedium
    val DisplaySmall = Typography().displaySmall
    val HeadlineLarge = Typography().headlineLarge
    val HeadlineMedium = Typography().headlineMedium
    val HeadlineSmall = Typography().headlineSmall
    val TitleLarge = Typography().titleLarge
    val TitleMedium = Typography().titleMedium
    val TitleSmall = Typography().titleSmall
    val BodyLarge = Typography().bodyLarge
    val BodyMedium = Typography().bodyMedium
    val BodySmall = Typography().bodySmall
    val LabelLarge = Typography().labelLarge
    val LabelMedium = Typography().labelMedium
    val LabelSmall = Typography().labelSmall
}

val CustomShapes = Shapes(
    medium = RoundedCornerShape(24.dp)
)

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
    val Rainbow = listOf(AppColors.Primary, AppColors.Secondary, AppColors.Tertiary, AppColors.Accent)
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
