package com.mr3y.podcaster.core.opml.model

sealed interface OpmlResult {
    data object Idle : OpmlResult

    data object Loading : OpmlResult

    sealed interface Error : OpmlResult {
        data object NoContentInOpmlFile : Error

        data object EncodingError : Error

        data object DecodingError : Error

        data object NetworkError : Error

        data class UnknownFailure(val error: Exception) : Error
    }
}
