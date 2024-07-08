package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mr3y.podcaster.LocalStrings

@Composable
fun TopBar(
    onUpButtonClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val strings = LocalStrings.current
    val navIconContentColor = if (colors.navigationIconContentColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else colors.navigationIconContentColor
    TopAppBar(
        navigationIcon = {
            if (onUpButtonClick != null) {
                IconButton(
                    onClick = onUpButtonClick,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent, contentColor = navIconContentColor),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.icon_navigate_up_content_description,
                    )
                }
            }
        },
        title = title,
        actions = actions,
        colors = colors,
        modifier = modifier.fillMaxWidth(),
        scrollBehavior = scrollBehavior,
    )
}
