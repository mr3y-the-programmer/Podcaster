plugins {
    alias(libs.plugins.podcaster.compose.android.lib)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.ui.resources"
}

ksp {
    arg("lyricist.packageName", "com.mr3y.podcaster")
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    ksp(libs.lyricist.processor)
    implementation(libs.lyricist)
}
