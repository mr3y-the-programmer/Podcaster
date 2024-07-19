package com.mr3y.podcaster.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

@Composable
fun rememberSharedContentState(key: Any): SharedTransitionScope.SharedContentState? {
    return LocalSharedTransitionScope.current?.rememberSharedContentState(key)
}

fun Modifier.sharedElement(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    state: SharedTransitionScope.SharedContentState?,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ ->
        spring(
            stiffness = StiffnessMediumLow,
            visibilityThreshold = Rect.VisibilityThreshold
        )
    },
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null)
        this
    else
        with(sharedTransitionScope) {
            sharedElement(
                state,
                animatedVisibilityScope,
                boundsTransform,
                placeHolderSize,
                renderInOverlayDuringTransition,
                zIndexInOverlay,
                clipInOverlayDuringTransition,
            )
        }
}

fun Modifier.sharedBounds(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    state: SharedTransitionScope.SharedContentState?,
    enter: EnterTransition = fadeIn() + sharedTransitionScope.scaleInSharedContentToBounds(ContentScale.Fit),
    exit: ExitTransition = fadeOut() + sharedTransitionScope.scaleOutSharedContentToBounds(ContentScale.Fit),
    boundsTransform: BoundsTransform = BoundsTransform { _, _ ->
        spring(
            stiffness = StiffnessMediumLow,
            visibilityThreshold = Rect.VisibilityThreshold
        )
    },
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null)
        this
    else
        with(sharedTransitionScope) {
            sharedBounds(
                state,
                animatedVisibilityScope,
                enter,
                exit,
                boundsTransform,
                placeHolderSize,
                renderInOverlayDuringTransition,
                zIndexInOverlay,
                clipInOverlayDuringTransition,
            )
        }
}

fun Modifier.skipToLookaheadSize(
    sharedTransitionScope: SharedTransitionScope?
): Modifier {
    return if (sharedTransitionScope == null)
        this
    else
        with(sharedTransitionScope) {
            skipToLookaheadSize()
        }
}

private fun SharedTransitionScope?.scaleInSharedContentToBounds(
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
): EnterTransition {
    return this?.scaleInSharedContentToBounds(contentScale, alignment) ?: EnterTransition.None
}

private fun SharedTransitionScope?.scaleOutSharedContentToBounds(
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
): ExitTransition {
    return this?.scaleOutSharedContentToBounds(contentScale, alignment) ?: ExitTransition.None
}

@ExperimentalSharedTransitionApi
private val ParentClip: OverlayClip =
    object : OverlayClip {
        override fun getClipPath(
            state: SharedTransitionScope.SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density
        ): Path? {
            return state.parentSharedContentState?.clipPathInOverlay
        }
    }
