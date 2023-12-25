package com.example.guitartuner.domain.repository.tuner

interface PitchGenerationRepository {

    fun playStringSelectSound(string: Int)

    fun playInTuneSound(string: Int)
}