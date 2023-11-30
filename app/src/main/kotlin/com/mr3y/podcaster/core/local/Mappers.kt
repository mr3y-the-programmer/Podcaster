package com.mr3y.podcaster.core.local

import com.mr3y.podcaster.EpisodeEntity
import com.mr3y.podcaster.PodcastEntity
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Genre
import com.mr3y.podcaster.core.model.Podcast

fun mapToPodcast(
    id: Long,
    guid: String,
    title: String,
    description: String,
    podcastUrl: String,
    website: String,
    artworkUrl: String,
    author: String,
    owner: String,
    languageCode: String,
    episodeCount: Int,
    genres: List<Genre>
): Podcast {
    return Podcast(
        id,
        guid,
        title,
        description,
        podcastUrl,
        website,
        artworkUrl,
        author,
        owner,
        languageCode,
        episodeCount,
        genres
    )
}

fun mapToEpisode(
    id: Long,
    podcastId: Long,
    guid: String,
    title: String,
    description: String,
    episodeUrl: String,
    datePublishedTimestamp: Long,
    datePublishedFormatted: String,
    durationInSec: Int?,
    episodeNum: Int?,
    artworkUrl: String,
    enclosureUrl: String,
    enclosureSizeInBytes: Long,
    podcastTitle: String?,
    isDownloaded: Boolean,
    isCompleted: Boolean,
    progressInSec: Int?
): Episode {
    return Episode(
        id,
        podcastId,
        guid,
        title,
        description,
        episodeUrl,
        datePublishedTimestamp,
        datePublishedFormatted,
        durationInSec,
        episodeNum,
        artworkUrl,
        enclosureUrl,
        enclosureSizeInBytes,
        podcastTitle,
        isDownloaded,
        isCompleted,
        progressInSec
    )
}

fun Podcast.toPodcastEntity(): PodcastEntity {
    return PodcastEntity(
        id,
        guid,
        title,
        description,
        podcastUrl,
        website,
        artworkUrl,
        author,
        owner,
        languageCode,
        episodeCount,
        genres
    )
}

fun Episode.toEpisodeEntity(): EpisodeEntity {
    return EpisodeEntity(
        id,
        podcastId,
        guid,
        title,
        description,
        episodeUrl,
        datePublishedTimestamp,
        datePublishedFormatted,
        durationInSec,
        episodeNum,
        artworkUrl,
        enclosureUrl,
        enclosureSizeInBytes,
        podcastTitle,
        isDownloaded,
        isCompleted,
        progressInSec
    )
}
