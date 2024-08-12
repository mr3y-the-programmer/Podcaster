package com.mr3y.podcaster.gradle

import com.android.build.api.dsl.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.test")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            val extension = extensions.getByType<TestExtension>()
            configureAndroidTestExtension(extension)
        }
    }

    private fun Project.configureAndroidTestExtension(
        testExtension: TestExtension
    ) {
        testExtension.apply {
            compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

            defaultConfig {
                minSdk = 28 // Generating baseline profiles isn't supported on devices running Android API 27 and lower.
                targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        configureKotlin()
        configureKtlint()
    }
}