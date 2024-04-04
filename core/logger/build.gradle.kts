plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.logger"
}

dependencies {
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)

    implementation(libs.kermit)
    implementation(libs.kermit.crashlytics)
}
