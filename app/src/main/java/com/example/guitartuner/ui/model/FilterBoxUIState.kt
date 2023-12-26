package com.example.guitartuner.ui.model

data class FilterBoxUIState<T>(
    val key: String,
    val value: T,
    val text: String,
    val isEnabled: Boolean,
    val isSelected: Boolean,
)

    