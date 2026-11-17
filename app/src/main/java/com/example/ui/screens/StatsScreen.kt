package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroSurface
import com.example.ui.theme.DarkHeroSurfaceVariant
import com.example.ui.theme.DekuEmerald
import com.example.ui.theme.HeroGold
import com.example.ui.theme.HeroOrange
import com.example.ui.theme.UravityPink
import com.example.viewmodel.HeroTrainingViewModel

@Composable
fun StatsScreen(
    viewModel: HeroTrainingViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.heroProfile.collectAsState()
    val totalHeroPoints by viewModel.totalPoints.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkHeroBg)
            .verticalScroll(rememberScrollState())
            .testTag("stats_screen")
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkHeroSurface)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "ANÁLISIS DE MEJORA CONSTANTE",
                    color = DekuEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Estadísticas de Héroe",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Level Progression Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nivel ${profile.heroLevel}",
                                color = HeroGold,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = profile.heroRankTitle,
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DekuEmerald.copy(alpha = 0.2f))
                                .border(1.dp, DekuEmerald, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Racha: ${profile.trainingStreakDays} días 🔥",
                                color = DekuEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val xpRatio = (profile.currentXp.toFloat() / profile.xpForNextLevel.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { xpRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = UravityPink,
                        trackColor = Color.DarkGray
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${profile.currentXp} XP obtenidos",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Faltan ${profile.xpForNextLevel - profile.currentXp} XP para Nivel ${profile.heroLevel + 1}",
                            color = HeroGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Continuous Improvement Mastery Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = DekuEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = " Áreas de Habilidad con Uravity",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    MasteryMeterRow(
                        label = "Control de Gravedad Cero",
                        percentage = profile.gravityControlMastery,
                        barColor = UravityPink
                    )

                    MasteryMeterRow(
                        label = "Reflejos de Combate Full Cowling",
                        percentage = profile.combatReflexesMastery,
                        barColor = DekuEmerald
                    )

                    MasteryMeterRow(
                        label = "Potencia y Comando Vocal",
                        percentage = profile.vocalPowerMastery,
                        barColor = HeroGold
                    )

                    MasteryMeterRow(
                        label = "Resistencia y Estamina Conjunta",
                        percentage = profile.enduranceStamina,
                        barColor = HeroOrange
                    )
                }
            }

            // Cumulative Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen de Esfuerzo Compartido",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBlock(title = "Puntos de Héroe", value = "${totalHeroPoints ?: 0} HP")
                        MetricBlock(title = "Sesiones Hechas", value = "$sessionCount")
                        MetricBlock(title = "Horas Totales", value = "12.5 h")
                    }
                }
            }

            // Heroic Achievements with Ochako
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = HeroGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = " Logros de Cooperación",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "🪐",
                        title = "Dúo Flotante",
                        desc = "Completar 5 entrenamientos de gravedad cero sin interrupciones."
                    )
                    AchievementItem(
                        icon = "📢",
                        title = "Grito Plus Ultra",
                        desc = "Alcanzar 95% de potencia vocal en el detector de micrófono."
                    )
                    AchievementItem(
                        icon = "⚡",
                        title = "Reflejos Relámpago",
                        desc = "Registrar un tiempo de reacción inferior a 300 milisegundos."
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MasteryMeterRow(
    label: String,
    percentage: Int,
    barColor: Color
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.LightGray, fontSize = 12.sp)
            Text(text = "$percentage%", color = barColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = Color.DarkGray
        )
    }
}

@Composable
private fun MetricBlock(
    title: String,
    value: String
) {
    Column {
        Text(text = title, color = Color.Gray, fontSize = 11.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
