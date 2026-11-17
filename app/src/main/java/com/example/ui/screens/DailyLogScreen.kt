package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.remote.TrainingSessionDoc
import com.example.data.remote.UiState
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroSurface
import com.example.ui.theme.DarkHeroSurfaceVariant
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.UravityPink
import com.example.viewmodel.HeroTrainingViewModel

@Composable
fun DailyLogScreen(
    viewModel: HeroTrainingViewModel,
    modifier: Modifier = Modifier
) {
    val sessionsState by viewModel.sessionsState.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val totalHeroPoints by viewModel.totalPoints.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkHeroBg)
            .testTag("daily_log_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkHeroSurface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REGISTRO DE PROGRESO DIARIO",
                            color = DekuEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Sesiones con Uravity",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(UravityPink.copy(alpha = 0.2f))
                            .border(1.dp, UravityPink, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${sessionCount} Sesiones",
                            color = UravityPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Overview Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    title = "Puntos de Héroe",
                    value = "${totalHeroPoints ?: 0} HP",
                    accentColor = HeroGold,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    title = "Compañera",
                    value = "Ochako Uravity",
                    accentColor = UravityPink,
                    modifier = Modifier.weight(1f)
                )
            }

            when (val state = sessionsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UravityPink)
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pudimos sincronizar tus sesiones: ${state.message}",
                            color = Color(0xFFFFB4AB),
                            fontSize = 13.sp
                        )
                    }
                }
                is UiState.Success -> {
                    if (sessions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no hay sesiones registradas hoy. ¡Inicia una videollamada o minijuego con Uraraka para registrar tu primer entrenamiento!",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(sessions, key = { it.id }) { session ->
                                TrainingSessionCard(
                                    session = session,
                                    onDelete = { viewModel.deleteSession(session) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Session
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = DekuEmerald,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_session_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar Práctica")
        }

        // Add Session Dialog
        if (showAddDialog) {
            AddSessionDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onConfirm = { title, type, duration, points, notes ->
                    viewModel.logQuickTrainingSession(
                        title = title,
                        type = type,
                        durationMin = duration,
                        points = points,
                        notes = notes
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color.Gray, fontSize = 11.sp)
            Text(
                text = value,
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun TrainingSessionCard(
    session: TrainingSessionDoc,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_item_${session.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(UravityPink.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.trainingType,
                            color = UravityPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HeroGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.rankGrade,
                            color = HeroGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Registro",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = session.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${session.durationMinutes} min • 🗓️ ${session.dateString}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "+${session.heroPoints} HP",
                    color = DekuEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (session.notes.isNotEmpty()) {
                Text(
                    text = session.notes,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Ochako partner note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "💬 ", fontSize = 12.sp)
                    Text(
                        text = session.partnerQuote,
                        color = Color(0xFFF48FB1),
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSessionDialog(
    viewModel: HeroTrainingViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("Entrenamiento Personalizado") }
    var trainingType by remember { mutableStateOf("Control de Gravedad") }
    var durationText by remember { mutableStateOf("25") }
    var notes by remember { mutableStateOf("") }
    val recognizedText by viewModel.voiceHelper.recognizedText.collectAsState()
    val isListening by viewModel.voiceHelper.isSpeechActive.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar Práctica Compartida",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la Sesión") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duración (minutos)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (recognizedText.isNotEmpty() && notes.isEmpty()) recognizedText else notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas de Práctica con Ochako") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (isListening) viewModel.voiceHelper.stopListening()
                                else viewModel.voiceHelper.startListening()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Dictar por voz",
                                tint = if (isListening) DekuEmerald else UravityPink
                            )
                        }
                    }
                )

                if (isListening) {
                    Text(
                        text = "🎙️ Dictando nota con el micrófono...",
                        color = DekuEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dur = durationText.toIntOrNull() ?: 20
                    val finalNotes = if (notes.isNotEmpty()) notes else if (recognizedText.isNotEmpty()) recognizedText else "Sesión completada con éxito."
                    val points = dur * 8
                    onConfirm(title, trainingType, dur, points, finalNotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DekuEmerald)
            ) {
                Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = DarkHeroSurface
    )
}
