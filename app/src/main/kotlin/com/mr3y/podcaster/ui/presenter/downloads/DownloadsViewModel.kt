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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) : ViewModel() {

    private val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

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

    LaunchedEffect(Unit) {
        launch {
            // if the user has no downloads
            repository.hasDownloads()
                .filter { hasDownloads -> !hasDownloads }
                .collect {
                    isLoading = false
                }
        }
        launch {
            snapshotFlow { downloads }
                .drop(1)
                .collect {
                    isLoading = false
                }
        }
    }

    return DownloadsUIState(
        isLoading = isLoading,
        downloads = downloads,
    )
}
