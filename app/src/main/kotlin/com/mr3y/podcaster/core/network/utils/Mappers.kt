package com.mr3y.podcaster.core.network.utils

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Genre
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.network.model.NetworkEpisode
import com.mr3y.podcaster.core.network.model.NetworkEpisodes
import com.mr3y.podcaster.core.network.model.NetworkPodcast
import com.mr3y.podcaster.core.network.model.NetworkPodcasts
import com.mr3y.podcaster.core.network.model.PodcastEpisodeFeed
import com.mr3y.podcaster.core.network.model.PodcastFeed

fun NetworkPodcast.mapToPodcast(): Podcast = this.feed.mapToPodcast()

fun NetworkPodcasts.mapToPodcasts(): List<Podcast> = this.feeds.map(PodcastFeed::mapToPodcast)

fun NetworkEpisode.mapToEpisode(): Episode = this.episodeFeed.mapToEpisode()

fun NetworkEpisodes.mapToEpisodes(): List<Episode> = this.episodesFeed.map(PodcastEpisodeFeed::mapToEpisode)

fun PodcastFeed.mapToPodcast() = Podcast(
    id = id,
    guid = guid,
    title = title,
    description = description,
    podcastUrl = podcastUrl,
    website = website,
    artworkUrl = artworkUrl,
    author = author,
    owner = owner,
    languageCode = languageCode,
    episodeCount = episodeCount,
    genres = genres.map { (id, label) -> Genre(id, label) }
)

fun PodcastEpisodeFeed.mapToEpisode() = Episode(
    id = id,
    podcastId = podcastId,
    guid = guid,
    title = title,
    description = description,
    episodeUrl = episodeUrl,
    datePublishedTimestamp = datePublished,
    datePublishedFormatted = datePublishedFormatted,
    durationInSec = durationInSec,
    episodeNum = episodeNum,
    artworkUrl = artworkUrl,
    enclosureUrl = enclosureUrl,
    enclosureSizeInBytes = enclosureSizeInBytes,
    podcastTitle = podcastTitle,
    isDownloaded = false,
    isCompleted = false,
    progressInSec = null,
)
