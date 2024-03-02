package com.mr3y.podcaster.core.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mr3y.podcaster.PodcasterDatabase
import com.mr3y.podcaster.core.local.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RecentSearchesDao {

    fun recentSearchQueries(): Flow<List<String>>

    fun addNewSearchQuery(queryText: String)

    fun deleteSearchQuery(queryText: String)
}

class DefaultRecentSearchesDao @Inject constructor(
    private val database: PodcasterDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : RecentSearchesDao {

    override fun recentSearchQueries(): Flow<List<String>> {
        return database.recentSearchEntryQueries.getAllRecentSearchEntries { queryText, _ -> queryText }
            .asFlow()
            .mapToList(dispatcher)
    }

    override fun addNewSearchQuery(queryText: String) {
        database.recentSearchEntryQueries.insertNewRecentSearchEntry(queryText)
    }

    override fun deleteSearchQuery(queryText: String) {
        database.recentSearchEntryQueries.deleteRecentSearchEntry(queryText)
    }
}
