plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.data"
}

dependencies {

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.logger)
    implementation(libs.result)

    testImplementation(projects.core.networkTestFixtures)
    testImplementation(projects.core.databaseTestFixtures)
    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(libs.bundles.unit.testing)
    testImplementation(libs.ktor.client.mock)
}
