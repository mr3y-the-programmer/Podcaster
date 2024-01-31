package com.mr3y.podcaster.ui.presenter.downloads

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.ui.presenter.BasePresenterTest
import com.mr3y.podcaster.ui.preview.Episodes
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DownloadsPresenterTest : BasePresenterTest<Nothing>() {

    @Test
    fun `test downloads presenter state`() = runTest(testDispatcher) {
        moleculeFlow(RecompositionMode.Immediate) {
            DownloadsPresenter(repository = repository)
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            assertThat(initialState.downloads).isEmpty()

            val emptyState = awaitItem()
            assertThat(emptyState.isLoading).isFalse()
            assertThat(emptyState.downloads).isEmpty()

            repository.addEpisodeOnDeviceIfNotExist(Episodes[0])
            repository.updateEpisodeDownloadStatus(Episodes[0].id, EpisodeDownloadStatus.Queued)

            var currentState = awaitItem()
            assertThat(currentState.downloads).hasSize(1)

            repository.updateEpisodeDownloadStatus(Episodes[0].id, EpisodeDownloadStatus.Downloading)

            currentState = awaitItem()
            assertThat(currentState.downloads).hasSize(1)
            assertThat(currentState.downloads.first().downloadMetadata).isEqualTo(
                EpisodeDownloadMetadata(Episodes[0].id, EpisodeDownloadStatus.Downloading)
            )

            repository.updateEpisodeDownloadProgress(Episodes[0].id, 0.2f)

            currentState = awaitItem()
            assertThat(currentState.downloads.single().downloadMetadata).isEqualTo(
                EpisodeDownloadMetadata(Episodes[0].id, EpisodeDownloadStatus.Downloading, 0.2f)
            )

            expectNoEvents()
        }
    }
}