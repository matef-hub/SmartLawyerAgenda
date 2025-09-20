package com.example.smartlawyeragenda.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.ui.theme.*

/**
 * Theme Toggle Component for Dark Mode Switching
 */


@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutCubic
        )
    )
    
    Button(
        onClick = onThemeToggle,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDarkMode) AppColors.Surface else AppColors.Primary
        ),
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Icon(
            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = if (isDarkMode) "الوضع المظلم" else "الوضع المضيء",
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation)
        )
        Spacer(modifier = Modifier.width(AppSpacing.Small))
        Text(
            text = if (isDarkMode) "الوضع المظلم" else "الوضع المضيء",
            style = AppTypography.LabelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ThemeToggleSwitch(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutCubic
        )
    )
    
    Row(
        modifier = modifier
            .clickable { onThemeToggle() }
            .padding(AppSpacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = if (isDarkMode) "الوضع المظلم" else "الوضع المضيء",
            tint = if (isDarkMode) AppColors.Primary else AppColors.OnSurfaceVariant,
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation)
        )
        
        Spacer(modifier = Modifier.width(AppSpacing.Small))
        
        Text(
            text = if (isDarkMode) "الوضع المظلم" else "الوضع المضيء",
            style = AppTypography.BodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isDarkMode) AppColors.Primary else AppColors.OnSurfaceVariant
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Switch(
            checked = isDarkMode,
            onCheckedChange = { onThemeToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppColors.Primary,
                checkedTrackColor = AppColors.Primary.copy(alpha = 0.3f),
                uncheckedThumbColor = AppColors.Neutral400,
                uncheckedTrackColor = AppColors.Neutral300
            )
        )
    }
}

@Composable
fun ThemeSelectorCard(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Level2)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.Small))
                Text(
                    text = "إعدادات المظهر",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            
            ThemeToggleSwitch(
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            
            Text(
                text = if (isDarkMode) 
                    "الوضع المظلم مفعل - يتم حفظ الطاقة وتقليل إجهاد العين" 
                else 
                    "الوضع المضيء مفعل - واجهة واضحة ومشرقة",
                style = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedThemeTransition(
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseInOutCubic
        )
    )
    
    Box(
        modifier = Modifier.alpha(alpha)
    ) {
        content()
    }
}
