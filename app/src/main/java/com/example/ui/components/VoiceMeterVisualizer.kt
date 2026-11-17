package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.UravityPink
import kotlin.random.Random

@Composable
fun VoiceMeterVisualizer(
    amplitude: Float, // 0f to 1f
    isActive: Boolean,
    modifier: Modifier = Modifier,
    label: String = "Detector de Voz de Héroe"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_offset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(12.dp)
            .testTag("voice_meter_visualizer"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            val percentage = if (isActive) {
                ((amplitude * 100f).coerceAtLeast(15f)).toInt()
            } else 0
            Text(
                text = if (isActive) "POTENCIA: $percentage%" else "MICRÓFONO EN ESPERA",
                color = if (isActive) DekuEmerald else Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Animated sound waveform bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val barCount = 14
            for (i in 0 until barCount) {
                val factor = if (isActive) {
                    val randomSeed = ((i % 4) + 1) * 0.2f
                    (amplitude * 0.7f + waveOffset * randomSeed).coerceIn(0.15f, 1f)
                } else {
                    0.08f
                }

                val barHeight = (32 * factor).dp
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = if (isActive) {
                                    listOf(HeroGold, UravityPink, DekuEmerald)
                                } else {
                                    listOf(Color.DarkGray, Color.Gray)
                                }
                            )
                        )
                )
            }
        }
    }
}
