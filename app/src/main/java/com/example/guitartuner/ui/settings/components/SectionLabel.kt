package com.example.guitartuner.ui.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview

/** UI component displaying a list section label with [title] text. */
@Composable
fun SectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
@ThemePreview
fun PreviewSectionLabel() {
    PreviewWrapper {
        SectionLabel("Header")
    }
}