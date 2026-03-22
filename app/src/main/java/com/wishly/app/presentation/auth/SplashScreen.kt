package com.wishly.app.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wishly.app.R
import com.wishly.app.ui.components.GradientCircularProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    isLoggedIn: Boolean = false
) {
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val progressAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = LinearEasing)
        )

        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing)
        )

        progressAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )

        delay(500)

        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE6E0FF).copy(alpha = 0.8f),
                        Color(0xFFD0FFF5).copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wishly_brand_logo),
                contentDescription = "Logo",
                modifier = Modifier.alpha(logoAlpha.value)
            )

            Text(
                text = "Share Your Wishes",
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.titleSmall.copy(
                    shadow = Shadow(
                        color = Color(0xFF24243F).copy(alpha = 0.3f),
                        offset = Offset(1.0f, 3.0f),
                        blurRadius = 3f
                    )
                ),
                modifier = Modifier
                    .alpha(textAlpha.value)
            )

            GradientCircularProgressIndicator(
                modifier = Modifier
                    .size(80.dp)
                    .padding(12.dp)
                    .alpha(progressAlpha.value),
                strokeWidth = 8.dp,
                durationMillis = 1300
            )
        }
    }
}