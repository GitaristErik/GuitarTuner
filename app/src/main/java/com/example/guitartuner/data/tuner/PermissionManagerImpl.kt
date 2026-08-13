package com.example.guitartuner.data.tuner

import android.Manifest
import android.app.Application
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
import java.lang.ref.WeakReference

/**
 * Holds the current [ComponentActivity] so permission requests can be issued
 * without scoping the entire tuner graph to the activity.
 */
class ActivityHolder {
    @Volatile
    private var activityRef: WeakReference<ComponentActivity>? = null

    var activity: ComponentActivity?
        get() = activityRef?.get()
        set(value) {
            activityRef = value?.let { WeakReference(it) }
        }
}

class PermissionManagerImpl(
    private val application: Application,
    private val activityHolder: ActivityHolder,
) : LifecycleEventObserver, PermissionManager {

    private val _state = MutableStateFlow(makeState())
    override val state = _state.asStateFlow()

    private var isFirstRequest: Boolean = true
    private var attachedActivity: ComponentActivity? = null

    override val hasRequiredPermissions: Boolean
        get() = application.hasPermission(PM_RECORD_AUDIO)

    private val canRequest: Boolean
        get() {
            val activity = activityHolder.activity
            return isFirstRequest ||
                (activity?.shouldShowRequestPermissionRationale(PM_RECORD_AUDIO) == true)
        }

    fun attach(activity: ComponentActivity) {
        if (attachedActivity === activity) {
            updateState()
            return
        }
        attachedActivity?.lifecycle?.removeObserver(this)
        attachedActivity = activity
        activityHolder.activity = activity
        activity.lifecycle.addObserver(this)
        updateState()
    }

    fun detach(activity: ComponentActivity) {
        if (attachedActivity !== activity) return
        activity.lifecycle.removeObserver(this)
        attachedActivity = null
        if (activityHolder.activity === activity) {
            activityHolder.activity = null
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            updateState()
        }
    }

    override suspend fun requestPermissions() {
        if (hasRequiredPermissions) {
            updateState()
            return
        }
        // Do not refresh state until the user answers; ON_RESUME will update.
        isFirstRequest = false
        activityHolder.activity?.requestPermissions(arrayOf(PM_RECORD_AUDIO), REQUEST_CODE)
    }

    private fun updateState() {
        _state.value = makeState()
    }

    private fun makeState() = PermissionManager.PermissionState(
        hasRequiredPermissions = hasRequiredPermissions,
        canRequest = canRequest
    )

    companion object {
        const val PM_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        private const val REQUEST_CODE = 1001
    }
}

fun Context.hasPermission(permission: String): Boolean =
    runCatching {
        ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)
