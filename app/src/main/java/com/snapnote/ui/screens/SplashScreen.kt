package com.snapnote.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5B5891),
                        Color(0xFF3D3A6B),
                        Color(0xFF17134A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📸",
                    fontSize = 52.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SnapNote",
                modifier = Modifier.alpha(textAlpha),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Capture · Extract · Organize",
                modifier = Modifier.alpha(subtitleAlpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(56.dp))

            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .alpha(subtitleAlpha),
                color = Color.White.copy(alpha = 0.6f),
                strokeWidth = 2.dp
            )
        }

        Text(
            text = "v1.0",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(subtitleAlpha),
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}
