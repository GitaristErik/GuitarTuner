package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.TuningSet
import kotlinx.coroutines.flow.StateFlow

interface TuningSetsRepository {

    val favoritesTuningSets: StateFlow<List<TuningSet>>

    val currentInstrument: StateFlow<Instrument>

    val currentTuningSet: StateFlow<TuningSet>

    fun selectTuning(tuningId: Int)

    suspend fun tuneUpString(stringId: Int, semitones: Int = 1)
    suspend fun tuneDownString(stringId: Int, semitones: Int = 1)
    suspend fun tuneUpTuning(semitones: Int = 1)
    suspend fun tuneDownTuning(semitones: Int = 1)


    val tuningsList: StateFlow<List<Pair<TuningSet, Instrument>>>
    val instrumentsAvailableList: StateFlow<List<Pair<Instrument, Boolean>>>
    val stringsCountAvailableList: StateFlow<List<Pair<Int, Boolean>>>

    suspend fun updateTuningSet(tuningSet: TuningSet): Int
    fun <T> updateTuningSet(tuningId: Int, tuningMap: Map<String, T>)
    fun deleteTuning(tuningId: Int)

    fun updateInstrument(instrument: Instrument)

    fun filterTunings(builder: TuningFilterBuilder.() -> Unit)

    abstract class TuningFilterBuilder {

        sealed interface TuningFilter {
            enum class General : TuningFilter { ALL, FAVORITES; }

            data class InstrumentId(val id: Set<Int>) : TuningFilter {
                constructor(vararg id: Int) : this(id.toSet())
            }

            data class CountStrings(val count: Set<Int>) : TuningFilter {
                constructor(vararg count: Int) : this(count.toSet())
            }


            infix fun or(other: TuningFilter) = setOf(this, other)
            infix fun or(other: Set<TuningFilter>) = setOf(this) + other
        }


        protected val filters: MutableSet<TuningFilter> = mutableSetOf()
        protected var startPaging: Int = 0
        protected var limit: Int = Int.MAX_VALUE

        fun filter(vararg filter: TuningFilter) = this.also { filters += filter }
        fun filter(vararg filter: Set<TuningFilter>) =
            this.also { filters.addAll(filter.toList().flatten()) }

        fun start(start: Int) = this.also { it.startPaging = start }
        fun limit(limit: Int) = this.also { it.limit = limit }
    }
}