package com.mr3y.podcaster.service

import android.net.Uri
import androidx.media3.common.AdPlaybackState
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.test.utils.FakeMediaSource
import androidx.media3.test.utils.FakeTimeline
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import assertk.assertions.support.expected
import assertk.assertions.support.show
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
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], manifest = Config.NONE)
class ServiceMediaPlayerTest {

    private val internalPlayer = TestExoPlayerBuilder(ApplicationProvider.getApplicationContext())
        .setMediaSourceFactory(
            mockk {
                every { createMediaSource(any()) } answers {
                    FakeMediaSource(
                        FakeTimeline(
                            FakeTimeline.TimelineWindowDefinition(
                                /* periodCount = */
                                1,
                                /* id = */
                                SampleMediaItem.mediaId,
                                /* isSeekable = */
                                true,
                                /* isDynamic = */
                                false,
                                /* isLive = */
                                false,
                                /* isPlaceholder = */
                                false,
                                /* durationUs = */
                                TimeUnit.SECONDS.toMicros(CorrectEpisodeDurationInSec.toLong()),
                                /* defaultPositionUs = */
                                0,
                                /* windowOffsetInFirstPeriodUs = */
                                0,
                                /* adPlaybackStates = */
                                listOf(AdPlaybackState.NONE),
                                /* mediaItem = */
                                SampleMediaItem,
                            ),
                        ),
                    )
                }
            },
        )
        .build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(testDispatcher)
    private val database = FakeDatabaseModule.provideInMemoryDatabaseInstance()
    private val httpClient = FakeHttpClient.getInstance()
    private val podcastsRepo = DefaultPodcastsRepository(
        podcastsDao = DefaultPodcastsDao(database, testDispatcher),
        recentSearchesDao = DefaultRecentSearchesDao(database, testDispatcher),
        networkClient = DefaultPodcastIndexClient(httpClient, TestLogger()),
    )

    private val mediaPlayer = ServiceMediaPlayer(
        player = internalPlayer,
        podcastsRepository = podcastsRepo
    )

    @Before
    fun setUp() {
        mediaPlayer.setMediaItem(SampleMediaItem)
    }

    @Test
    fun `test listening for state update is working as expected`() = scope.runTest {
        assertThat(mediaPlayer.currentlyPlayingEpisode.value).isNull()

        // Start subscribing to currentlyPlayingEpisode updates
        mediaPlayer.startListeningForUpdatesIn(backgroundScope)

        // When a new Episode comes in, assert we get notified of this.
        val currentEpisode = CurrentlyPlayingEpisode(Episodes[0], PlayingStatus.Loading, 1.0f)

        podcastsRepo.setCurrentlyPlayingEpisode(currentEpisode)
        assertThat(mediaPlayer.currentlyPlayingEpisode.value).isNotNull().isEqualTo(currentEpisode)

        // And any change in the playing status, speed,...etc also got reflected and assert the episode duration is corrected
        mediaPlayer.prepare()
        awaitReady()
        mediaPlayer.play()

        podcastsRepo.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)

        val updatedEpisode = currentEpisode.copy(episode = currentEpisode.episode.copy(durationInSec = CorrectEpisodeDurationInSec), playingStatus = PlayingStatus.Playing)
        assertThat(mediaPlayer.currentlyPlayingEpisode.value).isEqualTo(updatedEpisode)

        // make sure we update the progress of the current playing episode as well as we move forward
        mediaPlayer.seekTo((CorrectEpisodeDurationInSec * 1000L) / 5L)
        awaitReady()

        // TODO: this delay is hacky and should be avoided somehow, find a way to remove this
        delay(1.seconds)
        podcastsRepo.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)

        assertThat(mediaPlayer.currentlyPlayingEpisode.value)
            .isNotNull()
            .prop(CurrentlyPlayingEpisode::episode)
            .prop(Episode::progressInSec)
            .isNotNull()
            .isEqualTo(CorrectEpisodeDurationInSec / 5)

        // Lastly, assert we mark an episode as completed when reaching the end of it
        mediaPlayer.seekTo(mediaPlayer.duration)
        awaitReady()

        delay(1.seconds)
        podcastsRepo.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)

        // sometimes the progress doesn't match exactly the duration when they should match so we allow a small tolerance value
        val expectedRange = ((mediaPlayer.duration / 1000).toInt() - Tolerance)..((mediaPlayer.duration / 1000).toInt() + Tolerance)
        assertThat(mediaPlayer.currentlyPlayingEpisode.value)
            .isNotNull()
            .prop(CurrentlyPlayingEpisode::episode)
            .prop(Episode::progressInSec)
            .isNotNull()
            .isInRange(expectedRange)

        assertThat(mediaPlayer.currentlyPlayingEpisode.value)
            .isNotNull()
            .prop(CurrentlyPlayingEpisode::episode)
            .prop(Episode::isCompleted)
            .isTrue()

        // If we switched and played a different episode, we get to know that as well.
        mediaPlayer.seekToDefaultPosition()
        awaitReady()

        val newEpisode = CurrentlyPlayingEpisode(Episodes[1], PlayingStatus.Loading, 1.0f)
        podcastsRepo.setCurrentlyPlayingEpisode(newEpisode)
        assertThat(mediaPlayer.currentlyPlayingEpisode.value).isEqualTo(newEpisode.copy(episode = newEpisode.episode.copy(durationInSec = CorrectEpisodeDurationInSec)))
    }

    @Test
    fun `test synchronization between player events and episodes state`() = scope.runTest {
        assertThat(mediaPlayer.currentlyPlayingEpisode.value).isNull()

        mediaPlayer.startListeningForUpdatesIn(backgroundScope)

        val currentEpisode = CurrentlyPlayingEpisode(Episodes[0], PlayingStatus.Loading, 1.0f)
        podcastsRepo.setCurrentlyPlayingEpisode(currentEpisode)

        mediaPlayer.attachPlayerListener()

        mediaPlayer.prepare()
        awaitReady()
        mediaPlayer.play()

        assertThat(mediaPlayer.currentlyPlayingEpisode.value)
            .isNotNull()
            .prop(CurrentlyPlayingEpisode::playingStatus)
            .isEqualTo(PlayingStatus.Playing)

        mediaPlayer.pause()

        assertThat(mediaPlayer.currentlyPlayingEpisode.value)
            .isNotNull()
            .prop(CurrentlyPlayingEpisode::playingStatus)
            .isEqualTo(PlayingStatus.Paused)
    }

    private fun awaitReady() {
        TestPlayerRunHelper.runUntilPlaybackState(internalPlayer, Player.STATE_READY)
    }

    private fun Assert<Int>.isInRange(range: IntRange) = given { value ->
        if (value in range) return@given
        expected("value to be in range:${show(range)} but was:${show(value)}")
    }

    companion object {
        val SampleMediaItem = MediaItem.Builder()
            .setMediaId(Episodes[0].id.toString())
            .setUri(Uri.Builder().encodedPath(Episodes[0].enclosureUrl).build())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(Episodes[0].title)
                    .setArtist(Episodes[0].podcastTitle)
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setArtworkUri(Uri.parse(Episodes[0].artworkUrl))
                    .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                    .build()
            )
            .build()

        val CorrectEpisodeDurationInSec: Int
            get() = Episodes[0].durationInSec?.plus(1) ?: error("Episode duration isn't specified")

        val Tolerance: Int = 1
    }
}
