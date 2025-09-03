package com.example.guitartuner.data.tuner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManagerImpl(
    private val activity: ComponentActivity
) : LifecycleEventObserver, PermissionManager {

    private val _state by lazy { MutableStateFlow(makeState()) }
    override val state by lazy { _state.asStateFlow() }

    private var isFirstRequest: Boolean = true

    init {
        activity.lifecycle.addObserver(this)
    }

    override val hasRequiredPermissions
        get() = activity.hasPermission(PM_RECORD_AUDIO)
            .also { isFirstRequest = false }

    private val canRequest
        get() = isFirstRequest ||
                activity.shouldShowRequestPermissionRationale(PM_RECORD_AUDIO)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            updateState()
        }
    }

    override suspend fun requestPermissions() {
        if (!hasRequiredPermissions) {
            // Use direct permission request to avoid late ActivityResult registration.
            activity.requestPermissions(arrayOf(PM_RECORD_AUDIO), 1001)
            // State will be refreshed on next resume; proactively update now too.
            updateState()
        }
    }

    private fun updateState() {
        _state.value = makeState()
    }

    private fun makeState() = PermissionManager.PermissionState(
        hasRequiredPermissions = hasRequiredPermissions,
        canRequest = canRequest
    )

    companion object {
        const val PM_RECORD_AUDIO = (Manifest.permission.RECORD_AUDIO)
    }
}

fun Context.hasPermission(permission: String): Boolean =
    runCatching {
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

