package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun DownloadButton(
    downloadMetadata: EpisodeDownloadMetadata?,
    onDownload: () -> Unit,
    onResumingDownload: () -> Unit,
    onPausingDownload: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.tertiaryPrimary,
    inactiveOutlineColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
) {
    when (val status = downloadMetadata?.downloadStatus) {
        EpisodeDownloadStatus.NotDownloaded, EpisodeDownloadStatus.Downloaded, null -> {
            val isDownloaded = status == EpisodeDownloadStatus.Downloaded
            val borderColor = if (isDownloaded) contentColor.copy(alpha = 0.38f) else contentColor
            OutlinedIconButton(
                onClick = if (!isDownloaded) {
                    onDownload
                } else {
                    {}
                },
                modifier = modifier,
                enabled = !isDownloaded,
                shape = CircleShape,
                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = contentColor),
                border = BorderStroke(1.dp, borderColor),
            ) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                )
            }
        }
        else -> {
            val isPaused = status == EpisodeDownloadStatus.Paused
            val outlineStroke = with(LocalDensity.current) {
                Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            }
            OutlinedIconButton(
                onClick = if (isPaused) onResumingDownload else onPausingDownload,
                modifier = modifier,
                shape = CircleShape,
                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = contentColor),
                border = null,
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Filled.ArrowDownward else Icons.Filled.Square,
                    contentDescription = null,
                    modifier = Modifier
                        .drawBehind {
                            drawCircle(
                                color = inactiveOutlineColor,
                                style = outlineStroke,
                            )
                            drawArc(
                                color = contentColor,
                                startAngle = 270f,
                                sweepAngle = downloadMetadata.downloadProgress * 360f,
                                useCenter = false,
                                style = outlineStroke,
                            )
                        }
                        .padding(8.dp),
                )
            }
        }
    }
}

@PodcasterPreview
@Composable
fun DownloadButtonNotDownloadedPreview() {
    PodcasterTheme(dynamicColor = false) {
        DownloadButton(
            downloadMetadata = EpisodeDownloadMetadata(
                episodeId = 17870829L,
                downloadStatus = EpisodeDownloadStatus.NotDownloaded,
                downloadProgress = 0f,
            ),
            onDownload = { },
            onResumingDownload = {},
            onPausingDownload = {},
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadButtonPausedPreview() {
    PodcasterTheme(dynamicColor = false) {
        DownloadButton(
            downloadMetadata = EpisodeDownloadMetadata(
                episodeId = 17870829L,
                downloadStatus = EpisodeDownloadStatus.Paused,
                downloadProgress = 0.5f,
            ),
            onDownload = { },
            onResumingDownload = {},
            onPausingDownload = {},
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadButtonQueuedResumedPreview() {
    PodcasterTheme(dynamicColor = false) {
        DownloadButton(
            downloadMetadata = EpisodeDownloadMetadata(
                episodeId = 17870829L,
                downloadStatus = EpisodeDownloadStatus.Downloading,
                downloadProgress = 0.5f,
            ),
            onDownload = { },
            onResumingDownload = {},
            onPausingDownload = {},
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadButtonDownloadedPreview() {
    PodcasterTheme(dynamicColor = false) {
        DownloadButton(
            downloadMetadata = EpisodeDownloadMetadata(
                episodeId = 17870829L,
                downloadStatus = EpisodeDownloadStatus.Downloaded,
                downloadProgress = 0f,
            ),
            onDownload = { },
            onResumingDownload = {},
            onPausingDownload = {},
        )
    }
}
