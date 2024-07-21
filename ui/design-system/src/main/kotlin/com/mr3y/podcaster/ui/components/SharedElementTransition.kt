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
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment.Companion.Center
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
            visibilityThreshold = Rect.VisibilityThreshold,
        )
    },
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null) {
        this
    } else {
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
}

fun Modifier.sharedBounds(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    state: SharedTransitionScope.SharedContentState?,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    boundsTransform: BoundsTransform = BoundsTransform { _, _ ->
        spring(
            stiffness = StiffnessMediumLow,
            visibilityThreshold = Rect.VisibilityThreshold,
        )
    },
    resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Center),
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier {
    return if (sharedTransitionScope == null || animatedVisibilityScope == null || state == null) {
        this
    } else {
        with(sharedTransitionScope) {
            sharedBounds(
                state,
                animatedVisibilityScope,
                enter,
                exit,
                boundsTransform,
                resizeMode,
                placeHolderSize,
                renderInOverlayDuringTransition,
                zIndexInOverlay,
                clipInOverlayDuringTransition,
            )
        }
    }
}

fun Modifier.renderInSharedTransitionScopeOverlay(
    sharedTransitionScope: SharedTransitionScope?,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: (LayoutDirection, Density) -> Path? = { _, _ -> null },
): Modifier {
    return if (sharedTransitionScope == null) {
        this
    } else {
        with(sharedTransitionScope) {
            renderInSharedTransitionScopeOverlay(
                zIndexInOverlay = zIndexInOverlay,
                clipInOverlayDuringTransition = clipInOverlayDuringTransition,
            )
        }
    }
}

fun Modifier.animateEnterExit(
    animatedVisibilityScope: AnimatedVisibilityScope?,
): Modifier {
    return if (animatedVisibilityScope == null) {
        this
    } else {
        with(animatedVisibilityScope) {
            animateEnterExit(
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
            )
        }
    }
}

fun Modifier.skipToLookaheadSize(
    sharedTransitionScope: SharedTransitionScope?,
): Modifier {
    return if (sharedTransitionScope == null) {
        this
    } else {
        with(sharedTransitionScope) {
            skipToLookaheadSize()
        }
    }
}

@ExperimentalSharedTransitionApi
private val ParentClip: OverlayClip =
    object : OverlayClip {
        override fun getClipPath(
            state: SharedTransitionScope.SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Path? {
            return state.parentSharedContentState?.clipPathInOverlay
        }
    }
