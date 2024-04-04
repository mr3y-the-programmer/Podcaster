plugins {
    alias(libs.plugins.podcaster.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.core.logger"
}

dependencies {

    implementation(projects.core.logger)
}
