package com.mr3y.podcaster.core.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

val Episode.dateTimePublished: ZonedDateTime
    get() = ZonedDateTime.ofInstant(Instant.ofEpochSecond(datePublishedTimestamp), ZoneId.systemDefault())
