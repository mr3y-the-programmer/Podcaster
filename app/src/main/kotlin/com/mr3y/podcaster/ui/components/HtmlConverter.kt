package com.mr3y.podcaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString

@Composable
fun rememberHtmlToAnnotatedString(text: String): AnnotatedString {
    return remember(text) { htmlToAnnotatedString(text) }
}
