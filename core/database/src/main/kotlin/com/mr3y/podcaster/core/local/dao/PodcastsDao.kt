package com.mr3y.podcaster.core.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mr3y.podcaster.CurrentlyPlayingEntity
import com.mr3y.podcaster.PodcasterDatabase
import com.mr3y.podcaster.core.local.di.IODispatcher
import com.mr3y.podcaster.core.local.mapToEpisode
import com.mr3y.podcaster.core.local.mapToEpisodeDownloadMetadata
import com.mr3y.podcaster.core.local.mapToEpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.local.mapToPodcast
import com.mr3y.podcaster.core.local.toEpisodeEntity
import com.mr3y.podcaster.core.local.toPodcastEntity
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PodcastsDao {

    fun getAllPodcasts(): Flow<List<Podcast>>

    suspend fun getAllPodcastsNonObservable(): List<Podcast>

    fun getPodcast(podcastId: Long): Podcast?

    fun isPodcastAvailable(podcastId: Long): Flow<Boolean>

    fun countPodcasts(): Flow<Long>

    fun countDownloads(): Flow<Long>

    fun isPodcastAvailableNonObservable(podcastId: Long): Boolean

    fun upsertPodcast(podcast: Podcast)

    fun deletePodcast(podcastId: Long)

    fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>>

    fun getEpisodesForPodcast(podcastId: Long): List<Episode>

    fun getCompletedEpisodes(): Flow<List<Episode>>

    fun getEpisodeOrNull(episodeId: Long): Episode?

    fun getEpisode(episodeId: Long): Episode

    fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?>

    fun getCurrentlyPlayingEpisodeNonObservable(): CurrentlyPlayingEpisode?

    fun isEpisodeAvailable(episodeId: Long): Flow<Boolean>

    fun isEpisodeAvailableNonObservable(episodeId: Long): Boolean

    fun setCurrentlyPlayingEpisode(currentlyPlaying: CurrentlyPlayingEpisode)

    fun updateCurrentlyPlayingEpisodeStatus(newStatus: PlayingStatus)

    suspend fun updateCurrentlyPlayingEpisodeSpeed(newSpeed: Float)

    fun upsertEpisode(episode: Episode)

    fun addEpisode(episode: Episode)

    fun updateEpisodeDownloadStatus(episodeId: Long, newStatus: EpisodeDownloadStatus)

    fun updateEpisodeDownloadProgress(episodeId: Long, progress: Float)

    fun getEpisodeDownloadMetadataById(episodeId: Long): Flow<EpisodeDownloadMetadata?>

    fun getEpisodesDownloadMetadataByIds(episodesIds: Set<Long>): Flow<List<EpisodeDownloadMetadata>>

    fun getEpisodeWithDownloadMetadataForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<EpisodeWithDownloadMetadata>>

    fun getDownloadingEpisodesWithDownloadMetadata(): Flow<List<EpisodeWithDownloadMetadata>>

    fun markEpisodeAsCompleted(episodeId: Long)

    fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long)

    fun updateEpisodeDuration(durationInSec: Int?, episodeId: Long)

    fun updateEpisodesPodcastTitle(title: String, podcastId: Long)

    fun deleteUntouchedEpisodes(podcastId: Long)

    fun deleteEpisode(episodeId: Long)

    fun getQueueEpisodesIds(): Flow<List<Long>>

    fun getQueueEpisodes(): List<Episode>

    fun getFavouriteEpisodes(): Flow<List<Episode>>

    fun countFavouriteEpisodes(): Long

    fun addEpisodeToQueue(episode: Episode)

    fun replaceEpisodeInQueue(newEpisode: Episode, oldEpisodeId: Long)

    fun removeEpisodeFromQueue(episodeId: Long)

    fun isEpisodeInQueue(episodeId: Long): Boolean

    fun deleteAllInQueueExcept(episodesIds: Set<Long>)

    fun toggleEpisodeFavouriteStatus(isFavourite: Boolean, episode: Episode)
}

