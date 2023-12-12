package com.mr3y.podcaster.ui.presenter.subscriptions

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.presenter.RefreshResult

data class SubscriptionsUIState(
    val refreshResult: RefreshResult?,
    val subscriptions: List<Podcast>,
    val episodes: List<Episode>
)

sealed interface SubscriptionsUIEvent {

    data object Refresh : SubscriptionsUIEvent

    data object RefreshResultConsumed : SubscriptionsUIEvent
}
