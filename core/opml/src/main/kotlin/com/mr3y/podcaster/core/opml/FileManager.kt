package com.mr3y.podcaster.core.opml

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val application = context as Application
    private val result = Channel<String?>()

    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>

    private var content: String? = null

    fun save(name: String, content: String) {
        this.content = content

        if (!this.content.isNullOrBlank()) {
            createDocumentLauncher.launch(name)
        }
    }

    suspend fun read(): String? {
        openDocumentLauncher.launch(
            arrayOf("application/xml", "application/octet-stream", "text/xml", "text/x-opml"),
        )
        return result.receiveAsFlow().first()
    }

    fun registerActivityWatcher() {
        val callback = object : Application.ActivityLifecycleCallbacks {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
            val appList = application.packageManager.queryIntentActivities(launcherIntent, 0)

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                if (
                    activity is ComponentActivity &&
                    appList.any { it.activityInfo.name == activity::class.qualifiedName }
                ) {
                    registerDocumentCreateActivityResult(activity)
                    registerDocumentOpenActivityResult(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityResumed(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivityStopped(activity: Activity) = Unit

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(activity: Activity) = Unit
        }
        application.registerActivityLifecycleCallbacks(callback)
    }

    private fun registerDocumentCreateActivityResult(activity: ComponentActivity) {
        createDocumentLauncher = activity.registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/xml"),
        ) { uri ->
            if (uri == null) return@registerForActivityResult

            val outputStream = application.contentResolver.openOutputStream(uri)
            outputStream?.use { it.write(content?.toByteArray()) }

            content = null
        }
    }

    private fun registerDocumentOpenActivityResult(activity: ComponentActivity) {
        openDocumentLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@registerForActivityResult

            val inputStream = application.contentResolver.openInputStream(uri)
            inputStream?.use {
                val content = it.bufferedReader().readText()
                result.trySend(content)
            }
        }
    }
}
