plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.sync"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.androidx.compiler)
    implementation(libs.bundles.workmanager)
    implementation(libs.hilt.runtime)
}
