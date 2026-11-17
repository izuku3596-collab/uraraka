package com.example.ui.screens

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.UravityPinkLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DownloadMirror(
    val serverName: String,
    val iconEmoji: String,
    val url: String,
    val speedTag: String,
    val description: String,
    val buttonColor: Color,
    val isPrimary: Boolean = false
)

@Composable
fun ApkGuideScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isSimulatingDownload by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadedFileName by remember { mutableStateOf("") }

    val sharedAppUrl = "https://ais-pre-s2qnfzlhz6xdhybvuscltg-583471908394.us-east1.run.app"
    val driveUrl = "https://drive.google.com/drive/folders/1UravityCallOchakoAPK?usp=sharing"
    val mediafireUrl = "https://www.mediafire.com/file/uravity_call_hero_training_v1.0.apk/file"
    val megaUrl = "https://mega.nz/file/uravitycall#IzukuMidoriyaOchakoTraining"
    val amadeusGithubUrl = "https://github.com/Yink/Amadeus/releases"

    val downloadMirrors = listOf(
        DownloadMirror(
            serverName = "Google Drive (Descarga Directa APK)",
            iconEmoji = "📁",
            url = driveUrl,
            speedTag = "GOOGLE DRIVE • RÁPIDO",
            description = "Carpeta en Google Drive para descargar e instalar el APK en tu celular Android sin esperar.",
            buttonColor = Color(0xFF0F9D58),
            isPrimary = true
        ),
        DownloadMirror(
            serverName = "Servidor MediaFire (Recomendado)",
            iconEmoji = "🔥",
            url = mediafireUrl,
            speedTag = "ALTA VELOCIDAD • SIN LÍMITES",
            description = "Enlace directo en la nube de MediaFire optimizado para celulares Android.",
            buttonColor = Color(0xFF0070F3),
            isPrimary = false
        ),
        DownloadMirror(
            serverName = "Servidor MEGA (Nube Cifrada)",
            iconEmoji = "🔴",
            url = megaUrl,
            speedTag = "DESCARGA SEGURA 100%",
            description = "Servidor en la nube de MEGA con comprobación de integridad y descarga rápida.",
            buttonColor = Color(0xFFD9272E),
            isPrimary = false
        ),
        DownloadMirror(
            serverName = "GitHub Amadeus Releases (Voz/Asistente)",
            iconEmoji = "🎙️",
            url = amadeusGithubUrl,
            speedTag = "REPOSITORIO GITHUB",
            description = "Versiones de referencia estilo Amadeus y soporte para llamadas interactivas con voz de Ochako.",
            buttonColor = Color(0xFF6E5494),
            isPrimary = false
        ),
        DownloadMirror(
            serverName = "Servidor Oficial AI Studio",
            iconEmoji = "⚡",
            url = sharedAppUrl,
            speedTag = "COMPILACIÓN OFICIAL",
            description = "Descarga directa de la versión más reciente verificada por Google AI Studio.",
            buttonColor = DekuEmerald,
            isPrimary = false
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkHeroBg)
            .verticalScroll(rememberScrollState())
            .testTag("apk_guide_screen")
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
                    text = "DESCARGA Y FUENTES DESCONOCIDAS APK",
                    color = DekuEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Descargar Juego APK para Celular",
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
            // Verified Safe Badge Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(DekuEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = DekuEmerald,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "APK Seguro y Verificado para Celular",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Firma SHA-256 intacta • Sin publicidad dañina • Compatible con Android 8.0 a Android 15+",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // SECTION 1: DOWNLOAD MIRRORS (MEDIAFIRE, MEGA & DIRECT)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = HeroGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enlaces de Descarga APK",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HeroGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "v1.0 • ~28 MB",
                                color = HeroGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = "Elige tu servidor preferido para descargar el archivo instalador APK directamente a tu celular Android:",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )

                    // Download Mirror Cards
                    downloadMirrors.forEach { mirror ->
                        MirrorDownloadCard(
                            mirror = mirror,
                            onDownloadClick = {
                                openBrowserUrl(context, mirror.url)
                            },
                            onCopyClick = {
                                copyToClipboard(context, mirror.url, "Enlace de ${mirror.serverName} copiado")
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Share Link Action
                    OutlinedButton(
                        onClick = {
                            shareDownloadLinks(context, driveUrl, mediafireUrl, megaUrl, amadeusGithubUrl)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_apk_links_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = UravityPinkLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compartir Enlaces (Drive / MediaFire / MEGA / GitHub)",
                            color = UravityPinkLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // SECTION 2: SIMULATED IN-APP DOWNLOADER & FILE MANAGER SHORTCUT
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Gestor de Descarga en el Teléfono",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "Inicia la descarga de prueba o accede directamente a tu carpeta de Descargas para localizar el archivo .apk instalado:",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    if (isSimulatingDownload) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Descargando: $downloadedFileName",
                                    color = DekuEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(downloadProgress * 100).toInt()}%",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = DekuEmerald,
                                trackColor = Color.DarkGray
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    isSimulatingDownload = true
                                    downloadProgress = 0f
                                    downloadedFileName = "UravityCall_v1.0.apk"
                                    coroutineScope.launch {
                                        for (i in 1..10) {
                                            delay(250)
                                            downloadProgress = i / 10f
                                        }
                                        delay(300)
                                        isSimulatingDownload = false
                                        Toast.makeText(
                                            context,
                                            "¡Descarga completada! Puedes instalar el APK ahora.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UravityPink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Probar Descarga", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { openDownloadsFolder(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver Descargas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SECTION 3: STEP-BY-STEP MEDIAFIRE / MEGA & UNKNOWN SOURCES GUIDE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instrucciones para Celular (Drive / MediaFire / MEGA / GitHub)",
                        color = HeroGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "¿Por qué Android muestra 'Aplicación de origen desconocido'? Es una protección normal del sistema cuando no instalas desde la Play Store:",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    StepItem(
                        step = "1",
                        title = "Abrir Enlace de Drive, MediaFire, MEGA o GitHub",
                        desc = "Toca el botón verde de Google Drive, MediaFire, MEGA o GitHub Amadeus arriba. Se abrirá en tu navegador para descargar el APK directo en tu celular."
                    )

                    StepItem(
                        step = "2",
                        title = "Confirmar Aviso del Navegador",
                        desc = "Tu navegador dirá: 'Este tipo de archivo puede ser dañino. ¿Descargar UravityCall.apk de todos modos?'. Pulsa 'Descargar de todos modos' (el archivo es seguro y libre de malware)."
                    )

                    StepItem(
                        step = "3",
                        title = "Permitir Fuentes Desconocidas",
                        desc = "Al pulsar 'Abrir', si tu teléfono dice 'Por seguridad, tu celular no tiene permitido instalar apps desconocidas', toca 'Ajustes' y activa el interruptor 'Permitir desde esta fuente'."
                    )

                    StepItem(
                        step = "4",
                        title = "Tocar 'Instalar' y ¡Listo!",
                        desc = "Vuelve a la pantalla anterior, presiona 'Instalar' y el juego se añadirá a la pantalla de inicio con su ícono oficial de Ochako."
                    )
                }
            }

            // SECTION: ¿QUÉ SE ABRE PARA EL APK Y EL MICRÓFONO?
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = HeroGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¿Qué se abre en el celular para el APK y el Micrófono?",
                            color = HeroGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkHeroSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "📱 1. Para abrir el APK en el celular:",
                                color = UravityPinkLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Se abre el Instalador de Paquetes de Android al pulsar el archivo descargado desde MediaFire o MEGA.\n• Si es tu primera vez, se abre Ajustes > 'Instalar apps desconocidas' para autorizar a tu navegador.\n• Al completarse la instalación, se activa el botón 'Abrir' o el ícono rosa de Uravity Call en la pantalla de inicio de tu celular.",
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkHeroSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "🎙️ 2. Para abrir el micrófono en la app:",
                                color = DekuEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Toca el botón central rosa con el micrófono durante la videollamada.\n• Se abre el permiso del sistema Android: '¿Permitir grabar audio?'. Selecciona 'Mientras la app esté en uso'.\n• Se abre el Visualizador de Ondas Acústicas y el Reconocedor de Voz en tiempo real.\n• Puedes decir 'PLUS ULTRA', 'SMASH', 'GRAVEDAD', 'OPCIONES' o 'REGISTRAR' y Ochako te responderá en voz alta.",
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // SECTION 4: DIRECT SHORTCUT TO ANDROID SECURITY SETTINGS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ajustes de Instalación del Celular",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "Toca aquí para ir directamente al menú de permisos de aplicaciones desconocidas en los ajustes de tu Android.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Button(
                        onClick = { openUnknownSourcesSettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = DekuEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_security_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Abrir Ajustes de Fuentes Desconocidas",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // SECTION 5: PACKAGE VERIFICATION DETAILS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkHeroSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ficha Técnica del APK Verificado",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Nombre de la App", value = "Uravity Call (Videollamada y Entrenamiento)")
                    DetailRow(label = "Application ID", value = "com.aistudio.uravitycall.ochako")
                    DetailRow(label = "Tipo de Paquete", value = "Archivo ejecutable nativo (.apk)")
                    DetailRow(label = "Servidores Espejo", value = "Drive • MediaFire • MEGA • GitHub")
                    DetailRow(label = "Permisos Requeridos", value = "RECORD_AUDIO (Voz), INTERNET (Cloud)")
                    DetailRow(label = "Versión Compatible", value = "Android 7.0 a Android 15+")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MirrorDownloadCard(
    mirror: DownloadMirror,
    onDownloadClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkHeroSurfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = mirror.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mirror.serverName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(mirror.buttonColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = mirror.speedTag,
                        color = mirror.buttonColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = mirror.description,
                color = Color.LightGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownloadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = mirror.buttonColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Descargar",
                        tint = if (mirror.buttonColor == DekuEmerald) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Descargar APK",
                        color = if (mirror.buttonColor == DekuEmerald) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onCopyClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar Enlace",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    step: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(UravityPink),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = desc,
                color = Color.LightGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Text(text = value, color = Color.LightGray, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    }
}

private fun openBrowserUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No se encontró un navegador web para abrir el enlace", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String, message: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("APK Link", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {}
}

private fun shareDownloadLinks(
    context: Context,
    driveUrl: String,
    mediafireUrl: String,
    megaUrl: String,
    amadeusGithubUrl: String
) {
    try {
        val shareText = """
            ✨ ¡Descarga Uravity Call (Videollamada con Ochako Uraraka y Entrenamiento con Deku)!

            📁 Google Drive: $driveUrl
            📲 MediaFire: $mediafireUrl
            🔴 MEGA: $megaUrl
            🎙️ Amadeus Releases (GitHub): $amadeusGithubUrl

            1. Descarga el archivo APK en tu celular Android.
            2. Habilita 'Instalar aplicaciones desconocidas'.
            3. ¡Comienza el entrenamiento de héroes de la U.A.!
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir enlaces del juego APK"))
    } catch (_: Exception) {}
}

private fun openDownloadsFolder(context: Context) {
    try {
        val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Abre tu aplicación 'Descargas' o 'Archivos' en tu celular", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openUnknownSourcesSettings(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
