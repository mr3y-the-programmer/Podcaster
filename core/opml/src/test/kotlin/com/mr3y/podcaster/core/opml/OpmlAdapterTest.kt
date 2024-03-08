package com.mr3y.podcaster.core.opml

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.github.michaelbull.result.Ok
import com.mr3y.podcaster.core.logger.TestLogger
import com.mr3y.podcaster.core.opml.model.OpmlPodcast
import com.mr3y.podcaster.core.sampledata.Podcasts
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Before
import org.junit.Test

class OpmlAdapterTest {

    private lateinit var sut: OpmlAdapter

    @Before
    fun setUp() {
        val xmlInstance = XML {
            autoPolymorphic = true
            indentString = "  "
            defaultPolicy {
                pedantic = false
                ignoreUnknownChildren()
            }
        }
        sut = OpmlAdapter(xmlInstance, TestLogger())
    }

    @Test
    fun `test decoding is working as expected`() {
        val pocketCastsExportedOpml = """
            <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
            <opml version="1.0">
              <head>
                <title>Pocket Casts Feeds</title>
              </head>
              <body>
                <outline text="feeds">
                  <outline type="rss" text="Android Developers Backstage" xmlUrl="https://adbackstage.libsyn.com/rss" />
                  <outline type="rss" text="Waveform: The MKBHD Podcast" xmlUrl="https://feeds.megaphone.fm/STU4418364045" />
                  <outline type="rss" text="Now in Android" xmlUrl="https://nowinandroid.libsyn.com/rss" />
                </outline>
              </body>
            </opml>
        """.trimIndent()
        val expectedPocketCastsFeeds = setOf(
            OpmlPodcast(title = "Android Developers Backstage", link = "https://adbackstage.libsyn.com/rss"),
            OpmlPodcast(title = "Waveform: The MKBHD Podcast", link = "https://feeds.megaphone.fm/STU4418364045"),
            OpmlPodcast(title = "Now in Android", link = "https://nowinandroid.libsyn.com/rss"),
        )

        var result = sut.decode(pocketCastsExportedOpml)
        assertThat(result).isInstanceOf<Ok<List<OpmlPodcast>>>()
        result as Ok<List<OpmlPodcast>>
        assertThat(result.value.toSet()).isEqualTo(expectedPocketCastsFeeds)

        val antennaPodExportedOpml = """
            <?xml version='1.0' encoding='UTF-8' standalone='no' ?>
            <opml version="2.0">
              <head>
                <title>AntennaPod Subscriptions</title>
                <dateCreated>08 Mar 24 15:45:10 +0200</dateCreated>
              </head>
              <body>
                <outline text="Android Developers Backstage" title="Android Developers Backstage" type="rss" xmlUrl="https://adbackstage.libsyn.com/rss" htmlUrl="http://androidbackstage.blogspot.com/" />
              </body>
            </opml>
        """.trimIndent()
        val expectedAntennaPodFeeds = setOf(
            OpmlPodcast(title = "Android Developers Backstage", link = "https://adbackstage.libsyn.com/rss"),
        )

        result = sut.decode(antennaPodExportedOpml)
        assertThat(result).isInstanceOf<Ok<List<OpmlPodcast>>>()
        result as Ok<List<OpmlPodcast>>
        assertThat(result.value.toSet()).isEqualTo(expectedAntennaPodFeeds)
    }

    @Test
    fun `test encoding is working as expected`() {
        val subscriptions = Podcasts.take(2)
        val expectedExportedResult = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
            <opml version="2.0">
              <head>
                <title>Podcaster Subscriptions</title>
              </head>
              <body>
                <outline title="Fragmented - An Android Developer Podcast" text="Fragmented - An Android Developer Podcast" type="rss" xmlUrl="https://feeds.simplecast.com/LpAGSLnY" htmlUrl="http://www.fragmentedpodcast.com" />
                <outline title="Android Police" text="Android Police" type="rss" xmlUrl="http://feeds.feedburner.com/AndroidPolicePodcast" htmlUrl="https://www.androidpolice.com" />
              </body>
            </opml>
            
        """.trimIndent()

        val result = sut.encode(subscriptions)
        assertThat(result).isInstanceOf<Ok<String>>()
        result as Ok<String>
        assertThat(result.value).isEqualTo(expectedExportedResult)
    }
}
