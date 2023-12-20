package com.mr3y.podcaster.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.mr3y.podcaster.ui.theme.primaryTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    isRefreshingDone: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primaryTertiary,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshingDone) {
        if (isRefreshingDone && state.isRefreshing) {
            state.endRefresh()
        }
    }

    Box(modifier = modifier.nestedScroll(state.nestedScrollConnection)) {
        content()
        
        PullToRefreshContainer(
            state = state,
            contentColor = contentColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}