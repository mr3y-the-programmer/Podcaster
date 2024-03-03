import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.appversioning)
    alias(libs.plugins.play.publisher)
}

android {
    namespace = "com.mr3y.podcaster"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mr3y.podcaster"
        minSdk = 26
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            if (rootProject.file("keystore.properties").exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(rootProject.file("keystore.properties")))
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ktlint {
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

appVersioning {
    overrideVersionName { gitTag, providerFactory, variantInfo ->
        val buildNumber = providerFactory
            .environmentVariable("GITHUB_RUN_NUMBER")
            .getOrElse("0").toInt()
        val buildTypeSuffix = if (variantInfo.isDebugBuild) "-debug" else ""
        if (buildNumber != 0) {
            "${gitTag.rawTagName}$buildTypeSuffix - #$buildNumber (${gitTag.commitHash})"
        } else {
            "${gitTag.rawTagName}$buildTypeSuffix (${gitTag.commitHash})"
        }
    }
    overrideVersionCode { _, _, _ ->
        Instant.now().epochSecond.toInt()
    }
}

play {
    // Enable automated publishing for CI only for now, as we don't need it locally.
    if (System.getenv("CI").toBoolean()) {
        defaultToAppBundles.set(true)
        track.set("production")
    } else {
        enabled.set(false)
    }
}

hilt {
    enableAggregatingTask = true
}

ksp {
    arg("lyricist.packageName", "com.mr3y.podcaster")
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.splashscreen)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)

    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.logger)
    implementation(projects.core.network)
    implementation(projects.core.data)
    implementation(projects.core.sync)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.htmlconverter)
    implementation(libs.kmpalette.core)
    implementation(libs.lyricist)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.navigation)
    implementation(libs.navigation.typed)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.molecule)
    implementation(libs.coil)
    implementation(libs.result)

    implementation(libs.kotlinx.serialization)

    implementation(platform(libs.firebase.bom))
    val excludeAndroidxDataStore = Action<ExternalModuleDependency> {
        // Crashlytics is built with datastore v1.0 but we're using v1.1, and v1.1 isn't binary compatible with v1.0
        exclude(group = "androidx.datastore", module = "datastore-preferences")
    }
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics, excludeAndroidxDataStore)

    implementation(libs.datastore.pref)
    implementation(libs.aboutlibraries.m3)

    ksp(libs.lyricist.processor)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(libs.hilt.workmanager)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.common)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer.workmanager)
    implementation(libs.coroutines.guava)

    kspTest(libs.hilt.compiler)
    testImplementation(projects.core.databaseTestFixtures)
    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(projects.core.networkTestFixtures)
    testImplementation(libs.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.ktor.client.mock)

    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.workmanager.testing)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.leakcanary)
}
