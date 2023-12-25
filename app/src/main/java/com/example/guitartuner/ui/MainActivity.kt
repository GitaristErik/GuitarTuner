package com.example.guitartuner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.guitartuner.ui.core.BaseApp
import com.example.guitartuner.ui.theme.GuitarTunerTheme
import com.example.guitartuner.ui.theme.PreviewWindowWrapper
import com.example.guitartuner.ui.tuner.TunerViewModel
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.rohankhayech.android.util.ui.preview.OrientationThemePreview
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.compose.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    val viewModel by inject<TunerViewModel>()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        linkScope()

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

    private fun linkScope(scopeId: ScopeID = SCOPE_ID_KEY) {
        getKoin()
            .getOrCreateScope(scopeId, named("session"))
            .linkTo(scope)
    }

    companion object {
        private const val SCOPE_ID_KEY = "SCOPE_ID_KEY"

        @Composable
        fun koinMainViewModel() = getKoin()
            .getScopeOrNull(SCOPE_ID_KEY)
            ?.getOrNull<MainActivity>()
            ?.viewModel
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