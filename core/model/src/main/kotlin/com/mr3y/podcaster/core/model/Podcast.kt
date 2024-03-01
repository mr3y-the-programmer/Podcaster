package com.mr3y.podcaster.core.model

data class Podcast(
    val id: Long,
    val guid: String,
    val title: String,
    val description: String,
    val podcastUrl: String,
    val website: String,
    val artworkUrl: String,
    val author: String,
    val owner: String,
    val languageCode: String,
    val episodeCount: Int,
    val genres: List<Genre>,
) {
    override fun toString(): String {
        return "\nPodcast(\n" +
            "id = ${id}L,\n" +
            "guid = \"$guid\",\n" +
            "title = \"$title\",\n" +
            "description = \"$description\",\n" +
            "podcastUrl = \"$podcastUrl\",\n" +
            "website = \"$website\",\n" +
            "artworkUrl = \"$artworkUrl\",\n" +
            "author = \"$author\",\n" +
            "owner = \"$owner\",\n" +
            "languageCode = \"$languageCode\",\n" +
            "episodeCount = $episodeCount,\n" +
            "genres = $genres\n" +
            ")"
    }
}

data class Genre(
    val id: Int,
    val label: String,
)
