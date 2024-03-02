package com.mr3y.podcaster.core.logger.internal

import com.mr3y.podcaster.core.logger.Logger
import javax.inject.Inject
import co.touchlab.kermit.Logger as KermitLogger

class DefaultLogger @Inject constructor(
    private val kermitLogger: KermitLogger,
) : Logger {
    override fun d(throwable: Throwable?, tag: String, message: () -> String) {
        kermitLogger.d(throwable, tag, message)
    }

    override fun i(throwable: Throwable?, tag: String, message: () -> String) {
        kermitLogger.i(throwable, tag, message)
    }

    override fun e(throwable: Throwable?, tag: String, message: () -> String) {
        kermitLogger.e(throwable, tag, message)
    }

    override fun v(throwable: Throwable?, tag: String, message: () -> String) {
        kermitLogger.v(throwable, tag, message)
    }

    override fun w(throwable: Throwable?, tag: String, message: () -> String) {
        kermitLogger.w(throwable, tag, message)
    }
}
