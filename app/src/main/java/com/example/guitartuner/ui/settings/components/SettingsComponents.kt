package com.example.guitartuner.ui.settings.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.ui.settings.components.SettingsComponents.PreferenceActionLink
import com.example.guitartuner.ui.settings.components.SettingsComponents.PreferenceSelector
import com.example.guitartuner.ui.settings.components.SettingsComponents.PreferenceSwitch
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview

object SettingsComponents {

    sealed interface SelectOption<T : Enum<T>> {
        val type: T get() = this as T

        interface ResId<T : Enum<T>> : SelectOption<T> {
            @get:StringRes
            val labelRes: Int
        }

        interface String<T : Enum<T>> : SelectOption<T> {
            val label: kotlin.String
        }

        interface Icon<T : Enum<T>> : SelectOption<T> {
            val iconVector: ImageVector
        }
    }

    /**
     * This is a Composable function that creates a SwitchPreference component.
     *
     * @param title The title of the switch preference.
     * @param subtitle The subtitle of the switch preference.
     * @param checked A boolean indicating whether the switch is currently checked.
     * @param onChanged A callback function that is invoked when the switch is toggled.
     */
    @Composable
    fun PreferenceSwitch(title: String, subtitle: String, checked: Boolean, onChanged: () -> Unit) =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChanged)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
            ) {
                Title(value = title)
                Subtitle(value = subtitle)
            }
            Switch(
                checked = checked,
                modifier = Modifier
                    .weight(.1f)
                    .padding(end = 16.dp),
                onCheckedChange = { onChanged() },
            )
        }

    @Composable
    fun PreferenceSwitchLeft(title: String, subtitle: String, checked: Boolean, onChanged: () -> Unit) =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChanged)
                .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = checked,
                onCheckedChange = { onChanged() },
            )
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Title(value = title)
                Subtitle(value = subtitle)
            }
        }


    /**
     * This is a Composable function that creates a SelectPreference component.
     *
     * @param T The type of the SelectOption.
     * @param title The title of the preference.
     * @param selected The currently selected option.
     * @param options An array of all available options.
     * @param onSelected A callback function that is invoked when an option is selected.
     */
    @Composable
    fun <T : SelectOption<T>> PreferenceSelector(
        title: String, selected: T, options: Array<T>, onSelected: (T) -> Unit
    ) = Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = options.indexOf(selected),
            initialFirstVisibleItemScrollOffset = -500
        )

        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        LazyRow(
            state = listState,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        ) {
            items(items = options, key = { when(it) {
                is SelectOption.ResId<*> -> it.labelRes
                is SelectOption.String<*> -> it.label
                is SelectOption.Icon<*> -> it.iconVector.hashCode()
                else -> throw IllegalArgumentException("Unknown SelectOption type")
            } }) { item ->
                SelectOption(
                    title = when(item) {
                        is SelectOption.ResId<*> -> stringResource(item.labelRes)
                        is SelectOption.String<*> -> item.label
                        else -> null
                    },
                    selected = item == selected,
                    onSelected = { onSelected(item) },
                    icon = (item as? SelectOption.Icon<*>)?.iconVector
                )
            }
        }
    }


    /**
     * This is a Composable function that creates an ActionPreference component.
     * @param title The title of the action.
     * @param icon The icon of the action.
     * @param onClick A callback function that is invoked when the action is clicked.
     * @param subtitle The subtitle of the action. It is optional.
     */
    @Composable
    fun PreferenceActionLink(
        title: String,
        icon: ImageVector,
        onClick: () -> Unit,
        subtitle: String? = null,
    ) = Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
    ) {
        if (subtitle == null) {
            Title(value = title)
        } else {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
            ) {
                Title(value = title)
                Subtitle(value = subtitle)
            }
        }
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.requiredSize(28.dp)
        )
    }

    @Composable
    fun PreferenceActionLinkLeft(
        title: String,
        icon: ImageVector,
        onClick: () -> Unit,
        subtitle: String? = null,
    ) = Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.requiredSize(24.dp)
        )
        if (subtitle == null) {
            Title(value = title)
        } else {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
            ) {
                Title(value = title)
                Subtitle(value = subtitle)
            }
        }
    }


    /**
     * This is a Composable function that creates a Title component.
     * @param value The text of the title.
     * @param modifier The modifier of the title. It is optional.
     */
    @Composable
    private fun Title(value: String, modifier: Modifier = Modifier) = Text(
        text = value,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )


    /**
     * This is a Composable function that creates a Subtitle component.
     * @param value The text of the subtitle.
     */
    @Composable
    private fun Subtitle(value: String) = Text(
        text = value,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium
    )


    /**
     * This is a Composable function that creates a SelectOption component.
     *
     * @param title The title of the option.
     * @param selected A boolean indicating whether the option is currently selected.
     * @param onSelected A callback function that is invoked when the option is selected.
     * @param icon The icon of the option. It is optional.
     */
    @Composable
    private fun SelectOption(
        selected: Boolean, onSelected: () -> Unit, title: String?, icon: ImageVector? = null
    ) {
        val color by animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSecondaryContainer, label = ""
        )
        val containerColor by animateColorAsState(
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondaryContainer, label = ""
        )
        val iconTintColor by animateColorAsState(
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondary, label = ""
        )

        FilledTonalButton(
            onClick = onSelected,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor, contentColor = color
            ),
            content = {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTintColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        maxLines = 1
                    )
                }
            }
        )
    }
}


@ThemePreview
@Composable
private fun PreviewSwitchPreferenceOn() {
    PreviewWrapper {
        PreferenceSwitch(title = "Advanced Mode",
            subtitle = "Display secondary data such as octave and frequency",
            checked = true,
            onChanged = {})
    }
}

@ThemePreview
@Composable
private fun PreviewSwitchPreferenceOff() {
    PreviewWrapper {
        PreferenceSwitch(title = "Noise Suppressor",
            subtitle = "Removes Background Noise",
            checked = false,
            onChanged = {})
    }
}

@ThemePreview
@Composable
private fun PreviewSelectPreferenceNotation() {
    PreviewWrapper {
        PreferenceSelector(
            title = "Notation",
            selected = Settings.StringLayout.LIST,
            options = Settings.StringLayout.entries.toTypedArray(),
            onSelected = {})
    }
}

@ThemePreview
@Composable
private fun PreviewActionPreference() {
    PreviewWrapper {
        PreferenceActionLink(title = stringResource(R.string.about),
            icon = Icons.Outlined.Person,
            onClick = {})
    }
}

@ThemePreview
@Composable
private fun PreviewActionPreferenceSubtitle() {
    PreviewWrapper {
        PreferenceActionLink(
            title = stringResource(R.string.about),
            icon = Icons.Default.Person,
            onClick = {},
            subtitle = "Supporting line text lorem ipsum dolor sit amet, consectetur."
        )
    }
}
