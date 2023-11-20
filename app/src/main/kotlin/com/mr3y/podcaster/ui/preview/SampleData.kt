package com.mr3y.podcaster.ui.preview

val Podcasts = listOf(
    Podcast(
        id = 741941L,
        title = "Fragmented - An Android Developer Podcast",
        description = "The Fragmented Podcast is a podcast for Android Developers hosted by Donn Felker and Kaushik Gopal. Our goal is to help you become a better Android Developer. We chat about topics such as Testing, Dependency Injection,  Patterns and Practices, useful libraries, and much more. We will also be interviewing some of the top developers out there. Subscribe now and join us on the journey of becoming a better Android Developer.",
        podcastUrl = "https:\\/\\/feeds.simplecast.com\\/LpAGSLnY",
        artworkUrl = "https:\\/\\/image.simplecastcdn.com\\/images\\/528a06b0-814e-4f75-bf94-f89b84d2a943\\/06a29d17-2373-40e2-b733-5c9037ba8443\\/3000x3000\\/light.jpg?aid=rss_feed",
    ),
    Podcast(
        id = 170143L,
        title = "Android Developers Backstage",
        description = "Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.\\n\\nSubscribe to Android Developers YouTube \\u2192 https:\\/\\/goo.gle\\/AndroidDevs",
        podcastUrl = "https:\\/\\/adbackstage.libsyn.com\\/rss",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    ),
    Podcast(
        id = 721941L,
        title = "Fragmented - An Android Developer Podcast",
        description = "The Fragmented Podcast is a podcast for Android Developers hosted by Donn Felker and Kaushik Gopal. Our goal is to help you become a better Android Developer. We chat about topics such as Testing, Dependency Injection,  Patterns and Practices, useful libraries, and much more. We will also be interviewing some of the top developers out there. Subscribe now and join us on the journey of becoming a better Android Developer.",
        podcastUrl = "https:\\/\\/feeds.simplecast.com\\/LpAGSLnY",
        artworkUrl = "https:\\/\\/image.simplecastcdn.com\\/images\\/528a06b0-814e-4f75-bf94-f89b84d2a943\\/06a29d17-2373-40e2-b733-5c9037ba8443\\/3000x3000\\/light.jpg?aid=rss_feed",
    ),
    Podcast(
        id = 160143L,
        title = "Android Developers Backstage",
        description = "Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.\\n\\nSubscribe to Android Developers YouTube \\u2192 https:\\/\\/goo.gle\\/AndroidDevs",
        podcastUrl = "https:\\/\\/adbackstage.libsyn.com\\/rss",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    ),
    Podcast(
        id = 741441L,
        title = "Fragmented - An Android Developer Podcast",
        description = "The Fragmented Podcast is a podcast for Android Developers hosted by Donn Felker and Kaushik Gopal. Our goal is to help you become a better Android Developer. We chat about topics such as Testing, Dependency Injection,  Patterns and Practices, useful libraries, and much more. We will also be interviewing some of the top developers out there. Subscribe now and join us on the journey of becoming a better Android Developer.",
        podcastUrl = "https:\\/\\/feeds.simplecast.com\\/LpAGSLnY",
        artworkUrl = "https:\\/\\/image.simplecastcdn.com\\/images\\/528a06b0-814e-4f75-bf94-f89b84d2a943\\/06a29d17-2373-40e2-b733-5c9037ba8443\\/3000x3000\\/light.jpg?aid=rss_feed",
    ),
    Podcast(
        id = 175143L,
        title = "Android Developers Backstage",
        description = "Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.\\n\\nSubscribe to Android Developers YouTube \\u2192 https:\\/\\/goo.gle\\/AndroidDevs",
        podcastUrl = "https:\\/\\/adbackstage.libsyn.com\\/rss",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    ),
    Podcast(
        id = 741961L,
        title = "Fragmented - An Android Developer Podcast",
        description = "The Fragmented Podcast is a podcast for Android Developers hosted by Donn Felker and Kaushik Gopal. Our goal is to help you become a better Android Developer. We chat about topics such as Testing, Dependency Injection,  Patterns and Practices, useful libraries, and much more. We will also be interviewing some of the top developers out there. Subscribe now and join us on the journey of becoming a better Android Developer.",
        podcastUrl = "https:\\/\\/feeds.simplecast.com\\/LpAGSLnY",
        artworkUrl = "https:\\/\\/image.simplecastcdn.com\\/images\\/528a06b0-814e-4f75-bf94-f89b84d2a943\\/06a29d17-2373-40e2-b733-5c9037ba8443\\/3000x3000\\/light.jpg?aid=rss_feed",
    ),
    Podcast(
        id = 173243L,
        title = "Android Developers Backstage",
        description = "Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.\\n\\nSubscribe to Android Developers YouTube \\u2192 https:\\/\\/goo.gle\\/AndroidDevs",
        podcastUrl = "https:\\/\\/adbackstage.libsyn.com\\/rss",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    )
)


data class Podcast(
    val id: Long,
    val title: String,
    val description: String,
    val podcastUrl: String,
    val artworkUrl: String
)

