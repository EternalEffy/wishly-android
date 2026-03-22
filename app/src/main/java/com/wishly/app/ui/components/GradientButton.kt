package com.wishly.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = modifier,
        style = LocalTextStyle.current.copy(
            shadow = androidx.compose.ui.graphics.Shadow(
                color = Color(0xFF0F0F1A).copy(alpha = 0.4f),
                offset = Offset(2.0f, 4.0f),
                blurRadius = 3f
            )
        )
    )
}

@Composable
fun ButtonLoadingContent(
    loadingText: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Color.White,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        ButtonText(text = loadingText)
    }
}

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultHeight: Float = 50f,
    enableShineAnimation: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shinePosition by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                delayMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(defaultHeight.dp)
                .dropShadow(
                    shape = RoundedCornerShape(defaultHeight.dp / 2),
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 4.dp,
                        color = Color(0xFF7C5DFA).copy(alpha = 0.4f),
                        offset = DpOffset(x = 0.dp, 6.dp)
                    )
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF7C5DFA),
                            Color(0xFF787ff0)
                        )
                    ),
                    shape = RoundedCornerShape(defaultHeight.dp / 2)
                )
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = false,
                        radius = 60.dp,
                        color = Color(0xFFA6ABF8).copy(alpha = 0.3f)
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
        ) {
            if (enableShineAnimation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(defaultHeight.dp / 2))
                        .drawBehind {
                            val shineWidth = size.width * 0.2f

                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.1f),
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.1f),
                                        Color.Transparent
                                    ),
                                    start = Offset(
                                        x = shinePosition * size.width - shineWidth,
                                        y = -50f
                                    ),
                                    end = Offset(
                                        x = shinePosition * size.width,
                                        y = 50f
                                    )
                                )
                            )

                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.1f),
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.1f),
                                        Color.Transparent
                                    ),
                                    start = Offset(
                                        x = size.width - (shinePosition * size.width),
                                        y = -50f
                                    ),
                                    end = Offset(
                                        x = size.width - (shinePosition * size.width) + shineWidth,
                                        y = 50f
                                    )
                                )
                            )
                        }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}