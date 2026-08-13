package com.example.guitartuner.data.tuner

import be.tarsos.dsp.pitch.PitchProcessor
import com.example.guitartuner.domain.entity.settings.Settings

fun Settings.TunerPitchDetectionAlgorithm.toTarsosAlgorithm(): PitchProcessor.PitchEstimationAlgorithm =
    when (this) {
        Settings.TunerPitchDetectionAlgorithm.YIN ->
            PitchProcessor.PitchEstimationAlgorithm.YIN
        Settings.TunerPitchDetectionAlgorithm.FFT_YIN ->
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
        Settings.TunerPitchDetectionAlgorithm.MPM ->
            PitchProcessor.PitchEstimationAlgorithm.MPM
        Settings.TunerPitchDetectionAlgorithm.AMDF ->
            PitchProcessor.PitchEstimationAlgorithm.AMDF
        Settings.TunerPitchDetectionAlgorithm.DYWA ->
            PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
    }
