package com.mr3y.podcaster.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.sampledata.EpisodeWithDetails
import com.mr3y.podcaster.ui.components.MoveToNextButton
import com.mr3y.podcaster.ui.components.MoveToPreviousButton
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import java.text.DecimalFormat
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val twoDigitsFormatter = DecimalFormat("00")

@Composable
fun ExpandedPlayerView(
    currentlyPlayingEpisode: CurrentlyPlayingEpisode,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onForward: (Int) -> Unit,
    onReplay: (Int) -> Unit,
    onPlaybackSpeedChange: (oldSpeed: Float) -> Float,
    progress: Int,
    onSeeking: (Int) -> Unit,
    isSeekingToNextEnabled: Boolean,
    isSeekingToPreviousEnabled: Boolean,
    onSeekToNext: () -> Unit,
    onSeekToPrevious: () -> Unit,
    onBack: () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val (episode, playingStatus, playbackSpeed) = currentlyPlayingEpisode
    BackHandler(onBack = onBack)
    with(sharedTransitionScope) {
        Scaffold(
            containerColor = containerColor,
            modifier = modifier.sharedBoundsIfNotNull(
                sharedTransitionScope = this,
                animatedVisibilityScope = animatedVisibilityScope,
                state = this?.rememberSharedContentState(key = SharedTransitionElementKey.RootBounds)
            ),
        ) { contentPadding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(top = 48.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                AnimatedContent(
                    targetState = episode,
                    transitionSpec = {
                        (
                                fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
                                )
                            .togetherWith(fadeOut(animationSpec = tween(90)) + scaleOut(animationSpec = tween(90)))
                    },
                    contentKey = { it.id },
                    label = "Animated Episode details",
                ) { targetEpisode ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val sharedArtworkTransitionKey = SharedTransitionElementKey.Artwork(targetEpisode.id).hashCode().toString()
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(targetEpisode.artworkUrl)
                                .placeholderMemoryCacheKey(sharedArtworkTransitionKey)
                                .memoryCacheKey(sharedArtworkTransitionKey)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .sharedElementIfNotNull(
                                    sharedTransitionScope = this@with,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    state = this@with?.rememberSharedContentState(key = sharedArtworkTransitionKey)
                                )
                                .size(360.dp),
                        )
                        Text(
                            text = targetEpisode.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.skipToLookaheadSizeIfNotNull(this@with)
                        )

                        val strings = LocalStrings.current
                        Text(
                            text = if (playingStatus == PlayingStatus.Loading) {
                                strings.buffering_playback
                            } else {
                                targetEpisode.podcastTitle ?: ""
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Slider(
                    value = episode.durationInSec?.let { progress.toFloat().div(it.toFloat()) } ?: 1f,
                    onValueChange = { updatedValue ->
                        episode.durationInSec?.let {
                            onSeeking((updatedValue * it).toInt())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primaryTertiary,
                        activeTrackColor = MaterialTheme.colorScheme.primaryTertiary,
                        activeTickColor = MaterialTheme.colorScheme.onPrimaryTertiary.copy(alpha = 0.38f),
                        inactiveTrackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.45f)
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = progress.formatAsDuration(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                    Text(
                        text = episode.durationInSec.takeIf { it != null && it > 30 }.formatAsDuration(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    MoveToPreviousButton(
                        onClick = onSeekToPrevious,
                        isEnabled = isSeekingToPreviousEnabled,
                        modifier = Modifier.size(56.dp),
                        iconSize = 32.dp,
                    )

                    OutlinedIconButton(
                        onClick = { onReplay(10) },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.inverseSurface,
                        ),
                        border = null,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Replay10,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                    }

                    IconButton(
                        onClick = {
                            if (playingStatus == PlayingStatus.Paused || playingStatus == PlayingStatus.Error) {
                                onResume()
                            } else {
                                onPause()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryTertiary,
                            contentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
                        ),
                    ) {
                        when (playingStatus) {
                            PlayingStatus.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimaryTertiary,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                            PlayingStatus.Playing -> {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                )
                            }
                            PlayingStatus.Paused, PlayingStatus.Error -> {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                )
                            }
                        }
                    }

                    OutlinedIconButton(
                        onClick = { onForward(30) },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.inverseSurface,
                        ),
                        border = null,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Forward30,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                    }

                    MoveToNextButton(
                        onClick = onSeekToNext,
                        isEnabled = isSeekingToNextEnabled,
                        modifier = Modifier.size(56.dp),
                        iconSize = 32.dp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    var currentSpeed by remember(currentlyPlayingEpisode) { mutableFloatStateOf(playbackSpeed) }
                    AnimatedContent(
                        targetState = currentSpeed,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { it } + fadeIn(animationSpec = tween(220, delayMillis = 90))) togetherWith (slideOutHorizontally { -it } + fadeOut(animationSpec = tween(90)))
                            } else {
                                (slideInHorizontally { -it } + fadeIn(animationSpec = tween(220, delayMillis = 90))) togetherWith (slideOutHorizontally { it } + fadeOut(animationSpec = tween(90)))
                            }
                        },
                        label = "Animated Playback Speed",
                    ) { targetState ->
                        TextButton(
                            onClick = {
                                currentSpeed = onPlaybackSpeedChange(targetState)
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.inverseSurface),
                        ) {
                            Text(
                                text = "${targetState}x",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsedPlayerView(
    currentlyPlayingEpisode: CurrentlyPlayingEpisode,
    onResume: () -> Unit,
    onPause: () -> Unit,
    progress: Int,
    contentWindowInsets: WindowInsets,
    containerColor: Color,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val (episode, playingStatus) = currentlyPlayingEpisode
    with(sharedTransitionScope) {
        Card(
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = modifier.sharedBoundsIfNotNull(
                sharedTransitionScope = this,
                animatedVisibilityScope = animatedVisibilityScope,
                state = this?.rememberSharedContentState(key = SharedTransitionElementKey.RootBounds)
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(contentWindowInsets.asPaddingValues())
                    .fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    val sharedArtworkTransitionKey = SharedTransitionElementKey.Artwork(episode.id).hashCode().toString()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(episode.artworkUrl)
                            .placeholderMemoryCacheKey(sharedArtworkTransitionKey)
                            .memoryCacheKey(sharedArtworkTransitionKey)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .sharedElementIfNotNull(
                                sharedTransitionScope = this@with,
                                animatedVisibilityScope = animatedVisibilityScope,
                                state = this@with?.rememberSharedContentState(key = sharedArtworkTransitionKey)
                            )
                            .clip(MaterialTheme.shapes.small)
                            .size(64.dp),
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        val strings = LocalStrings.current
                        Text(
                            text = strings.currently_playing,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = episode.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Normal,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                        )
                    }

                    PlayPauseCompactButton(
                        isSelected = true,
                        playingStatus = playingStatus,
                        onPlay = onResume,
                        onPause = onPause,
                        contentPadding = 0.dp,
                        iconSize = 32.dp,
                    )
                }
                val progressPercentage = episode.durationInSec?.let {
                    (progress.toFloat() / it.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                } ?: 1f
                val progressColor = MaterialTheme.colorScheme.primaryTertiary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.45f))
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = progressColor,
                                size = size.copy(width = size.width * progressPercentage),
                            )
                        },
                )
            }
        }
    }
}

private fun Modifier.sharedElementIfNotNull(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    state: SharedTransitionScope.SharedContentState?,
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null)
        this
    else
        with(sharedTransitionScope) {
            sharedElement(state, animatedVisibilityScope)
        }
}

private fun Modifier.sharedBoundsIfNotNull(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    state: SharedTransitionScope.SharedContentState?,
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null)
        this
    else
        with(sharedTransitionScope) {
            sharedBounds(state, animatedVisibilityScope)
        }
}

private fun Modifier.skipToLookaheadSizeIfNotNull(
    sharedTransitionScope: SharedTransitionScope?
): Modifier {
    return if (sharedTransitionScope == null)
        this
    else
        with(sharedTransitionScope) {
            skipToLookaheadSize()
        }
}

private sealed interface SharedTransitionElementKey {

    data object RootBounds : SharedTransitionElementKey

    data class Artwork(val episodeId: Long) : SharedTransitionElementKey
}

private fun Int?.formatAsDuration(): String {
    if (this == null) {
        return "--:--"
    }
    return toDuration(DurationUnit.SECONDS).toComponents { hours, minutes, seconds, _ ->
        val minutesFormatted = twoDigitsFormatter.format(minutes)
        val secondsFormatted = twoDigitsFormatter.format(seconds)
        when {
            hours > 0 -> "$hours:$minutesFormatted:$secondsFormatted"
            minutes > 0 -> "$minutesFormatted:$secondsFormatted"
            else -> "00:$secondsFormatted"
        }
    }
}

@PodcasterPreview
@Composable
fun ExpandedPlayerViewPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        ExpandedPlayerView(
            currentlyPlayingEpisode = CurrentlyPlayingEpisode(
                episode = EpisodeWithDetails,
                playingStatus = PlayingStatus.Paused,
                playingSpeed = 1.0f,
            ),
            onResume = {},
            onPause = {},
            isSeekingToPreviousEnabled = true,
            isSeekingToNextEnabled = false,
            onSeekToNext = {},
            onSeekToPrevious = {},
            onForward = {},
            onReplay = {},
            onPlaybackSpeedChange = { it },
            progress = 1150,
            onSeeking = {},
            onBack = {},
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun CollapsedPlayerViewPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        CollapsedPlayerView(
            currentlyPlayingEpisode = CurrentlyPlayingEpisode(
                episode = EpisodeWithDetails,
                playingStatus = PlayingStatus.Paused,
                playingSpeed = 1.0f,
            ),
            onResume = {},
            onPause = {},
            progress = 1450,
            contentWindowInsets = WindowInsets.navigationBars,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(16.dp),
        )
    }
}
