package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guitartuner.ui.utils.AppNavigationInfo

@Composable
fun TunerScreen(
    appNavigationInfo: AppNavigationInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Tuner Screen",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
