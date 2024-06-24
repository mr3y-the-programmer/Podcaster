package com.mr3y.podcaster.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.dateTimePublished
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun rememberFormattedEpisodeDate(episode: Episode): String {
    return remember(episode.datePublishedTimestamp) { format(episode.dateTimePublished) }
}

private fun format(dateTime: ZonedDateTime): String {
    val pattern = if (ZonedDateTime.now(ZoneId.systemDefault()).year != dateTime.year) "MMM d, yyyy" else "MMM d"
    return DateTimeFormatter.ofPattern(pattern).format(dateTime.toLocalDate())
}
