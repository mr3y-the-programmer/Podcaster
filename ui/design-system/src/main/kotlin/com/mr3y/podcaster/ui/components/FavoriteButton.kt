package com.mr3y.podcaster.ui.components

import android.graphics.Matrix
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.core.graphics.PathParser
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.primaryTertiary
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primaryTertiary,
) {
    OutlinedIconToggleButton(
        checked = isFavorite,
        onCheckedChange = onToggle,
        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            checkedContainerColor = Color.Transparent,
            checkedContentColor = contentColor,
        ),
        border = null,
        modifier = modifier,
    ) {
        var showSparkles by remember { mutableStateOf(false) }
        val animatedFillFraction by animateFloatAsState(
            targetValue = if (isFavorite) 0f else 1f,
            animationSpec = tween(durationMillis = 2_000, easing = EaseOutQuart),
            finishedListener = {
                if (isFavorite) {
                    showSparkles = true
                }
            },
            label = "HeartFillAnimation",
        )
        val sparkleAlphaAnimation = remember { Animatable(1f) }
        val sparkleTranslationAnimation = remember { Animatable(1f) }
        LaunchedEffect(showSparkles) {
            if (showSparkles) {
                val job1 = launch {
                    sparkleAlphaAnimation.animateTo(0f, animationSpec = tween(2_000))
                }
                val job2 = launch {
                    sparkleTranslationAnimation.animateTo(1.3f, animationSpec = tween(2_000))
                }
                job1.join()
                job2.join()
                showSparkles = false
            }
            sparkleAlphaAnimation.snapTo(1f)
            sparkleTranslationAnimation.snapTo(1f)
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val heartPath = PathParser.createPathFromPathData(HeartPathData)
                .asComposePath()
                .apply {
                    val pathSize = getBounds().size
                    val matrix = Matrix()
                    matrix.postScale(
                        (size.width * 0.6f / pathSize.width),
                        (size.height * 0.6f / pathSize.height),
                    )
                    matrix.postTranslate(-(pathSize.width * 0.25f), -(pathSize.height * 0.25f))
                    this.asAndroidPath().transform(matrix)
                }
            val pathSize = heartPath.getBounds().size
            translate(left = (size.width - pathSize.width) / 2f, top = (size.height - pathSize.height) / 2f) {
                drawPath(heartPath, contentColor, style = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                clipPath(heartPath) {
                    drawRect(
                        color = contentColor,
                        topLeft = Offset(x = 0f, animatedFillFraction * size.height),
                    )
                }
            }
            if (showSparkles) {
                for (angleInDegrees in 0 until 360 step 30) {
                    val theta = angleInDegrees * PI.toFloat() / 180f
                    val radius = size.minDimension / 2f
                    val startRadius = radius * 0.92f
                    val endRadius = radius * 0.7f
                    val startPos = Offset(
                        cos(theta) * startRadius,
                        sin(theta) * startRadius,
                    )
                    val endPos = Offset(
                        cos(theta) * endRadius,
                        sin(theta) * endRadius,
                    )
                    drawLine(
                        color = contentColor,
                        strokeWidth = 2f,
                        start = center + (startPos * sparkleTranslationAnimation.value),
                        end = center + (endPos * sparkleTranslationAnimation.value),
                        alpha = sparkleAlphaAnimation.value,
                    )
                }
            }
        }
    }
}

private const val HeartPathData = "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"

@PodcasterPreview
@Composable
fun FavoriteButtonPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteButton(
            isFavorite = isFavorite,
            onToggle = { isFavorite = it },
        )
    }
}
