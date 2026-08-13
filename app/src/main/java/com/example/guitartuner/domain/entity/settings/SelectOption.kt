package com.example.guitartuner.domain.entity.settings

import androidx.annotation.StringRes

/**
 * Domain-level selectable option contracts (no Compose / UI dependencies).
 */
sealed interface SelectOption<T : Enum<T>> {
    val type: T get() = this as T

    interface ResId<T : Enum<T>> : SelectOption<T> {
        @get:StringRes
        val labelRes: Int
    }

    interface StringLabel<T : Enum<T>> : SelectOption<T> {
        val label: String
    }
}
