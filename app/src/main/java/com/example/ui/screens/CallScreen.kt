package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.model.CallState
import com.example.model.DialogueBank
import com.example.model.MinigameType
import com.example.model.OchakoDialogueLine
import com.example.ui.components.OchakoAvatarView
import com.example.ui.components.VoiceMeterVisualizer
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroSurface
import com.example.ui.theme.DarkHeroSurfaceVariant
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.UravityPink
import com.example.ui.theme.UravityPinkDark
import com.example.ui.theme.UravityPinkLight
import com.example.viewmodel.HeroTrainingViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CallScreen(
    viewModel: HeroTrainingViewModel,
    modifier: Modifier = Modifier
) {
    val callState by viewModel.callState.collectAsState()
    val callDuration by viewModel.callDurationSeconds.collectAsState()
    val currentDialogue by viewModel.currentDialogue.collectAsState()
    val currentExpression by viewModel.currentExpression.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val activeMinigame by viewModel.activeMinigame.collectAsState()

    val isListening by viewModel.voiceHelper.isSpeechActive.collectAsState()
    val amplitude by viewModel.voiceHelper.audioAmplitude.collectAsState()
    val recognizedText by viewModel.voiceHelper.recognizedText.collectAsState()
    val isOchakoSpeaking by viewModel.voiceHelper.isOchakoSpeaking.collectAsState()
    val isOriginalAnimePitch by viewModel.voiceHelper.isOriginalAnimePitch.collectAsState()
    val speechStatusMessage by viewModel.voiceHelper.speechStatusMessage.collectAsState()

    var showMinigameSelector by remember { mutableStateOf(false) }
    var showAllOptionsModal by remember { mutableStateOf(false) }
    var showMicAndApkHelp by remember { mutableStateOf(false) }

    when (callState) {
        CallState.DISCONNECTED -> {
            // Idle call launcher screen
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkHeroBg)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OchakoAvatarView(
                        expression = currentExpression,
                        isSpeaking = false,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Text(
                        text = "Ochako Uraraka (Uravity)",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Heroína Pro en Formación • U.A. High Clase 1-A",
                        color = UravityPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Hero quote
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "\"¡Vamos a dar nuestro mejor esfuerzo, Deku-kun! ¡El entrenamiento continuo nos hará héroes increíbles!\"",
                            color = Color(0xFFF8BBD0),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Call button
                    FloatingActionButton(
                        onClick = { viewModel.startIncomingCall() },
                        containerColor = UravityPink,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("start_simulated_call_fab"),
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Iniciar Videollamada",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Toca para conectar con Uraraka",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        CallState.INCOMING -> {
            // Incoming call ringing screen
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkHeroBg, Color(0xFF2E1025), DarkHeroBg)
                        )
                    )
                    .testTag("incoming_call_screen"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "VIDEOLAMADA ENTRANTE",
                        color = DekuEmerald,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OchakoAvatarView(
                        expression = currentExpression,
                        isSpeaking = true,
                        modifier = Modifier.size(160.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Ochako Uraraka",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Uravity • Enlace de Héroes UA",
                        color = UravityPink,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "\"¡Deku-kun! ¡Contesta, tenemos entrenamiento!\"",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp, bottom = 48.dp)
                    )

                    // Call Actions (Accept & Decline)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reject Button
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FloatingActionButton(
                                onClick = { viewModel.declineOrEndCall() },
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(64.dp)
                                    .testTag("decline_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Rechazar Llamada",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "Rechazar",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Answer Button
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FloatingActionButton(
                                onClick = { viewModel.answerCall() },
                                containerColor = DekuEmerald,
                                contentColor = Color.Black,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(76.dp)
                                    .testTag("answer_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Contestar Llamada",
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = "Contestar",
                                color = DekuEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        CallState.CONNECTED -> {
            // Active Fullscreen Video Call Screen
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .testTag("active_call_screen")
            ) {
                // Fullscreen Ochako Anime Stream
                OchakoAvatarView(
                    expression = currentExpression,
                    isSpeaking = isOchakoSpeaking,
                    fullScreen = true
                )

                // Top Header Overlay: Call Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(DekuEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EN VIVO • URAVITY",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val minutes = callDuration / 60
                        val seconds = callDuration % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            color = HeroGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // UA Network Security Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "UA-NET 5G HD",
                                color = DekuEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Help button explaining APK and Mic
                        IconButton(
                            onClick = { showMicAndApkHelp = true },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Ayuda APK y Micrófono",
                                tint = HeroGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // PiP Self View (Izuku Deku PIP)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 96.dp, end = 16.dp)
                        .size(width = 90.dp, height = 125.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(2.dp, DekuEmerald, RoundedCornerShape(14.dp))
                        .testTag("deku_self_view_pip")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ochako_excited),
                        contentDescription = "Izuku Deku Vista Previa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.85f
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "DEKU (TÚ)",
                            color = DekuEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Center Subtitle & Dialogue Box
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 95.dp)
                ) {
                    // Voice visualizer if active
                    if (isListening || amplitude > 0.05f) {
                        VoiceMeterVisualizer(
                            amplitude = amplitude,
                            isActive = isListening,
                            label = if (recognizedText.isNotEmpty()) "Comando: '$recognizedText'" else "Escuchando a Deku...",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Anime Dialogue Bubble
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, UravityPink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialogue_subtitle_box")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Ochako Uraraka",
                                        color = UravityPink,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (currentDialogue.title.isNotEmpty()) {
                                        Text(
                                            text = " • ${currentDialogue.title}",
                                            color = HeroGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isOriginalAnimePitch) UravityPink.copy(alpha = 0.25f)
                                                else Color.White.copy(alpha = 0.1f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isOriginalAnimePitch) UravityPink else Color.Gray,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.voiceHelper.toggleAnimeVoicePitch() }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isOriginalAnimePitch) "Voz Ochako ✨" else "Voz Normal",
                                            color = if (isOriginalAnimePitch) UravityPinkLight else Color.LightGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.voiceHelper.speakOchako(currentDialogue.text) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Repetir voz",
                                            tint = HeroGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentDialogue.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )

                            // Suggested Replies Pills
                            if (currentDialogue.suggestedReplies.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    currentDialogue.suggestedReplies.forEach { reply ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(UravityPinkDark.copy(alpha = 0.6f))
                                                .border(1.dp, UravityPink, RoundedCornerShape(14.dp))
                                                .clickable { viewModel.sendReply(reply) }
                                                .padding(horizontal = 9.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = reply,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // "ME DIJO LAS OPCIONES TODAS" ACTIONS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Narrate All Options Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(HeroGold.copy(alpha = 0.25f))
                                        .border(1.dp, HeroGold, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.speakAllOptionsNarration() }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            tint = HeroGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "¡Dime todas las opciones!",
                                            color = HeroGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Open Full Options Catalog Modal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DekuEmerald.copy(alpha = 0.25f))
                                        .border(1.dp, DekuEmerald, RoundedCornerShape(10.dp))
                                        .clickable { showAllOptionsModal = true }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "📋 Menú de Opciones",
                                        color = DekuEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Minigame Quick Selector Dialog
                if (showMinigameSelector) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .clickable { showMinigameSelector = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkHeroBg,
                            border = androidx.compose.foundation.BorderStroke(2.dp, HeroGold),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .clickable(enabled = false) {}
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "¡Jugar con Ochako en la llamada!",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Selecciona un ejercicio de entrenamiento interactivo:",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )

                                MinigameOptionButton(
                                    title = "🪨 Control de Gravedad Cero",
                                    desc = "Atrapa y libera escombros flotantes",
                                    onClick = {
                                        showMinigameSelector = false
                                        viewModel.startMinigame(MinigameType.ZERO_GRAVITY_CATCH)
                                    }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                MinigameOptionButton(
                                    title = "⚡ Reflejos de Héroe Plus Ultra",
                                    desc = "Prueba de reacción ultrarrápida",
                                    onClick = {
                                        showMinigameSelector = false
                                        viewModel.startMinigame(MinigameType.HERO_REFLEXES)
                                    }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                MinigameOptionButton(
                                    title = "🎙️ Entrenamiento Vocal Heroico",
                                    desc = "Grita PLUS ULTRA al micrófono",
                                    onClick = {
                                        showMinigameSelector = false
                                        viewModel.startMinigame(MinigameType.VOCAL_POWER_DRILL)
                                    }
                                )
                            }
                        }
                    }
                }

                // In-Call Active Minigame Overlay
                if (activeMinigame != null) {
                    InCallMinigameOverlay(
                        viewModel = viewModel,
                        minigameType = activeMinigame!!,
                        onClose = { viewModel.stopMinigame() }
                    )
                }

                // All Options Catalog Modal ("Me dijo las opciones todas")
                if (showAllOptionsModal) {
                    AllOptionsModal(
                        viewModel = viewModel,
                        onClose = { showAllOptionsModal = false }
                    )
                }

                // Help Modal explaining what opens for APK & Microphone
                if (showMicAndApkHelp) {
                    MicAndApkHelpModal(
                        onClose = { showMicAndApkHelp = false }
                    )
                }

                // Bottom Call Controls Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Mic Guidance Banner
                        val bannerText = when {
                            isListening -> "🎙️ ESCUCHANDO TU VOZ... ¡Grita 'PLUS ULTRA'!"
                            !speechStatusMessage.isNullOrBlank() -> "⚠️ $speechStatusMessage"
                            else -> "🎙️ Toca el micrófono para hablar con Ochako"
                        }
                        Box(
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isListening) DekuEmerald.copy(alpha = 0.95f) else Color.Black.copy(alpha = 0.75f))
                                .border(1.dp, if (isListening) DekuEmerald else HeroGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = bannerText,
                                color = if (isListening) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        // Mic Mute Toggle
                        IconButton(
                            onClick = { viewModel.toggleMicMute() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isMicMuted) Color.Red else Color.White.copy(alpha = 0.2f))
                                .testTag("mute_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Silenciar Micrófono",
                                tint = Color.White
                            )
                        }

                        // Voice Command Shouting Trigger (Microphone Button)
                        IconButton(
                            onClick = {
                                if (isListening) {
                                    viewModel.voiceHelper.stopListening()
                                } else {
                                    viewModel.voiceHelper.startListening()
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isListening) DekuEmerald else UravityPink
                                )
                                .testTag("voice_command_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Activar Comando de Voz",
                                tint = if (isListening) Color.Black else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Minigame Launch Button
                        IconButton(
                            onClick = { showMinigameSelector = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HeroGold)
                                .testTag("in_call_minigame_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = "Minijuegos de entrenamiento",
                                tint = Color.Black
                            )
                        }

                        // Flip Camera
                        IconButton(
                            onClick = { viewModel.toggleCamera() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Cambiar Cámara",
                                tint = Color.White
                            )
                        }

                        // End Call Button
                        IconButton(
                            onClick = { viewModel.declineOrEndCall() },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                                .testTag("end_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Terminar Llamada",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun AllOptionsModal(
    viewModel: HeroTrainingViewModel,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkHeroBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, UravityPink),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxSize(0.85f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TODAS LAS OPCIONES DE OCHAKO",
                            color = DekuEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Catálogo de Diálogos y Voz",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // Narration Banner Button
                Button(
                    onClick = {
                        viewModel.speakAllOptionsNarration()
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UravityPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔊 Que Ochako me diga todas las opciones",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Lazy Column of Categorized Options
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // CATEGORY 1: Saludos y Mensajes
                    item {
                        CategoryHeader(title = "🌟 Saludos y Mensajes de Entrenamiento")
                    }
                    items(DialogueBank.incomingCallGreetings) { line ->
                        DialogueOptionCard(
                            line = line,
                            onListen = {
                                viewModel.playSpecificLine(line)
                                onClose()
                            },
                            onReply = { reply ->
                                viewModel.sendReply(reply)
                                onClose()
                            }
                        )
                    }

                    // CATEGORY 2: Elogios de Combate
                    item {
                        CategoryHeader(title = "⚡ Elogios de Combate y Ánimos")
                    }
                    items(DialogueBank.combatCheeringLines) { line ->
                        DialogueOptionCard(
                            line = line,
                            onListen = {
                                viewModel.playSpecificLine(line)
                                onClose()
                            },
                            onReply = { reply ->
                                viewModel.sendReply(reply)
                                onClose()
                            }
                        )
                    }

                    // CATEGORY 3: Consejos de la U.A.
                    item {
                        CategoryHeader(title = "🪐 Consejos de Héroes de la U.A.")
                    }
                    items(DialogueBank.tipsAndMotivation) { line ->
                        DialogueOptionCard(
                            line = line,
                            onListen = {
                                viewModel.playSpecificLine(line)
                                onClose()
                            },
                            onReply = { reply ->
                                viewModel.sendReply(reply)
                                onClose()
                            }
                        )
                    }

                    // CATEGORY 4: Comandos de Voz
                    item {
                        CategoryHeader(title = "🎙️ Comandos de Voz del Micrófono")
                    }
                    items(DialogueBank.availableVoiceCommands) { (cmd, desc) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkHeroSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HeroGold.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "🗣️ \"$cmd\"", color = HeroGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = desc, color = Color.LightGray, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.processVoiceCommand(cmd)
                                        onClose()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DekuEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Probar", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Text(
        text = title,
        color = HeroGold,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun DialogueOptionCard(
    line: OchakoDialogueLine,
    onListen: () -> Unit,
    onReply: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkHeroSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, UravityPink.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = line.title.ifEmpty { "Frase Heroica" },
                    color = UravityPinkLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Button(
                    onClick = onListen,
                    colors = ButtonDefaults.buttonColors(containerColor = HeroGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Escuchar", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = line.text,
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            if (line.suggestedReplies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    line.suggestedReplies.take(2).forEach { rep ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable { onReply(rep) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "💬 $rep", color = DekuEmerald, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MinigameOptionButton(
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, UravityPink.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = desc, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun InCallMinigameOverlay(
    viewModel: HeroTrainingViewModel,
    minigameType: MinigameType,
    onClose: () -> Unit
) {
    val debrisList by viewModel.floatingDebrisList.collectAsState()
    val score by viewModel.minigameScore.collectAsState()
    val combo by viewModel.minigameCombo.collectAsState()
    val reflexActive by viewModel.reflexTargetActive.collectAsState()
    val targetX by viewModel.reflexTargetX.collectAsState()
    val targetY by viewModel.reflexTargetY.collectAsState()
    val lastReflexMs by viewModel.lastReflexTimeMs.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .testTag("in_call_minigame_overlay")
    ) {
        // Overlay HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 90.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(1.dp, HeroGold, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "PUNTOS: $score | COMBO: x$combo",
                    color = HeroGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Red.copy(alpha = 0.75f))
                    .clickable { onClose() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CERRAR X",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        when (minigameType) {
            MinigameType.ZERO_GRAVITY_CATCH -> {
                // Interactive floating debris
                debrisList.forEach { debris ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = (debris.xFraction * 320).dp,
                                top = (debris.yFraction * 480).dp
                            )
                            .size(debris.sizeDp.dp)
                            .clip(CircleShape)
                            .background(UravityPink.copy(alpha = 0.8f))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { viewModel.onDebrisTapped(debris.id) }
                            .testTag("debris_${debris.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = debris.emoji, fontSize = 26.sp)
                    }
                }
            }

            MinigameType.HERO_REFLEXES -> {
                if (reflexActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = (targetX * 280).dp, top = (targetY * 460).dp)
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DekuEmerald)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable { viewModel.onReflexTargetTapped() }
                            .testTag("reflex_target_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡ TAP!", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                } else if (lastReflexMs > 0) {
                    Text(
                        text = "¡Tiempo de reacción: ${lastReflexMs}ms!",
                        color = DekuEmerald,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            MinigameType.VOCAL_POWER_DRILL -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡GRITA AL MICRÓFONO!",
                        color = HeroGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "¡Di 'PLUS ULTRA' o 'SMASH' para cargar energía!",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DekuEmerald)
                                .clickable { viewModel.simulateVoiceShout(95f, "PLUS ULTRA!") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "PLUS ULTRA!", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(UravityPink)
                                .clickable { viewModel.simulateVoiceShout(90f, "RELEASE!") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "RELEASE!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MicAndApkHelpModal(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkHeroBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, DekuEmerald),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxSize(0.82f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GUÍA RÁPIDA DE HÉROE",
                            color = DekuEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "¿Cómo Abrir el APK y el Micrófono?",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkHeroSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HeroGold.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📱 1. ¿Qué se abre para abrir la app APK?",
                                    color = HeroGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Al pulsar el archivo descargado desde MediaFire o MEGA, se abre el Instalador de Paquetes de Android.\n• Si aparece el mensaje de seguridad de 'fuente desconocida', pulsa 'Ajustes' y activa 'Permitir desde esta fuente'.\n• Luego pulsa 'Instalar' y finalmente 'Abrir' (o toca el ícono de Uravity Call en la pantalla de inicio de tu celular).",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkHeroSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DekuEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🎙️ 2. ¿Qué se abre para abrir el Micrófono?",
                                    color = DekuEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Toca el botón central rosa con el ícono de micrófono 🎙️ durante la llamada.\n• La primera vez, Android abrirá el diálogo: '¿Permitir que Uravity Call grabe audio?'. Elige 'Mientras la app esté en uso'.\n• Al activarse, se abre el Visualizador de Ondas de Voz y el Reconocedor de Voz en tiempo real.\n• Puedes decir palabras clave como 'PLUS ULTRA', 'SMASH', 'GRAVEDAD', 'OPCIONES' o 'REGISTRAR', y Ochako responderá al instante con su voz.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = DekuEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("¡Entendido! Volver al Entrenamiento", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

