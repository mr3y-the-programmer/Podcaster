plugins {
    alias(libs.plugins.podcaster.compose.android.lib)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.widget"
}

dependencies {
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)

    implementation(libs.bundles.glance)
    implementation(libs.material3)
    implementation(libs.coil.mp)

    implementation(projects.core.data)
    implementation(projects.core.logger)
    implementation(projects.core.model)
    implementation(projects.ui.resources)
    implementation(projects.ui.designSystem)
}