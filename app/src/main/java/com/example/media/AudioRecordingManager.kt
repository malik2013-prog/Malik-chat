package com.example.media

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random

data class RecordingResult(
  val fileUri: String?,
  val durationSeconds: Int,
  val waveform: List<Float>,
  val transcript: String?
)

class AudioRecordingManager(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Main)
  private var mediaRecorder: MediaRecorder? = null
  private var speechRecognizer: SpeechRecognizer? = null
  private var currentOutputFile: File? = null

  private var startTimeMs: Long = 0
  private var amplitudeJob: Job? = null
  private val collectedAmplitudes = mutableListOf<Float>()

  private val _isRecording = MutableStateFlow(false)
  val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

  private val _recordingDurationSeconds = MutableStateFlow(0)
  val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

  private val _liveAmplitudes = MutableStateFlow<List<Float>>(emptyList())
  val liveAmplitudes: StateFlow<List<Float>> = _liveAmplitudes.asStateFlow()

  private val _partialTranscript = MutableStateFlow("")
  val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

  // Media Player for voice playback
  private var activeMediaPlayer: MediaPlayer? = null
  private val _currentPlayingUri = MutableStateFlow<String?>(null)
  val currentPlayingUri: StateFlow<String?> = _currentPlayingUri.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _playbackProgress = MutableStateFlow(0f)
  val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()
  private var progressTrackingJob: Job? = null

  fun startRecording() {
    try {
      stopRecordingInternal(discard = true)
      collectedAmplitudes.clear()
      _liveAmplitudes.value = emptyList()
      _partialTranscript.value = ""
      _recordingDurationSeconds.value = 0

      val voiceDir = File(context.cacheDir, "voice_notes").apply { mkdirs() }
      val outputFile = File(voiceDir, "voice_${System.currentTimeMillis()}.m4a")
      currentOutputFile = outputFile

      val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }

      recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)
        setOutputFile(outputFile.absolutePath)
        prepare()
        start()
      }
      mediaRecorder = recorder
      startTimeMs = System.currentTimeMillis()
      _isRecording.value = true

      // Start live waveform sampling
      amplitudeJob = scope.launch(Dispatchers.Default) {
        while (isActive && _isRecording.value) {
          delay(100)
          val maxAmp = try {
            mediaRecorder?.maxAmplitude ?: 0
          } catch (_: Exception) {
            0
          }
          // Normalize to 0.1 .. 1.0 range
          val normalized = if (maxAmp > 0) {
            (maxAmp / 32767f).coerceIn(0.12f, 1.0f)
          } else {
            // Ambient breathing noise baseline
            Random.nextFloat().coerceIn(0.15f, 0.45f)
          }
          collectedAmplitudes.add(normalized)
          // Keep the last 24 amplitudes for the live waveform HUD
          val recent = collectedAmplitudes.takeLast(24)
          _liveAmplitudes.value = recent

          val elapsed = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt()
          _recordingDurationSeconds.value = elapsed
        }
      }

      // Initialize SpeechRecognizer for live speech-to-text transcription
      startSpeechRecognition()
    } catch (e: Exception) {
      Log.e("AudioRecordingManager", "Failed to start audio recording", e)
      stopRecordingInternal(discard = true)
    }
  }

  private fun startSpeechRecognition() {
    try {
      if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Log.w("AudioRecordingManager", "Speech recognition unavailable on device")
        return
      }

      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {}
          override fun onBeginningOfSpeech() {}
          override fun onRmsChanged(rmsdB: Float) {}
          override fun onBufferReceived(buffer: ByteArray?) {}
          override fun onEndOfSpeech() {}
          override fun onError(error: Int) {
            Log.w("AudioRecordingManager", "SpeechRecognizer error: $error")
          }
          override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
              _partialTranscript.value = matches[0]
            }
          }
          override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
              _partialTranscript.value = matches[0]
            }
          }
          override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().language)
          putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
          putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        startListening(intent)
      }
    } catch (e: Exception) {
      Log.w("AudioRecordingManager", "Could not start speech recognizer", e)
    }
  }

  fun stopRecording(): RecordingResult {
    val duration = max(1, _recordingDurationSeconds.value)
    val transcript = _partialTranscript.value.ifBlank { null }
    val file = currentOutputFile

    stopRecordingInternal(discard = false)

    val waveform = if (collectedAmplitudes.isNotEmpty()) {
      // Downsample collected amplitudes to 20-30 points
      val step = max(1, collectedAmplitudes.size / 20)
      collectedAmplitudes.filterIndexed { index, _ -> index % step == 0 }.take(20)
    } else {
      List(16) { Random.nextFloat().coerceIn(0.2f, 0.9f) }
    }

    return RecordingResult(
      fileUri = file?.takeIf { it.exists() && it.length() > 0 }?.absolutePath,
      durationSeconds = duration,
      waveform = waveform,
      transcript = transcript
    )
  }

  fun cancelRecording() {
    stopRecordingInternal(discard = true)
  }

  private fun stopRecordingInternal(discard: Boolean) {
    _isRecording.value = false
    amplitudeJob?.cancel()
    amplitudeJob = null

    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.destroy()
    } catch (_: Exception) {}
    speechRecognizer = null

    try {
      mediaRecorder?.apply {
        try {
          stop()
        } catch (_: Exception) {}
        release()
      }
    } catch (_: Exception) {}
    mediaRecorder = null

    if (discard) {
      try {
        currentOutputFile?.delete()
      } catch (_: Exception) {}
      currentOutputFile = null
      _partialTranscript.value = ""
      _recordingDurationSeconds.value = 0
    }
  }

  // --- AUDIO PLAYBACK ---
  fun playAudio(uriString: String, speed: Float = 1.0f, onFinished: () -> Unit = {}) {
    stopPlayback()

    try {
      val file = File(uriString)
      val player = MediaPlayer().apply {
        if (file.exists()) {
          setDataSource(file.absolutePath)
        } else {
          // If remote or not local file
          setDataSource(uriString)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          playbackParams = playbackParams.setSpeed(speed)
        }
        prepare()
        start()
      }

      activeMediaPlayer = player
      _currentPlayingUri.value = uriString
      _isPlaying.value = true

      player.setOnCompletionListener {
        _isPlaying.value = false
        _playbackProgress.value = 0f
        _currentPlayingUri.value = null
        onFinished()
      }

      progressTrackingJob?.cancel()
      progressTrackingJob = scope.launch(Dispatchers.Default) {
        val totalMs = player.duration.toFloat().coerceAtLeast(100f)
        while (isActive && _isPlaying.value && activeMediaPlayer == player) {
          delay(50)
          val curMs = try { player.currentPosition.toFloat() } catch (_: Exception) { 0f }
          _playbackProgress.value = (curMs / totalMs).coerceIn(0f, 1f)
        }
      }
    } catch (e: Exception) {
      Log.e("AudioRecordingManager", "Error playing audio from $uriString", e)
      stopPlayback()
    }
  }

  fun pausePlayback() {
    activeMediaPlayer?.let {
      if (it.isPlaying) {
        it.pause()
        _isPlaying.value = false
      }
    }
  }

  fun resumePlayback() {
    activeMediaPlayer?.let {
      it.start()
      _isPlaying.value = true
    }
  }

  fun stopPlayback() {
    progressTrackingJob?.cancel()
    progressTrackingJob = null
    try {
      activeMediaPlayer?.apply {
        if (isPlaying) stop()
        release()
      }
    } catch (_: Exception) {}
    activeMediaPlayer = null
    _isPlaying.value = false
    _playbackProgress.value = 0f
    _currentPlayingUri.value = null
  }
}
