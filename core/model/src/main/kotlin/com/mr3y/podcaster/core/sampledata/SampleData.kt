package com.mr3y.podcaster.core.sampledata

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.Genre
import com.mr3y.podcaster.core.model.Podcast

val Podcasts = listOf(
    Podcast(
        id = 741941L,
        guid = "1fff9550-8e93-5c5b-ab6e-94cc6be73a34",
        title = "Fragmented - An Android Developer Podcast",
        description = "The Fragmented Podcast is a podcast for Android Developers hosted by Donn Felker and Kaushik Gopal. Our goal is to help you become a better Android Developer. We chat about topics such as Testing, Dependency Injection,  Patterns and Practices, useful libraries, and much more. We will also be interviewing some of the top developers out there. Subscribe now and join us on the journey of becoming a better Android Developer.",
        podcastUrl = "https://feeds.simplecast.com/LpAGSLnY",
        website = "http://www.fragmentedpodcast.com",
        artworkUrl = "https://image.simplecastcdn.com/images/528a06b0-814e-4f75-bf94-f89b84d2a943/06a29d17-2373-40e2-b733-5c9037ba8443/3000x3000/light.jpg?aid=rss_feed",
        author = "Donn Felker, Kaushik Gopal",
        owner = "Donn Felker & Kaushik Gopal",
        languageCode = "en-us",
        episodeCount = 250,
        genres = listOf(Genre(id = 102, label = "Technology")),
    ),
    Podcast(
        id = 1033257L,
        guid = "6252780a-40e6-5b99-9604-baa46d5440fc",
        title = "Android Police",
        description = "Subscribe to AndroidPolice.com's weekly podcast where we discuss the news that hit the site in the week prior. Approximate duration of each show is 45-60 minutes.",
        podcastUrl = "http://feeds.feedburner.com/AndroidPolicePodcast",
        website = "https://www.androidpolice.com",
        artworkUrl = "https://artwork.captivate.fm/595c52ab-deab-4a5c-81ed-bc8126b28417/aJ0LG8Dql_Uq1q-1GnH_04cY.jpg",
        author = "AndroidPolice.com staff",
        owner = "AndroidPolice.com staff",
        languageCode = "en",
        episodeCount = 45,
        genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")),
    ),
    Podcast(
        id = 320527L,
        guid = "6bd91a49-3115-52ab-a005-c5bd48b3bc1a",
        title = "Android Authority Podcast",
        description = """The Android Authority Podcast – discussing topics in Android every week.
                The Android Authority Podcast brings you all the top stories and features based on your favorite mobile operating system: Android. We help make Android more accessible to all, and dive deeper into the details when we can, there's something in here for everyone.
                Check out androidauthority.com for all the best news and reviews for your favorite phones and tablets, then join our community forums to join in on the discussion. Don't forget to hit our YouTube channel for even more.
        """.trimIndent(),
        podcastUrl = "https://androidauthority.libsyn.com/rss",
        website = "http://www.androidauthority.com/podcast",
        artworkUrl = "https://static.libsyn.com/p/assets/3/a/e/a/3aea54f506f92979/1400px_-_Margin_FullBG.png",
        author = "AndroidAuthority.com",
        owner = "AndroidAuthority.com",
        languageCode = "en",
        episodeCount = 270,
        genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")),
    ),
    Podcast(
        id = 573341L,
        guid = "4cd07da6-cc70-5e79-b6df-29f2960d9fb9",
        title = "Android Strength Podcast",
        description = "Welcome to the AndroidStrength podcast! Everyday, ordinary people sacrifice everything to transform their bodies. They commit their lives to push themselves beyond the limit. They are on a journey to become extraordinary. These are their stories.",
        podcastUrl = "https://anchor.fm/s/699d5da0/podcast/rss",
        website = "http://www.androidstrength.com",
        artworkUrl = "https://d3t3ozftmdmh3i.cloudfront.net/staging/podcast_uploaded_nologo/17619208/33eaa27f2f0c425b.jpeg",
        author = "Marc Mulzer",
        owner = "Marc Mulzer",
        languageCode = "en-us",
        episodeCount = 4,
        genres = listOf(Genre(id = 29, label = "Health"), Genre(id = 30, label = "Fitness")),
    ),
    Podcast(
        id = 1004348L,
        guid = "ad6a6e01-8083-5e3f-90f7-5fa4fe67d65a",
        title = "App Reviews - New iOS and Android Apps Review",
        description = "Our purpose is to provide you insight into the very best in iOS and Android applications through reviews and latest news sections. Our goal is to sort through the best of the best and provide you with the information you need to choose — so you don’t have to spend your days in front of a mobile phone just to find the perfect app for you.",
        podcastUrl = "https://appreviewspod.podbean.com/feed.xml",
        website = "https://appreviewspod.podbean.com",
        artworkUrl = "https://pbcdn1.podbean.com/imglogo/image-logo/763889/app-reviews-2.png",
        author = "Timur Taepov",
        owner = "Timur Taepov",
        languageCode = "en",
        episodeCount = 1,
        genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")),
    ),
    Podcast(
        id = 85131L,
        guid = "3213cd55-7ed8-53f3-afa8-202d72ad996b",
        title = "Android's Amazing Podcast",
        description = "Welcome to Android's Amazing Podcast a comic book podcast sponsored by Android's Amazing Comics in Sayville, NY! James Santana and Hunter van Lierop bring you comic book news, reviews, and interesting dives into the wonderful world of comic books!",
        podcastUrl = "https://anchor.fm/s/b364a24/podcast/rss",
        website = "https://www.androidscomics.com/",
        artworkUrl = "https://d3t3ozftmdmh3i.cloudfront.net/production/podcast_uploaded_nologo/1781073/1781073-1659014636699-b17cae1e5fc73.jpg",
        author = "Android's Amazing Comics",
        owner = "Android's Amazing Comics",
        languageCode = "en",
        episodeCount = 231,
        genres = listOf(Genre(id = 42, label = "Leisure"), Genre(id = 49, label = "Hobbies")),
    ),
    Podcast(
        id = 456857L,
        guid = "e1fcce27-9145-5205-b153-2ce8891ef075",
        title = "Now in Android",
        description = """This show gives listeners a quick run-down on things that the Android team has done recently that developers may want to check out. It covers library and platform releases, articles, videos, podcasts, samples, codelabs - whatever seems relevant and interesting for Android developers.
                Subscribe to Android Developers YouTube → https://goo.gle/AndroidDevs
                Android’s a big platform and there are many things being released all the time; listen to this podcast to stay up to date on what those things are.
        """.trimIndent(),
        podcastUrl = "https://nowinandroid.libsyn.com/rss",
        website = "http://d.android.com",
        artworkUrl = "https://static.libsyn.com/p/assets/b/9/1/c/b91cd7167d326f8a/2020-01-30.png",
        author = "Now in Android",
        owner = "Google Developer Studio",
        languageCode = "en",
        episodeCount = 87,
        genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 67, label = "Science")),
    ),
    Podcast(
        id = 170143L,
        guid = "5865f850-e68b-56b3-9599-b2aaf51f8b03",
        title = "Android Developers Backstage",
        description = """Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.
                        Subscribe to Android Developers YouTube → https://goo.gle/AndroidDevs
        """.trimIndent(),
        podcastUrl = "https://adbackstage.libsyn.com/rss",
        website = "http://androidbackstage.blogspot.com/",
        artworkUrl = "https://static.libsyn.com/p/assets/c/9/e/0/c9e07a90cf263f3b40be95ea3302a6a1/Android_Devs_Backstage_Thumb_v2.png",
        author = "Android Developers",
        owner = "Android Developers",
        languageCode = "en",
        episodeCount = 202,
        genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")),
    ),
)

