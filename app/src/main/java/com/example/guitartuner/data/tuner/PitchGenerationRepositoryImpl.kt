package com.example.guitartuner.data.tuner

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.guitartuner.domain.repository.tuner.PitchGenerationRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver

class PitchGenerationRepositoryImpl(
    private val tuningSetsRepository: TuningSetsRepository,
) : PitchGenerationRepository {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> midi.start()
            Lifecycle.Event.ON_PAUSE -> midi.stop()
            Lifecycle.Event.ON_CREATE -> midi = MidiController(
                tuningSetsRepository.currentInstrument.value.countStrings
            )

            else -> {}
        }

        if (!tuningSetSubscribed) {
            source.lifecycleScope.launch {
                tuningSetsRepository.currentInstrument.collectLatest {
                    recreateMidiDriver(it.countStrings)
                }
            }
            tuningSetSubscribed = true
        }
    }

    private var tuningSetSubscribed = false


    private lateinit var midi: MidiController

    private fun getMidiNoteFromString(stringId: Int) =
        tuningSetsRepository.currentTuningSet
            .value.pitches.getOrNull(stringId)?.tone?.run {
                (degree - 9) + (octave - 4) * 12
            }


    /** Plays the string selection sound for the specified [string]. */
    override fun playStringSelectSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(getMidiNoteFromString(string)!!),
            DURATION_ON_SELECT_SOUND,
            tuningSetsRepository.currentInstrument.value.midiInstrument
        )
    }

    /** Plays the in tune sound for the selected string. */
    override fun playInTuneSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(getMidiNoteFromString(string)!!) + 12,
            DURATION_IN_TUNE_SOUND,
            GeneralMidiConstants.MARIMBA
        )
    }

    /**
     * Recreates the MIDI driver when the number of strings
     * in the new tuning is different from the current tuning.
     *
     * @param countStrings The number of strings in the new instrument.
     */
    private fun recreateMidiDriver(countStrings: Int) {
        midi.stop()
        midi = MidiController(countStrings)
        midi.start()
    }

    private class MidiController(numStrings: Int) {
        private var midiDriver: MidiDriver? = null

        /** Array of threads for each string. Used to wait for the duration of a note.  */
        private lateinit var stringThread: Array<Thread?>

        /** Array of mutexes for each string thread.  */
        private lateinit var stringMutex: Array<Any?>

        init {
            init(numStrings)
        }

        private fun init(numStrings: Int) {
            midiDriver = MidiDriver.getInstance()

            // Setup string thread array.
            stringThread = arrayOfNulls(numStrings)
            stringMutex = arrayOfNulls(numStrings)
            for (i in 0 until numStrings) {
                // Setup string thread.
                stringThread[i] = null
                stringMutex[i] = Any()
            }
        }

        /**
         * Starts the midi controller and system driver.
         */
        fun start() {
            if (isStarted) return
            midiDriver!!.start()
            isStarted = true
        }

        /**
         * Stops all playing notes and then stops the midi controller and system driver.
         */
        fun stop() {
            if (isStopped) return
            for (i in stringThread.indices) {
                stopNote(i)
            }
            midiDriver!!.stop()
            isStopped = true
        }

        private var isStarted: Boolean = false
            private set(value) {
                field = value
                if (value) isStopped = false
            }
        private var isStopped: Boolean = false
            private set(value) {
                field = value
                if (value) isStarted = false
            }

        /**
         * Plays the specified note on the specified string for the specified duration.
         * @param string The position of the string on the tuning.
         * @param midiNote The MIDI note number to play.
         * @param duration The duration of the note, in ms.
         * @param instrument The MIDI instrument code to play.
         */
        @JvmOverloads
        fun playNote(
            string: Int,
            midiNote: Int,
            duration: Long,
            instrument: Byte = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN
        ) {
            stopNote(string)

            // Play note.
            synchronized(stringMutex[string]!!) {
                stringThread[string] = Thread({
                    try {
                        setInstrument(string, instrument)

                        // Send note on event
                        val event = ByteArray(3)
                        event[0] = (MidiConstants.NOTE_ON.toInt() or string.toByte()
                            .toInt()).toByte() // Status byte and channel
                        event[1] = midiNote.toByte() // Pitch (midi note number)
                        event[2] = 0x7F.toByte() // Velocity
                        midiDriver!!.write(event)

                        // Wait for duration of note.
                        Thread.sleep(duration)
                    } catch (e: InterruptedException) {
                        // Cancel thread sleep.
                    } finally {
                        synchronized(stringMutex[string]!!) {

                            // Stop the note playing after duration or interrupted.
                            // Send note off event.
                            val event = ByteArray(3)
                            event[0] = (MidiConstants.NOTE_OFF.toInt() or string.toByte()
                                .toInt()).toByte() // Status byte and channel
                            event[1] = midiNote.toByte() // Pitch (midi note number)
                            event[2] = 0x00.toByte() // Velocity
                            midiDriver!!.write(event)

                            // Clean up thread.
                            stringThread[string] = null
                        }
                    }
                }, "string_thread_$string")
                stringThread[string]!!.start()
            }
        }

        /**
         * Stops the currently playing note on the specified string.
         * @param string The position of the string on the tuning.
         */
        fun stopNote(string: Int) {
            if (isNotePlaying(string)) {
                stringThread[string]!!.interrupt()
            }
        }

        /**
         * Sets the instrument on the specified channel to the specified instrument.
         * @param channel The MIDI channel number.
         * @param instrument The MIDI instrument code to set.
         */
        private fun setInstrument(channel: Int, instrument: Byte) {
            val event = ByteArray(2)
            event[0] = (MidiConstants.PROGRAM_CHANGE.toInt() or channel.toByte().toInt()).toByte()
            event[1] = instrument
            midiDriver!!.write(event)
        }

        private fun isNotePlaying(string: Int): Boolean {
            return stringThread[string] != null
        }

        companion object {
            /**
             * Converts the specified note index to a MIDI note number.
             * @param noteIndex The internal note index.
             * @return The corresponding MIDI note number.
             */
            fun noteIndexToMidi(noteIndex: Int): Int {
                return noteIndex + A4_MIDI_NOTE_NUMBER
            }

            const val A4_MIDI_NOTE_NUMBER = 69
        }
    }

    companion object {
        const val DURATION_IN_TUNE_SOUND = 50L
        const val DURATION_ON_SELECT_SOUND = 150L
    }
}