package com.example.smartlawyeragenda.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Comprehensive Animation System for Smart Lawyer Agenda App
 * Provides consistent animations and transitions throughout the app
 */

// ==================== ANIMATION SPECS ====================

object AppAnimationSpecs {
    val Fast = tween<Float>(durationMillis = 300, easing = EaseOutCubic)
    val Medium = tween<Float>(durationMillis = 500, easing = EaseOutCubic)
    val Slow = tween<Float>(durationMillis = 800, easing = EaseOutCubic)
    val VerySlow = tween<Float>(durationMillis = 1200, easing = EaseOutCubic)
    
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val Snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessHigh
    )
    
    val Gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// ==================== FADE ANIMATIONS ====================

@Composable
fun FadeInAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .alpha(alpha)
    ) {
        content()
    }
}

@Composable
fun FadeOutAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    duration: Int = 300,
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration,
            easing = EaseInCubic
        )
    )
    
    Box(
        modifier = modifier
            .alpha(alpha)
    ) {
        content()
    }
}

// ==================== SCALE ANIMATIONS ====================

@Composable
fun ScaleInAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .scale(scale)
    ) {
        content()
    }
}

@Composable
fun ScaleOutAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    duration: Int = 300,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = duration,
            easing = EaseInCubic
        )
    )
    
    Box(
        modifier = modifier
            .scale(scale)
    ) {
        content()
    }
}

// ==================== SLIDE ANIMATIONS ====================

@Composable
fun SlideInFromBottomAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 100f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        content()
    }
}

@Composable
fun SlideInFromTopAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else -100f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        content()
    }
}

@Composable
fun SlideInFromLeftAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val offsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -100f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX
            }
    ) {
        content()
    }
}

@Composable
fun SlideInFromRightAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val offsetX by animateFloatAsState(
        targetValue = if (visible) 0f else 100f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX
            }
    ) {
        content()
    }
}

// ==================== ROTATION ANIMATIONS ====================

@Composable
fun RotateInAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 800,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (visible) 0f else 360f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseInOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        content()
    }
}

// ==================== COMBINED ANIMATIONS ====================

@Composable
fun FadeInScaleInAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 600,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .alpha(alpha)
            .scale(scale)
    ) {
        content()
    }
}

@Composable
fun FadeInSlideInAnimation(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    duration: Int = 600,
    slideDirection: SlideDirection = SlideDirection.Bottom,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else when (slideDirection) {
            SlideDirection.Bottom -> 100f
            SlideDirection.Top -> -100f
            SlideDirection.Left -> 0f
            SlideDirection.Right -> 0f
        },
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = EaseOutCubic
        )
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier
            .alpha(alpha)
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        content()
    }
}

// ==================== STAGGERED ANIMATIONS ====================

@Composable
fun StaggeredFadeInAnimation(
    modifier: Modifier = Modifier,
    itemCount: Int,
    staggerDelay: Int = 100,
    duration: Int = 400,
    content: @Composable (index: Int) -> Unit
) {
    repeat(itemCount) { index ->
        FadeInAnimation(
            modifier = modifier,
            delay = index * staggerDelay,
            duration = duration
        ) {
            content(index)
        }
    }
}

// ==================== ENUMS ====================

enum class SlideDirection {
    Top,
    Bottom,
    Left,
    Right
}

// ==================== ANIMATION UTILITIES ====================

@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + slideInVertically(),
    exit: ExitTransition = fadeOut() + slideOutVertically(),
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

@Composable
fun CrossfadeAnimation(
    targetState: Any?,
    modifier: Modifier = Modifier,
    content: @Composable (targetState: Any?) -> Unit
) {
    Crossfade(
        targetState = targetState,
        modifier = modifier
    ) { state ->
        content(state)
    }
}
