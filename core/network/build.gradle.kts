import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.core.network"

    defaultConfig {

        buildConfigField("String", "API_KEY", "\"${getValueOfKey("API_KEY")}\"")
        buildConfigField("String", "API_SECRET", "\"${getValueOfKey("API_SECRET")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            ),
        )
    }
}

fun getValueOfKey(key: String): String {
    return if (System.getenv("CI").toBoolean()) {
        System.getenv(key)
    } else {
        val properties = Properties()
        properties.load(FileInputStream(rootProject.file("local.properties")))
        properties.getProperty(key)
    }
}

dependencies {

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)
    implementation(projects.core.model)
    implementation(projects.core.logger)
    implementation(libs.bundles.ktor)
    implementation(libs.result)
    implementation(libs.kotlinx.serialization)

    testImplementation(projects.core.networkTestFixtures)
    testImplementation(projects.core.loggerTestFixtures)
    testImplementation(libs.bundles.unit.testing)
}
