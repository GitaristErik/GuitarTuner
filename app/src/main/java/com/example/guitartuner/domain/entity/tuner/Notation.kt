package com.example.guitartuner.domain.entity.tuner

import com.example.guitartuner.domain.entity.tuner.Note.A
import com.example.guitartuner.domain.entity.tuner.Note.B
import com.example.guitartuner.domain.entity.tuner.Note.C
import com.example.guitartuner.domain.entity.tuner.Note.D
import com.example.guitartuner.domain.entity.tuner.Note.E
import com.example.guitartuner.domain.entity.tuner.Note.F
import com.example.guitartuner.domain.entity.tuner.Note.G
import com.example.guitartuner.ui.settings.components.SettingsComponents

enum class Notation(
    val convertFromNote: (Note) -> String
) : SettingsComponents.SelectOption.String<Notation> {

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

    /*    LocalizedClassic({ note ->
            when (note) {
                C -> "До"
                D -> "Ре"
                E -> "Мі"
                F -> "Фа"
                G -> "Соль"
                A -> "Ля"
                B -> "Сі"
            }
        });*/

//    abstract fun convertFromNote(note: Note): String

    val notesMap by lazy {
        Note.entries.associateWith { note -> this.convertFromNote(note) }
    }

    override val label: String
        get() = "$this (${listOf(A, B, C)
            .joinToString(separator = ", ") { this.convertFromNote(it) }})"
}
