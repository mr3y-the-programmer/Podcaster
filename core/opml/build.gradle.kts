plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.opml"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        )
    }
}

dependencies {

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.logger)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(libs.bundles.serialization)
    implementation(libs.result)

    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(libs.bundles.unit.testing)
}