val Episodes = listOf(
    Episode(
        id = 14319430597L,
        podcastId = 170143L,
        guid = "31f84f8f-185c-4a77-a4c3-dcba511e75f5",
        title = "Android Studio, behind the scenes",
        description = "<p>Raluca Sauciuc joins Tor and Romain to talk about what goes on behind the scenes in Android Studio. Raluca takes us through the tools and workflows used by the Android Studio team to improve performance and memory usage, and avoid future regressions. She also explains how the team adopts new versions of the IntelliJ IDE and platform, and how they can deal with massive code merges.</p> <p >Raluca, Romain, and Tor</p> <p >Romain: @romainguy and romainguy@androiddev.social</p> <p >Tor: @tornorbye and tornorbye@androiddev.social</p> <p>Chet: @chethaase and chethaase@androiddev.social</p> <p>Subscribe to A...",
        episodeUrl = "http://adbackstage.libsyn.com/episode-196-android-studio-behind-the-scenes",
        datePublishedTimestamp = 1679506275L,
        datePublishedFormatted = "March 22, 2023 12:31pm",
        durationInSec = 2948,
        episodeNum = null,
        artworkUrl = "",
        enclosureUrl = "https://traffic.libsyn.com/secure/adbackstage/ADB196_v1.mp3?dest-id=2710847",
        enclosureSizeInBytes = 70728192L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
    Episode(
        id = 16899052117L,
        podcastId = 456857L,
        guid = "ee3b9851-d1f9-4f40-9ee9-2bd10e9c0d40",
        title = "Now in Android: 96 - New APIs for adaptive layouts, Google Play updates, and more!",
        description = "<p>Welcome to Now in Android, your ongoing guide to what’s new and notable in the world of Android development. Today, we’re covering updates on the new APIs for adaptive layouts in Jetpack Compose, the latest from Google Play, and more!</p> <p>For links to these items, check out Now in Android #96 on Medium → https://goo.gle/3uJJqMJ<br /> <br /> Now in Android podcast → https://goo.gle/2BDIo9y            <br /> Now in Android articles → https://goo.gle/2xtWmsu         <br /> <br /> Now in Android playlist → https://goo.gle/now-in-android           <br /> Subscribe to Android Developers → https://goo.gle/AndroidDevs</p>",
        episodeUrl = "http://nowinandroid.libsyn.com/96-new-apis-for-adaptive-layouts-google-play-updates-and-more",
        datePublishedTimestamp = 1701293769L,
        datePublishedFormatted = "November 29, 2023 3:36pm",
        durationInSec = 145,
        episodeNum = null,
        artworkUrl = "https://static.libsyn.com/p/assets/3/5/1/b/351b94906e4f1393e5bbc093207a2619/NIA096_Podcast_Thumb.png",
        enclosureUrl = "https://traffic.libsyn.com/secure/nowinandroid/NIA096_v1.mp3?dest-id=1831685",
        enclosureSizeInBytes = 3824746L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
    Episode(
        id = 15576747231L,
        podcastId = 456857L,
        guid = "fb56eca7-4e44-49ae-896d-9f78395ae2c4",
        title = "Now in Android: 90 - Android brand, ART updates, Dagger KSP, and more!",
        description = "<p>Welcome to Now in Android, your ongoing guide to what’s new and notable in the world of Android development. Today, we’re covering updates on the new modern look for the Android brand, latest ARTwork on hundreds of millions of devices, library releases, articles, and more!</p> <p >For links to these items, check out Now in Android #90 on Medium → https://goo.gle/44JY2Iy </p> <p><br /> Now in Android podcast → https://goo.gle/2BDIo9y           <br /> Now in Android articles → https://goo.gle/2xtWmsu         <br /> <br /> Now in Android playlist → https://goo.gle/now-in-android           <br /> Subscribe to Android Developers → https:",
        episodeUrl = "http://nowinandroid.libsyn.com/90-android-brand-art-updates-dagger-ksp-and-more",
        datePublishedTimestamp = 1694112333L,
        datePublishedFormatted = "September 07, 2023 1:45pm",
        durationInSec = 249,
        episodeNum = null,
        artworkUrl = "https://static.libsyn.com/p/assets/8/1/f/4/81f4055b310d94c0e5bbc093207a2619/NIA090_Podcast_Thumb.png",
        enclosureUrl = "https://traffic.libsyn.com/secure/nowinandroid/NIA090_v2.mp3?dest-id=1831685",
        enclosureSizeInBytes = 6354391L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
    Episode(
        id = 2396578917L,
        podcastId = 741941L,
        guid = "47d9f50d-684f-4ab4-8a6d-a8a676eae943",
        title = "211: Why Learning React is Good For You as a Developer",
        description = "<p>In this episode Donn talks about why you need to learn React (or Flutter) - so you can truly understand the Unidirectional data flow pattern in a framework that was built for that purpose alone.</p><p>Working with other frameworks which bolt on a custom unidirectional data flow is often hard to understand. When you work with React and learn how it works, the concept of Unidirectional data flow starts to make much more sense as that is the default way to implement UI's in technologies like React (and Flutter).</p><p>This exposes you to the pattern...",
        episodeUrl = "http://www.fragmentedpodcast.com",
        datePublishedTimestamp = 1621314000L,
        datePublishedFormatted = "May 18, 2021 12:00am",
        durationInSec = 888,
        episodeNum = 211,
        artworkUrl = "",
        enclosureUrl = "https://cdn.simplecast.com/audio/20f35050-e836-44cd-8f7f-fd13e8cb2e44/episodes/b63bf3dd-25c7-417d-9f05-be281581984c/audio/1ff2033d-a444-4941-89cc-16146bec994e/default_tc.mp3?aid=rss_feed&feed=LpAGSLnY",
        enclosureSizeInBytes = 14216363L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
    Episode(
        id = 17536508L,
        podcastId = 320527L,
        guid = "4ee74480-9ee9-4554-9515-023d91198cec",
        title = "Fin Part Deux",
        description = "<p>This is the last episode of the second season of the Android Authority podcast. That sucks. But that doesn’t mean we don’t have tech to talk about. Adam, Joe, and Jonathan gather for one last time to talk about Motorola’s upcoming flagship, and care for your RAZR. LG is keeping the headphone jack flame a’burnin. And finally, we look back on some other operating systems that have come and gone over the years. RIP webOS, RIP Windows Phone, RIP #AAPodcast</p> <p>Podcast Pick: Real Graphene Power Bank 10,000 mAh</p> <p>Visit http://www.andauth.co/Podca...",
        episodeUrl = "http://podcasts.androidauthority.com/fin-part-deux",
        datePublishedTimestamp = 1580514520L,
        datePublishedFormatted = "January 31, 2020 5:48pm",
        durationInSec = 3532,
        episodeNum = 130,
        artworkUrl = "",
        enclosureUrl = "https://traffic.libsyn.com/secure/androidauthority/aapod_fin_part_deux_final.mp3?dest-id=242501",
        enclosureSizeInBytes = 84771967L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
    Episode(
        id = 17870829L,
        podcastId = 573341L,
        guid = "http://www.androidstrength.com/podcast/ed-brown-jr-fear-of-failure",
        title = "Ed Brown Jr: Fear Of Failure",
        description = "Fear of failure. How do you deal with that? How do you recover from failures? How do you keep going and working towards a goal which seems so far away, which seems so hard to achieve and takes so long to get to? Ed Brown Jr is one of those guys who can tell us how. He has dedicated his entire life to becoming a Pro Bodybuilder. He has already been very successful, winning show after show, but not quite there yet. Ed works harder than anybody I know in the gym. He is relentless about pursuing his goal. When...",
        episodeUrl = "http://www.androidstrength.com/podcast/ed-brown-jr-fear-of-failure",
        datePublishedTimestamp = 1465084800L,
        datePublishedFormatted = "June 04, 2016 7:00pm",
        durationInSec = 1,
        episodeNum = null,
        artworkUrl = "http://www.androidstrength.com/assets/androidstrength_podcast_cover.jpg",
        enclosureUrl = "http://www.androidstrength.com/audio/android_strength_podcast_episode_1_ed_brown.mp3",
        enclosureSizeInBytes = 131217150L,
        podcastTitle = null,
        isCompleted = false,
        progressInSec = null,
    ),
)

val EpisodesWithDownloadMetadata = listOf(
    EpisodeWithDownloadMetadata(
        episode = Episodes[0],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[0].id,
            downloadStatus = EpisodeDownloadStatus.NotDownloaded,
            downloadProgress = 0f,
        ),
    ),
    EpisodeWithDownloadMetadata(
        episode = Episodes[1],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[1].id,
            downloadStatus = EpisodeDownloadStatus.Paused,
            downloadProgress = 0.4f,
        ),
    ),
    EpisodeWithDownloadMetadata(
        episode = Episodes[2],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[2].id,
            downloadStatus = EpisodeDownloadStatus.Downloaded,
            downloadProgress = 1f,
        ),
    ),
    EpisodeWithDownloadMetadata(
        episode = Episodes[3],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[3].id,
            downloadStatus = EpisodeDownloadStatus.Downloading,
            downloadProgress = 0.7f,
        ),
    ),
    EpisodeWithDownloadMetadata(
        episode = Episodes[4],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[4].id,
            downloadStatus = EpisodeDownloadStatus.Queued,
            downloadProgress = 0f,
        ),
    ),
    EpisodeWithDownloadMetadata(
        episode = Episodes[5],
        downloadMetadata = EpisodeDownloadMetadata(
            episodeId = Episodes[5].id,
            downloadStatus = EpisodeDownloadStatus.NotDownloaded,
            downloadProgress = 0f,
        ),
    ),
)

