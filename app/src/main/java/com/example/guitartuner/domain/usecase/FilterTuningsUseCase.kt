package com.example.guitartuner.domain.usecase

import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository

class FilterTuningsUseCase(
    private val tuningSetsRepository: TuningSetsRepository,
) {
    operator fun invoke(builder: TuningSetsRepository.TuningFilterBuilder.() -> Unit) {
        tuningSetsRepository.filterTunings(builder)
    }
}
