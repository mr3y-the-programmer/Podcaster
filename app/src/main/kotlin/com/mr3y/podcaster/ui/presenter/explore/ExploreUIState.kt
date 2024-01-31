package com.mr3y.podcaster.ui.presenter.explore

import androidx.compose.ui.text.input.TextFieldValue
import com.mr3y.podcaster.core.model.Podcast

data class ExploreUIState(
    val searchQuery: TextFieldValue,
    val searchResult: SearchResult?,
    val previousSearchQueries: List<String>,
)

sealed interface SearchResult {
    data object Loading : SearchResult

    data class SearchByTermSuccess(val podcasts: List<Podcast>) : SearchResult

    data class SearchByUrlSuccess(val podcast: Podcast) : SearchResult

    data class Error(val isFeedUrl: Boolean, val errorResponse: Any) : SearchResult
}

sealed interface ExploreUIEvent {
    data object Search : ExploreUIEvent

    data class UpdateSearchQuery(val newSearchQuery: TextFieldValue) : ExploreUIEvent

    data class DeleteSearchQuery(val searchQuery: String) : ExploreUIEvent

    data object Retry : ExploreUIEvent

    data object ResultConsumed : ExploreUIEvent
}
