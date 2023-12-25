package com.example.guitartuner.data.tuner

import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.repository.tuner.ChromaticScale
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PitchRepositoryImpl(
    private val coroutineScope: CoroutineScope
) : PitchRepository {
    private val _pitchList by lazy {
        MutableStateFlow(
            listOf<Pitch>()
        )
    }

    override fun getPitchById(id: Int): Pitch {
        return _pitchList.value[id]
    }

    override fun findPitchByTone(tone: Tone): Pitch {
        return _pitchList.value.find { it.tone == tone }!!
    }

    override val pitchList: StateFlow<List<Pitch>>
        get() = _pitchList.asStateFlow()

    init {
        initPitches()
    }

    private fun initPitches() {
        coroutineScope.launch {
            _pitchList.update {
                ChromaticScale.entries.mapIndexed { index, scale ->
                    Pitch(
                        id = index,
                        frequency = scale.frequency.toDouble(),
                        Tone(
                            scale.note,
                            scale.octave,
                            if (scale.semitone) Alteration.SHARP else Alteration.NATURAL
                        )
                    )
                }
            }
        }
    }
}