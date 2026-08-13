package com.example.guitartuner.domain.entity.tuner

import com.example.guitartuner.domain.entity.settings.SelectOption
import com.example.guitartuner.domain.entity.tuner.Note.A
import com.example.guitartuner.domain.entity.tuner.Note.B
import com.example.guitartuner.domain.entity.tuner.Note.C
import com.example.guitartuner.domain.entity.tuner.Note.D
import com.example.guitartuner.domain.entity.tuner.Note.E
import com.example.guitartuner.domain.entity.tuner.Note.F
import com.example.guitartuner.domain.entity.tuner.Note.G

enum class Notation(
    val convertFromNote: (Note) -> String
) : SelectOption.StringLabel<Notation> {

    English({ note ->
        when (note) {
            C -> "C"
            D -> "D"
            E -> "E"
            F -> "F"
            G -> "G"
            A -> "A"
            B -> "B"
        }
    }),

    Solfeggio({ note ->
        when (note) {
            C -> "Do"
            D -> "Re"
            E -> "Mi"
            F -> "Fa"
            G -> "Sol"
            A -> "La"
            B -> "Si"
        }
    }),

    German({ note ->
        when (note) {
            C -> "C"
            D -> "D"
            E -> "E"
            F -> "F"
            G -> "G"
            A -> "A"
            B -> "H"
        }
    });

    val notesMap by lazy {
        Note.entries.associateWith { note -> this.convertFromNote(note) }
    }

    override val label: String
        get() = "$this (${listOf(A, B, C)
            .joinToString(separator = ", ") { this.convertFromNote(it) }})"
}
