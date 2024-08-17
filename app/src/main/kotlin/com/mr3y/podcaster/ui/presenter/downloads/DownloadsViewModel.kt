package com.mr3y.podcaster.ui.presenter.downloads

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) : BaseMoleculeViewModel<Nothing>() {

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        DownloadsPresenter(repository = podcastsRepository)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun DownloadsPresenter(
    repository: PodcastsRepository,
): DownloadsUIState {
    var isLoading by remember { mutableStateOf(true) }
    val downloads by repository.getDownloads().collectAsState(initial = emptyList())

    LaunchedEffect(downloads) {
        if (downloads.isNotEmpty()) {
            isLoading = false
        } else {
            delay(1000)
            isLoading = false
        }
    }

    return DownloadsUIState(
        isLoading = isLoading,
        downloads = downloads,
    )
}
