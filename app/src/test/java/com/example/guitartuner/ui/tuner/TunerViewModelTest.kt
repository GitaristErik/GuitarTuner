package com.example.guitartuner.ui.tuner

import app.cash.turbine.test
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.Tuning
import com.example.guitartuner.domain.entity.tuner.TuningSet
import com.example.guitartuner.domain.entity.tuner.previewInstrument
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.domain.repository.tuner.PitchGenerationRepository
import com.example.guitartuner.domain.repository.tuner.TunerRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.ui.model.TunerEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TunerViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val permissionFlow = MutableStateFlow(
        PermissionManager.PermissionState(hasRequiredPermissions = true, canRequest = false)
    )
    private val tunerFlow = MutableStateFlow<Tuning?>(null)
    private val instrumentFlow = MutableStateFlow(previewInstrument)
    private val tuningSetFlow = MutableStateFlow(
        TuningSet(
            tuningId = 1,
            name = "Standard",
            pitches = listOf(
                pitch(Note.E, 2),
                pitch(Note.A, 2),
                pitch(Note.D, 3),
                pitch(Note.G, 3),
                pitch(Note.B, 3),
                pitch(Note.E, 4),
            ),
            instrumentId = 1,
        )
    )
    private val favoritesFlow = MutableStateFlow(listOf(tuningSetFlow.value))
    private val settingsFlow = MutableStateFlow(Settings.previewSettings())

    private lateinit var permissionManager: PermissionManager
    private lateinit var tunerRepository: TunerRepository
    private lateinit var pitchGenerationRepository: PitchGenerationRepository
    private lateinit var tuningSetsRepository: TuningSetsRepository
    private lateinit var settingsManager: SettingsManager

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        permissionManager = mockk {
            every { state } returns permissionFlow
            every { hasRequiredPermissions } returns true
            coEvery { requestPermissions() } just runs
        }
        tunerRepository = mockk(relaxed = true) {
            every { state } returns tunerFlow
            every { autoMode = any() } just runs
            every { selectTone(any()) } just runs
        }
        pitchGenerationRepository = mockk(relaxed = true)
        tuningSetsRepository = mockk(relaxed = true)
        every { tuningSetsRepository.currentInstrument } returns instrumentFlow
        every { tuningSetsRepository.currentTuningSet } returns tuningSetFlow
        every { tuningSetsRepository.favoritesTuningSets } returns favoritesFlow
        every { tuningSetsRepository.selectTuning(any()) } just runs

        settingsManager = mockk(relaxed = true)
        every { settingsManager.state } returns settingsFlow
        every { settingsManager.generalNotation } returns settingsFlow.value.generalNotation
        every { settingsManager.soundPlaySoundInTune } returns false
        every { settingsManager.soundPlaySoundOnSelect } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectString_disablesAutoAndUpdatesSelection() = runTest(dispatcher) {
        val vm = createVm()
        advanceUntilIdle()

        vm.onEvent(TunerEvent.SelectString(0))
        advanceUntilIdle()

        assertFalse(vm.uiState.value.autoDetect)
        assertEquals(0, vm.uiState.value.selectedString)
        verify { tunerRepository.autoMode = false }
        verify { tunerRepository.selectTone(any()) }
    }

    @Test
    fun setAutoDetect_clearsSelectedString() = runTest(dispatcher) {
        val vm = createVm()
        advanceUntilIdle()

        vm.onEvent(TunerEvent.SelectString(2))
        advanceUntilIdle()
        vm.onEvent(TunerEvent.SetAutoDetect(true))
        advanceUntilIdle()

        assertTrue(vm.uiState.value.autoDetect)
        assertEquals(null, vm.uiState.value.selectedString)
    }

    @Test
    fun tunedFlags_emitNewListAndResetOnTuningChange() = runTest(dispatcher) {
        val vm = createVm()
        advanceUntilIdle()

        vm.uiState.test {
            var state = awaitItem()
            while (state.buttons == null || state.tunedStrings.isEmpty()) {
                state = awaitItem()
            }

            tunerFlow.value = Tuning(
                closestPitch = tuningSetFlow.value.pitches[0],
                currentFrequency = 82.4,
                deviation = 0,
                isTuned = true,
            )
            val tuned = awaitItem()
            assertTrue(tuned.tunedStrings[0])

            tuningSetFlow.value = tuningSetFlow.value.copy(tuningId = 2, name = "Drop D")
            val reset = awaitItem()
            assertTrue(reset.tunedStrings.all { !it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createVm() = TunerViewModel(
        permissionManager = permissionManager,
        tunerRepository = tunerRepository,
        pitchGenerationRepository = pitchGenerationRepository,
        tuningSetsRepository = tuningSetsRepository,
        settingsManager = settingsManager,
    )

    private fun pitch(note: Note, octave: Int) = Pitch(
        id = note.ordinal + octave * 12,
        frequency = 100.0,
        tone = Tone(note, octave, Alteration.NATURAL),
    )
}
