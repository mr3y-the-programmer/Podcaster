plugins {
    alias(libs.plugins.podcaster.compose.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.ui"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
                "-opt-in=coil3.annotation.ExperimentalCoilApi",
            ),
        )
    }
}

dependencies {
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.bundles.compose)
    implementation(libs.core.ktx)

    implementation(projects.core.model)
    implementation(projects.ui.preview)
    implementation(projects.ui.resources)

    // For previews
    debugImplementation(libs.ui.tooling)
}
