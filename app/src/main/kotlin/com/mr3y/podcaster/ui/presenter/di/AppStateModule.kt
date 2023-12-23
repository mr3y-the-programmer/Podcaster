package com.mr3y.podcaster.ui.presenter.di

import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    @ApplicationScope
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    @Provides
    @Singleton
    fun providePodcasterAppStateInstance(repo: PodcastsRepository, @ApplicationScope applicationScope: CoroutineScope): PodcasterAppState {
        return PodcasterAppState(repo, applicationScope)
    }
}
