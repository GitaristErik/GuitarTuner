package com.example.guitartuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.guitartuner.ui.BaseApp
import com.example.guitartuner.ui.theme.GuitarTunerTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.rohankhayech.android.util.ui.preview.LandscapeThemePreview
import com.rohankhayech.android.util.ui.preview.TabletThemePreview
import com.rohankhayech.android.util.ui.preview.ThemePreview

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
fun PreviewWrapper(dpSize: DpSize) {
    GuitarTunerTheme {
        BaseApp(
            windowSize = WindowSizeClass.calculateFromSize(dpSize),
            displayFeatures = emptyList()
        )
    }
}

@ThemePreview
@Composable
private fun TunerPreview() {
    PreviewWrapper(DpSize(400.dp, 600.dp))
}

@TabletThemePreview
@Composable
private fun TabletPreview() {
    PreviewWrapper(DpSize(800.dp, 600.dp))
}

@LandscapeThemePreview
@Composable
private fun LandscapePreview() {
    PreviewWrapper(DpSize(600.dp, 400.dp))
}