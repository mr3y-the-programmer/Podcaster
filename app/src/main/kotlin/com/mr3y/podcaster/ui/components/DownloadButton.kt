package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun DownloadButton(
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.tertiaryPrimary
) {
    OutlinedIconButton(
        onClick = onDownload,
        modifier = modifier
            .size(48.dp)
            .padding(8.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = contentColor),
        border = BorderStroke(1.dp, contentColor)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowDownward,
            contentDescription = null,
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadButtonPreview() {
    PodcasterTheme(dynamicColor = false) {
        DownloadButton(
            onDownload = { },
            onCancelDownload = {}
        )
    }
}
