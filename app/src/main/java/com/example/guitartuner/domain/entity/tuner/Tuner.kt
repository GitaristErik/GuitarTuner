package com.example.guitartuner.domain.entity.tuner

class Tuner(
//    tuning: Tuning = Tuning.STANDARD
) {

    companion object {

        /** Threshold in semitones that note offset must be below to be considered in tune. */
        const val TUNED_OFFSET_THRESHOLD = 0.15

        /** Time in ms that a note must be held below the threshold for before being considered in tune. */
        const val TUNED_SUSTAIN_TIME = 900

        // Audio Dispatcher Constants
        /** Microphone sample rate. */
        private const val SAMPLE_RATE = 22050

        /** Audio buffer size. */
        private const val AUDIO_BUFFER_SIZE = 1024

        /** Index of the lowest detectable note. */
//        val LOWEST_NOTE = Notes.getIndex("D1")

        /** Index of the highest detectable note. */
//        val HIGHEST_NOTE = Notes.getIndex("B4")
    }
}