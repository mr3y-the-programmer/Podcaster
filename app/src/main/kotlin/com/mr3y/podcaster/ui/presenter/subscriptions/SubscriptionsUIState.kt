package com.mr3y.podcaster.ui.presenter.subscriptions

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast

data class SubscriptionsUIState(
    val refreshResult: RefreshResult?,
    val subscriptions: List<Podcast>,
    val episodes: List<Episode>
)

sealed interface RefreshResult {
    data object Ok : RefreshResult

    data object Error : RefreshResult

    data object Mixed : RefreshResult
}

sealed interface SubscriptionsUIEvent {

    data object Refresh : SubscriptionsUIEvent

    data object RefreshResultConsumed : SubscriptionsUIEvent
}
