package com.mr3y.podcaster.core.data.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal fun <T : Any> Flow<T>.asResultFlow(): Flow<Result<T, Any>> {
    return map<T, Result<T, Any>> { Ok(it) }
        .catch { exception -> emit(Err(exception)) }
}
