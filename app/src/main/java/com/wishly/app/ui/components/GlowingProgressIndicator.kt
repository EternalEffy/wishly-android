package com.wishly.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientCircularProgressIndicator(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF947AFF).copy(alpha = 0.2f),
        Color(0xFF7C5DFA),
        Color(0xFF947AFF).copy(alpha = 0.2f)
    ),
    backgroundColor: Color = gradientColors.first().copy(alpha = 0.1f),
    strokeWidth: Dp = 4.dp,
    durationMillis: Int = 5000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chasingProgress")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val (startAngle, sweepAngle) = calculateAngles(phase)

    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier) {
        val size = this.size
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.minDimension - strokePx) / 2f

        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokePx)
        )

        drawArc(
            brush = Brush.radialGradient(
                colors = gradientColors,
                center = center
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}

private fun calculateAngles(phase: Float): Pair<Float, Float> {
    return if (phase < 0.5f) {
        val progress = phase / 0.5f
        val startAngle = -90f
        val sweepAngle = 0f + (270f * progress)
        startAngle to sweepAngle

    } else {
        val progress = (phase - 0.5f) / 0.5f

        val startAngle = -90f + (360f * progress)

        val minSweep = 10f
        val sweepAngle = 270f - ((270f - minSweep) * progress)

        startAngle to sweepAngle
    }
}