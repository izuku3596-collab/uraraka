package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MinigameType
import com.example.ui.components.VoiceMeterVisualizer
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroSurface
import com.example.ui.theme.DarkHeroSurfaceVariant
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.HeroOrange
import com.example.ui.theme.UravityPink
import com.example.ui.theme.UravityPinkLight
import com.example.viewmodel.HeroTrainingViewModel

@Composable
fun MinigamesScreen(
    viewModel: HeroTrainingViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gravedad Cero", "Reflejos Heroicos", "Entrenamiento Vocal")

    val activeMinigame by viewModel.activeMinigame.collectAsState()
    val score by viewModel.minigameScore.collectAsState()
    val combo by viewModel.minigameCombo.collectAsState()
    val debrisList by viewModel.floatingDebrisList.collectAsState()

    val reflexActive by viewModel.reflexTargetActive.collectAsState()
    val targetX by viewModel.reflexTargetX.collectAsState()
    val targetY by viewModel.reflexTargetY.collectAsState()
    val lastReflexMs by viewModel.lastReflexTimeMs.collectAsState()
    val bestReflexMs by viewModel.bestReflexTimeMs.collectAsState()

    val isListening by viewModel.voiceHelper.isSpeechActive.collectAsState()
    val amplitude by viewModel.voiceHelper.audioAmplitude.collectAsState()
    val recognizedText by viewModel.voiceHelper.recognizedText.collectAsState()
    val voicePower by viewModel.voicePowerRecorded.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkHeroBg)
            .testTag("minigames_screen")
    ) {
        // Hero Header Tag
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkHeroSurface)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GIMNASIO GAMMA // UA",
                        color = DekuEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Minijuegos con Uravity",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Score pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(HeroGold.copy(alpha = 0.2f))
                        .border(1.dp, HeroGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PUNTOS: $score",
                        color = HeroGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkHeroSurfaceVariant,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = UravityPink
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        if (activeMinigame != null) {
                            viewModel.stopMinigame()
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) UravityPink else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Minijuego 1: Control de Gravedad Cero
                ZeroGravityDebrisGame(
                    viewModel = viewModel,
                    isActive = activeMinigame == MinigameType.ZERO_GRAVITY_CATCH,
                    debrisList = debrisList,
                    score = score,
                    combo = combo,
                    onStart = { viewModel.startMinigame(MinigameType.ZERO_GRAVITY_CATCH) },
                    onStop = { viewModel.stopMinigame() }
                )
            }

            1 -> {
                // Minijuego 2: Reflejos Plus Ultra
                ReflexChallengeGame(
                    viewModel = viewModel,
                    isActive = activeMinigame == MinigameType.HERO_REFLEXES,
                    reflexActive = reflexActive,
                    targetX = targetX,
                    targetY = targetY,
                    lastMs = lastReflexMs,
                    bestMs = bestReflexMs,
                    score = score,
                    onStart = { viewModel.startMinigame(MinigameType.HERO_REFLEXES) },
                    onStop = { viewModel.stopMinigame() }
                )
            }

            2 -> {
                // Minijuego 3: Entrenamiento Vocal Heroico
                VoiceTrainingDrillGame(
                    viewModel = viewModel,
                    isActive = isListening,
                    amplitude = amplitude,
                    recognizedText = recognizedText,
                    recordedPower = voicePower
                )
            }
        }
    }
}

@Composable
private fun ZeroGravityDebrisGame(
    viewModel: HeroTrainingViewModel,
    isActive: Boolean,
    debrisList: List<com.example.model.FloatingDebris>,
    score: Int,
    combo: Int,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("zero_gravity_game_area")
    ) {
        if (!isActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ochako_training),
                        contentDescription = "Uravity entrenamiento",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Control de Gravedad Cero",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ochako hace flotar los escombros con su Quirk. ¡Tócalos rápidamente antes de que se escapen para ganar puntos de héroe y combos!",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = UravityPink),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_gravity_game_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("¡Comenzar Entrenamiento!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Active Debris Field
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(2.dp, UravityPink.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                // Top Game Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMBO: x$combo 🔥",
                        color = DekuEmerald,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Finalizar y Guardar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Floating debris pieces
                debrisList.forEach { debris ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = (debris.xFraction * 270).dp,
                                top = (debris.yFraction * 440).dp
                            )
                            .size(debris.sizeDp.dp)
                            .clip(CircleShape)
                            .background(UravityPink)
                            .border(2.dp, HeroGold, CircleShape)
                            .clickable { viewModel.onDebrisTapped(debris.id) }
                            .testTag("interactive_debris_${debris.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = debris.emoji, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflexChallengeGame(
    viewModel: HeroTrainingViewModel,
    isActive: Boolean,
    reflexActive: Boolean,
    targetX: Float,
    targetY: Float,
    lastMs: Long,
    bestMs: Long,
    score: Int,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reflex_game_area")
    ) {
        if (!isActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = DekuEmerald,
                        modifier = Modifier.size(56.dp)
                    )

                    Text(
                        text = "Reflejos Plus Ultra",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "Mejora tus reflejos de combate a la par que Izuku perfecciona el One For All. Cuando aparezca el blanco, ¡tócalo lo más rápido posible!",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "Mejor Récord: ${bestMs}ms", color = HeroGold, fontWeight = FontWeight.Bold)
                        Text(text = "Rango Ideal: < 350ms", color = DekuEmerald, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = DekuEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_reflex_btn")
                    ) {
                        Text("Iniciar Desafío de Reflejos", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(2.dp, DekuEmerald, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Último: ${lastMs}ms", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "Mejor: ${bestMs}ms", color = HeroGold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Terminar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (reflexActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = (targetX * 260).dp, top = (targetY * 420).dp)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DekuEmerald)
                            .border(4.dp, Color.White, CircleShape)
                            .clickable { viewModel.onReflexTargetTapped() }
                            .testTag("reflex_target_click"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡ SMASH!", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                } else {
                    Text(
                        text = if (lastMs > 0) "¡Excelente! ${lastMs}ms. Esperando siguiente..." else "¡Atento a la señal!",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceTrainingDrillGame(
    viewModel: HeroTrainingViewModel,
    isActive: Boolean,
    amplitude: Float,
    recognizedText: String,
    recordedPower: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("voice_training_area"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grito Heroico y Comandos Vocales",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Utiliza el micrófono del dispositivo para decir los gritos de batalla de la UA. ¡Uravity evaluará tu potencia vocal y sincronización!",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Voice Visualizer
                VoiceMeterVisualizer(
                    amplitude = amplitude,
                    isActive = isActive,
                    label = if (recognizedText.isNotEmpty()) "Detectado: '$recognizedText'" else "Esperando tu voz...",
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (isActive) viewModel.voiceHelper.stopListening()
                            else viewModel.voiceHelper.startListening()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isActive) Color.Red else DekuEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_mic_listen_btn")
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            tint = if (isActive) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isActive) "Detener Escucha" else "Activar Micrófono",
                            color = if (isActive) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset command drills to practice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Comandos recomendados para gritar:",
                    color = HeroGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val presetPhrases = listOf(
                    "¡PLUS ULTRA!" to 95f,
                    "¡DETROIT SMASH!" to 90f,
                    "¡RELEASE (LIBERACIÓN)!" to 85f,
                    "¡VAMOS CON TODO, OCHAKO!" to 88f
                )

                presetPhrases.forEach { (phrase, pwr) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { viewModel.simulateVoiceShout(pwr, phrase) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = phrase, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Probar grito 📢", color = UravityPinkLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
