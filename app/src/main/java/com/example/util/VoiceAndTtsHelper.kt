package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAndTtsHelper(private val context: Context) : RecognitionListener {

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f) // 0f to 1f
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _isSpeechActive = MutableStateFlow(false)
    val isSpeechActive: StateFlow<Boolean> = _isSpeechActive.asStateFlow()

    private val _isOchakoSpeaking = MutableStateFlow(false)
    val isOchakoSpeaking: StateFlow<Boolean> = _isOchakoSpeaking.asStateFlow()

    private val _isOriginalAnimePitch = MutableStateFlow(true)
    val isOriginalAnimePitch: StateFlow<Boolean> = _isOriginalAnimePitch.asStateFlow()

    private val _speechStatusMessage = MutableStateFlow<String?>(null)
    val speechStatusMessage: StateFlow<String?> = _speechStatusMessage.asStateFlow()

    var onCommandRecognized: ((String) -> Unit)? = null

    init {
        initTts()
        initSpeechRecognizer()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale.forLanguageTag("es-ES")
                val result = textToSpeech?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                applyVoiceProfile()
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isOchakoSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isOchakoSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isOchakoSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isOchakoSpeaking.value = false
                    }
                })
                isTtsReady = true
            }
        }
    }

    private fun applyVoiceProfile() {
        if (_isOriginalAnimePitch.value) {
            textToSpeech?.setSpeechRate(1.08f)
            textToSpeech?.setPitch(1.24f) // Bright anime pitch inspired by Ochako Uraraka
        } else {
            textToSpeech?.setSpeechRate(1.0f)
            textToSpeech?.setPitch(1.0f)
        }
    }

    fun toggleAnimeVoicePitch() {
        _isOriginalAnimePitch.value = !_isOriginalAnimePitch.value
        applyVoiceProfile()
    }

    private fun initSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceAndTtsHelper)
                }
                _speechStatusMessage.value = null
            } else {
                _speechStatusMessage.value = "Servicio de reconocimiento de voz no disponible en este dispositivo."
            }
        } catch (e: Exception) {
            Log.e("VoiceHelper", "Could not initialize SpeechRecognizer", e)
            _speechStatusMessage.value = "No se pudo inicializar el reconocedor de voz."
        }
    }

    fun speakOchako(text: String) {
        if (!isTtsReady || textToSpeech == null) {
            return
        }
        _isOchakoSpeaking.value = true
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ochako_utterance_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isOchakoSpeaking.value = false
    }

    fun startListening() {
        if (isListening) return
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _speechStatusMessage.value = "Activa los Servicios de Voz de Google para usar el micrófono."
            return
        }
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            _speechStatusMessage.value = null
            speechRecognizer?.startListening(intent)
            isListening = true
            _isSpeechActive.value = true
            triggerHaptic(50)
        } catch (e: Exception) {
            Log.e("VoiceHelper", "Error starting listening", e)
            isListening = false
            _isSpeechActive.value = false
            _speechStatusMessage.value = "No se pudo iniciar el micrófono. Verifica el permiso de audio."
        }
    }

    fun stopListening() {
        if (!isListening) return
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        isListening = false
        _isSpeechActive.value = false
        _audioAmplitude.value = 0f
    }

    fun triggerHaptic(durationMs: Long = 60) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    // RecognitionListener callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _isSpeechActive.value = true
        _speechStatusMessage.value = null
    }

    override fun onBeginningOfSpeech() {
        _isSpeechActive.value = true
    }

    override fun onRmsChanged(rmsdB: Float) {
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
        _audioAmplitude.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isSpeechActive.value = false
        isListening = false
    }

    override fun onError(error: Int) {
        _isSpeechActive.value = false
        isListening = false
        _audioAmplitude.value = 0f
        _speechStatusMessage.value = when (error) {
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Falta permiso de micrófono para escuchar comandos."
            SpeechRecognizer.ERROR_NO_MATCH -> "No se entendió el comando. Intenta decir 'PLUS ULTRA' o 'GRAVEDAD'."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No se detectó voz. Toca el micrófono para intentarlo de nuevo."
            else -> null
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val text = matches[0]
            _recognizedText.value = text
            onCommandRecognized?.invoke(text)
        }
        _isSpeechActive.value = false
        isListening = false
        _audioAmplitude.value = 0f
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            // Update preview text without firing final command execution multiple times
            _recognizedText.value = matches[0]
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
    }
}
