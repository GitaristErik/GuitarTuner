package com.example.guitartuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.guitartuner.ui.BaseApp
import com.example.guitartuner.ui.theme.GuitarTunerTheme
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewWrapper() {
    val windowSize = WindowSizeClass.calculateFromSize(
        with(LocalConfiguration.current) {
            DpSize(width = screenWidthDp.dp, height = screenHeightDp.dp)
        }
    )

    GuitarTunerTheme {
        BaseApp(
            windowSize = windowSize,
            displayFeatures = emptyList()
        )
    }
}

@OrientationThemePreview
@Composable
private fun TunerPreview() {
    PreviewWrapper()
}