import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.podcaster.android.app)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.appversioning)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.mr3y.podcaster"

    defaultConfig {
        applicationId = "com.mr3y.podcaster"
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

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            all {
                it.useJUnit {
                    if (project.hasProperty("screenshot")) {
                        includeCategories("com.mr3y.podcaster.ui.screens.ScreenshotTests")
                    } else {
                        excludeCategories("com.mr3y.podcaster.ui.screens.ScreenshotTests")
                    }
                }
            }
        }
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

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=coil3.annotation.ExperimentalCoilApi",
            )
        )
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(libs.bundles.androidx.core)

    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.logger)
    implementation(projects.core.network)
    implementation(projects.core.data)
    implementation(projects.core.sync)
    implementation(projects.core.opml)
    implementation(projects.ui.resources)
    implementation(projects.ui.preview)
    implementation(projects.ui.designSystem)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation)
    implementation(libs.molecule)
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

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(libs.hilt.workmanager)

    implementation(libs.bundles.media3)

    kspTest(libs.hilt.compiler)
    testImplementation(projects.core.databaseTestFixtures)
    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(projects.core.networkTestFixtures)
    testImplementation(libs.bundles.unit.testing)
    testImplementation(libs.bundles.android.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.media3.testUtils.core)
    testImplementation(libs.media3.testUtils.robolectric)

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
