package com.example.guitartuner.domain.entity.common

import android.util.Log
import com.example.guitartuner.domain.entity.tuner.Alteration.FLAT
import com.example.guitartuner.domain.entity.tuner.Alteration.NATURAL
import com.example.guitartuner.domain.entity.tuner.Alteration.SHARP
import com.example.guitartuner.domain.entity.tuner.Note.A
import com.example.guitartuner.domain.entity.tuner.Note.B
import com.example.guitartuner.domain.entity.tuner.Note.C
import com.example.guitartuner.domain.entity.tuner.Note.D
import com.example.guitartuner.domain.entity.tuner.Note.E
import com.example.guitartuner.domain.entity.tuner.Note.F
import com.example.guitartuner.domain.entity.tuner.Note.G
import com.example.guitartuner.domain.entity.tuner.Tone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.pow

open class ChromaticScaleGenerator {

    open fun generateAsFlow(
        referenceFrequency: Double = 440.0
    ): Flow<Pair<Double, Tone>> = flow {
//        Log.e("ChromaticScaleGenerator", "generateToFlow: $referenceFrequency")
        List(FREQUENCIES_COUNT) { i ->
            val note = generator(i, referenceFrequency)
//            Log.e("ChromaticScaleGenerator", "generateToFlow: $note")
            emit(note)
        }
    }

    open fun generate(referenceFrequency: Double = 440.0) =
        List(FREQUENCIES_COUNT) { generator(it, referenceFrequency) }.toMap()

    open fun generator(i: Int, referenceFrequency: Double = 440.0): Pair<Double, Tone> {
        val frequency = referenceFrequency * 2.0.pow((i - 57) / 12.0)
        val tone = Tone(
            note = stringNotes[i % 12].parseToNote(),
            octave = i / 12,
            alteration = stringNotes[i % 12].parseToAlteration(),
            degree = (i % 12) + 1
        )
        return frequency to tone
    }


    companion object {
        const val FREQUENCIES_COUNT = 120


        private val stringNotes = listOf(
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
        )

        private fun String.parseToNote() = when (trim('#')) {
            "C" -> C
            "D" -> D
            "E" -> E
            "F" -> F
            "G" -> G
            "A" -> A
            "B" -> B
            else -> throw IllegalArgumentException("Invalid note: $this")
        }

        private fun String.parseToAlteration() = when {
            contains('#') -> SHARP
            contains('b') -> FLAT
            else -> NATURAL
        }
    }
}
