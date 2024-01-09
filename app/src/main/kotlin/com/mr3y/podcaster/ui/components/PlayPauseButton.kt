package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.onTertiaryPrimary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.tertiaryPrimary
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun PlayPauseCompactButton(
    isSelected: Boolean,
    playingStatus: PlayingStatus?,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryPrimary,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryPrimary,
    contentPadding: Dp = 8.dp,
    iconSize: Dp = 24.dp
) {
    if (!isSelected || playingStatus == PlayingStatus.Paused || playingStatus == PlayingStatus.Error) {
        IconButton(
            onClick = onPlay,
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .padding(contentPadding),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
    } else {
        OutlinedIconButton(
            onClick = onPause,
            modifier = modifier
                .size(48.dp)
                .padding(contentPadding),
            shape = CircleShape,
            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = containerColor)
        ) {
            if (playingStatus == PlayingStatus.Playing) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                CircularProgressIndicator(
                    color = containerColor,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun PlayPauseExpandedButton(
    isSelected: Boolean,
    playingStatus: PlayingStatus?,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    durationInSec: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryTertiary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryTertiary
) {
    ElevatedButton(
        onClick = {
            if (!isSelected || playingStatus == PlayingStatus.Paused || playingStatus == PlayingStatus.Error) {
                onPlay()
            } else {
                onPause()
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 24.dp,
            bottom = 8.dp
        ),
        modifier = modifier
    ) {
        if (isSelected && (playingStatus == PlayingStatus.Playing || playingStatus == PlayingStatus.Loading)) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp)
            )
            val label = if (playingStatus == PlayingStatus.Loading) "Loading" else "Playing"
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = "${durationInSec.toDuration(DurationUnit.SECONDS)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

class IsSelectedParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

@PodcasterPreview
@Composable
fun PlayPauseCompactButtonPreview(
    @PreviewParameter(IsSelectedParameterProvider::class) isSelected: Boolean
) {
    PodcasterTheme(dynamicColor = false) {
        PlayPauseCompactButton(
            isSelected = isSelected,
            playingStatus = PlayingStatus.Playing,
            onPlay = {},
            onPause = {}
        )
    }
}

@PodcasterPreview
@Composable
fun PlayPauseExpandedButtonPreview(
    @PreviewParameter(IsSelectedParameterProvider::class) isSelected: Boolean
) {
    PodcasterTheme(dynamicColor = false) {
        PlayPauseExpandedButton(
            isSelected = isSelected,
            playingStatus = PlayingStatus.Playing,
            onPlay = {},
            onPause = {},
            durationInSec = 888
        )
    }
}
