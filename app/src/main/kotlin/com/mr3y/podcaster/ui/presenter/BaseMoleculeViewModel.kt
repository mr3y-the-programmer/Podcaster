package com.mr3y.podcaster.ui.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class BaseMoleculeViewModel<Event : Any> : ViewModel() {

    protected val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

    protected val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)
}
