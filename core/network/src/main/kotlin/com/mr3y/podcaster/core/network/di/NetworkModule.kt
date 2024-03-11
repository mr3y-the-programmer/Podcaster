package com.mr3y.podcaster.core.network.di

import android.content.Context
import com.mr3y.podcaster.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.sha1
import kotlinx.serialization.json.Json
import okhttp3.Cache
import java.io.File
import java.util.Formatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJsonInstance(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideKtorClientInstance(@ApplicationContext applicationContext: Context, jsonInstance: Json): HttpClient {
        fun byteToHex(binary: ByteArray): String {
            Formatter().use { formatter ->
                for (b in binary) {
                    formatter.format("%02x", b)
                }
                return formatter.toString()
            }
        }

        fun authHeader(epoch: Long): String {
            val authHash = sha1("${BuildConfig.API_KEY}${BuildConfig.API_SECRET}$epoch".toByteArray())
            return byteToHex(authHash)
        }

        // Add the necessary authentication headers to every request going out to PodcastIndex endpoints
        val requestAuthenticationPlugin = createClientPlugin("requestAuthenticatorPlugin") {
            onRequest { request, _ ->
                request.headers {
                    val epoch = (System.currentTimeMillis() / 1000).let { if (BuildConfig.DEBUG) it - 150 else it }
                    append("User-Agent", "Podcaster/1.0")
                    append("X-Auth-Date", epoch.toString())
                    append("X-Auth-Key", BuildConfig.API_KEY)
                    append("Authorization", authHeader(epoch))
                }
            }
        }

        return HttpClient(OkHttp) {
            engine {
                config {
                    cache(Cache(directory = File(applicationContext.cacheDir, "okhttp_cache"), maxSize = 80L * 1024L * 1024L))
                }
            }
            install(HttpRequestRetry) {
                retryIf(3) { _, httpResponse ->
                    when {
                        httpResponse.status.value in 500..599 -> true
                        httpResponse.status == HttpStatusCode.TooManyRequests -> true
                        else -> false
                    }
                }
                constantDelay()
            }
            install(requestAuthenticationPlugin)
            install(ContentNegotiation) {
                json(jsonInstance)
            }
        }
    }
}
