package com.mr3y.podcaster.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.EpisodeWithDetails
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.onTertiaryPrimary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun ExpandedPlayerViewScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        // TODO: set the color to a gradient of artwork's dominant color & transparent.
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(top = 48.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            AsyncImage(
                model = EpisodeWithDetails.artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(360.dp)
            )
            Text(
                text = EpisodeWithDetails.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = EpisodeWithDetails.podcastTitle ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = 0.5f,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primaryTertiary,
                    activeTrackColor = MaterialTheme.colorScheme.primaryTertiary,
                    activeTickColor = MaterialTheme.colorScheme.onPrimaryTertiary.copy(alpha = 0.38f)
                )
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "00:00",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Text(
                    text = "53:36",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.inverseSurface)
                ) {
                    Text(
                        text = "1.0x",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedIconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    border = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay10,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryTertiary,
                        contentColor = MaterialTheme.colorScheme.onPrimaryTertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedIconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    border = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Forward30,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollapsedPlayerView(
    currentlyPlayingEpisode: CurrentlyPlayingEpisode,
    onResume: () -> Unit,
    onPause: () -> Unit,
    contentWindowInsets: WindowInsets,
    modifier: Modifier = Modifier
) {
    val (episode, playingStatus) = currentlyPlayingEpisode
    Card(
        shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(contentWindowInsets)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = episode.artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .size(64.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                val strings = LocalStrings.current
                Text(
                    text = strings.currently_playing,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = episode.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
            }
            if (playingStatus == PlayingStatus.Paused || playingStatus == PlayingStatus.Error) {
                IconButton(
                    onClick = onResume,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryPrimary,
                        contentColor = MaterialTheme.colorScheme.onTertiaryPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                OutlinedIconButton(
                    onClick = onPause,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.tertiaryPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@PodcasterPreview
@Composable
fun ExpandedPlayerViewScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        ExpandedPlayerViewScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@PodcasterPreview
@Composable
fun CollapsedPlayerViewPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        CollapsedPlayerView(
            currentlyPlayingEpisode = CurrentlyPlayingEpisode(
                episode = EpisodeWithDetails,
                playingStatus = PlayingStatus.Paused
            ),
            onResume = {},
            onPause = {},
            contentWindowInsets = WindowInsets.navigationBars,
            modifier = Modifier.padding(16.dp)
        )
    }
}
