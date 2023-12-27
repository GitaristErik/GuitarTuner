package com.example.guitartuner.ui.model

data class FilterBoxUIState<T>(
    val key: String,
    val value: T,
    var text: String,
    val isEnabled: Boolean,
//    val isSelected: Boolean,
)

    