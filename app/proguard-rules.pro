# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-verbose
-allowaccessmodification
-repackageclasses

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-dontwarn com.mr3y.podcaster.PodcasterDatabase
-dontwarn com.mr3y.podcaster.StringsKt
-dontwarn com.mr3y.podcaster.core.data.PodcastsRepository
-dontwarn com.mr3y.podcaster.core.data.internal.DefaultPodcastsRepository
-dontwarn com.mr3y.podcaster.core.local.dao.DefaultPodcastsDao
-dontwarn com.mr3y.podcaster.core.local.dao.DefaultRecentSearchesDao
-dontwarn com.mr3y.podcaster.core.local.dao.PodcastsDao
-dontwarn com.mr3y.podcaster.core.local.dao.RecentSearchesDao
-dontwarn com.mr3y.podcaster.core.local.di.DatabaseModule_ProvideDatabaseInstanceFactory
-dontwarn com.mr3y.podcaster.core.local.di.DatabaseModule_ProvideIOCoroutineDispatcherFactory
-dontwarn com.mr3y.podcaster.core.logger.Logger
-dontwarn com.mr3y.podcaster.core.logger.di.LoggingModule_ProvideLoggerInstanceFactory
-dontwarn com.mr3y.podcaster.core.network.PodcastIndexClient
-dontwarn com.mr3y.podcaster.core.network.di.NetworkModule_ProvideJsonInstanceFactory
-dontwarn com.mr3y.podcaster.core.network.di.NetworkModule_ProvideKtorClientInstanceFactory
-dontwarn com.mr3y.podcaster.core.network.internal.DefaultPodcastIndexClient
-dontwarn com.mr3y.podcaster.core.opml.FileManager
-dontwarn com.mr3y.podcaster.core.opml.OpmlAdapter
-dontwarn com.mr3y.podcaster.core.opml.OpmlManager
-dontwarn com.mr3y.podcaster.core.opml.di.XMLSerializerModule_ProvideIOCoroutineDispatcherFactory
-dontwarn com.mr3y.podcaster.core.opml.di.XMLSerializerModule_ProvideXMLInstanceFactory
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error$DecodingError
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error$EncodingError
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error$NetworkError
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error$NoContentInOpmlFile
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error$UnknownFailure
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Error
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Loading
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult$Success
-dontwarn com.mr3y.podcaster.core.opml.model.OpmlResult
-dontwarn com.mr3y.podcaster.core.sync.InitializerKt
-dontwarn com.mr3y.podcaster.core.sync.SubscriptionsSyncWorker_AssistedFactory
-dontwarn com.mr3y.podcaster.ui.components.DownloadButtonKt
-dontwarn com.mr3y.podcaster.ui.components.ErrorKt
-dontwarn com.mr3y.podcaster.ui.components.HtmlConverterKt
-dontwarn com.mr3y.podcaster.ui.components.LoadingIndicatorKt
-dontwarn com.mr3y.podcaster.ui.components.PaddingValuesKt
-dontwarn com.mr3y.podcaster.ui.components.PlayPauseButtonKt
-dontwarn com.mr3y.podcaster.ui.components.PullToRefreshKt
-dontwarn com.mr3y.podcaster.ui.components.QueueButtonsKt
-dontwarn com.mr3y.podcaster.ui.components.TopAppBarKt
-dontwarn com.mr3y.podcaster.ui.resources.PodcasterEnStringsKt
-dontwarn com.mr3y.podcaster.ui.resources.PodcasterStrings
-dontwarn com.mr3y.podcaster.ui.theme.ColorUtilsKt
-dontwarn com.mr3y.podcaster.ui.theme.Theme
-dontwarn com.mr3y.podcaster.ui.theme.ThemeKt
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation