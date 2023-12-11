package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.guitartuner.ui.utils.AppNavigationInfo

@Composable
fun TunerScreen(
    appNavigationInfo: AppNavigationInfo,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "Tuner Screen")
    }
}
