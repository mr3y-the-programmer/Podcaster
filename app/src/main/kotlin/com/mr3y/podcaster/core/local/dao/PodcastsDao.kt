package com.mr3y.podcaster.core.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mr3y.podcaster.PodcasterDatabase
import com.mr3y.podcaster.core.local.di.IODispatcher
import com.mr3y.podcaster.core.local.mapToEpisode
import com.mr3y.podcaster.core.local.mapToPodcast
import com.mr3y.podcaster.core.local.toEpisodeEntity
import com.mr3y.podcaster.core.local.toPodcastEntity
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PodcastsDao {

    fun getAllPodcasts(): Flow<List<Podcast>>

    fun getPodcast(podcastId: Long): Podcast?

    fun isPodcastAvailable(podcastId: Long): Flow<Boolean>

    fun isPodcastAvailableNonObservable(podcastId: Long): Boolean

    fun upsertPodcast(podcast: Podcast)

    fun deletePodcast(podcastId: Long)

    fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>>

    fun getEpisodesForPodcast(podcastId: Long): List<Episode>

    fun getDownloadedEpisodes(): Flow<List<Episode>>

    fun getCompletedEpisodes(): Flow<List<Episode>>

    fun getEpisode(episodeId: Long): Flow<Episode?>

    fun isEpisodeAvailable(episodeId: Long): Flow<Boolean>

    fun isEpisodeAvailableNonObservable(episodeId: Long): Boolean

    fun upsertEpisode(episode: Episode)

    fun markEpisodeAsDownloaded(episodeId: Long)

    fun markEpisodeAsCompleted(episodeId: Long)

    fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long)

    fun updateEpisodesPodcastTitle(title: String, podcastId: Long)

    fun deleteUntouchedEpisodes(podcastId: Long)
}

class DefaultPodcastsDao @Inject constructor(
    private val database: PodcasterDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : PodcastsDao {

    override fun getAllPodcasts(): Flow<List<Podcast>> {
        return database.podcastEntityQueries.getAllPodcasts(::mapToPodcast)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getPodcast(podcastId: Long): Podcast? {
        return database.podcastEntityQueries.getPodcast(podcastId, mapper = ::mapToPodcast)
            .executeAsOneOrNull()
    }

    override fun isPodcastAvailable(podcastId: Long): Flow<Boolean> {
        return database.podcastEntityQueries.hasPodcast(podcastId)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { it == 1L }
    }

    override fun isPodcastAvailableNonObservable(podcastId: Long): Boolean {
        return database.podcastEntityQueries.hasPodcast(podcastId)
            .executeAsOneOrNull()
            .let { it == 1L }
    }

    override fun upsertPodcast(podcast: Podcast) {
        database.podcastEntityQueries.insertPodcast(podcast.toPodcastEntity())
    }

    override fun deletePodcast(podcastId: Long) {
        database.podcastEntityQueries.deletePodcast(podcastId)
    }

    override fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>> {
        return database.episodeEntityQueries.getEpisodesForPodcasts(podcastsIds, limit, mapper = ::mapToEpisode)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getEpisodesForPodcast(podcastId: Long): List<Episode> {
        return database.episodeEntityQueries.getEpisodesForPodcast(podcastId, mapper = ::mapToEpisode)
            .executeAsList()
    }

    override fun getDownloadedEpisodes(): Flow<List<Episode>> {
        return database.episodeEntityQueries.getDownloadedEpisodes(::mapToEpisode)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getCompletedEpisodes(): Flow<List<Episode>> {
        return database.episodeEntityQueries.getCompletedEpisodes(::mapToEpisode)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getEpisode(episodeId: Long): Flow<Episode?> {
        return database.episodeEntityQueries.getEpisode(episodeId, mapper = ::mapToEpisode)
            .asFlow()
            .mapToOneOrNull(dispatcher)
    }

    override fun isEpisodeAvailable(episodeId: Long): Flow<Boolean> {
        return database.episodeEntityQueries.hasEpisode(episodeId)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { it == 1L }
    }

    override fun isEpisodeAvailableNonObservable(episodeId: Long): Boolean {
        return database.episodeEntityQueries.hasEpisode(episodeId)
            .executeAsOneOrNull()
            .let { it == 1L }
    }

    override fun upsertEpisode(episode: Episode) {
        val queries = database.episodeEntityQueries
        queries.transaction {
            val episodeEntity = queries.getEpisode(id = episode.id, mapper = ::mapToEpisode).executeAsOneOrNull()
            if (episodeEntity != null) {
                queries.updateEpisodeInfo(
                    id = episode.id,
                    guid = episode.guid,
                    title = episode.title,
                    description = episode.description,
                    episodeUrl = episode.episodeUrl,
                    datePublishedTimestamp = episode.datePublishedTimestamp,
                    datePublishedFormatted = episode.datePublishedFormatted,
                    durationInSec = episode.durationInSec,
                    episodeNum = episode.episodeNum,
                    artworkUrl = episode.artworkUrl,
                    enclosureUrl = episode.enclosureUrl,
                    enclosureSizeInBytes = episode.enclosureSizeInBytes,
                    podcastTitle = episode.podcastTitle
                )
            } else {
                database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
            }
        }
    }

    override fun markEpisodeAsDownloaded(episodeId: Long) {
        database.episodeEntityQueries.setEpisodeDownloaded(episodeId)
    }

    override fun markEpisodeAsCompleted(episodeId: Long) {
        database.episodeEntityQueries.setEpisodeCompleted(episodeId)
    }

    override fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long) {
        database.episodeEntityQueries.updateEpisodeProgress(progressInSec, episodeId)
    }

    override fun updateEpisodesPodcastTitle(title: String, podcastId: Long) {
        database.episodeEntityQueries.updateEpisodePodcastTitleByPodcastId(title, podcastId)
    }

    override fun deleteUntouchedEpisodes(podcastId: Long) {
        database.episodeEntityQueries.deleteUntouchedEpisodesForPodcast(podcastId)
    }
}
