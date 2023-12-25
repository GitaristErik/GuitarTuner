package com.example.guitartuner.ui.tuner.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.settings.Settings.TunerDisplayType
import com.example.guitartuner.domain.entity.tuner.Tuner
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.LargeFontPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import kotlin.math.abs
import kotlin.math.sign

/**
 * UI component consisting of a visual meter and
 * text label displaying the current tuning [offset][deviation].
 *
 * @param deviation The offset between the currently playing note and the selected string.
 * @param displayType Type of tuning offset value to display.
 */
@Composable
fun TuningDisplay(
    deviation: Double?, isTuned: Boolean, displayType: TunerDisplayType
) {
    // colors
    val colorProcessing = MaterialTheme.colorScheme.tertiary
    val colorGood = MaterialTheme.colorScheme.primary
    val colorBad = MaterialTheme.colorScheme.error

    val colorProcessingBack = MaterialTheme.colorScheme.tertiaryContainer
    val colorGoodBack = MaterialTheme.colorScheme.primaryContainer
    val colorBadBack = MaterialTheme.colorScheme.errorContainer

    val colorOnBack = MaterialTheme.colorScheme.onSurface
    val colorBack = MaterialTheme.colorScheme.outlineVariant

    // Calculate meter position.
    val meterPosition by animateFloatAsState(
        targetValue = remember(deviation) {
            derivedStateOf {
                // (noteOffset.toFloat() / 4f).coerceIn(-1f..1f)
                deviation?.toFloat()?.coerceIn(-1f..1f) ?: 0f
            }
        }.value, label = "Tuning Meter Position"
    )
    val absPosition = abs(meterPosition)

    val (targetColor, targetBackgroundColor) = remember(absPosition) {
        derivedStateOf {
            if (absPosition != 0f) {
                // Gradient from green to red based on offset.
                if (absPosition < 0.5) {
                    lerp(colorGood, colorProcessing, absPosition * 2f) to lerp(
                        colorGoodBack,
                        colorProcessingBack,
                        absPosition * 2f
                    )

                } else {
                    lerp(colorProcessing, colorBad, (absPosition - 0.5f) * 2f) to lerp(
                        colorProcessingBack,
                        colorBadBack,
                        (absPosition - 0.5f) * 2f
                    )
                }
            } else {
//                colorOnBack to colorBack
                // Listening color
                colorOnBack.copy(alpha = 0.85f)
                    .compositeOver(colorBack) to colorOnBack.copy(alpha = 0.1f)
                    .compositeOver(colorBack)

            }
        }
    }.value

    // Calculate colour of meter and label.
    val color by animateColorAsState(
        targetValue = targetColor, label = "Tuning Meter Color"
    )
    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor, label = "Tuning Meter Background Color"
    )

//    val inTune = deviation != null && abs(deviation) < Tuner.TUNED_OFFSET_THRESHOLD

    val indicatorSize by animateFloatAsState(
        targetValue = if (isTuned) 1f else 2 / 180f,
        animationSpec = if (isTuned) tween(Tuner.TUNED_SUSTAIN_TIME - 50, 50) else spring(),
//        finishedListener = { if (it == 1f) { onTuned() } },
        label = "Tuning Indicator Size"
    )

    // Content
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AccidentalIcon(R.drawable.music_accidental_flat, contentDescription = "Flat")
        TuningMeter(
            indicatorPosition = meterPosition,
            indicatorSize = indicatorSize,
            color = color,
            backgroundColor = backgroundColor
        ) {
            TuningMeterLabel(noteOffset = deviation, color = color, displayType = displayType)
        }
        AccidentalIcon(R.drawable.music_accidental_sharp, contentDescription = "Sharp")
    }
}

/**
 * Meter visually displaying the current tuning offset.
 *
 * @param indicatorPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
 * @param indicatorSize Size of the indicator as a percentage value from 0.0 (no width) to 1.0 (full width of meter).
 * @param color Color of the indicator.
 * @param backgroundColor Color of the track
 * @param labelContent Label to display inside the meter arc.
 */
