package com.mr3y.podcaster.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

fun Project.configureKtlint() {
    ktlint {
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
        }
    }
}

private fun Project.ktlint(action: KtlintExtension.() -> Unit) = extensions.configure<KtlintExtension>(action)
