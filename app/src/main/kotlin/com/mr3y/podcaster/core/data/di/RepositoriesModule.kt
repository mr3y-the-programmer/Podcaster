package com.mr3y.podcaster.core.data.di

import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.data.internal.DefaultPodcastsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun providePodcastsRepositoryInstance(impl: DefaultPodcastsRepository): PodcastsRepository
}
