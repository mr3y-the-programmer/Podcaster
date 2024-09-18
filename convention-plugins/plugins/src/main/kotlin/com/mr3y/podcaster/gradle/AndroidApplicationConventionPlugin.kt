package com.mr3y.podcaster.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val applicationExtension = extensions.getByType<ApplicationExtension>()
            val composeExtension = extensions.getByType<ComposeCompilerGradlePluginExtension>()
            configureAndroidApplicationExtension(applicationExtension, composeExtension)
        }
    }


    private fun Project.configureAndroidApplicationExtension(
        applicationExtension: ApplicationExtension,
        composeExtension: ComposeCompilerGradlePluginExtension,
    ) {
        applicationExtension.apply {
            compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

            defaultConfig {
                minSdk = libs.findVersion("minSdk").get().toString().toInt()
                targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

            composeExtension.apply {
                reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
                jniLibs {
                    excludes += "**/libdatastore_shared_counter.so"
                }
            }
        }

        configureKotlin()
        configureKtlint()
    }
}
