package com.example.guitartuner.domain.repository.tuner

import kotlinx.coroutines.flow.StateFlow

interface PermissionManager {
    val state: StateFlow<PermissionState>
    val hasRequiredPermissions: Boolean

    suspend fun requestPermissions()

    data class PermissionState(
        val hasRequiredPermissions: Boolean,
        val canRequest: Boolean = false,
    )
}