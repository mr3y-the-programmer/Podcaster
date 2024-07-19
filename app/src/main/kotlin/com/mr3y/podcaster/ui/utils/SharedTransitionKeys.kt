package com.mr3y.podcaster.ui.utils

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast

val Podcast.artworkSharedTransitionKey: String
    get() = id.toString()

val Episode.artworkSharedTransitionKey: String
    get() = id.toString()

val Episode.dateSharedTransitionKey: String
    get() = "${id}-${datePublishedTimestamp}"