@Composable
private fun TuningMeter(
    indicatorPosition: Float,
    indicatorSize: Float,
    color: Color,
    backgroundColor: Color,
    labelContent: @Composable () -> Unit
) {
    val dirIndicatorPos = when (LocalLayoutDirection.current) {
        LayoutDirection.Ltr -> indicatorPosition
        LayoutDirection.Rtl -> -indicatorPosition
    }

    Column(
        modifier = Modifier
            .defaultMinSize(210.dp, 116.dp)
            .drawBehind {
                drawMeter(
                    indicatorColor = color,
                    trackColor = backgroundColor,
                    indicatorPosition = dirIndicatorPos,
                    indicatorSize = indicatorSize
                )
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        labelContent()
    }
}

/**
 * Draws a circular meter with a variable-size indicator and background track.
 *
 * @param indicatorColor Color of the indicator.
 * @param trackColor Color of the background track.
 * @param indicatorPosition Position of the indicator on the track, as a percentage value from -1.0 (leftmost) to 1.0 (rightmost).
 * @param indicatorSize Size of the indicator as a percentage value from 0.0 (no width) to 1.0 (full width of meter).
 */
private fun DrawScope.drawMeter(
    indicatorColor: Color,
    trackColor: Color,
    indicatorPosition: Float,
    indicatorSize: Float,
) {
    // Arc size
    val strokeWidth = 20.dp.toPx()
    val arcSize =
        size.copy(height = size.height * 2 - strokeWidth * 2, width = size.width - strokeWidth)
    val offset = Offset(strokeWidth / 2, strokeWidth / 2)

    // Background Track
    drawArc(
        color = trackColor,
        startAngle = -180f,
        sweepAngle = 180f,
        size = arcSize,
        topLeft = offset,
        style = Stroke(
            width = strokeWidth, cap = StrokeCap.Round
        ),
        useCenter = false
    )

    // Indicator
    val startAngle = -90f
    val indicatorSpan = indicatorSize * 180f
    val indicatorAngle = indicatorPosition * (90f - (indicatorSpan / 2)) - (indicatorSpan / 2)
    drawArc(
        color = indicatorColor,
        startAngle = startAngle + indicatorAngle,
        sweepAngle = indicatorSpan,
        size = arcSize,
        topLeft = offset,
        style = Stroke(
            width = strokeWidth, cap = StrokeCap.Round
        ),
        useCenter = false
    )
}

/**
 * Label displaying the [note offset][noteOffset]
 * and tuning state with the specified [color] and [displayType].
 */
@Composable
private fun TuningMeterLabel(
    noteOffset: Double?, displayType: TunerDisplayType, color: Color
) {
    Spacer(modifier = Modifier.height(24.dp))

    // Listening
    if (noteOffset == null) {
        Icon(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(48.dp),
            tint = color,
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null
        )
        Text(text = stringResource(id = R.string.listening))

        // In Tune
    } else if (abs(noteOffset) < Tuner.TUNED_OFFSET_THRESHOLD) {
        Icon(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(48.dp),
            tint = color,
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null
        )
        Text(text = stringResource(id = R.string.in_tune))

        // Out of Tune
    } else {
        val offset = noteOffset * displayType.multiplier
        val dp = if (displayType == TunerDisplayType.CENTS) 1 else 0 // decimal places

        Text( // Offset Value
            color = color,
            text = "%+.${dp}f".format(offset),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = when (displayType) {
                TunerDisplayType.SIMPLE -> stringResource(
                    id = if (noteOffset.sign > 0) R.string.tune_down else R.string.tune_up
                )

                TunerDisplayType.SEMITONES -> stringResource(id = R.string.semitones)
                TunerDisplayType.CENTS -> stringResource(id = R.string.cents)
            }
        )
    }
}

/**
 * Composable displaying an accidental (sharp or flat) icon.
 * @param icon The icon resource.
 * @param contentDescription Description of the icon for accessibility.
 */
@Composable
private fun AccidentalIcon(
    @DrawableRes icon: Int, contentDescription: String
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.requiredSize(32.dp)
        )
    }
}

// PREVIEWS
@ThemePreview
@Composable
private fun ListeningPreview() {
    PreviewWrapper {
        TuningDisplay(
            deviation = null,
            isTuned = false,
            TunerDisplayType.SEMITONES
        )
    }
}

@ThemePreview
@Composable
private fun InTunePreview() {
    PreviewWrapper {
        TuningDisplay(
            deviation = 0.09, isTuned = true, TunerDisplayType.SEMITONES
        )
    }
}

@ThemePreview
@Composable
private fun ProcessToGoodPreview() {
    Column {
        PreviewWrapper {
            TuningDisplay(
                deviation = 1.07, isTuned = false, TunerDisplayType.SIMPLE
            )
        }
        PreviewWrapper {
            TuningDisplay(
                deviation = -1.67, isTuned = false, TunerDisplayType.SIMPLE
            )
        }
    }
}

@ThemePreview
@Composable
private fun ProcessToBadPreview() {
    Column {
        PreviewWrapper {
            TuningDisplay(
                deviation = 2.17, isTuned = false, TunerDisplayType.SIMPLE
            )
        }
        PreviewWrapper {
            TuningDisplay(
                deviation = -3.07, isTuned = false, TunerDisplayType.SIMPLE
            )
        }
    }
}

@ThemePreview
@Composable
private fun RedPreview() {
    PreviewWrapper {
        TuningDisplay(
            deviation = -27.7, isTuned = false, TunerDisplayType.CENTS
        )
    }
}

@LargeFontPreview
@Composable
private fun LargeFontLabelPreview() {
    PreviewWrapper {
        TuningDisplay(
            deviation = 2.7, isTuned = false, TunerDisplayType.SIMPLE
        )
    }
}

@LargeFontPreview
@Composable
private fun LargeFontIconPreview() {
    PreviewWrapper {
        TuningDisplay(
            deviation = 0.07, isTuned = true, TunerDisplayType.SEMITONES
        )
    }
}
