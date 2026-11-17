package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.HeroTrainingRepository
import com.example.data.remote.TrainingSessionDoc
import com.example.data.remote.UiState
import com.example.model.CallState
import com.example.model.DialogueBank
import com.example.model.FloatingDebris
import com.example.model.HeroProfile
import com.example.model.MinigameType
import com.example.model.OchakoDialogueLine
import com.example.model.OchakoExpression
import com.example.util.VoiceAndTtsHelper
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class HeroTrainingViewModel(
    private val repository: HeroTrainingRepository,
    private val currentUserId: String,
    val voiceHelper: VoiceAndTtsHelper,
    initialDisplayName: String? = null
) : ViewModel() {

    private val _firestoreErrorMessage = MutableStateFlow<String?>(null)
    val firestoreErrorMessage: StateFlow<String?> = _firestoreErrorMessage.asStateFlow()

    // Two-tier Flow error unwrapping for realtime sessions list
    val sessionsState: StateFlow<UiState<List<TrainingSessionDoc>>> =
        repository.observeTrainingSessions(currentUserId)
            .map<List<TrainingSessionDoc>, UiState<List<TrainingSessionDoc>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing training sessions", rootError)
                emit(UiState.Error(rootError.message ?: "No se pudieron cargar las sesiones"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val sessions: StateFlow<List<TrainingSessionDoc>> = sessionsState
        .map { state -> (state as? UiState.Success)?.data ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val totalPoints: StateFlow<Int> = sessions
        .map { list -> list.sumOf { it.heroPoints.toInt() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), 0)

    val sessionCount: StateFlow<Int> = sessions
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), 0)

    private val _heroProfile = MutableStateFlow(HeroProfile())
    val heroProfile: StateFlow<HeroProfile> = _heroProfile.asStateFlow()

    // Call state
    private val _callState = MutableStateFlow(CallState.DISCONNECTED)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _callDurationSeconds = MutableStateFlow(0)
    val callDurationSeconds: StateFlow<Int> = _callDurationSeconds.asStateFlow()

    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _currentDialogue = MutableStateFlow(DialogueBank.incomingCallGreetings.first())
    val currentDialogue: StateFlow<OchakoDialogueLine> = _currentDialogue.asStateFlow()

    private val _currentExpression = MutableStateFlow(OchakoExpression.NORMAL)
    val currentExpression: StateFlow<OchakoExpression> = _currentExpression.asStateFlow()

    // Floating Zero Gravity Debris Minigame State
    private val _floatingDebrisList = MutableStateFlow<List<FloatingDebris>>(emptyList())
    val floatingDebrisList: StateFlow<List<FloatingDebris>> = _floatingDebrisList.asStateFlow()

    private val _minigameScore = MutableStateFlow(0)
    val minigameScore: StateFlow<Int> = _minigameScore.asStateFlow()

    private val _minigameCombo = MutableStateFlow(0)
    val minigameCombo: StateFlow<Int> = _minigameCombo.asStateFlow()

    private val _activeMinigame = MutableStateFlow<MinigameType?>(null)
    val activeMinigame: StateFlow<MinigameType?> = _activeMinigame.asStateFlow()

    // Reflex minigame
    private val _reflexTargetActive = MutableStateFlow(false)
    val reflexTargetActive: StateFlow<Boolean> = _reflexTargetActive.asStateFlow()

    private val _reflexTargetX = MutableStateFlow(0.5f)
    val reflexTargetX: StateFlow<Float> = _reflexTargetX.asStateFlow()

    private val _reflexTargetY = MutableStateFlow(0.5f)
    val reflexTargetY: StateFlow<Float> = _reflexTargetY.asStateFlow()

    private val _lastReflexTimeMs = MutableStateFlow(0L)
    val lastReflexTimeMs: StateFlow<Long> = _lastReflexTimeMs.asStateFlow()

    private val _bestReflexTimeMs = MutableStateFlow(320L)
    val bestReflexTimeMs: StateFlow<Long> = _bestReflexTimeMs.asStateFlow()

    // Voice Training
    private val _voicePowerRecorded = MutableStateFlow(0f) // 0 to 100
    val voicePowerRecorded: StateFlow<Float> = _voicePowerRecorded.asStateFlow()

    private val _lastVoiceCommand = MutableStateFlow("")
    val lastVoiceCommand: StateFlow<String> = _lastVoiceCommand.asStateFlow()

    private var callTimerJob: Job? = null
    private var debrisSpawnJob: Job? = null
    private var profileObserverJob: Job? = null
    private var reflexSpawnTime = 0L

    init {
        setupVoiceRecognition()
        initializeFirestoreUser(initialDisplayName)
    }

    private fun initializeFirestoreUser(displayName: String?) {
        viewModelScope.launch {
            val seedResult = repository.ensureInitialHeroData(currentUserId, displayName)
            if (seedResult.isFailure) {
                _firestoreErrorMessage.value =
                    seedResult.exceptionOrNull()?.localizedMessage
                        ?: "No se pudo inicializar el perfil en la nube."
            }
        }
        profileObserverJob = viewModelScope.launch {
            repository.observeHeroProfile(currentUserId)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing hero profile", rootError)
                    _firestoreErrorMessage.value =
                        rootError.localizedMessage ?: "Error al sincronizar el perfil"
                }
                .collect { profile ->
                    _heroProfile.value = profile
                }
        }
    }

    fun dismissError() {
        _firestoreErrorMessage.value = null
    }

    fun prepareSignOut() {
        callTimerJob?.cancel()
        debrisSpawnJob?.cancel()
        profileObserverJob?.cancel()
        voiceHelper.stopSpeaking()
        voiceHelper.stopListening()
    }

    private fun setupVoiceRecognition() {
        voiceHelper.onCommandRecognized = { text ->
            processVoiceCommand(text)
        }
    }

    fun startIncomingCall() {
        _callState.value = CallState.INCOMING
        val greeting = DialogueBank.incomingCallGreetings.random()
        _currentDialogue.value = greeting
        _currentExpression.value = greeting.expression
        voiceHelper.triggerHaptic(200)
    }

    fun answerCall() {
        _callState.value = CallState.CONNECTED
        _callDurationSeconds.value = 0
        val line = _currentDialogue.value
        speakDialogue(line)
        startCallTimer()
    }

    fun declineOrEndCall() {
        val wasConnected = _callState.value == CallState.CONNECTED
        val duration = _callDurationSeconds.value
        _callState.value = CallState.DISCONNECTED
        callTimerJob?.cancel()
        voiceHelper.stopSpeaking()
        voiceHelper.stopListening()
        _activeMinigame.value = null

        if (wasConnected && duration >= 10) {
            logQuickTrainingSession(
                title = "Videollamada de Práctica con Uravity",
                type = "Combate Conjunto",
                durationMin = (duration / 60).coerceAtLeast(1),
                points = (duration * 5).coerceAtLeast(50),
                notes = "Videollamada táctica de entrenamiento. Izuku y Ochako repasaron estrategias heroicas."
            )
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_callState.value == CallState.CONNECTED) {
                delay(1000)
                _callDurationSeconds.value += 1
            }
        }
    }

    fun toggleMicMute() {
        _isMicMuted.value = !_isMicMuted.value
        if (_isMicMuted.value) {
            voiceHelper.stopListening()
        }
    }

    fun toggleCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun sendReply(replyText: String) {
        viewModelScope.launch {
            val response = when {
                replyText.contains("PLUS ULTRA", ignoreCase = true) -> {
                    OchakoDialogueLine(
                        id = "pu_reply",
                        text = "¡¡SIII!! ¡¡PLUS ULTRA, DEKU-KUN!! ¡Siento la energía fluyendo! ¡Vamos a romper nuestros límites!",
                        expression = OchakoExpression.EXCITED,
                        audioMood = "cheer"
                    )
                }
                replyText.contains("escombros", ignoreCase = true) || replyText.contains("minijuego", ignoreCase = true) -> {
                    startMinigame(MinigameType.ZERO_GRAVITY_CATCH)
                    OchakoDialogueLine(
                        id = "minigame_reply",
                        text = "¡Buena idea! ¡Activaré Cero Gravedad sobre estos escombros de entrenamiento! ¡Tócalos antes de que caigan!",
                        expression = OchakoExpression.TRAINING,
                        audioMood = "combat"
                    )
                }
                replyText.contains("náuseas", ignoreCase = true) -> {
                    OchakoDialogueLine(
                        id = "nausea_reply",
                        text = "¡Jeje, gracias por preocuparte, Deku-kun! ¡He estado entrenando con Gunhead y mi resistencia mejoró un montón!",
                        expression = OchakoExpression.BLUSHING,
                        audioMood = "warm"
                    )
                }
                else -> {
                    val cheers = DialogueBank.combatCheeringLines.random()
                    cheers.copy(text = "¡Así se habla, Izuku! " + cheers.text)
                }
            }
            _currentDialogue.value = response
            _currentExpression.value = response.expression
            speakDialogue(response)
        }
    }

    fun processVoiceCommand(command: String) {
        _lastVoiceCommand.value = command
        val upper = command.uppercase(Locale.getDefault())

        when {
            upper.contains("PLUS ULTRA") || upper.contains("SMASH") -> {
                sendReply("¡PLUS ULTRA!")
                addHeroPoints(100, persistNow = true)
                _voicePowerRecorded.value = 95f
            }
            upper.contains("GRAVEDAD") || upper.contains("RELEASE") || upper.contains("LIBERAR") -> {
                sendReply("¡Hagamos control de escombros!")
                startMinigame(MinigameType.ZERO_GRAVITY_CATCH)
            }
            upper.contains("REFLEJO") || upper.contains("RAPIDO") || upper.contains("ATAQUE") -> {
                startMinigame(MinigameType.HERO_REFLEXES)
                sendReply("¡Modo reflejos activado!")
            }
            upper.contains("REGISTRAR") || upper.contains("GUARDAR") || upper.contains("SESION") || upper.contains("SESIÓN") -> {
                logQuickTrainingSession(
                    title = "Sesión Registrada por Voz",
                    type = "Comando de Voz",
                    durationMin = 15,
                    points = 150,
                    notes = "Sesión registrada mediante comando vocal: '$command'"
                )
                val reply = OchakoDialogueLine(
                    id = "voice_reg",
                    text = "¡Listo, Deku-kun! ¡Anoté la sesión en nuestro cuaderno de progreso heroico!",
                    expression = OchakoExpression.EXCITED,
                    audioMood = "warm"
                )
                _currentDialogue.value = reply
                _currentExpression.value = reply.expression
                speakDialogue(reply)
            }
            upper.contains("OPCION") || upper.contains("OPCIÓN") || upper.contains("TODAS") || upper.contains("MENU") || upper.contains("MENÚ") -> {
                speakAllOptionsNarration()
            }
            else -> {
                val reply = OchakoDialogueLine(
                    id = "generic_voice",
                    text = "¡Te escuché fuerte y claro, Deku-kun! Dijiste: '$command'. ¡Vamos a seguir entrenando!",
                    expression = OchakoExpression.TRAINING,
                    audioMood = "warm"
                )
                _currentDialogue.value = reply
                _currentExpression.value = reply.expression
                speakDialogue(reply)
            }
        }
    }

    fun speakAllOptionsNarration() {
        val optionsLine = OchakoDialogueLine(
            id = "all_options_narration",
            title = "Opciones Completas",
            text = "¡Claro que sí, Deku-kun! Te digo todas las opciones: puedes elegir entrenar control de gravedad cero, practicar tus reflejos de héroe, gritar Plus Ultra al micrófono, escuchar consejos de la U.A. o registrar nuestra sesión diaria. ¡Dime cuál prefieres o tócala en la pantalla!",
            expression = OchakoExpression.EXCITED,
            audioMood = "warm",
            suggestedReplies = listOf(
                "¡Entrenar Gravedad Cero!",
                "¡Reflejos Plus Ultra!",
                "¡Grito de Combate!",
                "¡Registrar Sesión!"
            )
        )
        _currentDialogue.value = optionsLine
        _currentExpression.value = OchakoExpression.EXCITED
        speakDialogue(optionsLine)
    }

    fun playSpecificLine(line: OchakoDialogueLine) {
        _currentDialogue.value = line
        _currentExpression.value = line.expression
        speakDialogue(line)
    }

    private fun speakDialogue(dialogue: OchakoDialogueLine) {
        voiceHelper.speakOchako(dialogue.text)
    }

    // Minigame controls
    fun startMinigame(type: MinigameType) {
        _activeMinigame.value = type
        _minigameScore.value = 0
        _minigameCombo.value = 0

        when (type) {
            MinigameType.ZERO_GRAVITY_CATCH -> startZeroGravityDebris()
            MinigameType.HERO_REFLEXES -> startReflexChallenge()
            MinigameType.VOCAL_POWER_DRILL -> voiceHelper.startListening()
        }
    }

    fun stopMinigame() {
        val type = _activeMinigame.value
        val score = _minigameScore.value
        debrisSpawnJob?.cancel()
        _activeMinigame.value = null
        _floatingDebrisList.value = emptyList()
        _reflexTargetActive.value = false

        if (score > 0 && type != null) {
            val typeName = when (type) {
                MinigameType.ZERO_GRAVITY_CATCH -> "Control de Gravedad"
                MinigameType.HERO_REFLEXES -> "Reflejos de Héroe"
                MinigameType.VOCAL_POWER_DRILL -> "Potencia Vocal"
            }
            logQuickTrainingSession(
                title = "Entrenamiento: $typeName",
                type = typeName,
                durationMin = 10,
                points = score,
                notes = "Minijuego completado con puntuación final de $score puntos."
            )
        } else {
            persistHeroProfile()
        }
    }

    private fun startZeroGravityDebris() {
        debrisSpawnJob?.cancel()
        debrisSpawnJob = viewModelScope.launch {
            var counter = 0
            while (_activeMinigame.value == MinigameType.ZERO_GRAVITY_CATCH) {
                if (_floatingDebrisList.value.size < 6) {
                    val newDebris = FloatingDebris(
                        id = counter++,
                        xFraction = Random.nextFloat().coerceIn(0.1f, 0.85f),
                        yFraction = Random.nextFloat().coerceIn(0.15f, 0.75f),
                        sizeDp = Random.nextInt(48, 76),
                        points = Random.nextInt(20, 60),
                        emoji = listOf("🪨", "✨", "🪐", "💎", "☄️").random()
                    )
                    _floatingDebrisList.value = _floatingDebrisList.value + newDebris
                }
                delay(1200)
            }
        }
    }

    fun onDebrisTapped(debrisId: Int) {
        voiceHelper.triggerHaptic(40)
        val debris = _floatingDebrisList.value.find { it.id == debrisId } ?: return
        _floatingDebrisList.value = _floatingDebrisList.value.filter { it.id != debrisId }
        val points = debris.points + (_minigameCombo.value * 10)
        _minigameScore.value += points
        _minigameCombo.value += 1
        addHeroPoints(points, persistNow = false)

        if (_minigameCombo.value % 5 == 0) {
            val cheer = DialogueBank.combatCheeringLines.random()
            _currentDialogue.value = cheer
            _currentExpression.value = OchakoExpression.EXCITED
            speakDialogue(cheer)
        }
    }

    private fun startReflexChallenge() {
        viewModelScope.launch {
            _reflexTargetActive.value = false
            delay(Random.nextLong(1000, 2500))
            if (_activeMinigame.value == MinigameType.HERO_REFLEXES) {
                _reflexTargetX.value = Random.nextFloat().coerceIn(0.15f, 0.8f)
                _reflexTargetY.value = Random.nextFloat().coerceIn(0.2f, 0.7f)
                _reflexTargetActive.value = true
                reflexSpawnTime = System.currentTimeMillis()
                voiceHelper.triggerHaptic(80)
            }
        }
    }

    fun onReflexTargetTapped() {
        if (!_reflexTargetActive.value) return
        val now = System.currentTimeMillis()
        val reactionMs = now - reflexSpawnTime
        _lastReflexTimeMs.value = reactionMs
        if (reactionMs < _bestReflexTimeMs.value || _bestReflexTimeMs.value == 0L) {
            _bestReflexTimeMs.value = reactionMs
        }
        _reflexTargetActive.value = false
        voiceHelper.triggerHaptic(60)

        val points = (1000 - reactionMs).coerceAtLeast(100).toInt() / 2
        _minigameScore.value += points
        _minigameCombo.value += 1
        addHeroPoints(points, persistNow = false)

        startReflexChallenge()
    }

    fun simulateVoiceShout(power: Float, phrase: String) {
        _voicePowerRecorded.value = power
        _lastVoiceCommand.value = phrase
        val points = (power * 3).toInt()
        _minigameScore.value += points
        addHeroPoints(points, persistNow = true)
        voiceHelper.triggerHaptic(100)

        val reply = OchakoDialogueLine(
            id = "shout_result",
            text = "¡Guao, Deku-kun! ¡Tu grito de '$phrase' alcanzó una potencia del ${power.toInt()}%! ¡Ese es el espíritu heroico!",
            expression = OchakoExpression.EXCITED,
            audioMood = "cheer"
        )
        _currentDialogue.value = reply
        _currentExpression.value = OchakoExpression.EXCITED
        speakDialogue(reply)
    }

    private fun addHeroPoints(amount: Int, persistNow: Boolean = true) {
        val current = _heroProfile.value
        var level = current.heroLevel
        var xp = current.currentXp + amount
        var nextThreshold = current.xpForNextLevel

        while (xp >= nextThreshold) {
            xp -= nextThreshold
            level += 1
            nextThreshold += 500
        }

        val updated = current.copy(
            heroLevel = level.coerceIn(1, 999),
            currentXp = xp.coerceIn(0, 1000000),
            xpForNextLevel = nextThreshold.coerceIn(100, 10000000)
        )
        _heroProfile.value = updated

        if (persistNow) {
            persistHeroProfile(updated)
        }
    }

    private fun persistHeroProfile(profile: HeroProfile = _heroProfile.value) {
        viewModelScope.launch {
            val result = repository.updateHeroProgress(currentUserId, profile)
            if (result.isFailure) {
                _firestoreErrorMessage.value =
                    result.exceptionOrNull()?.localizedMessage
                        ?: "No se pudo guardar el progreso de héroe."
            }
        }
    }

    fun logQuickTrainingSession(
        title: String,
        type: String,
        durationMin: Int,
        points: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date())
            val rank = when {
                points >= 300 -> "Rango S"
                points >= 150 -> "Rango A"
                else -> "Rango B"
            }
            val quote = DialogueBank.tipsAndMotivation.random().text

            val result = repository.addTrainingSession(
                userId = currentUserId,
                title = title,
                trainingType = type,
                dateString = dateStr,
                durationMinutes = durationMin,
                heroPoints = points,
                rankGrade = rank,
                notes = notes,
                partnerQuote = quote
            )
            if (result.isSuccess) {
                addHeroPoints(points, persistNow = true)
            } else {
                _firestoreErrorMessage.value =
                    result.exceptionOrNull()?.localizedMessage
                        ?: "No se pudo guardar la sesión de entrenamiento."
            }
        }
    }

    fun deleteSession(session: TrainingSessionDoc) {
        viewModelScope.launch {
            val result = repository.deleteTrainingSession(currentUserId, session.id)
            if (result.isFailure) {
                _firestoreErrorMessage.value =
                    result.exceptionOrNull()?.localizedMessage
                        ?: "No se pudo eliminar el registro."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        prepareSignOut()
        voiceHelper.destroy()
    }

    private companion object {
        const val TAG = "HeroTrainingVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
