package com.mr3y.podcaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.State
import coil3.request.ImageRequest

@Composable
fun CoilImage(
    artworkUrl: String,
    sharedTransitionKey: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    error: Painter? = rememberVectorPainter(Placeholder),
    fallback: Painter? = error,
    contentScale: ContentScale = ContentScale.Fit,
    config: (ImageRequest.Builder.() -> ImageRequest.Builder) = { this },
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(artworkUrl)
            .config()
            .placeholderMemoryCacheKey(sharedTransitionKey)
            .memoryCacheKey(sharedTransitionKey)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        error = error,
        fallback = fallback,
        contentScale = contentScale,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
    )
}

@Composable
fun CoilImage(
    artworkUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    error: Painter? = rememberVectorPainter(Placeholder),
    fallback: Painter? = error,
    contentScale: ContentScale = ContentScale.Fit,
    config: (ImageRequest.Builder.() -> ImageRequest.Builder) = { this },
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(artworkUrl)
            .config()
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        error = error,
        fallback = fallback,
        contentScale = contentScale,
    )
}

val Placeholder: ImageVector
    get() {
        if (_Placeholder != null) {
            return _Placeholder!!
        }
        _Placeholder = ImageVector.Builder(
            name = "Placeholder",
            defaultWidth = 180.dp,
            defaultHeight = 139.dp,
            viewportWidth = 180f,
            viewportHeight = 139f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFFD0D0D0)),
                    strokeLineWidth = 1f
                ) {
                    moveTo(0.001f, 0f)
                    lineTo(180.12f, 0f)
                    lineTo(180.12f, 139.794f)
                    lineTo(0.001f, 139.794f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFFFFF)),
                    strokeLineWidth = 1f
                ) {
                    moveTo(104.917f, 66.875f)
                    lineTo(70.668f, 101.124f)
                    lineTo(54.7f, 85.156f)
                    lineTo(12.762f, 127.093f)
                    lineTo(165.136f, 127.093f)
                    close()
                    moveTo(44.627f, 30.143f)
                    curveTo(41.506f, 30.143f, 38.509f, 31.384f, 36.302f, 33.591f)
                    curveTo(34.095f, 35.798f, 32.854f, 38.795f, 32.854f, 41.916f)
                    curveTo(32.854f, 45.037f, 34.095f, 48.034f, 36.302f, 50.241f)
                    curveTo(38.509f, 52.448f, 41.506f, 53.689f, 44.627f, 53.689f)
                    curveTo(47.748f, 53.689f, 50.745f, 52.448f, 52.952f, 50.241f)
                    curveTo(55.159f, 48.034f, 56.4f, 45.037f, 56.4f, 41.916f)
                    curveTo(56.4f, 38.795f, 55.159f, 35.798f, 52.952f, 33.591f)
                    curveTo(50.745f, 31.384f, 47.748f, 30.143f, 44.627f, 30.143f)
                    close()
                }
            }
        }.build()

        return _Placeholder!!
    }

@Suppress("ObjectPropertyName")
private var _Placeholder: ImageVector? = null
