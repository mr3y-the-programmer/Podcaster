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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultPodcastsDaoTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var sut: DefaultPodcastsDao

    @Before
    fun setUp() {
        sut = DefaultPodcastsDao(
            database = FakeDatabaseModule.provideInMemoryDatabaseInstance(),
            dispatcher = testDispatcher
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
    fun `assert upsert episode insert a new episode or update an existing episode without affecting updated local fields`() {
        val episode = EpisodeWithDetails
        assertThat(sut.getEpisode(episode.id)).isNull()

        sut.upsertEpisode(episode)
        assertThat(sut.getEpisode(episode.id)).isEqualTo(episode)
        sut.updateEpisodePlaybackProgress(episodeId = episode.id, progressInSec = 800)
        sut.markEpisodeAsCompleted(episode.id)

        val updatedEpisode = episode.copy(artworkUrl = "http://www.androidstrength.com/assets/androidstrength_podcast_cover.jpg", isCompleted = false, progressInSec = null)
        sut.upsertEpisode(updatedEpisode)
        assertThat(sut.getEpisode(updatedEpisode.id)).isEqualTo(updatedEpisode.copy(isCompleted = true, progressInSec = 800))
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
    }
}
