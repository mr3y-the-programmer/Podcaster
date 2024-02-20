package com.mr3y.podcaster.core.local.dao

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.mr3y.podcaster.core.local.di.FakeDatabaseModule
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.ui.preview.EpisodeWithDetails
import com.mr3y.podcaster.ui.preview.Episodes
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultPodcastsDaoTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var sut: DefaultPodcastsDao

    @Before
    fun setUp() {
        sut = DefaultPodcastsDao(
            database = FakeDatabaseModule.provideInMemoryDatabaseInstance(),
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `assert there should be at most 1 currently playing episode`() = runTest(testDispatcher) {
        sut.getCurrentlyPlayingEpisode().test {
            // Initially, there is no playing episode
            assertThat(awaitItem()).isNull()

            val currentlyPlayingEpisode = CurrentlyPlayingEpisode(EpisodeWithDetails, PlayingStatus.Playing, 1.0f)
            sut.setCurrentlyPlayingEpisode(currentlyPlayingEpisode)
            assertThat(awaitItem()).isEqualTo(currentlyPlayingEpisode)

            val updatedEpisode = currentlyPlayingEpisode.copy(playingStatus = PlayingStatus.Paused)
            sut.setCurrentlyPlayingEpisode(updatedEpisode)
            assertThat(awaitItem()).isEqualTo(updatedEpisode)

            expectNoEvents()
        }
    }

    @Test
    fun `assert deleting the backing episode of the currently playing episode nullifies it`() = runTest(testDispatcher) {
        sut.getCurrentlyPlayingEpisode().test {
            // Initially, there is no playing episode
            assertThat(awaitItem()).isNull()

            // Assume, we added 2 episodes and then played one of them.
            sut.addEpisode(EpisodeWithDetails)
            sut.addEpisode(EpisodeWithDetails.copy(id = 17536508L))

            val currentlyPlayingEpisode = CurrentlyPlayingEpisode(EpisodeWithDetails, PlayingStatus.Loading, 1.0f)
            sut.setCurrentlyPlayingEpisode(currentlyPlayingEpisode)
            assertThat(awaitItem()).isEqualTo(currentlyPlayingEpisode)

            // if we deleted an episode that is not currently playing, then it should have no effect on the currently playing episode
            sut.deleteEpisode(17536508L)
            assertThat(awaitItem()).isEqualTo(currentlyPlayingEpisode)

            sut.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
            assertThat(awaitItem()).isEqualTo(currentlyPlayingEpisode.copy(playingStatus = PlayingStatus.Playing))

            // but, if we deleted the episode that is currently playing, then we should have no playing episode
            sut.deleteEpisode(EpisodeWithDetails.id)
            assertThat(awaitItem()).isNull()

            // and any later updates should have no effect
            sut.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
            assertThat(awaitItem()).isNull()

            expectNoEvents()
        }
    }

    @Test
    fun `assert upsert episode insert a new episode or update an existing episode without affecting updated local fields`() {
        val episode = EpisodeWithDetails
        assertThat(sut.getEpisodeOrNull(episode.id)).isNull()

        sut.upsertEpisode(episode)
        assertThat(sut.getEpisodeOrNull(episode.id)).isEqualTo(episode)
        sut.updateEpisodePlaybackProgress(episodeId = episode.id, progressInSec = 800)
        sut.markEpisodeAsCompleted(episode.id)

        val updatedEpisode = episode.copy(artworkUrl = "http://www.androidstrength.com/assets/androidstrength_podcast_cover.jpg", isCompleted = false, progressInSec = null)
        sut.upsertEpisode(updatedEpisode)
        assertThat(sut.getEpisodeOrNull(updatedEpisode.id)).isEqualTo(updatedEpisode.copy(isCompleted = true, progressInSec = 800))
    }

    @Test
    fun `assert deleting podcast episodes doesn't affect touched episodes`() = runTest(testDispatcher) {
        val episodes = Episodes.slice(0..1).map { it.copy(podcastId = 456857L) }
        episodes.forEach {
            sut.addEpisode(it)
        }

        sut.markEpisodeAsCompleted(episodes[0].id)

        sut.deleteUntouchedEpisodes(podcastId = 456857L)
        assertThat(sut.getEpisodesForPodcast(podcastId = 456857L)).isEqualTo(listOf(episodes[0].copy(isCompleted = true)))

        // Reset
        sut.deleteEpisode(episodes[0].id)

        // Repeat the same steps but update the playback progress this time.
        episodes.forEach {
            sut.addEpisode(it)
        }
        sut.updateEpisodePlaybackProgress(100, episodes[0].id)

        sut.deleteUntouchedEpisodes(podcastId = 456857L)
        assertThat(sut.getEpisodesForPodcast(podcastId = 456857L)).isEqualTo(listOf(episodes[0].copy(progressInSec = 100)))

        // Reset
        sut.deleteEpisode(episodes[0].id)

        // Repeat the same steps but update the download status this time.
        episodes.forEach {
            sut.addEpisode(it)
        }
        sut.updateEpisodeDownloadStatus(episodes[0].id, EpisodeDownloadStatus.Queued)

        sut.deleteUntouchedEpisodes(podcastId = 456857L)
        assertThat(sut.getEpisodesForPodcast(podcastId = 456857L)).isEqualTo(listOf(episodes[0]))
        sut.getEpisodeDownloadMetadataById(episodes[0].id).test {
            assertThat(awaitItem()).isNotNull().isEqualTo(EpisodeDownloadMetadata(episodes[0].id, EpisodeDownloadStatus.Queued))
        }

        // Reset
        sut.deleteEpisode(episodes[0].id)

        // Repeat the same steps but add an episode to the queue this time.
        episodes.forEach {
            sut.addEpisode(it)
        }
        sut.addEpisodeToQueue(episodes[0])

        sut.deleteUntouchedEpisodes(podcastId = 456857L)
        assertThat(sut.getEpisodesForPodcast(podcastId = 456857L)).isEqualTo(listOf(episodes[0]))
    }
}
