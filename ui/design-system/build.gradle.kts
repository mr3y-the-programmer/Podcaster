plugins {
    alias(libs.plugins.podcaster.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.ui"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    implementation(projects.core.model)
    implementation(projects.ui.preview)
    implementation(projects.ui.resources)
}
