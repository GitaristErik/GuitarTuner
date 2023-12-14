package com.example.guitartuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.guitartuner.ui.BaseApp
import com.example.guitartuner.ui.theme.GuitarTunerTheme
import com.example.guitartuner.ui.theme.PreviewWindowWrapper
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.rohankhayech.android.util.ui.preview.OrientationThemePreview

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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