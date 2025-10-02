package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.smartlawyeragenda.R
import com.example.smartlawyeragenda.ui.components.*
import com.example.smartlawyeragenda.ui.theme.*

@Composable
fun LoginScreen(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(0.8f) }
    val slideAnimation = remember { Animatable(50f) }
    
    LaunchedEffect(key1 = true) {
        startAnimation = true
        
        launch {
            alphaAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = EaseOutCubic
                )
            )
        }
        
        launch {
            delay(200)
            scaleAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        launch {
            delay(300)
            slideAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = EaseOutCubic
                )
            )
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.Background,
                        AppColors.SurfaceVariant
                    )
                )
            )
    ) {
        // Background decorative elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
        ) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size((60 + index * 20).dp)
                        .offset(
                            x = (100 + index * 80).dp,
                            y = (150 + index * 100).dp
                        )
                        .background(
                            color = AppColors.Primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Large)
                .offset(y = slideAnimation.value.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced App logo with modern design
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scaleAnimation.value)
                    .alpha(alphaAnimation.value)
                    .shadow(
                        elevation = AppElevation.Level5,
                        shape = RoundedCornerShape(AppCornerRadius.Round)
                    ),
                shape = RoundedCornerShape(AppCornerRadius.Round),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Level5)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AppColors.Primary.copy(alpha = 0.1f),
                                    AppColors.Surface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚖️",
                        fontSize = 64.sp,
                        modifier = Modifier
                            .scale(scaleAnimation.value)
                            .alpha(alphaAnimation.value)
                    )
                }
            }
        
            Spacer(modifier = Modifier.height(AppSpacing.Large))
            
            // App title with enhanced typography
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(alphaAnimation.value)
                    .scale(scaleAnimation.value)
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            
            // Subtitle with better styling
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                modifier = Modifier
                    .alpha(alphaAnimation.value)
                    .scale(scaleAnimation.value)
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.Huge))
            
            // Enhanced Sign in button with modern design
            EnhancedButton(
                onClick = {
                    isLoading = true
                    onSignInClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(alphaAnimation.value)
                    .scale(scaleAnimation.value),
                text = stringResource(R.string.sign_in_with_google),
                icon = Icons.Default.AccountCircle,
                enabled = !isLoading,
                isLoading = isLoading,
                variant = ButtonVariant.Primary
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.Large))
            
            // Enhanced info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alphaAnimation.value)
                    .scale(scaleAnimation.value),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.SurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Level2)
            ) {
                Column(
                    modifier = Modifier.padding(AppSpacing.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppColors.Info,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                    
                    Text(
                        text = "تسجيل الدخول مطلوب للوصول إلى النسخ الاحتياطية على Google Drive",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
