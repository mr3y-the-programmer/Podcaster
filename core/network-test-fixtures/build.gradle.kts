plugins {
    alias(libs.plugins.podcaster.android.lib)
}

android {
    namespace = "com.mr3y.podcaster.core.network"
}

dependencies {

    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.mock)
}
