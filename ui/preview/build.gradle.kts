plugins {
    alias(libs.plugins.podcaster.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.ui.preview"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.ui.tooling.preview)
}
