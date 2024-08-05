package com.mr3y.podcaster.ui.presenter.favorites

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) : BaseMoleculeViewModel<Nothing>() {

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        FavoritesPresenter(repository = podcastsRepository)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun FavoritesPresenter(
    repository: PodcastsRepository,
): List<Episode> {
    val favorites by repository.getFavouriteEpisodes().collectAsState(initial = emptyList())
    return favorites
}
