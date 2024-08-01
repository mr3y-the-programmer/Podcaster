package com.mr3y.podcaster.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PodcasterAppWidgetReceiver : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var podcastsRepository: PodcastsRepository

    @Inject
    lateinit var logger: Logger

    override val glanceAppWidget: GlanceAppWidget
        get() = PodcasterAppWidget(podcastsRepository, logger)
}
