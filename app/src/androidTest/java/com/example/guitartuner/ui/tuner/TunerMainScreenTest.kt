package com.example.guitartuner.ui.tuner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.ui.theme.GuitarTunerTheme
import com.example.guitartuner.ui.tuner.components.previewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.example.guitartuner.ui.utils.ContentType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TunerMainScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsAutoLabel() {
        composeRule.setContent {
            GuitarTunerTheme {
                TunerMainScreen(
                    contentType = ContentType.SINGLE_PANE,
                    noteOffset = null,
                    isTuned = false,
                    tunings = previewTuningState,
                    currentTuningSet = previewTuningState[1],
                    buttonsUIState = previewButtonsUIState,
                    selectedString = null,
                    tuned = List(6) { false },
                    autoDetect = true,
                    settings = Settings.previewSettings(),
                    onSelectString = {},
                    onSelectTuning = {},
                    onTuneUpString = {},
                    onTuneDownString = {},
                    onTuneUpTuning = {},
                    onTuneDownTuning = {},
                    onAutoChanged = {},
                    onOpenTuningSelector = {},
                )
            }
        }

        composeRule.onNodeWithText("Auto", ignoreCase = true).assertIsDisplayed()
    }
}
