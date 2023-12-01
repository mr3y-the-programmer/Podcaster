package com.mr3y.podcaster.ui.preview

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE

// TODO: use the predefined PreviewX that ships
//  with ui-tooling-preview dependency when you update to 1.6.0 stable release.

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(device = "spec:width=411dp,height=891dp", uiMode = Configuration.UI_MODE_NIGHT_NO, wallpaper = GREEN_DOMINATED_EXAMPLE)
@Preview(device = "spec:width=411dp,height=891dp", uiMode = Configuration.UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, wallpaper = GREEN_DOMINATED_EXAMPLE)
annotation class PodcasterPreview

class DynamicColorsParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}
