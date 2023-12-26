package com.example.guitartuner.domain.entity.tuner

data class TuningSet(
    val tuningId: Int,
    val name: String,
    val pitches: List<Pitch>,
    val instrumentId: Int,
    val isFavorite: Boolean = false,
)