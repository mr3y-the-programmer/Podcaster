package com.mr3y.podcaster.core.local.di

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mr3y.podcaster.CurrentlyPlayingEntity
import com.mr3y.podcaster.DownloadableEpisodeEntity
import com.mr3y.podcaster.EpisodeEntity
import com.mr3y.podcaster.PodcastEntity
import com.mr3y.podcaster.PodcasterDatabase
import com.mr3y.podcaster.core.local.GenresColumnAdapter
import com.mr3y.podcaster.core.model.PlayingStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): PodcasterDatabase {
        return PodcasterDatabase(
            driver = AndroidSqliteDriver(
                schema = PodcasterDatabase.Schema,
                context = context,
                name = "podcaster_database.db"
            ),
            podcastEntityAdapter = PodcastEntity.Adapter(
                episodeCountAdapter = IntColumnAdapter,
                genresAdapter = GenresColumnAdapter
            ),
            episodeEntityAdapter = EpisodeEntity.Adapter(
                durationInSecAdapter = IntColumnAdapter,
                episodeNumAdapter = IntColumnAdapter,
                progressInSecAdapter = IntColumnAdapter
            ),
            currentlyPlayingEntityAdapter = CurrentlyPlayingEntity.Adapter(EnumColumnAdapter(), FloatColumnAdapter),
            downloadableEpisodeEntityAdapter = DownloadableEpisodeEntity.Adapter(EnumColumnAdapter(), FloatColumnAdapter)
        )
    }

    @Provides
    @IODispatcher
    fun provideIOCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}