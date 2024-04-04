package com.mr3y.podcaster.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            configureJava()
            configureKotlin()
            configureKtlint()
        }
    }
}
