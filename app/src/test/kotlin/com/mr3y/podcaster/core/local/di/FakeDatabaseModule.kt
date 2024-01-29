package com.mr3y.podcaster.core.local.di

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mr3y.podcaster.CurrentlyPlayingEntity
import com.mr3y.podcaster.DownloadableEpisodeEntity
import com.mr3y.podcaster.EpisodeEntity
import com.mr3y.podcaster.PodcastEntity
import com.mr3y.podcaster.PodcasterDatabase
import com.mr3y.podcaster.core.local.GenresColumnAdapter

object FakeDatabaseModule {

    fun provideInMemoryDatabaseInstance(): PodcasterDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PodcasterDatabase.Schema.create(driver)
        return PodcasterDatabase(
            driver,
            currentlyPlayingEntityAdapter = CurrentlyPlayingEntity.Adapter(EnumColumnAdapter(), FloatColumnAdapter),
            downloadableEpisodeEntityAdapter = DownloadableEpisodeEntity.Adapter(EnumColumnAdapter(), FloatColumnAdapter),
            episodeEntityAdapter = EpisodeEntity.Adapter(
                durationInSecAdapter = IntColumnAdapter,
                episodeNumAdapter = IntColumnAdapter,
                progressInSecAdapter = IntColumnAdapter
            ),
            podcastEntityAdapter = PodcastEntity.Adapter(
                episodeCountAdapter = IntColumnAdapter,
                genresAdapter = GenresColumnAdapter
            )
        )
    }
}
