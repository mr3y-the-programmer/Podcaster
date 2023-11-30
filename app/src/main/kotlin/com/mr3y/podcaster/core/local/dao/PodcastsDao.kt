package com.mr3y.podcaster.core.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
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
import javax.inject.Inject

interface PodcastsDao {

    fun getAllPodcasts(): Flow<List<Podcast>>

    fun getPodcast(podcastId: Long): Podcast?

    fun addPodcast(podcast: Podcast)

    fun deletePodcast(podcastId: Long)

    fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>>

    fun getEpisodesForPodcast(podcastId: Long): Flow<List<Episode>>

    fun getDownloadedEpisodes(): Flow<List<Episode>>

    fun getCompletedEpisodes(): Flow<List<Episode>>

    fun getEpisode(episodeId: Long): Episode?

    fun addEpisode(episode: Episode)

    fun markEpisodeAsDownloaded(episodeId: Long)

    fun markEpisodeAsCompleted(episodeId: Long)

    fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long)

    fun deleteUndownloadedEpisodes(podcastId: Long)
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
        return database.podcastEntityQueries.getPodcast(podcastId, mapper = ::mapToPodcast).executeAsOneOrNull()
    }

    override fun addPodcast(podcast: Podcast) {
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

    override fun getEpisodesForPodcast(podcastId: Long): Flow<List<Episode>> {
        return database.episodeEntityQueries.getEpisodesForPodcast(podcastId, mapper = ::mapToEpisode)
            .asFlow()
            .mapToList(dispatcher)
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

    override fun getEpisode(episodeId: Long): Episode? {
        return database.episodeEntityQueries.getEpisode(episodeId, mapper = ::mapToEpisode).executeAsOneOrNull()
    }

    override fun addEpisode(episode: Episode) {
        database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
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

    override fun deleteUndownloadedEpisodes(podcastId: Long) {
        database.episodeEntityQueries.deleteUndownloadedEpisodesForPodcast(podcastId)
    }
}
