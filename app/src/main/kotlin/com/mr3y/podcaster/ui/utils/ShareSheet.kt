package com.mr3y.podcaster.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mr3y.podcaster.LocalStrings

@Composable
fun TopBarMoreOptionsButton(
    title: String,
    sharedText: String,
    modifier: Modifier = Modifier,
    colors: IconButtonColors? = null
) {
    var showOptions by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    val strings = LocalStrings.current
    val context = LocalContext.current
    IconButton(
        onClick = { showOptions = !showOptions },
        colors = colors ?: IconButtonDefaults.iconButtonColors(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = strings.icon_more_options_content_description
        )
    }

    DropdownMenu(
        expanded = showOptions,
        onDismissRequest = { showOptions = false },
    ) {
        DropdownMenuItem(
            text = { Text(strings.share_label) },
            onClick = { showShareSheet = true }
        )
    }

    LaunchedEffect(showShareSheet) {
        if (showShareSheet) {
            launchShareSheet(context, title, sharedText)
            showShareSheet = false
            showOptions = false
        }
    }
}

internal fun launchShareSheet(context: Context, title: String, url: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
