package com.mr3y.podcaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.TextUnit
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString

@Composable
fun rememberHtmlToAnnotatedString(
    text: String,
    indentUnit: TextUnit? = null,
): AnnotatedString {
    return remember(text) {
        htmlToAnnotatedString(
            text,
            style = if (indentUnit != null) HtmlStyle(indentUnit = indentUnit) else HtmlStyle.DEFAULT,
        )
    }
}
