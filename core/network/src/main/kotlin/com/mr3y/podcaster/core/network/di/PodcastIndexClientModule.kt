package com.mr3y.podcaster.core.network.di

import com.mr3y.podcaster.core.network.PodcastIndexClient
import com.mr3y.podcaster.core.network.internal.DefaultPodcastIndexClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PodcastIndexClientModule {

    @Binds
    @Singleton
    abstract fun providePodcastIndexClientInstance(impl: DefaultPodcastIndexClient): PodcastIndexClient
}
