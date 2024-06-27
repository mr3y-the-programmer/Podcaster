plugins {
    alias(libs.plugins.podcaster.compose.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.ui"
}

dependencies {
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.bundles.compose)

    implementation(projects.core.model)
    implementation(projects.ui.preview)
    implementation(projects.ui.resources)
}
