package com.mr3y.podcaster.ui.presenter.downloads

import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata

data class DownloadsUIState(
    val isLoading: Boolean,
    val downloads: List<EpisodeWithDownloadMetadata>,
)
