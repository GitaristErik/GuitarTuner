package com.example.guitartuner.domain.entity.tuner

import org.billthefarmer.mididriver.GeneralMidiConstants

data class Instrument(
    val instrumentId: Int,
    val name: String,
    val countStrings: Int,
    val lowestPitch: Pitch,
    val highestPitch: Pitch,
    val midiInstrument: Byte = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN,
)