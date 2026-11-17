package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.remote.HeroTrainingRepository
import com.example.model.CallState
import com.example.ui.components.HeroHeader
import com.example.ui.screens.ApkGuideScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.DailyLogScreen
import com.example.ui.screens.MinigamesScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.signOut
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.UravityPink
import com.example.util.VoiceAndTtsHelper
import com.example.viewmodel.HeroTrainingViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class MainNavigationTab {
    CALL,
    MINIGAMES,
    DAILY_LOG,
    STATS,
    APK_GUIDE
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = { /* authStateFlow emits signed-in user automatically */ })
    } else {
        key(user.uid) {
            AuthenticatedHeroApp(
                currentUserId = user.uid,
                displayName = user.displayName
            )
        }
    }
}

@Composable
fun AuthenticatedHeroApp(
    currentUserId: String,
    displayName: String?
) {
    val viewModel: HeroTrainingViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val repository = HeroTrainingRepository(db)
                val voiceHelper = VoiceAndTtsHelper(app)
                HeroTrainingViewModel(
                    repository = repository,
                    currentUserId = currentUserId,
                    voiceHelper = voiceHelper,
                    initialDisplayName = displayName
                )
            }
        }
    )

    MainAppContainer(viewModel = viewModel)
}

@Composable
fun MainAppContainer(viewModel: HeroTrainingViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var selectedTab by remember { mutableStateOf(MainNavigationTab.CALL) }
    val callState by viewModel.callState.collectAsState()
    val heroProfile by viewModel.heroProfile.collectAsState()
    val firestoreError by viewModel.firestoreErrorMessage.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    BackHandler(enabled = selectedTab != MainNavigationTab.CALL || callState != CallState.DISCONNECTED) {
        if (callState != CallState.DISCONNECTED) {
            viewModel.declineOrEndCall()
        } else if (selectedTab != MainNavigationTab.CALL) {
            selectedTab = MainNavigationTab.CALL
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkHeroBg),
        bottomBar = {
            if (callState == CallState.DISCONNECTED) {
                NavigationBar(
                    containerColor = DarkHeroSurface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == MainNavigationTab.CALL,
                        onClick = { selectedTab = MainNavigationTab.CALL },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainNavigationTab.CALL) Icons.Filled.Call else Icons.Outlined.Call,
                                contentDescription = "Llamada"
                            )
                        },
                        label = { Text("Llamada", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainNavigationTab.MINIGAMES,
                        onClick = { selectedTab = MainNavigationTab.MINIGAMES },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainNavigationTab.MINIGAMES) Icons.Filled.Gamepad else Icons.Outlined.Gamepad,
                                contentDescription = "Minijuegos"
                            )
                        },
                        label = { Text("Minijuegos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainNavigationTab.DAILY_LOG,
                        onClick = { selectedTab = MainNavigationTab.DAILY_LOG },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainNavigationTab.DAILY_LOG) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                                contentDescription = "Progreso"
                            )
                        },
                        label = { Text("Progreso", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainNavigationTab.STATS,
                        onClick = { selectedTab = MainNavigationTab.STATS },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainNavigationTab.STATS) Icons.Filled.Insights else Icons.Outlined.Insights,
                                contentDescription = "Estadísticas"
                            )
                        },
                        label = { Text("Estadísticas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainNavigationTab.APK_GUIDE,
                        onClick = { selectedTab = MainNavigationTab.APK_GUIDE },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Guía APK"
                            )
                        },
                        label = { Text("APK Seguro", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors()
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!firestoreError.isNullOrBlank()) {
                Surface(
                    color = Color(0xFF4A151B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = firestoreError ?: "",
                            color = Color(0xFFFFB4AB),
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { viewModel.dismissError() }) {
                            Text("Cerrar", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (callState == CallState.DISCONNECTED && selectedTab != MainNavigationTab.CALL) {
                HeroHeader(
                    profile = heroProfile,
                    onCallClick = {
                        selectedTab = MainNavigationTab.CALL
                        viewModel.startIncomingCall()
                    },
                    onSignOutClick = {
                        signOut(
                            credentialManager = credentialManager,
                            scope = scope,
                            onBeforeSignOut = { viewModel.prepareSignOut() }
                        )
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    MainNavigationTab.CALL -> CallScreen(viewModel = viewModel)
                    MainNavigationTab.MINIGAMES -> MinigamesScreen(viewModel = viewModel)
                    MainNavigationTab.DAILY_LOG -> DailyLogScreen(viewModel = viewModel)
                    MainNavigationTab.STATS -> StatsScreen(viewModel = viewModel)
                    MainNavigationTab.APK_GUIDE -> ApkGuideScreen()
                }
            }
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = UravityPink,
    selectedTextColor = UravityPink,
    indicatorColor = UravityPink.copy(alpha = 0.2f),
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)
