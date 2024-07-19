package com.mr3y.podcaster.ui.utils

import com.mr3y.podcaster.core.model.Episode

val Episode.dateSharedTransitionKey: String
    get() = "${id}-${datePublishedTimestamp}"