val Episodes = listOf(
    Episode(
        id = 16066386437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    ),
    Episode(
        id = 17066386437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    ),
    Episode(
        id = 18066386437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    ),
    Episode(
        id = 19066386437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    ),
    Episode(
        id = 12066386437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    ),
    Episode(
        id = 12066786437L,
        title = "AndroidX, Gradle and Metalava",
        description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p >Romain: @romainguy,\\u00a0threads.net\\/@romainguy, romainguy@androiddev.social<\\/p> <p >Tor: threads.net\\/@tor.norbye and tornorbye@androiddev.social<\\/p> <p>Aurimas: androiddev.social\\/@Aurimas and www.liutikas.net\\/blog-posts<\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0https:\\/\\/goo.gle\\/adb-podcast\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Develope...",
        podcastId = 170143L,
        episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
        artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png"
    )
)

data class Episode(
    val id: Long,
    val title: String,
    val description: String,
    val podcastId: Long,
    val episodeUrl: String,
    val artworkUrl: String
)

val PodcastWithDetails = PodcastDetails(
    id = 170143L,
    title = "Android Developers Backstage",
    description = "Android Backstage, a podcast by and for Android developers. Hosted by developers from the Android engineering team, this show covers topics of interest to Android programmers, with in-depth discussions and interviews with engineers on the Android team at Google.\\n\\nSubscribe to Android Developers YouTube \\u2192 https:\\/\\/goo.gle\\/AndroidDevs",
    podcastUrl = "https:\\/\\/adbackstage.libsyn.com\\/rss",
    website = "http:\\/\\/androidbackstage.blogspot.com\\/",
    artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    author = "Android Developers",
    owner = "Android Developers",
    languageCode = "en",
    episodeCount = 202,
    genres = listOf(
        Genre(
            id = 102,
            label = "Technology"
        ),
        Genre(
            id = 55,
            label = "News"
        )
    )
)

data class PodcastDetails(
    val id: Long,
    val title: String,
    val description: String,
    val podcastUrl: String,
    val website: String,
    val artworkUrl: String,
    val author: String,
    val owner: String,
    val languageCode: String,
    val episodeCount: Int,
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val label: String
)

val EpisodeWithDetails = EpisodeDetails(
    id = 16066386437L,
    title = "AndroidX, Gradle and Metalava",
    description = "<p>In this episode, Tor and Romain chat with Aurimas Liutikas from the AndroidX team. Topics include performance tuning the AndroidX Gradle builds using configuration caching, local caching and remote caching, as well as tracking API compatibility using the Metalava tool.<\\/p> <p><img src= \\\"https:\\/\\/assets.libsyn.com\\/secure\\/show\\/332855\\/ADB_202_Thumbnail.jpg\\\" alt=\\\"\\\" width=\\\"420\\\" height=\\\"236\\\" \\/><\\/p> <p>Aurimas, Romain and Tor<\\/p> <p>\\u00a0<\\/p> <p dir=\\\"ltr\\\">Romain: <a href= \\\"https:\\/\\/twitter.com\\/romainguy\\\">@romainguy<\\/a>,\\u00a0<a href= \\\"https:\\/\\/www.threads.net\\/@romainguy\\\">threads.net\\/@romainguy<\\/a>, <a href= \\\"https:\\/\\/androiddev.social\\/@romainguy\\\">romainguy@androiddev.social<\\/a><\\/p> <p dir=\\\"ltr\\\">Tor: <a href= \\\"https:\\/\\/www.threads.net\\/@tor.norbye\\\">threads.net\\/@tor.norbye<\\/a> and <a href= \\\"https:\\/\\/androiddev.social\\/@tornorbye\\\">tornorbye@androiddev.social<\\/a><\\/p> <p>Aurimas: <a href= \\\"https:\\/\\/androiddev.social\\/@Aurimas\\\">androiddev.social\\/@Aurimas<\\/a> and <a href= \\\"https:\\/\\/www.liutikas.net\\/blog-posts\\\">www.liutikas.net\\/blog-posts<\\/a><\\/p> <p>\\u00a0<\\/p> <p>Catch videos on YouTube \\u2192\\u00a0<a href= \\\"https:\\/\\/goo.gle\\/adb-podcast\\\">https:\\/\\/goo.gle\\/adb-podcast<\\/a>\\u00a0 \\u00a0<\\/p> <p>Subscribe to Android Developers \\u00a0\\u2192 <a href= \\\"https:\\/\\/goo.gle\\/AndroidDevs\\\">https:\\/\\/goo.gle\\/AndroidDevs<\\/a>\\u00a0\\u00a0<\\/p>",
    episodeUrl = "http:\\/\\/adbackstage.libsyn.com\\/episode-202-androidx-gradle-and-metalava",
    datePublishedFormatted = "November 01, 2023 12:19pm",
    durationInSec = 3236,
    episodeNum = null,
    artworkUrl = "https:\\/\\/static.libsyn.com\\/p\\/assets\\/c\\/9\\/e\\/0\\/c9e07a90cf263f3b40be95ea3302a6a1\\/Android_Devs_Backstage_Thumb_v2.png",
    enclosureUrl = "https:\\/\\/traffic.libsyn.com\\/secure\\/adbackstage\\/ADB202_final.mp3?dest-id=2710847",
    enclosureSizeInBytes = 78634055L,
    podcastId = 170143L,
    podcastTitle = "Android Developers Backstage"
)

data class EpisodeDetails(
    val id: Long,
    val title: String,
    val description: String,
    val episodeUrl: String,
    val datePublishedFormatted: String,
    val durationInSec: Int?,
    val episodeNum: Int?,
    val artworkUrl: String,
    val enclosureUrl: String,
    val enclosureSizeInBytes: Long,
    val podcastId: Long,
    val podcastTitle: String
)