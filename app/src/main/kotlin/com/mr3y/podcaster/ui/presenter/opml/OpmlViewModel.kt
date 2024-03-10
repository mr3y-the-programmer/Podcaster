package com.mr3y.podcaster.ui.presenter.opml

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr3y.podcaster.core.opml.OpmlManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpmlViewModel @Inject constructor(
    private val opmlManager: OpmlManager,
) : ViewModel() {

    val result = opmlManager.result

    fun import() {
        viewModelScope.launch {
            opmlManager.cancelCurrentRunningTask()
            opmlManager.import()
        }
    }

    fun export() {
        viewModelScope.launch {
            opmlManager.cancelCurrentRunningTask()
            opmlManager.export()
        }
    }

    fun consumeResult() {
        viewModelScope.launch { opmlManager.resetResultState() }
    }
}