class DefaultPodcastsDao @Inject constructor(
    private val database: PodcasterDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : PodcastsDao {

    override fun getAllPodcasts(): Flow<List<Podcast>> {
        return database.podcastEntityQueries.getAllPodcasts(::mapToPodcast)
            .asFlow()
            .mapToList(dispatcher)
    }

    override suspend fun getAllPodcastsNonObservable(): List<Podcast> = withContext(dispatcher) {
        database.podcastEntityQueries.getAllPodcasts(::mapToPodcast).executeAsList()
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

    override fun countPodcasts(): Flow<Long> {
        return database.podcastEntityQueries.countPodcasts().asFlow().mapToOne(dispatcher)
    }

    override fun countDownloads(): Flow<Long> {
        return database.downloadableEpisodeEntityQueries.countDownloads().asFlow().mapToOne(dispatcher)
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

    override fun getCompletedEpisodes(): Flow<List<Episode>> {
        return database.episodeEntityQueries.getCompletedEpisodes(::mapToEpisode)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getEpisodeOrNull(episodeId: Long): Episode? {
        return database.episodeEntityQueries.getEpisode(episodeId, mapper = ::mapToEpisode)
            .executeAsOneOrNull()
    }

    override fun getEpisode(episodeId: Long): Episode {
        return database.episodeEntityQueries.getEpisode(episodeId, mapper = ::mapToEpisode)
            .executeAsOne()
    }

    override fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?> {
        return database.currentlyPlayingEntityQueries.getCurrentlyPlayingEpisode { episodeId, playingStatus, playingSpeed ->
            val episode = getEpisode(episodeId)
            CurrentlyPlayingEpisode(episode, playingStatus, playingSpeed)
        }
            .asFlow()
            .mapToOneOrNull(dispatcher)
    }

    override fun getCurrentlyPlayingEpisodeNonObservable(): CurrentlyPlayingEpisode? {
        return database.currentlyPlayingEntityQueries.getCurrentlyPlayingEpisode { episodeId, playingStatus, playingSpeed ->
            val episode = getEpisode(episodeId)
            CurrentlyPlayingEpisode(episode, playingStatus, playingSpeed)
        }
            .executeAsOneOrNull()
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

    override fun setCurrentlyPlayingEpisode(currentlyPlaying: CurrentlyPlayingEpisode) {
        val currentlyPlayingQueries = database.currentlyPlayingEntityQueries
        val episode = currentlyPlaying.episode
        if (!isEpisodeAvailableNonObservable(episode.id)) {
            upsertEpisode(episode)
        }
        currentlyPlayingQueries.transaction {
            if (currentlyPlayingQueries.hasCurrentlyPlayingEpisode().executeAsOneOrNull().let { it == 1L }) {
                currentlyPlayingQueries.deleteCurrentlyPlayingEpisode()
            }
            currentlyPlayingQueries.updateCurrentlyPlayingEpisode(CurrentlyPlayingEntity(episode.id, currentlyPlaying.playingStatus, currentlyPlaying.playingSpeed))
        }
    }

    override fun updateCurrentlyPlayingEpisodeStatus(newStatus: PlayingStatus) {
        return database.currentlyPlayingEntityQueries.updateCurrentlyPlayingEpisodeStatus(newStatus)
    }

    override suspend fun updateCurrentlyPlayingEpisodeSpeed(newSpeed: Float) = withContext(dispatcher) {
        database.currentlyPlayingEntityQueries.updateCurrentlyPlayingEpisodeSpeed(newSpeed)
    }

    @Suppress("DEPRECATION")
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
                    episodeNum = episode.episodeNum,
                    artworkUrl = episode.artworkUrl,
                    enclosureUrl = episode.enclosureUrl,
                    enclosureSizeInBytes = episode.enclosureSizeInBytes,
                    podcastTitle = episode.podcastTitle,
                )
            } else {
                database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
            }
        }
    }

    override fun addEpisode(episode: Episode) {
        database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
    }

    override fun updateEpisodeDownloadStatus(episodeId: Long, newStatus: EpisodeDownloadStatus) {
        database.downloadableEpisodeEntityQueries.updateEpisodeDownloadStatus(newStatus, episodeId)
    }

    override fun updateEpisodeDownloadProgress(episodeId: Long, progress: Float) {
        database.downloadableEpisodeEntityQueries.updateEpisodeDownloadProgress(progress, episodeId)
    }

    override fun getEpisodeDownloadMetadataById(episodeId: Long): Flow<EpisodeDownloadMetadata?> {
        return database.downloadableEpisodeEntityQueries.getDownloadableEpisodeById(episodeId, mapper = ::mapToEpisodeDownloadMetadata)
            .asFlow()
            .mapToOneOrNull(dispatcher)
    }

    override fun getEpisodesDownloadMetadataByIds(episodesIds: Set<Long>): Flow<List<EpisodeDownloadMetadata>> {
        return database.downloadableEpisodeEntityQueries.getDownloadableEpisodesByIds(episodesIds, mapper = ::mapToEpisodeDownloadMetadata)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getEpisodeWithDownloadMetadataForPodcasts(
        podcastsIds: Set<Long>,
        limit: Long,
    ): Flow<List<EpisodeWithDownloadMetadata>> {
        return database.downloadableEpisodeEntityQueries.getEpisodesWithDownloadMetadataForPodcast(podcastsIds, limit, mapper = ::mapToEpisodeWithDownloadMetadata)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getDownloadingEpisodesWithDownloadMetadata(): Flow<List<EpisodeWithDownloadMetadata>> {
        return database.downloadableEpisodeEntityQueries.getDownloadingEpisodesWithDownloadMetadata(mapper = ::mapToEpisodeWithDownloadMetadata)
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun markEpisodeAsCompleted(episodeId: Long) {
        database.episodeEntityQueries.setEpisodeCompleted(episodeId)
    }

    override fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long) {
        database.episodeEntityQueries.updateEpisodeProgress(progressInSec, episodeId)
    }

    override fun updateEpisodeDuration(durationInSec: Int?, episodeId: Long) {
        database.episodeEntityQueries.updateEpisodeDuration(durationInSec, episodeId)
    }

    override fun updateEpisodesPodcastTitle(title: String, podcastId: Long) {
        database.episodeEntityQueries.updateEpisodePodcastTitleByPodcastId(title, podcastId)
    }

    override fun deleteUntouchedEpisodes(podcastId: Long) {
        database.downloadableEpisodeEntityQueries.transaction {
            val ids = database.downloadableEpisodeEntityQueries.getUntouchedEpisodesIdsForPodcast(podcastId).executeAsList()
            ids.forEach { id ->
                if (!isEpisodeInQueue(id)) {
                    database.episodeEntityQueries.deleteEpisodesByIds(listOf(id))
                }
            }
        }
    }

    override fun deleteEpisode(episodeId: Long) {
        database.episodeEntityQueries.deleteEpisodesByIds(listOf(episodeId))
    }

    override fun getQueueEpisodesIds(): Flow<List<Long>> {
        return database.queueEntityQueries.getQueueEpisodesIds()
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun getQueueEpisodes(): List<Episode> {
        return database.queueEntityQueries.getQueueEpisodes(mapper = ::mapToEpisode)
            .executeAsList()
    }

    override fun getFavouriteEpisodes(): Flow<List<Episode>> {
        return database.episodeEntityQueries.getFavouriteEpisodes(mapper = ::mapToEpisode).asFlow().mapToList(dispatcher)
    }

    override fun countFavouriteEpisodes(): Long {
        return database.episodeEntityQueries.countFavouriteEpisodes().executeAsOne()
    }

    override fun addEpisodeToQueue(episode: Episode) {
        if (isEpisodeAvailableNonObservable(episode.id)) {
            database.queueEntityQueries.insertNewQueueEpisode(episode.id)
            return
        }

        database.episodeEntityQueries.transaction {
            database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
            database.queueEntityQueries.insertNewQueueEpisode(episode.id)
        }
    }

    override fun replaceEpisodeInQueue(newEpisode: Episode, oldEpisodeId: Long) {
        if (isEpisodeAvailableNonObservable(newEpisode.id)) {
            database.queueEntityQueries.replaceQueueEpisode(newEpisode.id, oldEpisodeId)
            return
        }

        database.episodeEntityQueries.transaction {
            database.episodeEntityQueries.insertEpisode(newEpisode.toEpisodeEntity())
            database.queueEntityQueries.replaceQueueEpisode(newEpisode.id, oldEpisodeId)
        }
    }

    override fun removeEpisodeFromQueue(episodeId: Long) {
        database.queueEntityQueries.deleteEpisodeFromQueue(episodeId)
    }

    override fun isEpisodeInQueue(episodeId: Long): Boolean {
        return database.queueEntityQueries.isEpisodeInQueue(episodeId)
            .executeAsOneOrNull()
            .let { it == 1L }
    }

    override fun deleteAllInQueueExcept(episodesIds: Set<Long>) {
        database.queueEntityQueries.clearQueueExceptEpisodes(episodesIds)
    }

    override fun toggleEpisodeFavouriteStatus(isFavourite: Boolean, episode: Episode) {
        if (isEpisodeAvailableNonObservable(episode.id)) {
            database.episodeEntityQueries.toggleEpisodeFavouriteStatus(isFavourite, episode.id)
            return
        }

        database.episodeEntityQueries.transaction {
            database.episodeEntityQueries.insertEpisode(episode.toEpisodeEntity())
            database.episodeEntityQueries.toggleEpisodeFavouriteStatus(isFavourite, episode.id)
        }
    }
}
