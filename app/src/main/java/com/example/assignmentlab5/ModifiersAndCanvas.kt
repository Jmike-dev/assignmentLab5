package com.example.assignmentlab5.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// TASK C: Animated Objective Progress Ring
@Composable
fun ObjectiveRing(
    progress: Float,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 6.dp
) {
    val animatedProgress = remember { Animatable(progress) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 600)
        )
    }

    val currentColor = lerp(
        start = Color(0xFFE53935), // Red at 0%
        stop = Color(0xFF4CAF50),  // Green at 100%
        fraction = animatedProgress.value
    )

    Canvas(
        modifier = modifier
            .size(size)
            .clickable { onAdvance() }
    ) {
        val stroke = strokeWidth.toPx()

        // Background track
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.3f),
            style = Stroke(width = stroke)
        )

        // Progress Arc
        drawArc(
            color = currentColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress.value,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

// TASK D: Reusable Shimmer Loading Modifier
fun Modifier.shimmerLoading(isLoading: Boolean): Modifier = composed {
    if (!isLoading) return@composed this

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    this.then(
        Modifier.drawWithContent {
            drawContent()
            val brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translateAnim - 200f, translateAnim - 200f),
                end = Offset(translateAnim, translateAnim)
            )
            drawRect(brush = brush)
        }
    )
}