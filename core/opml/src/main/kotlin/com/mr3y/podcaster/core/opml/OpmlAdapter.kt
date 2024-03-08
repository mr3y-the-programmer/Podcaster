package com.mr3y.podcaster.core.opml

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.opml.model.Body
import com.mr3y.podcaster.core.opml.model.Head
import com.mr3y.podcaster.core.opml.model.Opml
import com.mr3y.podcaster.core.opml.model.OpmlPodcast
import com.mr3y.podcaster.core.opml.model.Outline
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpmlAdapter @Inject constructor(
    private val xmlInstance: XML,
    private val logger: Logger
) {

    fun decode(content: String): Result<List<OpmlPodcast>, Any> {
        return try {
            val opml = xmlInstance.decodeFromString(serializer<Opml>(), content)
            val opmlFeeds = mutableListOf<OpmlPodcast>()

            fun flatten(outline: Outline) {
                if (outline.outlines.isNullOrEmpty() && !outline.xmlUrl.isNullOrBlank()) {
                    opmlFeeds.add(mapOutlineToOpmlPodcast(outline))
                }

                outline.outlines?.forEach { nestedOutline -> flatten(nestedOutline) }
            }

            opml.body.outlines.forEach { outline -> flatten(outline) }

            Ok(opmlFeeds.distinctBy { it.link })
        } catch (ex: Exception) {
            logger.e(ex, tag = "OpmlAdapter") {
                "Exception occurred on decoding Opml podcasts from content $content"
            }
            Err(ex)
        }
    }

    fun encode(podcasts: List<Podcast>): Result<String, Any> {
        return try {
            val opml = Opml(
                version = "2.0",
                head = Head("Podcaster Subscriptions", dateCreated = null),
                body = Body(outlines = podcasts.map(::mapPodcastToOutline))
            )

            val xmlString = xmlInstance.encodeToString(serializer<Opml>(), opml)

            StringBuilder(xmlString)
                .insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n")
                .appendLine()
                .toString()
                .let {
                    Ok(it)
                }
        } catch (ex: Exception) {
            logger.e(ex, tag = "OpmlAdapter") {
                "Exception occurred on encoding Opml podcasts $podcasts"
            }
            Err(ex)
        }
    }

    private fun mapPodcastToOutline(podcast: Podcast) =
        Outline(text = podcast.title, title = podcast.title, type = "rss", xmlUrl = podcast.podcastUrl, htmlUrl = podcast.website, outlines = null)

    private fun mapOutlineToOpmlPodcast(outline: Outline): OpmlPodcast {
        return OpmlPodcast(title = outline.title ?: outline.text, link = outline.xmlUrl!!)
    }
}
