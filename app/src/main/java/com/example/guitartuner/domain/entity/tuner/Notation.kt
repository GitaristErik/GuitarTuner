package com.example.guitartuner.domain.entity.tuner

import com.example.guitartuner.domain.entity.tuner.Note.*

enum class Notation(
    val convertFromNote: (Note) -> String
) {

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

    Classic({ note ->
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

}
