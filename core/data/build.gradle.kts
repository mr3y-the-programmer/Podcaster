plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.mr3y.podcaster.core.data"
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
                "proguard-rules.pro",
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

ktlint {
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
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
    testImplementation(libs.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
