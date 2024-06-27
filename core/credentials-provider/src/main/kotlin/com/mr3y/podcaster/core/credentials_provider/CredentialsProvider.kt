package com.mr3y.podcaster.core.credentials_provider

object CredentialsProvider {

    private lateinit var key: String
    private lateinit var secret: String

    fun setAPICredentials(key: String, secret: String) {
        this.key = key
        this.secret = secret
    }

    fun apiKey(): String {
        if (!::key.isInitialized) throw IllegalStateException("API Key isn't provided! make sure to set it first before trying to access its value")
        return key
    }

    fun apiSecret(): String {
        if (!::secret.isInitialized) throw IllegalStateException("API Secret isn't provided! make sure to set it first before trying to access its value")
        return secret
    }
}
