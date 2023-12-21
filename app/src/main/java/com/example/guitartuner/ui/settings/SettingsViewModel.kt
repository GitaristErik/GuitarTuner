package com.example.guitartuner.ui.settings

import androidx.lifecycle.ViewModel
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.settings.Settings

class SettingsViewModel(
    private val settingsManager: SettingsManager,
) : ViewModel() {

    val state by lazy { settingsManager.state }


    fun updateSettings(settings: Settings) {
        settingsManager.settings = settings
    }

}