val PodcastWithDetails = Podcast(
    id = 170143L,
    guid = "5865f850-e68b-56b3-9599-b2aaf51f8b03",
    title = "Android Developers Backstage",
    description = """Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.
                        Subscribe to Android Developers YouTube → https://goo.gle/AndroidDevs
    """.trimIndent(),
    podcastUrl = "https://adbackstage.libsyn.com/rss",
    website = "http://androidbackstage.blogspot.com/",
    artworkUrl = "https://static.libsyn.com/p/assets/c/9/e/0/c9e07a90cf263f3b40be95ea3302a6a1/Android_Devs_Backstage_Thumb_v2.png",
    author = "Android Developers",
    owner = "Android Developers",
    languageCode = "en",
    episodeCount = 202,
    genres = listOf(Genre(id = 102, label = "Technology"), Genre(id = 55, label = "News")),
)

val EpisodeWithDetails = Episode(
    id = 14319430597L,
    podcastId = 170143L,
    guid = "31f84f8f-185c-4a77-a4c3-dcba511e75f5",
    title = "Android Studio, behind the scenes",
    description = "<p dir=\"ltr\">Raluca Sauciuc joins Tor and Romain to talk about what goes on behind the scenes in Android Studio. Raluca takes us through the tools and workflows used by the Android Studio team to improve performance and memory usage, and avoid future regressions. She also explains how the team adopts new versions of the IntelliJ IDE and platform, and how they can deal with massive code merges.</p> <p dir=\"ltr\">Raluca, Romain, and Tor</p> <p dir=\"ltr\">Romain: <a href= \"https://twitter.com/romainguy\">@romainguy</a> and <a href= \"https://androiddev.social/@romainguy\">romainguy@androiddev.social</a></p> <p dir=\"ltr\">Tor: <a href= \"https://twitter.com/tornorbye\">@tornorbye</a> and <a href= \"https://androiddev.social/@tornorbye\">tornorbye@androiddev.social</a></p> <p>Chet: <a href= \"https://twitter.com/chethaase\">@chethaase</a> and <a href= \"https://androiddev.social/@chethaase\">chethaase@androiddev.social</a></p> <p>Subscribe to Android Developers YouTube → <a href= \"https://goo.gle/AndroidDevs\">https://goo.gle/AndroidDevs</a> </p>",
    episodeUrl = "http://adbackstage.libsyn.com/episode-196-android-studio-behind-the-scenes",
    datePublishedTimestamp = 1679506275L,
    datePublishedFormatted = "March 22, 2023 12:31pm",
    durationInSec = 2948,
    episodeNum = null,
    artworkUrl = "https://static.libsyn.com/p/assets/c/9/e/0/c9e07a90cf263f3b40be95ea3302a6a1/Android_Devs_Backstage_Thumb_v2.png",
    enclosureUrl = "https://traffic.libsyn.com/secure/adbackstage/ADB196_v1.mp3?dest-id=2710847",
    enclosureSizeInBytes = 70728192L,
    podcastTitle = "Android Developers Backstage",
    isCompleted = false,
    progressInSec = null,
)

val DownloadMetadata = EpisodeDownloadMetadata(
    episodeId = 14319430597L,
    downloadStatus = EpisodeDownloadStatus.Downloading,
    downloadProgress = 0.7f,
)
