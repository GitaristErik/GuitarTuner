package com.example.guitartuner.domain.entity.tuner

data class Pitch(
    val frequency: Double,
    val note: Note,
    val octave: Int
) {

    fun isValidPitch(
        ignoreFrequency: Boolean = true,
        customBounds: Pair<Pitch, Pitch> = Pitch(
            0.0, LOWEST_NOTE, LOWEST_OCTAVE
        ) to Pitch(0.0, HIGHEST_NOTE, HIGHEST_OCTAVE)
    ): Boolean {
        val (boundDown, boundUp) = customBounds

        if (!ignoreFrequency) {
            if (this.frequency !in boundDown.frequency..boundUp.frequency)
                return false
        }

        return if (this.octave !in boundDown.octave..boundUp.octave) {
            false
        } else {
            this.note !in boundDown.note..boundUp.note
        }
    }

    companion object {
        @JvmStatic
        val HIGHEST_NOTE = Note.B
        const val HIGHEST_OCTAVE = 4

        @JvmStatic
        val LOWEST_NOTE = Note.D
        const val LOWEST_OCTAVE = 0
    }
}
