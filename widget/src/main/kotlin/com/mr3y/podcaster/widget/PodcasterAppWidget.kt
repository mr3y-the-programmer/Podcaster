package com.mr3y.podcaster.widget

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil3.Bitmap
import coil3.BitmapImage
import coil3.Image
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.size.Scale
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.PlayingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PodcasterAppWidget(
    private val podcastsRepository: PodcastsRepository,
    private val logger: Logger
) : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(Small, Normal))

    enum class SizeBucket { Invalid, Narrow, Normal }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            val currentlyPlayingEpisode by podcastsRepository.getCurrentlyPlayingEpisode().collectAsState(initial = null)
            val sizeBucket = calculateSizeBucket()

            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    GlanceTheme.colors
                else
                    ColorProviders(light = LightColors, dark = DarkColors)
            ) {
                when (sizeBucket) {
                    SizeBucket.Invalid -> InvalidSizeUI()
                    SizeBucket.Narrow -> {
                        WidgetShallowSize(
                            playingStatus = currentlyPlayingEpisode?.playingStatus,
                            onPlayClick = { },
                            onPauseClick = { },
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                    SizeBucket.Normal -> {
                        WidgetNormalSize(
                            activeEpisode = currentlyPlayingEpisode,
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetNormalSize(
        activeEpisode: CurrentlyPlayingEpisode?,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val context = LocalContext.current.applicationContext
        Scaffold(
            backgroundColor = GlanceTheme.colors.surface,
            modifier = modifier.clickable {
                /*val launcherIntent = context.packageManager.getLaunchIntentForPackage("com.mr3y.podcaster")
                if (launcherIntent != null) {
                    context.startActivity(launcherIntent)
                }*/
                actionStartActivity<ComponentActivity>()
            }
        ) {
            if (activeEpisode == null) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = context.getString(R.string.no_episode_playing),
                        style = TextStyle(fontSize = 16.sp, color = GlanceTheme.colors.onSurface),
                    )
                }
            } else {
                Row(
                    modifier = GlanceModifier.padding(vertical = 8.dp).fillMaxSize(),
                ) {
                    AsyncImage(
                        model = activeEpisode.episode.artworkUrl,
                        contentDescription = null,
                        modifier = GlanceModifier.fillMaxHeight()
                    )

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Column(
                        modifier = GlanceModifier.defaultWeight()
                    ) {
                        Text(
                            text = activeEpisode.episode.title,
                            style = TextStyle(fontSize = 16.sp, color = GlanceTheme.colors.onSurface),
                            maxLines = 3,
                            modifier = GlanceModifier.defaultWeight(),
                        )
                        Text(
                            text = activeEpisode.episode.podcastTitle ?: "",
                            style = TextStyle(fontSize = 14.sp, color = GlanceTheme.colors.onSurfaceVariant),
                            maxLines = 2,
                            modifier = GlanceModifier.defaultWeight(),
                        )

                        ControlButtons(
                            playingStatus = activeEpisode.playingStatus,
                            onPlayClick = { },
                            onPauseClick = {},
                            onGoToPreviousClick = {},
                            onGoToNextClick = {},
                            modifier = GlanceModifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetShallowSize(
        playingStatus: PlayingStatus?,
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        modifier: GlanceModifier = GlanceModifier,
    ) {
        Scaffold(
            backgroundColor = GlanceTheme.colors.surface,
            modifier = modifier.clickable {
                actionStartActivity<ComponentActivity>()
            }
        ) {
            val context = LocalContext.current
            if (playingStatus == null) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = context.getString(R.string.no_episode_playing),
                        style = TextStyle(fontSize = 16.sp, color = GlanceTheme.colors.onSurface),
                    )
                }
            } else {
                val icon = if (playingStatus == PlayingStatus.Paused) R.drawable.outline_play_arrow_24 else R.drawable.outline_pause_24
                SquareIconButton(
                    imageProvider = ImageProvider(icon),
                    contentDescription = null,
                    onClick = if (playingStatus == PlayingStatus.Paused) onPlayClick else onPauseClick
                )
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    private fun AsyncImage(
        model: Any,
        contentDescription: String?,
        modifier: GlanceModifier = GlanceModifier
    ) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        val context = LocalContext.current

        LaunchedEffect(key1 = model) {
            val request = ImageRequest.Builder(context)
                .data(model)
                .size(200, 200)
                .scale(Scale.FILL)
                .target { image: Image ->
                    bitmap = (image as BitmapImage).bitmap
                }
                .build()

            launch(Dispatchers.IO) {
                val result = ImageLoader(context).execute(request)
                if (result is ErrorResult) {
                    val t = result.throwable
                    logger.e(t, tag = TAG) { "Image Request Error:" }
                }
            }
        }

        bitmap?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = contentDescription,
                contentScale = ContentScale.FillBounds,
                modifier = modifier.cornerRadius(12.dp),
            )
        }
    }

    @Composable
    private fun ControlButtons(
        playingStatus: PlayingStatus,
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        onGoToPreviousClick: () -> Unit,
        onGoToNextClick: () -> Unit,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.outline_skip_prev_24),
                contentDescription = null,
                onClick = onGoToPreviousClick
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            val icon = if (playingStatus == PlayingStatus.Paused) R.drawable.outline_play_arrow_24 else R.drawable.outline_pause_24
            CircleIconButton(
                imageProvider = ImageProvider(icon),
                contentDescription = null,
                onClick = if (playingStatus == PlayingStatus.Paused) onPlayClick else onPauseClick
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.outline_skip_next_24),
                contentDescription = null,
                onClick = onGoToNextClick
            )
        }
    }

    @Composable
    private fun InvalidSizeUI() {
        Box(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            Text(
                text = context.getString(R.string.invalid_size),
                style = TextStyle(fontSize = 14.sp, color = GlanceTheme.colors.onSurface),
            )
        }
    }

    @Composable
    private fun calculateSizeBucket(): SizeBucket {
        val size: DpSize = LocalSize.current
        val width = size.width

        return when {
            width < Small.width -> SizeBucket.Invalid
            width <= Normal.width -> SizeBucket.Narrow
            else -> SizeBucket.Normal
        }
    }

    companion object {
        const val TAG = "PodcasterAppWidget"
        val Small = DpSize(128.dp, 48.dp)
        val Normal = DpSize(256.dp, 124.dp)
    }
}
