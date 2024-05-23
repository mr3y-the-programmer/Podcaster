plugins {
    alias(libs.plugins.podcaster.compose.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.ui.preview"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.ui.tooling.preview)
}
