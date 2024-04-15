package com.mr3y.podcaster.ui.presenter

import android.net.Uri
import androidx.media3.common.AdPlaybackState
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.test.utils.FakeMediaSource
import androidx.media3.test.utils.FakeTimeline
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.mr3y.podcaster.core.data.internal.DefaultPodcastsRepository
import com.mr3y.podcaster.core.local.dao.DefaultPodcastsDao
import com.mr3y.podcaster.core.local.dao.DefaultRecentSearchesDao
import com.mr3y.podcaster.core.local.di.FakeDatabaseModule
import com.mr3y.podcaster.core.logger.TestLogger
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.network.di.FakeHttpClient
import com.mr3y.podcaster.core.network.internal.DefaultPodcastIndexClient
import com.mr3y.podcaster.core.sampledata.Episodes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], manifest = Config.NONE)
class PodcasterAppStateIntegrationTest {

    private val internalPlayer = TestExoPlayerBuilder(ApplicationProvider.getApplicationContext()).build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(testDispatcher)
    private val database = FakeDatabaseModule.provideInMemoryDatabaseInstance()
    private val httpClient = FakeHttpClient.getInstance()
    private val podcastsRepo = DefaultPodcastsRepository(
        podcastsDao = DefaultPodcastsDao(database, testDispatcher),
        recentSearchesDao = DefaultRecentSearchesDao(database, testDispatcher),
        networkClient = DefaultPodcastIndexClient(httpClient, TestLogger()),
    )

    private val podcasterAppState = PodcasterAppState(
        podcastsRepository = podcastsRepo,
        applicationScope = scope.backgroundScope
    )

    @Test
    fun `test manipulating queue operations`() {
        // Setup - prepare any mock data and listen for current episode updates
        podcasterAppState.currentlyPlayingEpisode.launchIn(scope.backgroundScope)
        // Add 5 episodes to the local playlist and play the episode in the middle of the playlist.
        Episodes.take(5).forEach { episode ->
            podcastsRepo.addEpisodeToQueue(episode)
        }
        val playlist = podcastsRepo.getQueueEpisodes()
        val episodeInMiddle = playlist[playlist.size / 2]
        val currentEpisode = CurrentlyPlayingEpisode(episodeInMiddle, PlayingStatus.Playing, 1.0f)
        podcastsRepo.setCurrentlyPlayingEpisode(currentEpisode)
        assertThat(podcasterAppState.currentlyPlayingEpisode.value).isNotNull().isEqualTo(currentEpisode)

        internalPlayer.setMediaSource(createFakeMediaSourceForEpisode(episodeInMiddle))
        assertThat(internalPlayer.mediaItemCount).isEqualTo(1)
        assertThat(internalPlayer.currentMediaItem?.mediaId).isNotNull().isEqualTo(episodeInMiddle.id.toString())
        assertThat(internalPlayer.currentMediaItemIndex).isEqualTo(0)

        // when syncing local playlist with player's playlist
        with(podcasterAppState) {
            internalPlayer.maybeAddQueueEpisodes()
            // then assert the player's playlist state is updated correctly and in the correct order
            assertThat(internalPlayer.mediaItemCount).isEqualTo(5)
            assertThat(internalPlayer.currentMediaItem?.mediaId).isNotNull().isEqualTo(episodeInMiddle.id.toString())
            assertThat(internalPlayer.currentMediaItemIndex).isEqualTo(2)
            repeat(5) { i ->
                assertThat(internalPlayer.getMediaItemAt(i).mediaId).isEqualTo(playlist[i].id.toString())
            }
        }
    }

    @Test
    fun `test replacing currently playing episode doesn't clear playlist`() {
        // Add 5 episodes to the playlist and seek to the episode in the middle.
        val playlist = Episodes.take(5).map { episode -> createFakeMediaSourceForEpisode(episode) }
        internalPlayer.setMediaSources(playlist)
        assertThat(internalPlayer.mediaItemCount).isEqualTo(5)
        assertThat(internalPlayer.currentMediaItemIndex).isEqualTo(0)

        internalPlayer.seekToNextMediaItem()
        internalPlayer.seekToNextMediaItem()

        assertThat(internalPlayer.currentMediaItemIndex).isEqualTo(2)

        with(podcasterAppState) {
            internalPlayer.setMediaItemForEpisode(Episodes[5])

            assertThat(internalPlayer.mediaItemCount).isEqualTo(5)
            assertThat(internalPlayer.currentMediaItemIndex).isEqualTo(2)
        }
    }

    private fun createFakeMediaSourceForEpisode(episode: Episode): MediaSource {
        val mediaItem = MediaItem.Builder()
            .setMediaId(episode.id.toString())
            .setUri(Uri.Builder().encodedPath(episode.enclosureUrl).build())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(episode.title)
                    .setArtist(episode.podcastTitle)
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setArtworkUri(Uri.parse(episode.artworkUrl))
                    .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                    .build(),
            )
            .build()

        return FakeMediaSource(
            FakeTimeline(
                FakeTimeline.TimelineWindowDefinition(
                    /* periodCount = */
                    1,
                    /* id = */
                    mediaItem.mediaId,
                    /* isSeekable = */
                    true,
                    /* isDynamic = */
                    false,
                    /* isLive = */
                    false,
                    /* isPlaceholder = */
                    false,
                    /* durationUs = */
                    TimeUnit.SECONDS.toMicros(episode.durationInSec?.toLong() ?: 100_000L),
                    /* defaultPositionUs = */
                    0,
                    /* windowOffsetInFirstPeriodUs = */
                    0,
                    /* adPlaybackStates = */
                    listOf(AdPlaybackState.NONE),
                    /* mediaItem = */
                    mediaItem,
                ),
            ),
        )
    }
}
