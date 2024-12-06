package com.example.guitartuner.domain.entity.tuner

import com.example.guitartuner.domain.entity.common.ChromaticScale
import org.billthefarmer.mididriver.GeneralMidiConstants

data class Instrument(
    val instrumentId: Int,
    val name: String,
    val countStrings: Int,
    val lowestPitch: Pitch,
    val highestPitch: Pitch,
    val midiInstrument: Byte = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN,
)

internal val previewInstrument by lazy {
    val chromaToPitch = { chroma: ChromaticScale ->
        Pitch(
            chroma.ordinal,
            chroma.frequency.toDouble(),
            Tone(
                chroma.note,
                chroma.octave,
                if(chroma.semitone) Alteration.SHARP else Alteration.NATURAL
            )
        )
    }
    Instrument(
        instrumentId = 0,
        name = "Guitar",
        countStrings = 6,
        lowestPitch = chromaToPitch(ChromaticScale.E0),
        highestPitch = chromaToPitch(ChromaticScale.C6_SHARP),
    )
}