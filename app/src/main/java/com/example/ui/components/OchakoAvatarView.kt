package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.OchakoExpression
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.UravityPink
import com.example.ui.theme.UravityPinkLight

@Composable
fun OchakoAvatarView(
    expression: OchakoExpression,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_avatar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.05f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 400 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val imageRes = when (expression) {
        OchakoExpression.NORMAL -> R.drawable.img_ochako_normal
        OchakoExpression.TRAINING -> R.drawable.img_ochako_training
        OchakoExpression.EXCITED, OchakoExpression.BLUSHING, OchakoExpression.THINKING -> R.drawable.img_ochako_excited
    }

    if (fullScreen) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("ochako_fullscreen_avatar")
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Ochako Uraraka Videollamada",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale),
                contentScale = ContentScale.Crop
            )

            // Dynamic anime zero gravity gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // Speaking indicator badge
            if (isSpeaking) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 64.dp)
                        .background(
                            Brush.horizontalGradient(listOf(UravityPink, HeroGold)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎙️ URAVITY HABLANDO...",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        // Circle / Card avatar view
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(140.dp)
                .scale(pulseScale)
                .testTag("ochako_circular_avatar")
        ) {
            // Animated glowing halo
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isSpeaking) DekuEmerald.copy(alpha = 0.8f) else UravityPinkLight,
                                Color.Transparent
                            )
                        )
                    )
            )

            // Border ring
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(listOf(UravityPink, DekuEmerald, HeroGold, UravityPink)),
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Ochako Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
