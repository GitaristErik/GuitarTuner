package com.example.guitartuner.domain.usecase

import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository

class SelectTuningUseCase(
    private val tuningSetsRepository: TuningSetsRepository,
) {
    operator fun invoke(tuningId: Int) {
        tuningSetsRepository.selectTuning(tuningId)
    }
}
