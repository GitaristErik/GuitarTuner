package com.example.guitartuner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.guitartuner.data.tuner.PermissionManagerImpl
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.ui.core.BaseApp
import com.example.guitartuner.ui.theme.GuitarTunerTheme
import com.example.guitartuner.ui.theme.PreviewWindowWrapper
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.rohankhayech.android.util.ui.preview.OrientationThemePreview
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private val permissionManager: PermissionManager by lazy { get() }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        (permissionManager as? PermissionManagerImpl)?.attach(this)

        setContent {
            GuitarTunerTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                BaseApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                )
            }
        }
    }

    override fun onDestroy() {
        (permissionManager as? PermissionManagerImpl)?.detach(this)
        super.onDestroy()
    }
}

@OrientationThemePreview
@Composable
private fun TunerPreview() {
    PreviewWindowWrapper {
        BaseApp(
            windowSize = it,
            displayFeatures = emptyList()
        )
    }
}
