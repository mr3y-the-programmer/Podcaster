package com.mr3y.podcaster.core.local.di

import com.mr3y.podcaster.core.local.dao.DefaultPodcastsDao
import com.mr3y.podcaster.core.local.dao.DefaultRecentSearchesDao
import com.mr3y.podcaster.core.local.dao.PodcastsDao
import com.mr3y.podcaster.core.local.dao.RecentSearchesDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DaosModule {

    @Binds
    @Singleton
    abstract fun provideRecentSearchesDaoInstance(impl: DefaultRecentSearchesDao): RecentSearchesDao

    @Binds
    @Singleton
    abstract fun providePodcastsDaoInstance(impl: DefaultPodcastsDao): PodcastsDao
}