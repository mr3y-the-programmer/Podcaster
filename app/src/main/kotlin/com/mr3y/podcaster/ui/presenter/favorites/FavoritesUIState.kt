package com.mr3y.podcaster.ui.presenter.favorites

import com.mr3y.podcaster.core.model.Episode

data class FavoritesUIState(
    val isLoading: Boolean,
    val favorites: List<Episode>,
)
