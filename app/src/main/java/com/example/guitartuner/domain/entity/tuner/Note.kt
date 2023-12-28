package com.example.guitartuner.domain.entity.tuner

enum class Note { C, D, E, F, G, A, B }

enum class Alteration { SHARP, FLAT, NATURAL }

data class Tone(
    val note: Note,
    val octave: Int,
    val alteration: Alteration = Alteration.NATURAL,
    val degree: Int = degrees.filterValues { it.contains(note to alteration) }.keys.first()
): Comparable<Tone> {

    override fun toString(): String {
        return "${note.name}${
            when (alteration) {
                Alteration.SHARP -> "#"
                Alteration.FLAT -> "b"
                Alteration.NATURAL -> ""
            }
        }$octave"
    }

    override fun compareTo(other: Tone): Int {
        return when {
            this.octave > other.octave -> 1
            this.octave < other.octave -> -1
            else -> this.degree - other.degree
        }
    }

    companion object {

        @JvmStatic
        val degrees by lazy {
            mapOf(
                1 to listOf(
                    Note.B to Alteration.SHARP,
                    Note.C to Alteration.NATURAL
                ),
                2 to listOf(
                    Note.C to Alteration.SHARP,
                    Note.D to Alteration.FLAT
                ),
                3 to listOf(Note.D to Alteration.NATURAL),
                4 to listOf(
                    Note.D to Alteration.SHARP,
                    Note.E to Alteration.FLAT
                ),
                5 to listOf(
                    Note.E to Alteration.NATURAL,
                    Note.F to Alteration.FLAT
                ),
                6 to listOf(
                    Note.E to Alteration.SHARP,
                    Note.F to Alteration.NATURAL
                ),
                7 to listOf(
                    Note.F to Alteration.SHARP,
                    Note.G to Alteration.FLAT
                ),
                8 to listOf(Note.G to Alteration.NATURAL),
                9 to listOf(
                    Note.G to Alteration.SHARP,
                    Note.A to Alteration.FLAT
                ),
                10 to listOf(Note.A to Alteration.NATURAL),
                11 to listOf(
                    Note.A to Alteration.SHARP,
                    Note.B to Alteration.FLAT
                ),
                12 to listOf(Note.B to Alteration.NATURAL)
            )
        }
    }
}