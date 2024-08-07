package com.mr3y.podcaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.State
import coil3.request.ImageRequest

@Composable
fun CoilImage(
    artworkUrl: String,
    sharedTransitionKey: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    config: (ImageRequest.Builder.() -> ImageRequest.Builder) = { this },
    onState: ((State) -> Unit)? = null,
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
        contentScale = contentScale,
        onState = onState,
    )
}
