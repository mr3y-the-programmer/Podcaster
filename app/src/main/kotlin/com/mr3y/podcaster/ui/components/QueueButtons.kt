package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun AddToQueueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.tertiaryPrimary,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = null,
        )
    }
}

@Composable
fun RemoveFromQueueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.tertiaryPrimary,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAddCheck,
            contentDescription = null,
        )
    }
}

@Composable
fun MoveToNextButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.inverseSurface,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        enabled = isEnabled,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContainerColor = Color.Transparent,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = null,
        )
    }
}

@Composable
fun MoveToPreviousButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.inverseSurface,
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContainerColor = Color.Transparent,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = null,
        )
    }
}

@PodcasterPreview
@Composable
fun AddToQueueButtonPreview() {
    PodcasterTheme(dynamicColor = false) {
        AddToQueueButton(onClick = { })
    }
}

@PodcasterPreview
@Composable
fun RemoveFromQueueButtonPreview() {
    PodcasterTheme(dynamicColor = false) {
        RemoveFromQueueButton(onClick = { })
    }
}

@PodcasterPreview
@Composable
fun MoveToNextButtonPreview() {
    PodcasterTheme(dynamicColor = false) {
        MoveToNextButton(onClick = { }, isEnabled = true)
    }
}

@PodcasterPreview
@Composable
fun MoveToPreviousButtonPreview() {
    PodcasterTheme(dynamicColor = false) {
        MoveToPreviousButton(onClick = { }, isEnabled = true)
    }
}
