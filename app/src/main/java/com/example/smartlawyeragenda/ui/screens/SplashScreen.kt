package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartlawyeragenda.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(0.5f) }
    val rotationAnimation = remember { Animatable(0f) }
    val progressAnimation = remember { Animatable(0f) }
    val slideAnimation = remember { Animatable(50f) }
    val logoScaleAnimation = remember { Animatable(0.3f) }
    val shimmerAnimation = remember { Animatable(0f) }
    val pulseAnimation = remember { Animatable(0.8f) }

    LaunchedEffect(key1 = true) {
        startAnimation = true

        launch {
            alphaAnimation.animateTo(1f, tween(1200, easing = EaseOutCubic))
        }
        launch {
            delay(300)
            logoScaleAnimation.animateTo(
                1f,
                spring(dampingRatio = 0.6f, stiffness = 200f)
            )
        }
        launch {
            delay(500)
            scaleAnimation.animateTo(
                1f,
                spring(dampingRatio = 0.7f, stiffness = 300f)
            )
        }
        launch {
            delay(700)
            slideAnimation.animateTo(0f, tween(800, easing = EaseOutCubic))
        }
        launch {
            delay(1000)
            progressAnimation.animateTo(1f, tween(2500, easing = EaseInOutCubic))
        }

        // Continuous animations
        launch {
            delay(800)
            while (true) {
                rotationAnimation.animateTo(360f, tween(8000, easing = LinearEasing))
                rotationAnimation.snapTo(0f)
            }
        }
        launch {
            delay(1200)
            while (true) {
                shimmerAnimation.animateTo(1f, tween(2000))
                shimmerAnimation.animateTo(0f, tween(2000))
            }
        }
        launch {
            delay(600)
            while (true) {
                pulseAnimation.animateTo(1.1f, tween(1500, easing = EaseInOutSine))
                pulseAnimation.animateTo(0.9f, tween(1500, easing = EaseInOutSine))
            }
        }

        delay(4000)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF242442),
                        Color(0xFF1C2C50),
                        Color(0xFF0F3460),
                        Color(0xFF533483)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBackgroundElements(alphaAnimation, rotationAnimation, shimmerAnimation)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            ProfessionalLogo(
                logoScaleAnimation,
                pulseAnimation,
                alphaAnimation,
                rotationAnimation
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .alpha(alphaAnimation.value)
                    .scale(scaleAnimation.value)
                    .offset(y = slideAnimation.value.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "أجندة المحاكم اليومية للمحامين",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB8C5D6),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .alpha(alphaAnimation.value * 0.9f)
                    .scale(scaleAnimation.value)
                    .offset(y = slideAnimation.value.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Professional Legal Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF8A9BAE),
                textAlign = TextAlign.Center,
                letterSpacing = 0.8.sp,
                modifier = Modifier
                    .alpha(alphaAnimation.value * 0.7f)
                    .scale(scaleAnimation.value)
                    .offset(y = slideAnimation.value.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            LoadingSection(progressAnimation, alphaAnimation, shimmerAnimation)

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun LoadingSection(
    progressAnimation: Animatable<Float, AnimationVector1D>,
    alphaAnimation: Animatable<Float, AnimationVector1D>,
    shimmerAnimation: Animatable<Float, AnimationVector1D>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Modern progress indicator
        Box(
            modifier = Modifier
                .size(80.dp)
                .alpha(alphaAnimation.value),
            contentAlignment = Alignment.Center
        ) {
            // Background track
            Canvas(modifier = Modifier.size(80.dp)) {
                drawCircle(
                    color = Color(0xFF2D4A6B),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            // Progress arc
            Canvas(modifier = Modifier.size(80.dp)) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00D4AA),
                            Color(0xFF4ECDC4),
                            Color(0xFF44A08D)
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progressAnimation.value,
                    useCenter = false,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Progress percentage
            Text(
                text = "${(progressAnimation.value * 100).toInt()}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00D4AA)
            )
        }

        // Loading text with shimmer effect
        Box {
            Text(
                text = "جاري التحميل...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB8C5D6),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnimation.value)
            )

            // Shimmer overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f * shimmerAnimation.value),
                                Color.Transparent
                            ),
                            startX = -100f + shimmerAnimation.value * 300f,
                            endX = shimmerAnimation.value * 300f
                        )
                    )
            )
        }

        // Status indicator
        Text(
            text = "Coded By Mohamed Atef",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF8A9BAE),
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp,
            modifier = Modifier.alpha(alphaAnimation.value * 0.8f)
        )
    }
}


