package com.example.guitartuner.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun <T : LifecycleEventObserver> T.ObserveLifecycleEvents(
    lifecycleOwner: LifecycleOwner
) = lifecycleOwner.lifecycle.let { lifecycle ->
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@ObserveLifecycleEvents)
        onDispose {
            lifecycle.removeObserver(this@ObserveLifecycleEvents)
        }
    }
}