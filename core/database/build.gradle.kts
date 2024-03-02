import app.cash.sqldelight.core.capitalize
import app.cash.sqldelight.gradle.SqlDelightTask

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.mr3y.podcaster.core.local"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }
    buildFeatures {
        compose = false
        buildConfig = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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

ktlint {
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
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

    testImplementation(libs.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(projects.core.databaseTestFixtures)
}