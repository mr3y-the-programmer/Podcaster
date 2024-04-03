package com.mr3y.podcaster.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidApplicationExtension(extension)
        }
    }


    private fun Project.configureAndroidApplicationExtension(
        applicationExtension: ApplicationExtension,
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

            composeOptions {
                kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }

        configureKotlin()
        configureKtlint()
    }
}
