plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.network"
}

dependencies {

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(projects.core.model)
    implementation(projects.core.logger)
    implementation(projects.core.credentialsProvider)
    implementation(libs.bundles.ktor)
    implementation(libs.result)
    implementation(libs.kotlinx.serialization)

    testImplementation(projects.core.networkTestFixtures)
    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(libs.bundles.unit.testing)
}
