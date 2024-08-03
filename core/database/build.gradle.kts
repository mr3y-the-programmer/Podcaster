import app.cash.sqldelight.core.capitalize
import app.cash.sqldelight.gradle.SqlDelightTask

plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.mr3y.podcaster.core.local"
}

androidComponents {
    onVariants(selector().all()) { variant ->
        // TODO: find a way to get rid of the obscure `afterEvaluate` here
        afterEvaluate {
            val sqlDelightTask = this.project.tasks.named("generate${variant.name.capitalize()}PodcasterDatabaseInterface").get() as SqlDelightTask

            project.tasks.getByName("ksp" + variant.name.capitalize() + "Kotlin") {
                (this as org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool<*>).setSource(sqlDelightTask.outputDirectory)
            }
        }
    }
}

sqldelight {
    databases {
        create("PodcasterDatabase") {
            packageName.set("com.mr3y.podcaster")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            ),
        )
    }
}

dependencies {

    implementation(projects.core.model)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.runtime)

    implementation(libs.sqldelight.driver)
    implementation(libs.sqldelight.flowext)
    implementation(libs.sqldelight.primitiveadapters)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.bundles.unit.testing)
    testImplementation(projects.core.databaseTestFixtures)
}
