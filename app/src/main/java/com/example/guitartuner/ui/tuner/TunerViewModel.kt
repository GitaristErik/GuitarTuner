package com.example.guitartuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartuner.data.tuner.PermissionManager
import kotlinx.coroutines.launch

class TunerViewModel(
    private val permissionManager: PermissionManager,
) : ViewModel() {

    val state by lazy { permissionManager.state }

    init {
        onRequestPermission()
    }

    fun onRequestPermission() {
        viewModelScope.launch {
            requestPermissions()
        }
    }

    private suspend fun requestPermissions() {
        permissionManager.requestPermissions()
    }
}