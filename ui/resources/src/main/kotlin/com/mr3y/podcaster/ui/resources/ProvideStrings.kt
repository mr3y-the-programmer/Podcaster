package com.mr3y.podcaster.ui.resources

import androidx.compose.runtime.Composable
import com.mr3y.podcaster.ProvideStrings

@Composable
fun ProvideAppStrings(
    content: @Composable () -> Unit
) {
    ProvideStrings(content = content)
}
