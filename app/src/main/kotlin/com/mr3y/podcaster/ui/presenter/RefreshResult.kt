package com.mr3y.podcaster.ui.presenter

sealed interface RefreshResult {
    data object Ok : RefreshResult

    data object Error : RefreshResult

    data object Mixed : RefreshResult
}
