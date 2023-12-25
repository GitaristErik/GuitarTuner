package com.example.guitartuner.domain.entity.tuner

data class Pitch(
    val id: Int,
    val frequency: Double,
    val tone: Tone
)