package com.example.guitartuner.data.tuner

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.Tuning
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import com.example.guitartuner.domain.repository.tuner.TunerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import kotlin.math.log2
import kotlin.math.roundToInt


class TunerRepositoryImpl(
    private val settingsManager: SettingsManager,
    private val permissionManager: PermissionManager,
    private val pitchRepository: PitchRepository,
//    private val database: AppDatabase,
) : PitchDetectionHandler, TunerRepository {

    private companion object {
        private const val SAMPLE_RATE = 22050
        private const val SAMPLE_RATE_BITS = 16
        private const val CHANNEL_COUNT = 1
        private const val OVERLAP = 0
        private const val BUFFER_SIZE = 2048
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }

    private val _state by lazy { MutableStateFlow<Tuning?>(null) }
    override val state by lazy { _state.asStateFlow() }


    private var lifecycleOwner: LifecycleOwner? = null
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        source.lifecycleScope.launch {
            when (event) {
                Lifecycle.Event.ON_RESUME -> startListener(settingsManager.settings)
                Lifecycle.Event.ON_PAUSE -> stopListener()
                else -> {}
            }
        }
        lifecycleOwner = source
    }


    override fun selectTone(tone: Tone) {
        selectedTone = tone
    }

    private var selectedTone: Tone? = null

    override var autoMode: Boolean = true
        get() = if (selectedTone == null) true else field
    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null

    private var noiseSuppressor: NoiseSuppressor? = null

    override fun handlePitch(result: PitchDetectionResult?, event: AudioEvent?) {
        val pitch = result?.pitch?.toDouble() ?: -1.0
        if (pitch >= 0) {
            lifecycleOwner?.run {
                lifecycleScope.launch(Dispatchers.IO) {
                    getTuning(pitch)?.let {
                        _state.value = it
                    }
                }
            }
        }
    }

    suspend fun restartListener() {
//        lifecycleOwner.lifecycleScope.launch {
        stopListener()
        startListener(settingsManager.settings)
//        }
    }

    private suspend fun startListener(settings: Settings) {
        withContext(Dispatchers.IO) {
            runCatching {
                if (permissionManager.hasRequiredPermissions.not()) {
                    return@withContext
                }

                val bufferSize = getBufferSize()
                val audioRecord = getAudioRecord(bufferSize).apply {
                    startRecording()
                }

                if (NoiseSuppressor.isAvailable() && settings.tunerEnableNoiseSuppressor) {
                    startNoiseSuppressor(audioRecord.audioSessionId)
                }

                pitchProcessor = PitchProcessor(
                    settings.tunerPitchDetectionAlgorithm.algorithm,
                    SAMPLE_RATE.toFloat(),
                    bufferSize,
                    this@TunerRepositoryImpl
                )
                audioDispatcher = getAudioDispatcher(audioRecord, bufferSize).apply {
                    addAudioProcessor(pitchProcessor)
                    run()
                }
            }.onFailure(::logError)
        }
    }

    private suspend fun stopListener() {
        withContext(Dispatchers.IO) {
            runCatching {
                stopNoiseSuppressor()

                audioDispatcher?.apply {
                    removeAudioProcessor(pitchProcessor)
                    stop()
                }

                noiseSuppressor = null
                pitchProcessor = null
                audioDispatcher = null
            }.onFailure(::logError)
        }
    }

    private fun startNoiseSuppressor(audioSessionId: Int) {
        runCatching {
            noiseSuppressor = NoiseSuppressor.create(audioSessionId)
                .apply { enabled = true }
        }.onFailure(::logError)
    }

    private fun stopNoiseSuppressor() {
        runCatching {
            noiseSuppressor?.apply {
                enabled = false
                release()
            }
        }.onFailure(::logError)
    }

    private suspend fun getTuning(detectedFrequency: Double): Tuning? {
        val (pitch, deviation) = if (autoMode) {
            detectPitchIdWithDeviation(detectedFrequency)?.let {
                pitchRepository.getPitchById(it.first)?.run { this to it.second }
            }
        } else {
            pitchRepository.findPitchByTone(selectedTone!!)?.let {
                it to getTuningDeviation(it.frequency, detectedFrequency)
            }
        } ?: return null

        return Tuning(
            closestPitch = pitch,
            currentFrequency = detectedFrequency,
            deviation = deviation,
            isTuned = deviation.absoluteValue < settingsManager.tunerMinDeviation
        )
    }

    private fun detectPitchIdWithDeviation(detectedFrequency: Double) =
        pitchRepository.purePitchesList.value.let {
            if (it.isEmpty()) return@let null

            var minDeviation = Int.MAX_VALUE
            var closestPitch = it.first()
            it.forEach { note ->
                getTuningDeviation(note.frequency, detectedFrequency).let { deviation ->
                    if (deviation.absoluteValue < minDeviation.absoluteValue) {
                        minDeviation = deviation
                        closestPitch = note
                    }
                }
            }
            closestPitch.pitchId to minDeviation
        }


    /**
     * Calculates the deviation of the detected frequency from the standard frequency.
     * The deviation is calculated in cents, which is a logarithmic unit of measure used for musical intervals.
     * A cent is 1/100 of a semitone, which is the smallest interval used in Western music.
     * A deviation of 100 cents corresponds to a semitone.
     * A deviation of 1200 cents corresponds to an octave.
     * A deviation of 1 cent corresponds to a frequency ratio of 2^(1/1200), -1 cent = 2^(-1/1200), 0 cents = 1.
     * @param standardFrequency The standard frequency.
     * @param detectedFrequency The detected frequency.
     * @return The deviation of the detected frequency from the standard frequency, in cents.
     * @see <a href="https://en.wikipedia.org/wiki/Cent_(music)">Cent (music)</a>
     */
    private fun getTuningDeviation(standardFrequency: Double, detectedFrequency: Double) =
        (1200 * log2(detectedFrequency / standardFrequency)).roundToInt()


    private fun getAudioDispatcher(audioRecord: AudioRecord, bufferSize: Int): AudioDispatcher {
        val format = TarsosDSPAudioFormat(
            SAMPLE_RATE.toFloat(),
            SAMPLE_RATE_BITS,
            CHANNEL_COUNT,
            true,
            false
        )
        val audioStream = AndroidAudioInputStream(audioRecord, format)

        return AudioDispatcher(audioStream, bufferSize, OVERLAP)
    }

    @SuppressLint("MissingPermission")
    private fun getAudioRecord(bufferSize: Int) =
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            ENCODING,
            bufferSize * 2
        )

    private fun getBufferSize(): Int {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            ENCODING
        )
        val minAudioBufferSizeInSamples = minBufferSize / 2

        return if (minAudioBufferSizeInSamples > BUFFER_SIZE) minAudioBufferSizeInSamples else BUFFER_SIZE
    }
}

fun logError(error: Throwable) = Log.e("TunerManager", error.message, error)