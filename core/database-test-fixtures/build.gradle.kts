plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.mr3y.podcaster.core.local.di"
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

sqldelight {
    databases {
        create("PodcasterDatabase") {
            packageName.set("com.mr3y.podcaster.test") // This must be different from packageName set in database module
            dependency(projects.core.database)
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
    implementation(projects.core.database)
    implementation(libs.sqldelight.sqlitedriver)
    implementation(libs.sqldelight.primitiveadapters)
    implementation(libs.sqlite.jdbc) {
        version { strictly(libs.versions.sqlite.jdbc.get()) }
    }
}