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
import com.markodevcic.peko.Peko
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManagerImpl(
    private val activity: ComponentActivity
) : LifecycleEventObserver, PermissionManager {

    private val _state by lazy { MutableStateFlow(makeState()) }
    override val state by lazy { _state.asStateFlow() }

    init {
        activity.lifecycle.addObserver(this)
    }


    override val hasRequiredPermissions
        get() = activity.hasPermission(PM_RECORD_AUDIO)
            .also { isFirstRequest = false }

    private var isFirstRequest: Boolean = true

    private val canRequest
        get() = isFirstRequest ||
                activity.shouldShowRequestPermissionRationale(PM_RECORD_AUDIO)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            updateState()
        }
    }

    override suspend fun requestPermissions() {
        if (!hasRequiredPermissions)
            runCatching {
                Peko.requestPermissionsAsync(activity, Manifest.permission.RECORD_AUDIO)
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