@Composable
private fun AnimatedBackgroundElements(
    alphaAnimation: Animatable<Float, AnimationVector1D>,
    rotationAnimation: Animatable<Float, AnimationVector1D>,
    shimmerAnimation: Animatable<Float, AnimationVector1D>
) {
    // Geometric background pattern
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaAnimation.value * 0.1f)
    ) {
        val width = size.width
        val height = size.height

        // Subtle pattern
        repeat(6) { i ->
            repeat(4) { j ->
                val x = (width / 6) * i
                val y = (height / 4) * j
                val radius = 40f + (shimmerAnimation.value * 20f)

                drawCircle(
                    color = Color(0xFF4ECDC4).copy(alpha = 0.05f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }

    // Floating particles
    repeat(12) { index ->
        val offsetX = (index * 60 + 40).dp
        val offsetY = (index * 80 + 100).dp

        Box(
            modifier = Modifier
                .size((8 + index % 4 * 2).dp)
                .offset(
                    x = offsetX + (shimmerAnimation.value * 20).dp,
                    y = offsetY + (sin(rotationAnimation.value * 0.01f + index) * 10).dp
                )
                .clip(CircleShape)
                .background(
                    Color(0xFF00D4AA).copy(alpha = 0.1f * alphaAnimation.value)
                )
        )
    }
}


@Composable
private fun ProfessionalLogo(
    logoScaleAnimation: Animatable<Float, AnimationVector1D>,
    pulseAnimation: Animatable<Float, AnimationVector1D>,
    alphaAnimation: Animatable<Float, AnimationVector1D>,
    rotationAnimation: Animatable<Float, AnimationVector1D>
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(logoScaleAnimation.value)
            .alpha(alphaAnimation.value),
        contentAlignment = Alignment.Center
    ) {
        // Rotating ring
        Canvas(
            modifier = Modifier
                .size(180.dp)
                .rotate(rotationAnimation.value)
        ) {
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF00D4AA),
                        Color(0xFF4ECDC4),
                        Color(0xFF44A08D),
                        Color(0xFF00D4AA)
                    )
                ),
                radius = size.minDimension / 2,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Main circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(pulseAnimation.value)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF2D4A6B),
                            Color(0xFF1A2940),
                            Color(0xFF0F1B2A)
                        ),
                        radius = 200f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF00D4AA).copy(alpha = 0.3f),
                                Color(0xFF00D4AA).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 150f
                        )
                    )
            )
            Canvas(Modifier.size(80.dp)) {
                drawProfessionalScales(this, Color(0xFF00D4AA))
            }
        }

        // Decorative dots
        repeat(8) { index ->
            val angle = (index * 45f) + rotationAnimation.value * 0.3f
            val radius = with(density) { 95.dp.toPx() }
            val x = cos(Math.toRadians(angle.toDouble())).toFloat() * radius
            val y = sin(Math.toRadians(angle.toDouble())).toFloat() * radius

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(x = (x / density.density).dp, y = (y / density.density).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ECDC4).copy(alpha = 0.6f))
                    .alpha(alphaAnimation.value)
            )
        }
    }
}

private fun drawProfessionalScales(drawScope: DrawScope, color: Color) {
    with(drawScope) {
        val center = Offset(size.width / 2, size.height / 2)
        val strokeWidth: Float = 3.dp.toPx()

        // Main pole
        drawLine(
            color = color,
            start = Offset(center.x, center.y - 30.dp.toPx()),
            end = Offset(center.x, center.y + 30.dp.toPx()),
            strokeWidth = strokeWidth
        )
        // Cross beam
        drawLine(
            color = color,
            start = Offset(center.x - 25.dp.toPx(), center.y - 15.dp.toPx()),
            end = Offset(center.x + 25.dp.toPx(), center.y - 15.dp.toPx()),
            strokeWidth = strokeWidth
        )
        // Left pan
        drawLine(
            color = color,
            start = Offset(center.x - 25.dp.toPx(), center.y - 15.dp.toPx()),
            end = Offset(center.x - 25.dp.toPx(), center.y),
            strokeWidth = strokeWidth
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - 35.dp.toPx(), center.y - 5.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        // Right pan
        drawLine(
            color = color,
            start = Offset(center.x + 25.dp.toPx(), center.y - 15.dp.toPx()),
            end = Offset(center.x + 25.dp.toPx(), center.y),
            strokeWidth = strokeWidth
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x + 15.dp.toPx(), center.y - 5.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        // Base
        drawRect(
            color = color,
            topLeft = Offset(center.x - 15.dp.toPx(), center.y + 25.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(30.dp.toPx(), 8.dp.toPx())
        )
    }
}
