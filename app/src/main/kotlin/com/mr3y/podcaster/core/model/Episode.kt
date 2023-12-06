package com.mr3y.podcaster.core.model

data class Episode(
    val id: Long,
    val podcastId: Long,
    val guid: String,
    val title: String,
    val description: String,
    val episodeUrl: String,
    val datePublishedTimestamp: Long,
    val datePublishedFormatted: String,
    val durationInSec: Int? = null,
    val episodeNum: Int? = null,
    val artworkUrl: String,
    val enclosureUrl: String,
    val enclosureSizeInBytes: Long,
    val podcastTitle: String? = null,
    val isDownloaded: Boolean = false,
    val isCompleted: Boolean = false,
    val progressInSec: Int? = null
) {
    override fun toString(): String {
        return "\nEpisode(\n" +
            "id = ${id}L,\n" +
            "podcastId = ${podcastId}L,\n" +
            "guid = \"$guid\",\n" +
            "title = \"$title\",\n" +
            "description = \"$description\",\n" +
            "episodeUrl = \"$episodeUrl\",\n" +
            "datePublishedTimestamp = ${datePublishedTimestamp}L,\n" +
            "datePublishedFormatted = \"$datePublishedFormatted\",\n" +
            "durationInSec = ${durationInSec},\n" +
            "episodeNum = ${episodeNum},\n" +
            "artworkUrl = \"$artworkUrl\",\n" +
            "enclosureUrl = \"$enclosureUrl\",\n" +
            "enclosureSizeInBytes = ${enclosureSizeInBytes}L,\n" +
            "podcastTitle = \"$podcastTitle\",\n" +
            "isDownloaded = $isDownloaded,\n" +
            "isCompleted = $isCompleted,\n" +
            "progressInSec = $progressInSec\n" +
        ")"
    }
}
