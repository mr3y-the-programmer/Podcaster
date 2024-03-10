package com.mr3y.podcaster.core.opml.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import nl.adaptivity.xmlutil.serialization.XML
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Module
@InstallIn(SingletonComponent::class)
object XMLSerializerModule {

    @Singleton
    @Provides
    fun provideXMLInstance(): XML {
        return XML {
            autoPolymorphic = true
            indentString = "  "
            defaultPolicy {
                pedantic = false
                ignoreUnknownChildren()
            }
        }
    }

    @Provides
    @IODispatcher
    fun provideIOCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
