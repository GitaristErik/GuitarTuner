package com.example.guitartuner.domain.entity.tuner

enum class Note { C, D, E, F, G, A, B }

enum class Alteration { SHARP, FLAT, NATURAL }

data class Tone(
    val note: Note,
    val octave: Int,
    val alteration: Alteration = Alteration.NATURAL,
): Comparable<Tone> {

    val degree = degrees.indexOfFirst { it.contains(note to alteration) }

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
        private val degrees by lazy {
            listOf(
                listOf(
                    Note.B to Alteration.SHARP,
                    Note.C to Alteration.NATURAL
                ),
                listOf(
                    Note.C to Alteration.SHARP,
                    Note.D to Alteration.FLAT
                ),
                listOf(Note.D to Alteration.NATURAL),
                listOf(
                    Note.D to Alteration.SHARP,
                    Note.E to Alteration.FLAT
                ),
                listOf(
                    Note.E to Alteration.NATURAL,
                    Note.F to Alteration.FLAT
                ),
                listOf(
                    Note.E to Alteration.SHARP,
                    Note.F to Alteration.NATURAL
                ),
                listOf(
                    Note.F to Alteration.SHARP,
                    Note.G to Alteration.FLAT
                ),
                listOf(Note.G to Alteration.NATURAL),
                listOf(
                    Note.G to Alteration.SHARP,
                    Note.A to Alteration.FLAT
                ),
                listOf(Note.A to Alteration.NATURAL),
                listOf(
                    Note.A to Alteration.SHARP,
                    Note.B to Alteration.FLAT
                ),
                listOf(Note.B to Alteration.NATURAL)
            )
        }
    }
}